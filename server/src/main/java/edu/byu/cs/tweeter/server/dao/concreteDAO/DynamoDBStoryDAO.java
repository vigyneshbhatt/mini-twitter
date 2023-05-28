package edu.byu.cs.tweeter.server.dao.concreteDAO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.server.dao.bean.FeedBean;
import edu.byu.cs.tweeter.server.dao.bean.StoryBean;
import edu.byu.cs.tweeter.server.dao.iDAO.iStoryDAO;
import edu.byu.cs.tweeter.util.FakeData;
import edu.byu.cs.tweeter.util.Pair;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;
import software.amazon.awssdk.enhanced.dynamodb.model.PageIterable;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

public class DynamoDBStoryDAO implements iStoryDAO {

    private static final String TableName = "story";
    private static final String PARTITION_KEY = "authorAlias";
    private static final String SORT_KEY = "timestamp";

    private final DynamoDbEnhancedClient enhancedClient;
    private boolean hasMorePages = false;

    public DynamoDBStoryDAO(DynamoDbEnhancedClient enhancedClient) {
        this.enhancedClient = enhancedClient;
    }

    private DynamoDbTable<StoryBean> getDynamoDbTable() {
        return enhancedClient.table(TableName, TableSchema.fromBean(StoryBean.class));
    }

    private Key getKey(String userAlias) {
        return Key.builder()
                .partitionValue(userAlias).build();
    }

    public void setHasMorePages(boolean hasMorePages) {
        this.hasMorePages = hasMorePages;
    }

    @Override
    public void postStatus(Status status) {
        try {
            StoryBean newItem = new StoryBean();

            newItem.setTimestamp(status.getTimestamp());
            newItem.setPostString(status.getPost());
            newItem.setMentions(status.getMentions());
            newItem.setUrls(status.getUrls());
            newItem.setUserAlias(status.getUser().getAlias());

            getDynamoDbTable().putItem(newItem);
        } catch (Exception e) {
            System.out.println("## StatusDAO postStatus Error- "+ e.getMessage());
            throw new RuntimeException("[Internal Error] Failed to post status to story: "+ e.getMessage());
        }
    }

    @Override
    public Pair<List<Status>, Boolean> getStory(User user, Status lastStatus, int limit) {
        Key key = getKey(user.getAlias());

        QueryEnhancedRequest.Builder requestBuilder = QueryEnhancedRequest.builder()
                .queryConditional(QueryConditional.keyEqualTo(key)).limit(limit);

        if(lastStatus!=null) {
            Map<String, AttributeValue> startKey = new HashMap<>();
            startKey.put(PARTITION_KEY, AttributeValue.builder().s(user.getAlias()).build());
            startKey.put(SORT_KEY, AttributeValue.builder().n(String.valueOf(lastStatus.getTimestamp())).build());

            requestBuilder.exclusiveStartKey(startKey);
        }

        QueryEnhancedRequest request = requestBuilder.scanIndexForward(false).build();

        List<Status> statuses = new ArrayList<>();

        try {
            PageIterable<StoryBean> pages = getDynamoDbTable().query(request);

            pages.stream().limit(1).forEach((Page<StoryBean> page) -> {
                setHasMorePages(page.lastEvaluatedKey() != null);
                page.items().forEach(status -> statuses.add(new Status(status.getPostString(), user, status.getTimestamp(), status.getUrls(), status.getMentions())));
            });
            return new Pair<>(statuses, hasMorePages);
        } catch (Exception e) {
            System.out.println("## StatusDAO getStory Error- "+ e.getMessage());
            throw new RuntimeException("[Internal Error] Failed to get story: " + e.getMessage());
        }
    }
}
