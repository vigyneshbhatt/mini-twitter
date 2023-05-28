package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Handler;

import java.util.List;

import edu.byu.cs.tweeter.client.model.net.ServerFacade;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.GetFeedRequest;
import edu.byu.cs.tweeter.model.net.response.GetFeedResponse;
import edu.byu.cs.tweeter.util.Pair;

/**
 * Background task that retrieves a page of statuses from a user's feed.
 */
public class GetFeedTask extends PagedStatusTask {
    public GetFeedTask(AuthToken authToken, User targetUser, int limit, Status lastStatus,
                       Handler messageHandler) {
        super(authToken, targetUser, limit, lastStatus, messageHandler);
    }

    @Override
    protected Pair<List<Status>, Boolean> getItems() {
        Status lastStatus;
        if (getLastItem() != null) {
            lastStatus = getLastItem();
        }
        else {
            lastStatus = null;
        }
        GetFeedRequest feedRequest = new GetFeedRequest(authToken, getTargetUser().getAlias(), getLimit(), lastStatus);
        try {
            GetFeedResponse feedResponse = new ServerFacade().getFeed(feedRequest, "/getfeed");
            if (feedResponse.isSuccess()) {
                return new Pair<>(feedResponse.getFeed(), feedResponse.getHasMorePages());
            }
        }
        catch (Exception e) {
            sendExceptionMessage(e);
        }
        return null;
    }
}
