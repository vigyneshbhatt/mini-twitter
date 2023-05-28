package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Handler;

import java.util.List;

import edu.byu.cs.tweeter.client.model.net.ServerFacade;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.GetStoryRequest;
import edu.byu.cs.tweeter.model.net.response.GetStoryResponse;
import edu.byu.cs.tweeter.util.Pair;

/**
 * Background task that retrieves a page of statuses from a user's story.
 */
public class GetStoryTask extends PagedStatusTask {

    public GetStoryTask(AuthToken authToken, User targetUser, int limit, Status lastStatus,
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
        GetStoryRequest storyRequest = new GetStoryRequest(authToken, getTargetUser().getAlias(),
                getLimit(), lastStatus);
        try {
            GetStoryResponse storyResponse = new ServerFacade().getStory(storyRequest, "/getstory");
            if (storyResponse.isSuccess()) {
                return new Pair<>(storyResponse.getStory(), storyResponse.getHasMorePages());
            }
        }
        catch (Exception e) {
            sendExceptionMessage(e);
        }

        return null;
    }
}