package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Bundle;
import android.os.Handler;

import java.util.Random;

import edu.byu.cs.tweeter.client.model.net.ServerFacade;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.IsFollowerRequest;
import edu.byu.cs.tweeter.model.net.response.IsFollowerResponse;

/**
 * Background task that determines if one user is following another.
 */
public class IsFollowerTask extends AuthenticatedTask {
    public static final String IS_FOLLOWER_KEY = "is-follower";
    /**
     * The alleged follower.
     */
    private User follower;
    /**
     * The alleged followee.
     */
    private User followee;

    private boolean isFollower;

    public IsFollowerTask(AuthToken authToken, User follower, User followee, Handler messageHandler) {
        super(messageHandler, authToken);
        this.follower = follower;
        this.followee = followee;
    }

    @Override
    protected void runTask() {
        IsFollowerRequest isFollowerRequest = new IsFollowerRequest(authToken, follower.getAlias(),
                followee.getAlias());

        try {
            IsFollowerResponse isFollowerResponse = new ServerFacade().isFollower(isFollowerRequest,
                    "/isfollower");
            if (!isFollowerResponse.isSuccess()) {
                sendFailedMessage(isFollowerResponse.getMessage());
            }
            else {
                isFollower = isFollowerResponse.isFollower();
                sendSuccessMessage();
            }
        }
        catch (Exception e) {
            sendExceptionMessage(e);
        }
    }

    @Override
    protected void loadSuccessBundle(Bundle msgBundle) {
        msgBundle.putBoolean(IS_FOLLOWER_KEY, isFollower);
    }
}
