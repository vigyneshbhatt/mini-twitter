package edu.byu.cs.tweeter.client.model.service.backgroundTask.Handler;
// FollowHandler

import android.os.Bundle;

import edu.byu.cs.tweeter.client.model.service.FollowService;

public class FollowHandler extends BackgroundTaskHandler<FollowService.FollowObserver> {

    public FollowHandler(FollowService.FollowObserver observer) {
        super(observer);
    }

    @Override
    protected void handleSuccess(Bundle data, FollowService.FollowObserver observer) {
        observer.handleSuccess();
    }

    @Override
    protected String getMessageFiller() {
        return "follow";
    }

}
