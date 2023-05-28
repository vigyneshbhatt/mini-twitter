package edu.byu.cs.tweeter.client.Presenter;

import java.util.List;

import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class FeedPresenter extends PagedPresenter<Status>{

    private static final String LOG_TAG = "FeedPresenter";

    public FeedPresenter(PagedView view, User user, AuthToken authToken) {
        super(view, user, authToken);
    }

    @Override
    void getItem(AuthToken authToken, User targetUser, int limit, Status lastItem) {
        userService.getFeed(authToken, targetUser, limit, lastItem, new ConcreteGetFeedObserver());
    }

    private class ConcreteGetFeedObserver extends ConcreteGetPagedItemsObserver {

        @Override
        String getMessageFiller() {
            return "statuses";
        }
    }
}
