package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Bundle;
import android.os.Handler;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public abstract class GetCountTask extends AuthenticatedTask{
    public static final String COUNT_KEY = "count";
    private int count;

    /**
     * The user whose count is being retrieved.
     * (This can be any user, not just the currently logged-in user.)
     */
    private final User targetUser;
    public GetCountTask(Handler messageHandler, User targetUser, AuthToken authToken) {
        super(messageHandler, authToken);
        this.targetUser=targetUser;
    }

    public User getTargetUser() {
        return targetUser;
    }

    @Override
    protected void runTask() {
        count = runCountTask();
        sendSuccessMessage();
    }

    protected abstract int runCountTask();
    @Override
    protected void loadSuccessBundle(Bundle msgBundle) {
        msgBundle.putInt(COUNT_KEY, count);
    }
}
