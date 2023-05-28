package edu.byu.cs.tweeter.server.dao.concreteDAO;

import java.security.SecureRandom;
import java.sql.Timestamp;
import java.util.Base64;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.server.dao.bean.AuthTokenBean;
import edu.byu.cs.tweeter.server.dao.bean.UserBean;
import edu.byu.cs.tweeter.server.dao.iDAO.iAuthTokenDAO;
import edu.byu.cs.tweeter.util.FakeData;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

public class DynamoDBAuthTokenDAO implements iAuthTokenDAO {

    private static final String TableName = "authtoken";
    private final DynamoDbEnhancedClient enhancedClient;
    private static final SecureRandom secureRandom = new SecureRandom();

    public DynamoDBAuthTokenDAO(DynamoDbEnhancedClient enhancedClient) {
        this.enhancedClient = enhancedClient;
    }

    private DynamoDbTable<AuthTokenBean> getDynamoDbTable() {
        return enhancedClient.table(TableName, TableSchema.fromBean(AuthTokenBean.class));
    }

    private Key getKey(String authToken) {
        return Key.builder()
                .partitionValue(authToken).build();
    }

    @Override
    public AuthToken generateToken() {
        byte[] randomBytes = new byte[24];
        secureRandom.nextBytes(randomBytes);
        String authTokenString = Base64.getUrlEncoder().encodeToString(randomBytes);
        String currTimeString = String.valueOf((new Timestamp(System.currentTimeMillis())).getTime());
        AuthToken authToken = new AuthToken(authTokenString, currTimeString);

        try {
            AuthTokenBean newItem = new AuthTokenBean();
            newItem.setAuthToken(authToken.getToken());
            newItem.setTimestamp(Long.valueOf(currTimeString));
            getDynamoDbTable().putItem(newItem);
            return authToken;
        }
        catch (Exception e) {
            throw new RuntimeException("[Internal Error] Failed to put authToken in table: "+ e.getMessage());
        }
    }

    @Override
    public void deleteToken(AuthToken authToken) {
        try {
            AuthTokenBean newItem = new AuthTokenBean();
            newItem.setAuthToken(authToken.getToken());
            getDynamoDbTable().deleteItem(newItem);
        } catch (Exception e) {
            throw new RuntimeException("[Internal Error] Failed to delete authToken: "+ e.getMessage());
        }
    }

    @Override
    public AuthToken getToken(String token) {
        try {
            Key key = getKey(token);
            AuthTokenBean item = getDynamoDbTable().getItem(key);
            return new AuthToken(item.getAuthToken(), String.valueOf(item.getTimestamp()));
        } catch (Exception e) {
            throw new RuntimeException("[Internal Error] Failed to get authToken: "+ e.getMessage());
        }

    }

    @Override
    public AuthToken validateAuthToken(AuthToken authToken) {

        AuthToken retrievedAuthToken = getToken(authToken.getToken());
        Long retrievedAuthTimestamp = Long.valueOf(retrievedAuthToken.getDatetime());
        Long currTime = System.currentTimeMillis();
        long timeDiff = (currTime - retrievedAuthTimestamp) / 60000; //get time diff in minutes

        if (timeDiff >= 15) {
            deleteToken(authToken);
        } else {
            AuthTokenBean item = new AuthTokenBean();
            item.setAuthToken(authToken.getToken());
            item.setTimestamp(currTime);

            try {
                AuthTokenBean updatedItem = getDynamoDbTable().updateItem(item);
                System.out.println("validation successful");
                return new AuthToken(item.getAuthToken(), String.valueOf(item.getTimestamp()));

            } catch (Exception e) {
                System.err.println("Validation failed: " + e.getMessage());

            }
        }
        return null;
    }
}
