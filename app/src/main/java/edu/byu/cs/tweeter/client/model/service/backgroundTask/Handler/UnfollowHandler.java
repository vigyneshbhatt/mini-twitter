package edu.byu.cs.tweeter.client.model.service.backgroundTask.Handler;

// UnfollowHandler

import android.os.Bundle;

import edu.byu.cs.tweeter.client.model.service.FollowService;

public class UnfollowHandler extends BackgroundTaskHandler<FollowService.UnfollowObserver> {

    public UnfollowHandler(FollowService.UnfollowObserver observer) {
        super(observer);
    }

    @Override
    protected void handleSuccess(Bundle data, FollowService.UnfollowObserver observer) {
        observer.handleSuccess();
    }

    @Override
    protected String getMessageFiller() {
        return "unfollow";
    }

}
