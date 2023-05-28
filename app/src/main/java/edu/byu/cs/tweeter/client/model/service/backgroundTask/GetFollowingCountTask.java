package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Handler;

import edu.byu.cs.tweeter.client.model.net.ServerFacade;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.GetFollowingCountRequest;
import edu.byu.cs.tweeter.model.net.response.GetFollowingCountResponse;

/**
 * Background task that queries how many other users a specified user is following.
 */
public class GetFollowingCountTask extends GetCountTask {
    public GetFollowingCountTask(AuthToken authToken, User targetUser, Handler messageHandler) {
        super(messageHandler, targetUser, authToken);
    }
    @Override
    protected int runCountTask() {
        GetFollowingCountRequest getFollowingCountRequest = new GetFollowingCountRequest(this.getTargetUser().getAlias(),
                authToken);
        try {
            GetFollowingCountResponse getFollowingCountResponse = new ServerFacade().
                    getFollowingCount(getFollowingCountRequest, "/getfollowingcount");
            if (getFollowingCountResponse.isSuccess()) {
                return getFollowingCountResponse.getFollowingCount();
            }
            else {
                sendFailedMessage(getFollowingCountResponse.getMessage());
            }
        }
        catch (Exception e) {
            sendExceptionMessage(e);
        }
        return -1;
    }

}
