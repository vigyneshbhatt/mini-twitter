package edu.byu.cs.tweeter.server.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import edu.byu.cs.tweeter.model.net.request.UnfollowRequest;
import edu.byu.cs.tweeter.model.net.response.UnfollowResponse;
import edu.byu.cs.tweeter.server.dao.factory.DynamoDBDAOFactory;
import edu.byu.cs.tweeter.server.service.FollowService;

public class UnfollowHandler implements RequestHandler<UnfollowRequest, UnfollowResponse> {
    @Override
    public UnfollowResponse handleRequest(UnfollowRequest input, Context context) {
        UnfollowResponse response = null;
        FollowService followingService = new FollowService(new DynamoDBDAOFactory());
        try {
            return followingService.unfollow(input);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }
}