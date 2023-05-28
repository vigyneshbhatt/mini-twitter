package edu.byu.cs.tweeter.server.dao.iDAO;

import java.util.List;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.util.Pair;

public interface iFeedDAO {
    public void postStatus(List<String> followers, Status status);

    public Pair<List<Status>, Boolean> getFeed(String username, Status lastStatus, Integer limit);

    public void batchWriteStatus(Status status, List<String> followers);
}
