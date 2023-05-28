package edu.byu.cs.tweeter.server.dao.concreteDAO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.server.dao.bean.AuthTokenBean;
import edu.byu.cs.tweeter.server.dao.bean.FeedBean;
import edu.byu.cs.tweeter.server.dao.iDAO.iFeedDAO;
import edu.byu.cs.tweeter.util.FakeData;
import edu.byu.cs.tweeter.util.Pair;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.BatchWriteItemEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.BatchWriteResult;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;
import software.amazon.awssdk.enhanced.dynamodb.model.PageIterable;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.WriteBatch;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;

public class DynamoDBFeedDAO implements iFeedDAO {

    private static final String TableName = "feed";
    private static final String PARTITION_KEY = "userAlias";
    private static final String SORT_KEY = "timestamp";

    private final DynamoDbEnhancedClient enhancedClient;
    private boolean hasMorePages = false;

    public DynamoDBFeedDAO(DynamoDbEnhancedClient enhancedClient) {
        this.enhancedClient = enhancedClient;
    }

    private DynamoDbTable<FeedBean> getDynamoDbTable() {
        return enhancedClient.table(TableName, TableSchema.fromBean(FeedBean.class));
    }

    private Key getKey(String userAlias) {
        return Key.builder()
                .partitionValue(userAlias).build();
    }

    public void setHasMorePages(boolean hasMorePages) {
        this.hasMorePages = hasMorePages;
    }

    @Override
    public void postStatus(List<String> followers, Status status) {
        User author  = status.getUser();

        try {
            for (String follower : followers) {
                FeedBean newItem = new FeedBean();
                newItem.setUserAlias(follower);
                newItem.setTimestamp(status.getTimestamp());

                newItem.setAuthorAlias(author.getAlias());
                newItem.setAuthorFirstName(author.getFirstName());
                newItem.setAuthorLastName(author.getLastName());
                newItem.setAuthorImageUrl(author.getImageUrl());

                newItem.setPostString(status.getPost());
                newItem.setMentions(status.getMentions());
                newItem.setUrls(status.getUrls());

                getDynamoDbTable().putItem(newItem);
            }
        } catch (Exception e) {
            throw new RuntimeException("[Internal Error] Failed to post status to followers' feed: "+ e.getMessage());
        }
    }

    @Override
    public Pair<List<Status>, Boolean> getFeed(String userAlias, Status lastStatus, Integer limit) {
        Key key = getKey(userAlias);

        QueryEnhancedRequest.Builder requestBuilder = QueryEnhancedRequest.builder()
                .queryConditional(QueryConditional.keyEqualTo(key))
                .limit(limit);

        if(lastStatus!=null) {
            Map<String, AttributeValue> startKey = new HashMap<>();
            startKey.put(PARTITION_KEY, AttributeValue.builder().s(userAlias).build());
            startKey.put(SORT_KEY, AttributeValue.builder().n(String.valueOf(lastStatus.getTimestamp())).build());

            requestBuilder.exclusiveStartKey(startKey);
        }

        QueryEnhancedRequest request = requestBuilder.scanIndexForward(false).build();

        List<Status> statuses = new ArrayList<>();

        try {
            PageIterable<FeedBean> pages = getDynamoDbTable().query(request);

            pages.stream().limit(1).forEach((Page<FeedBean> page) -> {
                setHasMorePages(page.lastEvaluatedKey() != null);
                page.items().forEach(status -> statuses.add(new Status(status.getPostString(),
                        new User(status.getAuthorFirstName(),status.getAuthorLastName(),status.getAuthorAlias(),status.getAuthorImageUrl()),
                        status.getTimestamp(), status.getUrls(), status.getMentions())));
            });

            return new Pair<>(statuses,hasMorePages);
        } catch (Exception e) {
            throw new RuntimeException("[Internal Error] Failed to get feed: "+ e.getMessage());
        }
    }

    @Override
    public void batchWriteStatus(Status status, List<String> followers) {
        List<FeedBean> batchToWrite = new ArrayList<>();
        System.out.println("### 4th Stage Success: In FeedDAO");

        // Add each user into the TableWriteItems object
        for (String follower : followers) {
            User author = status.getUser();
            FeedBean newItem = new FeedBean();
            newItem.setUserAlias(follower);

            newItem.setAuthorAlias(author.getAlias());
            newItem.setAuthorImageUrl(author.getImageUrl());
            newItem.setAuthorLastName(author.getLastName());
            newItem.setAuthorFirstName(author.getFirstName());

            newItem.setPostString(status.getPost());
            newItem.setTimestamp(status.getTimestamp());
            newItem.setUrls(status.getUrls());
            newItem.setMentions(status.getMentions());

            batchToWrite.add(newItem);

            if (batchToWrite.size() == 25) {
                // package this batch up and send to DynamoDB.
                writeChunkOfStatusDTOs(batchToWrite);
                batchToWrite = new ArrayList<>();
            }
        }

        // write any remaining
        if (batchToWrite.size() > 0) {
            // package this batch up and send to DynamoDB.
            writeChunkOfStatusDTOs(batchToWrite);
        }
    }

    private void writeChunkOfStatusDTOs(List<FeedBean> items) {

        System.out.println("### 5th Stage Success: BatchWriting");

        if(items.size() > 25) {
            throw new RuntimeException("[Internal Error] Batch size exceeds 25");
        }

        WriteBatch.Builder<FeedBean> writeBuilder = WriteBatch.builder(FeedBean.class).mappedTableResource(getDynamoDbTable());
        for (FeedBean item : items) {
            writeBuilder.addPutItem(builder -> builder.item(item));
        }
        BatchWriteItemEnhancedRequest batchWriteItemEnhancedRequest = BatchWriteItemEnhancedRequest.builder()
                .writeBatches(writeBuilder.build()).build();

        try {
            BatchWriteResult result = enhancedClient.batchWriteItem(batchWriteItemEnhancedRequest);
            // just hammer dynamodb again with anything that didn't get written this time
            if (result.unprocessedPutItemsForTable(getDynamoDbTable()).size() > 0) {
                writeChunkOfStatusDTOs(result.unprocessedPutItemsForTable(getDynamoDbTable()));
            }

        } catch (DynamoDbException e) {
            System.out.println("### Error, Cannot write batch to Feed table: "+e.getMessage());
            throw new RuntimeException("[Internal Error] Cannot write batch to Feed table");
        }
    }
}
