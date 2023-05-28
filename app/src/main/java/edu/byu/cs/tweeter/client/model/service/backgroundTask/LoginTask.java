package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Handler;

import edu.byu.cs.tweeter.client.model.net.ServerFacade;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.LoginRequest;
import edu.byu.cs.tweeter.model.net.response.LoginResponse;
import edu.byu.cs.tweeter.util.Pair;

/**
 * Background task that logs in a user (i.e., starts a session).
 */
public class LoginTask extends AuthenticateTask {
    public LoginTask(String username, String password, Handler messageHandler) {
        super(messageHandler, username, password);
    }

    @Override
    protected Pair<User, AuthToken> runAuthenticationTask() {
        LoginRequest loginRequest = new LoginRequest(username, password);
        try {
            LoginResponse loginResponse = new ServerFacade().login(loginRequest, "/login");
            if (loginResponse.isSuccess()) {

                return new Pair<>(loginResponse.getUser(), loginResponse.getAuthToken());
            }
            else {
                sendFailedMessage(loginResponse.getMessage());
            }
        }
        catch (Exception e) {
            sendExceptionMessage(e);
        }
        return null;
    }
}
