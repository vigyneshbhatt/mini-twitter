package edu.byu.cs.tweeter.client.model.service.backgroundTask.Handler;

// LogoutHandler

import android.os.Bundle;

import edu.byu.cs.tweeter.client.model.service.UserService;

public class LogoutHandler extends BackgroundTaskHandler<UserService.LogoutObserver> {

    public LogoutHandler(UserService.LogoutObserver observer) {
        super(observer);
    }

    @Override
    protected void handleSuccess(Bundle data, UserService.LogoutObserver observer) {
        observer.handleSuccess();
    }

    @Override
    protected String getMessageFiller() {
        return "logout";
    }

}
