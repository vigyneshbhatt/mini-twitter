package edu.byu.cs.tweeter.client.model.service.backgroundTask.Handler;

import android.os.Bundle;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.Service;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.AuthenticateTask;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class AuthenticateHandler extends BackgroundTaskHandler<Service.AuthenticateObserver> {
    public AuthenticateHandler(Service.AuthenticateObserver observer) {
        super(observer);
    }

    @Override
    protected void handleSuccess(Bundle data, Service.AuthenticateObserver observer) {
        User authenticateUser = (User) data.getSerializable(AuthenticateTask.USER_KEY);
        AuthToken authToken = (AuthToken) data.getSerializable(AuthenticateTask.AUTH_TOKEN_KEY);

        // Cache user session information
        Cache.getInstance().setCurrUser(authenticateUser);
        Cache.getInstance().setCurrUserAuthToken(authToken);

        observer.handleSuccess(authenticateUser, authToken);
    }

    @Override
    protected String getMessageFiller() {
        return "Authenticate";
    }
}

