package edu.byu.cs.tweeter.client.model.service.backgroundTask.Handler;

import android.os.Bundle;

import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetFollowersCountTask;

public class GetCountHandler extends BackgroundTaskHandler<FollowService.GetCountObserver> {
    boolean isCountFollowers;
    public GetCountHandler(FollowService.GetCountObserver observer, boolean isCountFollowers) {
        super(observer);
        this.isCountFollowers = isCountFollowers;
    }

    @Override
    protected void handleSuccess(Bundle data, FollowService.GetCountObserver observer) {
        int count = data.getInt(GetFollowersCountTask.COUNT_KEY);
        observer.handleSuccess(count, this.isCountFollowers);
    }

    @Override
    protected String getMessageFiller() {
        if (isCountFollowers){
            return "get Followers Count";
        } else {
            return "get Following Count";
        }

    }
}