package edu.byu.cs.tweeter.server.util;

import java.util.List;

import edu.byu.cs.tweeter.model.domain.Status;

public class UpdateFeedRequest {
    private List<String> followers;
    private Status status;

    public UpdateFeedRequest(List<String> followers, Status status) {
        this.followers = followers;
        this.status = status;
    }

    public List<String> getFollowers() {
        return followers;
    }

    public void setFollowers(List<String> followers) {
        this.followers = followers;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
