package edu.byu.cs.tweeter.server.dao.concreteDAO;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.server.dao.bean.UserBean;
import edu.byu.cs.tweeter.server.dao.iDAO.iUserDAO;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.BatchWriteItemEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.BatchWriteResult;
import software.amazon.awssdk.enhanced.dynamodb.model.WriteBatch;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;


public class DynamoDBUserDAO implements iUserDAO {

    private static final String TableName = "user";
    private final DynamoDbEnhancedClient enhancedClient;

    public DynamoDBUserDAO(DynamoDbEnhancedClient enhancedClient) {
        this.enhancedClient = enhancedClient;
    }

    private DynamoDbTable<UserBean> getDynamoDbTable() {
        return enhancedClient.table(TableName, TableSchema.fromBean(UserBean.class));
    }

    private Key getKey(String userAlias) {
        return Key.builder()
                .partitionValue(userAlias).build();
    }

    private static boolean isNonEmptyString(String value) {
        return (value != null && value.length() > 0);
    }

    public UserBean getItem(String userAlias) {
        DynamoDbTable<UserBean> table = getDynamoDbTable();
        Key key = getKey(userAlias);

        try {
            return table.getItem(key);
        } catch (Exception e) {
            throw new RuntimeException("[Internal Error] Failed to get user: "+ e.getMessage());
        }
    }

    public void putItem(String userAlias, String firstname, String lastname, String passwordHash, String imageUrl, int followee_count, int follower_count) {
        DynamoDbTable<UserBean> table = getDynamoDbTable();

        System.out.println("## DynamoDbUserDAO PutItem - "+ userAlias);
        UserBean newItem = new UserBean();
        newItem.setFirstName(firstname);
        newItem.setUserAlias(userAlias);
        newItem.setLastName(lastname);
        newItem.setPasswordHash(passwordHash);
        newItem.setImageURL(imageUrl);
        newItem.setFolloweeCount(followee_count);
        newItem.setFollowerCount(follower_count);

        try {
            table.putItem(newItem);
        } catch (Exception e) {
            System.out.println("## DynamoDbUserDAO PutItem - Failed to put user in table: "+ e.getMessage());
            throw new RuntimeException("[Internal Error] Failed to put user in table: "+ e.getMessage());
        }
    }

    public void deleteItem(String userAlias) {
        DynamoDbTable<UserBean> table = getDynamoDbTable();
        Key key = getKey(userAlias);

        try {
            table.deleteItem(key);
        } catch (Exception e) {
            throw new RuntimeException("[Internal Error] Failed to delete user: "+ e.getMessage());
        }
    }

    public void updateItem(String userAlias, Boolean isFollowerContext, int value) {
        DynamoDbTable<UserBean> table = getDynamoDbTable();
        Key key = getKey(userAlias);

        try {
            UserBean item = table.getItem(key);
            if(isFollowerContext){
                item.setFollowerCount(item.getFollowerCount()+value);
            } else {
                item.setFolloweeCount(item.getFolloweeCount()+value);
            }
            table.updateItem(item);

        } catch (Exception e) {
            throw new RuntimeException("[Internal Error] Failed to update count: "+ e.getMessage());
        }
    }

    @Override
    public User login(String userAlias, String passwordHash) {

        UserBean userItem = getItem(userAlias);
        if (userItem!= null) {
            System.out.println("## UserDAO Login saved passwordHash - "+ userItem.getPasswordHash());
            System.out.println("## UserDAO Login received passwordHash - "+ passwordHash);

            if(userItem.getPasswordHash().equals(passwordHash)){
                return new User(userItem.getFirstName(),userItem.getLastName(),userItem.getUserAlias(),userItem.getImageURL());

            } else throw new RuntimeException("[Bad Request] Password does not match");

        } else throw new RuntimeException("[Bad Request] User with userHandle - "+ userAlias + " does not exist");
    }

    @Override
    public User getUser(String userAlias) {
        UserBean userItem = getItem(userAlias);
        return new User(userItem.getFirstName(),userItem.getLastName(),userItem.getUserAlias(),userItem.getImageURL());
    }

    @Override
    public Boolean isExistingUser(String userAlias) {
        DynamoDbTable<UserBean> table = getDynamoDbTable();
        Key key = getKey(userAlias);

        return table.getItem(key)!= null;
    }

    @Override
    public User register(String userAlias, String passwordHash, String firstName, String lastName, String imageUrl) {
        putItem(userAlias, firstName, lastName, passwordHash, imageUrl, 0, 0);
        return getUser(userAlias);
    }

    @Override
    public void updateFollowerCount(String userAlias, int value) {
        updateItem(userAlias, true, value);
    }

    @Override
    public void updateFolloweeCount(String userAlias, int value) {
        updateItem(userAlias, false, value);
    }

    @Override
    public Integer getFollowerCount(String userAlias) {
        assert userAlias != null;
        return getItem(userAlias).getFollowerCount();
    }

    @Override
    public Integer getFolloweeCount(String userAlias) {
        assert userAlias != null;
        return getItem(userAlias).getFolloweeCount();
    }

    @Override
    public void batchWriteUsers(List<UserBean> users) {
        List<UserBean> batchToWrite = new ArrayList<>();
        for (UserBean user : users) {
            batchToWrite.add(user);

            if (batchToWrite.size() == 25) {
                // package this batch up and send to DynamoDB.
                writeChunkOfUserDTOs(batchToWrite);
                batchToWrite = new ArrayList<>();
            }
        }

        // write any remaining
        if (batchToWrite.size() > 0) {
            // package this batch up and send to DynamoDB.
            writeChunkOfUserDTOs(batchToWrite);
        }
    }
    private void writeChunkOfUserDTOs(List<UserBean> userDTOs) {
        if(userDTOs.size() > 25) {
            throw new RuntimeException("[Internal Error] Batch size exceeds 25");
        }

        WriteBatch.Builder<UserBean> writeBuilder = WriteBatch.builder(UserBean.class).mappedTableResource(getDynamoDbTable());
        for (UserBean item : userDTOs) {
            writeBuilder.addPutItem(builder -> builder.item(item));
        }
        BatchWriteItemEnhancedRequest batchWriteItemEnhancedRequest = BatchWriteItemEnhancedRequest.builder()
                .writeBatches(writeBuilder.build()).build();

        try {
            BatchWriteResult result = enhancedClient.batchWriteItem(batchWriteItemEnhancedRequest);

            // just hammer dynamodb again with anything that didn't get written this time
            if (result.unprocessedPutItemsForTable(getDynamoDbTable()).size() > 0) {
                writeChunkOfUserDTOs(result.unprocessedPutItemsForTable(getDynamoDbTable()));
            }

        } catch (DynamoDbException e) {
            System.out.println("### Error, Cannot write batch to User table: "+ e.getMessage());
            throw new RuntimeException("[Internal Error] Cannot write batch to User table");
        }
    }
}
