package edu.byu.cs.tweeter.client.model.service;

import edu.byu.cs.tweeter.client.model.service.backgroundTask.BackgroundTaskUtils;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetFeedTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetStoryTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetUserTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.Handler.AuthenticateHandler;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.Handler.GetPagedItemsHandler;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.Handler.GetUserHandler;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.Handler.LogoutHandler;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.LoginTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.LogoutTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.RegisterTask;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class UserService extends Service {

    public interface GetUserObserver extends ServiceObserver{
        void handleSuccess(User user);
    }

    public interface LogoutObserver extends ServiceObserver{
        void handleSuccess();
    }


    public void register(String firstName, String lastName, String alias, String password, String imageToUpload, AuthenticateObserver observer){
        RegisterTask registerTask = new RegisterTask(firstName, lastName, alias, password, imageToUpload, new AuthenticateHandler(observer));
        BackgroundTaskUtils.runTask(registerTask);
    }

    public void login(String username, String password, AuthenticateObserver observer){
        LoginTask loginTask = new LoginTask(username, password, new AuthenticateHandler(observer));
        BackgroundTaskUtils.runTask(loginTask);
    }

    public void logout(AuthToken authToken, LogoutObserver observer){
        LogoutTask logoutTask = new LogoutTask(authToken, new LogoutHandler(observer));
        BackgroundTaskUtils.runTask(logoutTask);
    }

    public void getUser(AuthToken authToken, String userAlias, GetUserObserver observer){
        GetUserTask getUserTask = new GetUserTask(authToken, userAlias, new GetUserHandler(observer));
        BackgroundTaskUtils.runTask(getUserTask);
    }

    public void getFeed(AuthToken authToken, User targetUser, int limit, Status lastStatus, GetPagedItemsObserver observer){
        GetFeedTask getFeedTask = new GetFeedTask(authToken, targetUser, limit, lastStatus, new GetPagedItemsHandler<Status>(observer,"get Feed"));
        BackgroundTaskUtils.runTask(getFeedTask);
    }

    public void getStory(AuthToken authToken, User targetUser, int limit, Status lastStatus, GetPagedItemsObserver observer){
        GetStoryTask getStoryTask = new GetStoryTask(authToken, targetUser, limit, lastStatus, new GetPagedItemsHandler<Status>(observer, "get Story"));
        BackgroundTaskUtils.runTask(getStoryTask);
    }


}
