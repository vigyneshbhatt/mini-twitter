package edu.byu.cs.tweeter.client.model.service.backgroundTask.Handler;

import android.os.Bundle;

import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetUserTask;
import edu.byu.cs.tweeter.model.domain.User;

/**
 * Message handler (i.e., observer) for GetUserTask.
 */
public class GetUserHandler extends BackgroundTaskHandler<UserService.GetUserObserver> {
    public GetUserHandler(UserService.GetUserObserver observer) {
        super(observer);
    }

    @Override
    protected void handleSuccess(Bundle data, UserService.GetUserObserver observer) {
        User user = (User) data.getSerializable(GetUserTask.USER_KEY);
        observer.handleSuccess(user);
    }

    @Override
    protected String getMessageFiller() {
        return "get User";
    }
}

