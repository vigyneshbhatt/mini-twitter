package edu.byu.cs.tweeter.server.dao.factory;

import edu.byu.cs.tweeter.server.dao.concreteDAO.DynamoDBAuthTokenDAO;
import edu.byu.cs.tweeter.server.dao.concreteDAO.DynamoDBFeedDAO;
import edu.byu.cs.tweeter.server.dao.concreteDAO.DynamoDBFollowDAO;
import edu.byu.cs.tweeter.server.dao.concreteDAO.DynamoDBStoryDAO;
import edu.byu.cs.tweeter.server.dao.concreteDAO.DynamoDBUserDAO;
import edu.byu.cs.tweeter.server.dao.iDAO.iAuthTokenDAO;
import edu.byu.cs.tweeter.server.dao.iDAO.iFeedDAO;
import edu.byu.cs.tweeter.server.dao.iDAO.iFollowDAO;
import edu.byu.cs.tweeter.server.dao.iDAO.iStoryDAO;
import edu.byu.cs.tweeter.server.dao.iDAO.iUserDAO;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

public class DynamoDBDAOFactory implements iDAOFactory {
    private iUserDAO dynamoDBUserDAO;
    private iAuthTokenDAO dynamoDBAuthTokenDAO;
    private iFeedDAO dynamoDBFeedDAO;
    private iStoryDAO dynamoDBStoryDAO;
    private iFollowDAO dynamoDBFollowDAO;

    private static DynamoDbClient dynamoDbClient = DynamoDbClient.builder()
            .region(Region.US_WEST_2)
            .build();

    private static DynamoDbEnhancedClient enhancedClient = DynamoDbEnhancedClient.builder()
            .dynamoDbClient(dynamoDbClient)
            .build();

    @Override
    public iUserDAO getUserDAO(){
        if (this.dynamoDBUserDAO == null){
            dynamoDBUserDAO = new DynamoDBUserDAO(enhancedClient);
        }
        return dynamoDBUserDAO;
    }

    @Override
    public iFeedDAO getFeedDAO() {
        if (this.dynamoDBFeedDAO == null){
            dynamoDBFeedDAO = new DynamoDBFeedDAO(enhancedClient);
        }
        return dynamoDBFeedDAO;
    }

    @Override
    public iStoryDAO getStoryDAO(){
        if (this.dynamoDBStoryDAO == null){
            dynamoDBStoryDAO = new DynamoDBStoryDAO(enhancedClient);
        }
        return dynamoDBStoryDAO;
    }

    @Override
    public iFollowDAO getFollowDAO() {
        if (this.dynamoDBFollowDAO == null){
            dynamoDBFollowDAO = new DynamoDBFollowDAO(enhancedClient);
        }
        return dynamoDBFollowDAO;
    }

    @Override
    public iAuthTokenDAO getAuthTokenDAO(){
        if (this.dynamoDBAuthTokenDAO == null){
            dynamoDBAuthTokenDAO = new DynamoDBAuthTokenDAO(enhancedClient);
        }
        return dynamoDBAuthTokenDAO;
    }


}
