package edu.byu.cs.tweeter.client.Presenter;

import java.util.List;

import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class FollowingPresenter extends PagedPresenter<User>{

    private static final String LOG_TAG = "FollowingPresenter";

    public FollowingPresenter(PagedView view, User user, AuthToken authToken) {
        super(view, user, authToken);
    }

    /**
     * Returns an instance of {@link FollowService}. Allows mocking of the FollowService class
     * for testing purposes. All usages of FollowService should get their FollowService
     * instance from this method to allow for mocking of the instance.
     *
     * @return the instance.
     */
    public FollowService getFollowingService() {
        return new FollowService();
    }

    @Override
    void getItem(AuthToken authToken, User targetUser, int limit, User lastItem) {
        new FollowService().getFollowees(authToken, targetUser, limit, lastItem, new ConcreteGetFollowingObserver());
    }

    private class ConcreteGetFollowingObserver extends ConcreteGetPagedItemsObserver {

        @Override
        String getMessageFiller() {
            return "followees";
        }
    }
}
