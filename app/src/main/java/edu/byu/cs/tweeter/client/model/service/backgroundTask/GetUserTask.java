package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Bundle;
import android.os.Handler;

import edu.byu.cs.tweeter.client.model.net.ServerFacade;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.GetUserRequest;
import edu.byu.cs.tweeter.model.net.response.GetUserResponse;

/**
 * Background task that returns the profile for a specified user.
 */
public class GetUserTask extends AuthenticatedTask {
    public static final String USER_KEY = "user";


    /**
     * Alias (or handle) for user whose profile is being retrieved.
     */
    private String alias;
    private User user;
    /**
     * Message handler that will receive task results.
     */

    public GetUserTask(AuthToken authToken, String alias, Handler messageHandler) {
        super(messageHandler, authToken);
        this.alias = alias;
    }

    @Override
    protected void runTask() {
        GetUserRequest getUserRequest = new GetUserRequest(authToken, alias);
        try {
            GetUserResponse getUserResponse = new ServerFacade().getUser(getUserRequest, "/getuser");
            if (getUserResponse.isSuccess()) {
                user = getUserResponse.getUser();
                //load image from URL?
                sendSuccessMessage();
            }
        }
        catch (Exception e) {
            sendExceptionMessage(e);
        }
    }

    @Override
    protected void loadSuccessBundle(Bundle msgBundle) {
        msgBundle.putSerializable(USER_KEY, user);
    }
}
