package edu.byu.cs.tweeter.client.Presenter;

import java.util.List;

import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class StoryPresenter extends PagedPresenter<Status> {

    private static final String LOG_TAG = "StoryPresenter";

    public StoryPresenter(PagedView view, User user, AuthToken authToken) {
        super(view, user, authToken);
    }

    @Override
    void getItem(AuthToken authToken, User targetUser, int limit, Status lastItem) {
        userService.getStory(authToken, targetUser, limit, lastItem, new ConcreteGetStoryObserver());
    }

    private class ConcreteGetStoryObserver extends ConcreteGetPagedItemsObserver {

        @Override
        String getMessageFiller() {
            return "statuses";
        }
    }
}
