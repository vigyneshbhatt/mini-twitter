package edu.byu.cs.tweeter.model.net.response;

public class GetFollowingCountResponse extends Response {
    private int followingCount;

    public GetFollowingCountResponse(String message) {
        super(false, message);
    }

    public GetFollowingCountResponse(int count) {
        super(true, null);
        this.followingCount = count;
    }

    public int getFollowingCount() {
        return followingCount;
    }

    public void setFollowingCount(int followingCount) {
        this.followingCount = followingCount;
    }
}
