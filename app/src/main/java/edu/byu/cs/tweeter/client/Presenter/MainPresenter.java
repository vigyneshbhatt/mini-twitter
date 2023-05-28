package edu.byu.cs.tweeter.client.Presenter;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.client.model.service.StatusService;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class MainPresenter extends Presenter {
    private static final String LOG_TAG = "MainActivity";

    public static final String CURRENT_USER_KEY = "CurrentUser";

    private final User user;
    private final AuthToken authToken;
    private StatusService statusService;


    public MainPresenter(MainView view, User user, AuthToken authToken) {
        super(view);
        this.user = user;
        this.authToken = authToken;
    }

    protected StatusService getStatusService(){
        if (statusService== null){
            statusService = new StatusService();
        }
        return statusService;
    }

    public void findIfFollower(AuthToken authToken, User user, User followee){
        new FollowService().findIfFollower(authToken, user, followee, new ConcreteIsFollowerObserver());
    }

    public void getFollowersCount(AuthToken authToken, User user){
        new FollowService().getFollowersCount(authToken, user, new ConcreteGetCountObserver());
    }

    public void getFollowingCount(AuthToken authToken, User user){
        new FollowService().getFollowingCount(authToken, user, new ConcreteGetCountObserver());
    }

    public void performFollow(AuthToken authToken, User user, User followee){
        new FollowService().follow(authToken, user, followee, new ConcreteFollowObserver());
    }

    public void performUnfollow(AuthToken authToken, User user, User followee){
        new FollowService().unfollow(authToken, user, followee, new ConcreteUnfollowObserver());
    }

    public void logout(AuthToken authToken){
        new UserService().logout(authToken, new ConcreteLogoutObserver());
    }

    public void postStatus(AuthToken authToken, String post){
        Status newStatus = new Status(post, Cache.getInstance().getCurrUser(), System.currentTimeMillis(), parseURLs(post), parseMentions(post));
        getStatusService().postStatus(authToken, newStatus, new ConcretePostStatusObserver());

    }



    public List<String> parseURLs(String post) {
        List<String> containedUrls = new ArrayList<>();
        for (String word : post.split("\\s")) {
            if (word.startsWith("http://") || word.startsWith("https://")) {

                int index = findUrlEndIndex(word);

                word = word.substring(0, index);

                containedUrls.add(word);
            }
        }

        return containedUrls;
    }

    public List<String> parseMentions(String post) {
        List<String> containedMentions = new ArrayList<>();

        for (String word : post.split("\\s")) {
            if (word.startsWith("@")) {
                word = word.replaceAll("[^a-zA-Z0-9]", "");
                word = "@".concat(word);

                containedMentions.add(word);
            }
        }

        return containedMentions;
    }

    public int findUrlEndIndex(String word) {
        if (word.contains(".com")) {
            int index = word.indexOf(".com");
            index += 4;
            return index;
        } else if (word.contains(".org")) {
            int index = word.indexOf(".org");
            index += 4;
            return index;
        } else if (word.contains(".edu")) {
            int index = word.indexOf(".edu");
            index += 4;
            return index;
        } else if (word.contains(".net")) {
            int index = word.indexOf(".net");
            index += 4;
            return index;
        } else if (word.contains(".mil")) {
            int index = word.indexOf(".mil");
            index += 4;
            return index;
        } else {
            return word.length();
        }
    }

    public interface MainView extends Presenter.View {
        void isFollower(boolean isFollower);
        void setCount(int count, boolean isCountFollowers);
        void enableFollowButton();
        void followSuccessful();
        void unfollowSuccessful();
        void logoutSuccessful();
        void postStatusSuccessful();

    }


    private class ConcreteIsFollowerObserver extends ConcreteServiceObserver implements FollowService.IsFollowerObserver {

        @Override
        public void handleSuccess(boolean isFollower) {
            ((MainView) view).isFollower(isFollower);
        }

        @Override
        void handleFailureExtra(String message) {

        }

        @Override
        void handleExceptionExtra(Exception ex) {

        }
    }

    private class ConcreteFollowObserver extends ConcreteServiceObserver implements FollowService.FollowObserver{

        @Override
        public void handleSuccess() {
            ((MainView) view).followSuccessful();
            ((MainView) view).enableFollowButton();
        }

        @Override
        void handleFailureExtra(String message) {
            ((MainView) view).enableFollowButton();
        }

        @Override
        void handleExceptionExtra(Exception ex) {
            ((MainView) view).enableFollowButton();
        }
    }

    private class ConcreteUnfollowObserver extends ConcreteServiceObserver implements FollowService.UnfollowObserver{

        @Override
        public void handleSuccess() {
            ((MainView) view).unfollowSuccessful();
            ((MainView) view).enableFollowButton();
        }

        @Override
        void handleFailureExtra(String message) {
            ((MainView) view).enableFollowButton();
        }

        @Override
        void handleExceptionExtra(Exception ex) {
            ((MainView) view).enableFollowButton();
        }
    }

    private class ConcreteGetCountObserver extends ConcreteServiceObserver implements FollowService.GetCountObserver{

        @Override
        public void handleSuccess(int count, boolean isCountFollowers) {
            ((MainView) view).setCount(count, isCountFollowers);
        }

        @Override
        void handleFailureExtra(String message) {

        }

        @Override
        void handleExceptionExtra(Exception ex) {

        }
    }

    private class ConcreteLogoutObserver extends ConcreteServiceObserver implements UserService.LogoutObserver{
        @Override
        public void handleSuccess() {
            ((MainView) view).logoutSuccessful();
        }

        @Override
        void handleFailureExtra(String message) {
        }

        @Override
        void handleExceptionExtra(Exception ex) {
        }
    }

    protected class ConcretePostStatusObserver extends ConcreteServiceObserver implements StatusService.PostStatusObserver{

        @Override
        public void handleSuccess() {
            ((MainView) view).postStatusSuccessful();
            view.displayInfoMessage("Successfully Posted!");
        }

        @Override
        void handleFailureExtra(String message) {

        }

        @Override
        void handleExceptionExtra(Exception ex) {

        }
    }
}
