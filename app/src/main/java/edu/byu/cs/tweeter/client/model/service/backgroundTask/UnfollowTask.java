package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Handler;

import edu.byu.cs.tweeter.client.model.net.ServerFacade;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.UnfollowRequest;
import edu.byu.cs.tweeter.model.net.response.UnfollowResponse;

/**
 * Background task that removes a following relationship between two users.
 */
public class UnfollowTask extends AuthenticatedTask {
    /**
     * The user that is being followed.
     */
    private final User followee;
    private final User user;

    public UnfollowTask(AuthToken authToken, User user, User followee, Handler messageHandler) {
        super(messageHandler,authToken);
        this.followee = followee;
        this.user=user;
    }

    @Override
    protected void runTask() {
        UnfollowRequest unfollowRequest = new UnfollowRequest(authToken, user.getAlias(), followee.getAlias());
        try {
            UnfollowResponse unfollowResponse = new ServerFacade().unfollow(unfollowRequest,
                    "/unfollow");
            if (!unfollowResponse.isSuccess()) {
                sendFailedMessage(unfollowResponse.getMessage());
            }
            else {
                sendSuccessMessage();
            }
        }
        catch (Exception e) {
            sendExceptionMessage(e);
        }
    }


}
