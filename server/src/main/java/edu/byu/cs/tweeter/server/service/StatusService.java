package edu.byu.cs.tweeter.server.service;

import java.util.List;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.net.request.PostStatusRequest;
import edu.byu.cs.tweeter.model.net.response.PostStatusResponse;
import edu.byu.cs.tweeter.server.dao.factory.iDAOFactory;
import edu.byu.cs.tweeter.server.util.UpdateFeedRequest;

public class StatusService extends Service {
    public StatusService(iDAOFactory daoFactory){
        super (daoFactory);
    }

    public PostStatusResponse postStatusStory(PostStatusRequest request) {
        if(request.getStatus().getUser() == null) {
            throw new RuntimeException("[Bad Request] Status in the request needs to have a user");
        }

        if (daoFactory.getAuthTokenDAO().validateAuthToken(request.getAuthToken())!=null) {

//            List<String> followers = daoFactory.getFollowDAO().getAllFollowers(request.getAuthToken(), request.getStatus().getUser().getAlias());
//
//            daoFactory.getFeedDAO().postStatus(followers, request.getStatus());
            daoFactory.getStoryDAO().postStatus(request.getStatus());
            return new PostStatusResponse();

        } else return new PostStatusResponse("Failed authToken validation");
    }

    public void batchWriteStatus(UpdateFeedRequest request){
        List<String> followers = request.getFollowers();
        Status status = request.getStatus();
        System.out.println("### 3rd Stage Success: Calling FeedDAO to batchWrite");
        daoFactory.getFeedDAO().batchWriteStatus(status, followers);
    }
}
