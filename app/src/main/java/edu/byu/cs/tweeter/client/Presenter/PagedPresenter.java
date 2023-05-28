package edu.byu.cs.tweeter.client.Presenter;

import android.util.Log;

import java.util.List;

import edu.byu.cs.tweeter.client.model.service.Service;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public abstract class PagedPresenter<T> extends Presenter{

    protected final User user;
    protected final AuthToken authToken;
    protected boolean isLoading = false;
    protected boolean hasMorePages = true;
    protected UserService userService;
    protected T lastItem;


    private static final String LOG_TAG = "PagedPresenter";
    public static final int PAGE_SIZE = 10;


    public interface PagedView<U> extends View {
        void setLoading(boolean isLoading);
        void addItems(List<U> u);
        void switchToUser(User user);
    }

    public PagedPresenter(PagedView view, User user, AuthToken authToken) {
        super(view);
        this.user = user;
        this.authToken = authToken;
        userService = new UserService();
    }

    public T getLastItem(){
        return lastItem;
    };
    public void setLastItem(T item){
        lastItem = item;
    }

    public boolean isLoading() {
        return isLoading;
    }

    public void setLoading(boolean loading) {
        isLoading = loading;
    }

    public boolean isHasMorePages() {
        return hasMorePages;
    }

    public void setHasMorePages(boolean hasMorePages) {
        this.hasMorePages = hasMorePages;
    }

    public void getUser(AuthToken authToken, String userAlias){
        userService.getUser(authToken, userAlias, new ConcreteGetUserObserver());
    }

    public void loadMoreItems() {
        if (!isLoading && hasMorePages) {   // This guard is important for avoiding a race condition in the scrolling code.
            setLoading(true);
            ((PagedView) view).setLoading(isLoading);

            getItem(authToken, user, PAGE_SIZE, lastItem);

        }
    }

    abstract void getItem(AuthToken authToken, User targetUser, int limit, T lastItem);




    protected class ConcreteGetUserObserver extends ConcreteServiceObserver implements UserService.GetUserObserver {

        @Override
        public void handleSuccess(User user) {
            ((PagedView) view).switchToUser(user);
        }

        @Override
        void handleFailureExtra(String message) {
            String errorMessage = "Failed to retrieve user: " + message;
            Log.e(LOG_TAG, errorMessage);
        }

        @Override
        void handleExceptionExtra(Exception exception) {
            String errorMessage = "Failed to retrieve user because of exception: " + exception.getMessage();
            Log.e(LOG_TAG, errorMessage, exception);
        }
    }

    protected abstract class ConcreteGetPagedItemsObserver extends ConcreteServiceObserver implements Service.GetPagedItemsObserver<T>{

        @Override
        public void handleSuccess(List<T> items, boolean hasMorePages) {

            setLastItem((items.size() > 0) ? items.get(items.size() - 1) : null);
            setHasMorePages(hasMorePages);

            ((PagedView) view).setLoading(false);
            ((PagedView) view).addItems(items);
            setLoading(false);

        }

        @Override
        void handleFailureExtra(String message){
            String errorMessage = "Failed to retrieve " + getMessageFiller()+ " : " + message;
            Log.e(LOG_TAG, errorMessage);

            ((PagedView) view).setLoading(false);
            setLoading(false);
        }

        @Override
        void handleExceptionExtra(Exception exception) {
            String errorMessage = "Failed to retrieve " + getMessageFiller()+ " because of exception: " + exception.getMessage();
            Log.e(LOG_TAG, errorMessage, exception);

            ((PagedView) view).setLoading(false);
            setLoading(false);
        }

        abstract String getMessageFiller();
    }
}
