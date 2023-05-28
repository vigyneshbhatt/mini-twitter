package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Handler;

import edu.byu.cs.tweeter.client.model.net.ServerFacade;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.FollowRequest;
import edu.byu.cs.tweeter.model.net.response.FollowResponse;

/**
 * Background task that establishes a following relationship between two users.
 */
public class FollowTask extends AuthenticatedTask {
    /**
     * The user that is being followed.
     */
    private User followee;
    private User user;

    public FollowTask(AuthToken authToken, User user, User followee, Handler messageHandler) {
        super(messageHandler, authToken);
        this.followee = followee;
        this.user=user;
    }

    @Override
    protected void runTask() {
        FollowRequest followRequest = new FollowRequest(this.authToken, user.getAlias(), followee.getAlias());

        try {
            FollowResponse followResponse = new ServerFacade().follow(followRequest, "/follow");
            if (!followResponse.isSuccess()) {
                sendFailedMessage(followResponse.getMessage());
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
