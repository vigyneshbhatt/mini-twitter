package edu.byu.cs.tweeter.client.model.service;

import edu.byu.cs.tweeter.client.model.service.backgroundTask.BackgroundTaskUtils;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.FollowTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetFollowersCountTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetFollowersTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetFollowingCountTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetFollowingTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.Handler.GetCountHandler;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.Handler.FollowHandler;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.Handler.GetPagedItemsHandler;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.Handler.IsFollowerHandler;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.Handler.UnfollowHandler;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.IsFollowerTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.UnfollowTask;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

/**
 * Contains the business logic for getting the users a user is following.
 */
public class FollowService extends Service {

    public interface IsFollowerObserver extends ServiceObserver {
        void handleSuccess(boolean isFollower);
    }

    public interface FollowObserver extends ServiceObserver {
        void handleSuccess();
    }

    public interface UnfollowObserver extends ServiceObserver {
        void handleSuccess();
    }

    public interface GetCountObserver extends ServiceObserver {
        void handleSuccess(int count, boolean isCountFollowers);
    }


    /**
     * Limits the number of followees returned and returns the next set of
     * followees after any that were returned in a previous request.
     * This is an asynchronous operation.
     *
     * @param authToken the session auth token.
     * @param targetUser the user for whom followees are being retrieved.
     * @param limit the maximum number of followees to return.
     * @param lastFollowee the last followee returned in the previous request (can be null).
     */
    public void getFollowees(AuthToken authToken, User targetUser, int limit, User lastFollowee, GetPagedItemsObserver observer) {
        GetFollowingTask followingTask = new GetFollowingTask(authToken, targetUser, limit, lastFollowee, new GetPagedItemsHandler<User>(observer, "get followees"));
        BackgroundTaskUtils.runTask(followingTask);
    }

    /**
     * Limits the number of followers returned and returns the next set of
     * followers after any that were returned in a previous request.
     * This is an asynchronous operation.
     *
     * @param authToken the session auth token.
     * @param targetUser the user for whom followers are being retrieved.
     * @param limit the maximum number of followers to return.
     * @param lastFollower the last follower returned in the previous request (can be null).
     */
    public void getFollowers(AuthToken authToken, User targetUser, int limit, User lastFollower, GetPagedItemsObserver observer) {
        GetFollowersTask followersTask = new GetFollowersTask(authToken, targetUser, limit, lastFollower, new GetPagedItemsHandler<User>(observer, "get followers"));
        BackgroundTaskUtils.runTask(followersTask);
    }

    public void findIfFollower(AuthToken authToken, User user, User followee, IsFollowerObserver observer){
        IsFollowerTask isFollowerTask = new IsFollowerTask(authToken, user, followee, new IsFollowerHandler(observer));
        BackgroundTaskUtils.runTask(isFollowerTask);
    }

    public void follow(AuthToken authToken, User user, User followee, FollowObserver observer){
        FollowTask followTask = new FollowTask(authToken, user, followee, new FollowHandler(observer));
        BackgroundTaskUtils.runTask(followTask);
    }

    public void unfollow(AuthToken authToken, User user, User followee, UnfollowObserver observer){
        UnfollowTask unfollowTask = new UnfollowTask(authToken, user, followee, new UnfollowHandler(observer));
        BackgroundTaskUtils.runTask(unfollowTask);
    }

    public void getFollowersCount(AuthToken authToken, User user, GetCountObserver observer){
        GetFollowersCountTask getFollowersCountTask = new GetFollowersCountTask(authToken, user, new GetCountHandler(observer, true));
        BackgroundTaskUtils.runTask(getFollowersCountTask);
    }

    public void getFollowingCount(AuthToken authToken, User user, GetCountObserver observer){
        GetFollowingCountTask getFollowingCountTask = new GetFollowingCountTask(authToken, user, new GetCountHandler(observer, false));
        BackgroundTaskUtils.runTask(getFollowingCountTask);
    }

}
