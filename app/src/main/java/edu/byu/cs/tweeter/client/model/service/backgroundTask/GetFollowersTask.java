package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Handler;

import java.util.List;

import edu.byu.cs.tweeter.client.model.net.ServerFacade;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.GetFollowersRequest;
import edu.byu.cs.tweeter.model.net.response.GetFollowersResponse;
import edu.byu.cs.tweeter.util.Pair;

/**
 * Background task that retrieves a page of followers.
 */
public class GetFollowersTask extends PagedUserTask {
    public GetFollowersTask(AuthToken authToken, User targetUser, int limit, User lastFollower, Handler messageHandler) {
        super(authToken, targetUser, limit, lastFollower, messageHandler);
    }

    @Override
    protected Pair<List<User>, Boolean> getItems() {
        String lastFollowerAlias;
        if (getLastItem() != null) {
            lastFollowerAlias = getLastItem().getAlias();
        }
        else {
            lastFollowerAlias = null;
        }
        GetFollowersRequest followersRequest = new GetFollowersRequest(authToken,
                getTargetUser().getAlias(), getLimit(), lastFollowerAlias);
        try {
            GetFollowersResponse followersResponse = new ServerFacade().getFollowers(followersRequest,
                    "/getfollowers");
            if (followersResponse.isSuccess()) {
                return new Pair<>(followersResponse.getFollowers(), followersResponse.getHasMorePages());
            }
        }
        catch (Exception e) {
            sendExceptionMessage(e);
        }
        return null;
    }
}
