package edu.byu.cs.tweeter.server.dao.iDAO;

import java.util.List;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.net.request.GetFollowersRequest;
import edu.byu.cs.tweeter.model.net.request.GetFollowingRequest;
import edu.byu.cs.tweeter.util.Pair;

public interface iFollowDAO {

    public void follow(String userAlias, String followeeAlias);

    public void unfollow(String userAlias, String unfollowedUserHandle);

    public boolean isFollowing(String userAlias, String followeeAlias);

    public Pair<List<String>, Boolean> getFollowees(GetFollowingRequest request);

    public Pair<List<String>, Boolean> getFollowers(GetFollowersRequest request);

    public List<String> getAllFollowers(AuthToken authToken, String followeeAlias);

    public void batchWriteFollowers(List<String> followers, String followTarget);
}
