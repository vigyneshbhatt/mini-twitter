package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Handler;

import edu.byu.cs.tweeter.client.model.net.ServerFacade;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.RegisterRequest;
import edu.byu.cs.tweeter.model.net.response.RegisterResponse;
import edu.byu.cs.tweeter.util.Pair;

/**
 * Background task that creates a new user account and logs in the new user (i.e., starts a session).
 */
public class RegisterTask extends AuthenticateTask {

    /**
     * The user's first name.
     */
    private String firstName;
    /**
     * The user's last name.
     */
    private String lastName;

    /**
     * The base-64 encoded bytes of the user's profile image.
     */
    private String image;


    public RegisterTask(String firstName, String lastName, String username, String password,
                        String image, Handler messageHandler) {
        super(messageHandler, username,password);
        this.firstName = firstName;
        this.lastName = lastName;
        this.image = image;
    }

    @Override
    protected Pair<User, AuthToken> runAuthenticationTask() {
        RegisterRequest registerRequest = new RegisterRequest(firstName, lastName, username,
                password, image);
        try {
            RegisterResponse registerResponse = new ServerFacade().register(registerRequest,
                    "/register");
            if (registerResponse.isSuccess()) {
                return new Pair<>(registerResponse.getUser(),
                        registerResponse.getAuthToken());
            } else {
                sendFailedMessage(registerResponse.getMessage());
            }
        } catch (Exception e) {
            sendExceptionMessage(e);
        }
        return null;
    }
}
