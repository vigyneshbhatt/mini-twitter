package edu.byu.cs.tweeter.server.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.FollowRequest;
import edu.byu.cs.tweeter.model.net.request.GetFollowersRequest;
import edu.byu.cs.tweeter.model.net.request.GetFollowingRequest;
import edu.byu.cs.tweeter.model.net.request.GetFollowersCountRequest;
import edu.byu.cs.tweeter.model.net.request.GetFollowingCountRequest;
import edu.byu.cs.tweeter.model.net.request.IsFollowerRequest;
import edu.byu.cs.tweeter.model.net.request.UnfollowRequest;
import edu.byu.cs.tweeter.model.net.response.FollowResponse;
import edu.byu.cs.tweeter.model.net.response.GetFollowersResponse;
import edu.byu.cs.tweeter.model.net.response.GetFollowingResponse;
import edu.byu.cs.tweeter.model.net.response.GetFollowersCountResponse;
import edu.byu.cs.tweeter.model.net.response.GetFollowingCountResponse;
import edu.byu.cs.tweeter.model.net.response.IsFollowerResponse;
import edu.byu.cs.tweeter.model.net.response.UnfollowResponse;
import edu.byu.cs.tweeter.server.dao.concreteDAO.DynamoDBFollowDAO;
import edu.byu.cs.tweeter.server.dao.factory.iDAOFactory;
import edu.byu.cs.tweeter.util.Pair;

/**
 * Contains the business logic for getting the users a user is following.
 */
public class FollowService extends Service{

    public FollowService(iDAOFactory daoFactory) {
        super (daoFactory);
    }

    /**
     * Returns the users that the user specified in the request is following. Uses information in
     * the request object to limit the number of followees returned and to return the next set of
     * followees after any that were returned in a previous request. Uses the {@link DynamoDBFollowDAO} to
     * get the followees.
     *
     * @param request contains the data required to fulfill the request.
     * @return the followees.
     */
    public GetFollowingResponse getFollowees(GetFollowingRequest request) {
        if(request.getFollowerAlias() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have a follower alias");
        } else if(request.getLimit() <= 0) {
            throw new RuntimeException("[Bad Request] Request needs to have a positive limit");
        }

        if (daoFactory.getAuthTokenDAO().validateAuthToken(request.getAuthToken())!=null) {
            Pair<List<String>, Boolean> response = daoFactory.getFollowDAO().getFollowees(request);
            List<User> followees = new ArrayList<>();

            for (String userAlias : response.getFirst()){
                followees.add(daoFactory.getUserDAO().getUser(userAlias));
            }
            return new GetFollowingResponse(followees, response.getSecond());

        } else return new GetFollowingResponse("Failed authToken validation");
    }

    public GetFollowersResponse getFollowers(GetFollowersRequest request) {
        if(request.getFolloweeAlias() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have a followee alias");
        } else if(request.getLimit() <= 0) {
            throw new RuntimeException("[Bad Request] Request needs to have a positive limit");
        }

        if (daoFactory.getAuthTokenDAO().validateAuthToken(request.getAuthToken())!=null) {
            Pair<List<String>, Boolean> response = daoFactory.getFollowDAO().getFollowers(request);
            List<User> followers = new ArrayList<>();
            for (String userAlias : response.getFirst()){
                followers.add(daoFactory.getUserDAO().getUser(userAlias));
            }
            return new GetFollowersResponse(followers, response.getSecond());

        } else return new GetFollowersResponse("Failed authToken validation");

    }

    public GetFollowingCountResponse getFolloweeCount(GetFollowingCountRequest request) {
        if (daoFactory.getAuthTokenDAO().validateAuthToken(request.getAuthToken())!=null) {
            int num = daoFactory.getUserDAO().getFolloweeCount(request.getUserAlias());
            return new GetFollowingCountResponse(num);

        } else return new GetFollowingCountResponse("Failed authToken validation");
    }


    public GetFollowersCountResponse getFollowersCount(GetFollowersCountRequest request) {
        if (daoFactory.getAuthTokenDAO().validateAuthToken(request.getAuthToken())!=null) {
            int num = daoFactory.getUserDAO().getFollowerCount(request.getUserAlias());
            return new GetFollowersCountResponse(num);

        } else return new GetFollowersCountResponse("Failed authToken validation");
    }

    public FollowResponse follow(FollowRequest request) {
        if (daoFactory.getAuthTokenDAO().validateAuthToken(request.getAuthToken())!=null) {
            super.follow(request.getUserAlias(), request.getFolloweeAlias());
            return new FollowResponse();

        } else return new FollowResponse("Failed authToken validation");
    }

    public IsFollowerResponse isFollower(IsFollowerRequest request) {
        if (daoFactory.getAuthTokenDAO().validateAuthToken(request.getAuthToken())!=null) {
            boolean isFollower = daoFactory.getFollowDAO().isFollowing(request.getFollowerAlias(), request.getFolloweeAlias());
            System.out.println("## FollowService DAO return-"+ isFollower);
            return new IsFollowerResponse(isFollower);

        } else return new IsFollowerResponse("Failed authToken validation");
    }

    public UnfollowResponse unfollow(UnfollowRequest request) {

        if (daoFactory.getAuthTokenDAO().validateAuthToken(request.getAuthToken())!=null){
            daoFactory.getFollowDAO().unfollow(request.getUserAlias(), request.getFolloweeAlias());
            daoFactory.getUserDAO().updateFollowerCount(request.getFolloweeAlias(), -1);
            daoFactory.getUserDAO().updateFolloweeCount(request.getUserAlias(),-1);
            return new UnfollowResponse();
        } else return new UnfollowResponse("Failed authToken validation");
    }
}
