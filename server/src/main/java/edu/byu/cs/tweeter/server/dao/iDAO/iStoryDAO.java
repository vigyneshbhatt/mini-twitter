package edu.byu.cs.tweeter.server.dao.iDAO;

import java.util.List;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.util.Pair;

public interface iStoryDAO {

    public void postStatus(Status status);

    public Pair<List<Status>, Boolean> getStory(User user, Status lastStatus, int limit);
}
