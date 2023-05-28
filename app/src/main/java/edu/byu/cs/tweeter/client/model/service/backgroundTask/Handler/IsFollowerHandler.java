package edu.byu.cs.tweeter.client.model.service.backgroundTask.Handler;

// IsFollowerHandler

import android.os.Bundle;

import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.IsFollowerTask;

public class IsFollowerHandler extends BackgroundTaskHandler<FollowService.IsFollowerObserver> {
    public IsFollowerHandler(FollowService.IsFollowerObserver observer) {
        super(observer);
    }


    @Override
    protected void handleSuccess(Bundle data, FollowService.IsFollowerObserver observer) {
        boolean isFollower = data.getBoolean(IsFollowerTask.IS_FOLLOWER_KEY);
        observer.handleSuccess(isFollower);
    }

    @Override
    protected String getMessageFiller() {
        return "get Following relationship";
    }
}
