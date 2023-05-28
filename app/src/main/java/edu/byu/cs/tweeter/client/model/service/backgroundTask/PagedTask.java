package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Bundle;
import android.os.Handler;

import java.io.Serializable;
import java.util.List;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.util.Pair;

public abstract class PagedTask<T> extends AuthenticatedTask{

    public static final String ITEMS_KEY = "items";
    public static final String MORE_PAGES_KEY = "more-pages";

    private boolean hasMorePages;

    /**
     * The user whose items are being retrieved.
     * (This can be any user, not just the currently logged-in user.)
     */
    private final User targetUser;
    private List<T> items;

    /**
     * Maximum number of followed users to return (i.e., page size).
     */
    private int limit;

    /**
     * The last status returned in the previous page of results (can be null).
     * This allows the new page to begin where the previous page ended.
     */
    private final T lastItem;

    public PagedTask(Handler messageHandler, User targetUser, int limit, T lastItem, AuthToken authToken) {
        super(messageHandler, authToken);
        this.limit = limit;
        this.lastItem = lastItem;
        this.targetUser = targetUser;
    }

    public int getLimit() {
        return limit;
    }

    public T getLastItem() {
        return lastItem;
    }

    public User getTargetUser() {
        return targetUser;
    }

    @Override
    protected void runTask() {
        Pair<List<T>, Boolean> pageOfItems = getItems();
        items = pageOfItems.getFirst();
        hasMorePages = pageOfItems.getSecond();

        sendSuccessMessage();
    }

    @Override
    protected void loadSuccessBundle(Bundle msgBundle) {
        msgBundle.putSerializable(ITEMS_KEY, (Serializable) items);
        msgBundle.putBoolean(MORE_PAGES_KEY, hasMorePages);
    }

    protected abstract Pair<List<T>, Boolean> getItems();

    protected abstract List<User> getUsersForItems(List<T> items);
}
