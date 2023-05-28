package edu.byu.cs.tweeter.server.dao.iDAO;

import edu.byu.cs.tweeter.model.domain.AuthToken;

public interface iAuthTokenDAO {
    public AuthToken generateToken();

    public void deleteToken(AuthToken authToken);

    public AuthToken getToken(String token);

    public AuthToken validateAuthToken(AuthToken authToken);
}
