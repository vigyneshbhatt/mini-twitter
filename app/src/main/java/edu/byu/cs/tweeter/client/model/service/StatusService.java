package edu.byu.cs.tweeter.client.model.service;

import edu.byu.cs.tweeter.client.model.service.backgroundTask.BackgroundTaskUtils;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.Handler.PostStatusHandler;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.PostStatusTask;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;

public class StatusService extends Service{

    public interface PostStatusObserver extends ServiceObserver {
        void handleSuccess();
    }

    public void postStatus(AuthToken authToken, Status status, PostStatusObserver observer){
        PostStatusTask postStatusTask = new PostStatusTask(authToken, status, new PostStatusHandler(observer));
        BackgroundTaskUtils.runTask(postStatusTask);
    }

}
