package edu.byu.cs.tweeter.model.net.request;

import edu.byu.cs.tweeter.model.domain.AuthToken;

public class GetFollowingCountRequest {
    private String userAlias;
    private AuthToken authToken;

    private GetFollowingCountRequest() {}

    public GetFollowingCountRequest(String userAlias, AuthToken authToken) {
        this.userAlias = userAlias;
        this.authToken = authToken;
    }

    public String getUserAlias() {
        return userAlias;
    }

    public void setUserAlias(String userAlias) {
        this.userAlias = userAlias;
    }

    public AuthToken getAuthToken() {
        return authToken;
    }

    public void setAuthToken(AuthToken authToken) {
        this.authToken = authToken;
    }
}
