package edu.byu.cs.tweeter.server.dao.concreteDAO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.net.request.GetFollowersRequest;
import edu.byu.cs.tweeter.model.net.request.GetFollowingRequest;
import edu.byu.cs.tweeter.server.dao.bean.FollowBean;
import edu.byu.cs.tweeter.server.dao.iDAO.iFollowDAO;
import edu.byu.cs.tweeter.util.Pair;
import software.amazon.awssdk.core.pagination.sync.SdkIterable;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbIndex;
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

/**
 * A DAO for accessing 'following' data from the database.
 */
public class DynamoDBFollowDAO implements iFollowDAO {

    private static final String follower_handle_attribute = "follower_handle";
    private static final String followee_handle_attribute = "followee_handle";

    public static final String IndexName = "follows_index";
    private static final String TableName = "follows";
    private boolean hasMorePagesFollowees = false;
    private boolean hasMorePagesFollowers = false;

    private final DynamoDbEnhancedClient enhancedClient;

    public DynamoDBFollowDAO(DynamoDbEnhancedClient enhancedClient) {
        this.enhancedClient = enhancedClient;
    }

    private DynamoDbTable<FollowBean> getDynamoDbTable() {
        return enhancedClient.table(TableName, TableSchema.fromBean(FollowBean.class));
    }

    private Key getCompositeKey(String pk_handle, String sk_handle) {
        return Key.builder()
                .partitionValue(pk_handle).sortValue(sk_handle)
                .build();
    }

    private Key getPartitionKey(String pk_handle) {
        return Key.builder()
                .partitionValue(pk_handle)
                .build();
    }

    public void setHasMorePagesFollowees(boolean hasMorePagesFollowees){
        this.hasMorePagesFollowees = hasMorePagesFollowees;
    }

    public void setHasMorePagesFollowers(boolean hasMorePagesFollowers){
        this.hasMorePagesFollowers = hasMorePagesFollowers;
    }

    private static boolean isNonEmptyString(String value) {
        return (value != null && value.length() > 0);
    }

    public FollowBean getItem(String follower_handle, String followee_handle) {
        DynamoDbTable<FollowBean> table = getDynamoDbTable();
        Key key = getCompositeKey(follower_handle, followee_handle);

        try {
            return table.getItem(key);
        } catch (Exception e) {
            throw new RuntimeException("[Internal Error] Failed to get following relationship: "+ e.getMessage());
        }

    }

    public void putItem(String follower_handle, String followee_handle) {
        DynamoDbTable<FollowBean> table = getDynamoDbTable();

        FollowBean newItem = new FollowBean();
        newItem.setFollower_handle(follower_handle);
        newItem.setFollowee_handle(followee_handle);

        try {
            table.putItem(newItem);
        } catch (Exception e) {
            throw new RuntimeException("[Internal Error] Failed to follow user: "+ e.getMessage());
        }

    }

    public void deleteItem(String follower_handle, String followee_handle) {
        DynamoDbTable<FollowBean> table = getDynamoDbTable();
        Key key = getCompositeKey(follower_handle, followee_handle);
        try {
            table.deleteItem(key);
        } catch (Exception e) {
            throw new RuntimeException("[Internal Error] Failed to unfollow user: "+ e.getMessage());
        }
    }

    public List<String> getItems(String follower_handle, int pageSize, String last_followee_handle) {
        DynamoDbTable<FollowBean> table = getDynamoDbTable();
        Key key = getPartitionKey(follower_handle);

        QueryEnhancedRequest.Builder requestBuilder = QueryEnhancedRequest.builder()
                .queryConditional(QueryConditional.keyEqualTo(key))
                .limit(pageSize);

        if(isNonEmptyString(last_followee_handle)) {
            // Build up the Exclusive Start Key (telling DynamoDB where you left off reading items)
            Map<String, AttributeValue> startKey = new HashMap<>();
            startKey.put(follower_handle_attribute, AttributeValue.builder().s(follower_handle).build());
            startKey.put(followee_handle_attribute, AttributeValue.builder().s(last_followee_handle).build());

            requestBuilder.exclusiveStartKey(startKey);
        }

        QueryEnhancedRequest request = requestBuilder.scanIndexForward(true).build();
        List<String> followees = new ArrayList<>();

        try {
            PageIterable<FollowBean> pages = table.query(request);
            pages.stream()
                    .limit(1)
                    .forEach((Page<FollowBean> page) -> {
                        setHasMorePagesFollowees(page.lastEvaluatedKey() != null);
                        page.items().forEach(followee_relation -> followees.add(followee_relation.getFollowee_handle()));
                    });

            return followees;
        } catch (Exception e) {
            throw new RuntimeException("[Internal Error] Failed to get followees: "+ e.getMessage());
        }
    }

    public List<String> getItemsIndex(String followee_handle, int pageSize, String last_follower_handle) {
        DynamoDbIndex<FollowBean> index = enhancedClient.table(TableName, TableSchema.fromBean(FollowBean.class)).index(IndexName);
        Key key = getPartitionKey(followee_handle);

        QueryEnhancedRequest.Builder requestBuilder = QueryEnhancedRequest.builder()
                .queryConditional(QueryConditional.keyEqualTo(key))
                .limit(pageSize);

        if(isNonEmptyString(last_follower_handle)) {
            // Build up the Exclusive Start Key (telling DynamoDB where you left off reading items)
            Map<String, AttributeValue> startKey = new HashMap<>();
            startKey.put(followee_handle_attribute, AttributeValue.builder().s(followee_handle).build());
            startKey.put(follower_handle_attribute, AttributeValue.builder().s(last_follower_handle).build());

            requestBuilder.exclusiveStartKey(startKey);
        }

        QueryEnhancedRequest request = requestBuilder.scanIndexForward(true).build();
        List<String> followers = new ArrayList<>();

        try {
            SdkIterable<Page<FollowBean>> sdkIterable = index.query(request);
            PageIterable<FollowBean> pages = PageIterable.create(sdkIterable);
            pages.stream()
                    .limit(1)
                    .forEach((Page<FollowBean> page) -> {
                        setHasMorePagesFollowers(page.lastEvaluatedKey() != null);
                        page.items().forEach(follower_relation -> followers.add(follower_relation.getFollower_handle()));
                    });

            return followers;
        } catch (Exception e) {
            throw new RuntimeException("[Internal Error] Failed to get followers: "+ e.getMessage());
        }
    }

    @Override
    public void follow(String userAlias, String followeeAlias) {
        putItem(userAlias, followeeAlias);
    }

    @Override
    public void unfollow(String userAlias, String unfollowedUserHandle) {
        deleteItem(userAlias, unfollowedUserHandle);
    }

    @Override
    public boolean isFollowing(String userAlias, String followeeAlias) {
        return getItem(userAlias, followeeAlias)!= null;
    }

    @Override
    public Pair<List<String>, Boolean> getFollowees(GetFollowingRequest request) {
        assert request.getLimit() > 0;
        assert request.getFollowerAlias() != null;

        List<String> followees = null;

        followees = getItems(request.getFollowerAlias(), request.getLimit(),request.getLastFolloweeAlias());
        return new Pair<>(followees, hasMorePagesFollowees);
    }

    @Override
    public Pair<List<String>, Boolean> getFollowers(GetFollowersRequest request) {
        assert request.getLimit() > 0;
        assert request.getFolloweeAlias() != null;

        List<String> followers = getItemsIndex(request.getFolloweeAlias(), request.getLimit(), request.getLastFollowerAlias());
        return new Pair<>(followers, hasMorePagesFollowers);
    }

    @Override
    public List<String> getAllFollowers(AuthToken authToken, String followeeAlias) {

        Pair<List<String>, Boolean> response = getFollowers(new GetFollowersRequest(authToken, followeeAlias,250, null));
        List<String> users = response.getFirst();
        while(response.getSecond()){
            response = getFollowers(new GetFollowersRequest(authToken, followeeAlias, 250, users.get(users.size()-1)));
            users.addAll(response.getFirst());
        }
        System.out.println("Followers size: "+users.size());
        return users;
    }

    @Override
    public void batchWriteFollowers(List<String> followers, String followTarget) {
        List<FollowBean> batchToWrite = new ArrayList<>();

        // Add each user into the TableWriteItems object
        for (String follower : followers) {
            FollowBean newItem = new FollowBean();
            newItem.setFollower_handle(follower);
            newItem.setFollowee_handle(followTarget);
            batchToWrite.add(newItem);

            int i = 25;
            if (batchToWrite.size() == 25) {
                // package this batch up and send to DynamoDB.
                System.out.println("progress: "+ i);
                writeChunkOfFollowDTOs(batchToWrite);
                batchToWrite = new ArrayList<>();
                i+=25;
            }
        }

        // write any remaining
        if (batchToWrite.size() > 0) {
            // package this batch up and send to DynamoDB.
            writeChunkOfFollowDTOs(batchToWrite);
        }
    }

    private void writeChunkOfFollowDTOs(List<FollowBean> items) {

        if(items.size() > 25) {
            throw new RuntimeException("[Internal Error] Batch size exceeds 25");
        }

        WriteBatch.Builder<FollowBean> writeBuilder = WriteBatch.builder(FollowBean.class).mappedTableResource(getDynamoDbTable());
        for (FollowBean item : items) {
            writeBuilder.addPutItem(builder -> builder.item(item));
        }
        BatchWriteItemEnhancedRequest batchWriteItemEnhancedRequest = BatchWriteItemEnhancedRequest.builder()
                .writeBatches(writeBuilder.build()).build();

        try {
            BatchWriteResult result = enhancedClient.batchWriteItem(batchWriteItemEnhancedRequest);
            // just hammer dynamodb again with anything that didn't get written this time
            if (result.unprocessedPutItemsForTable(getDynamoDbTable()).size() > 0) {
                writeChunkOfFollowDTOs(result.unprocessedPutItemsForTable(getDynamoDbTable()));
            }

        } catch (DynamoDbException e) {
            System.out.println("### Error, Cannot write batch to Follows table: "+e.getMessage());
            throw new RuntimeException("[Internal Error] Cannot write batch to Follows table");
        }
    }
}
