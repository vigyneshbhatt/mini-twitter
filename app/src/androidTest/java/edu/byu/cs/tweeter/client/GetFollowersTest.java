package edu.byu.cs.tweeter.client;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import edu.byu.cs.tweeter.client.model.net.ServerFacade;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.net.request.GetFollowersRequest;
import edu.byu.cs.tweeter.model.net.request.RegisterRequest;
import edu.byu.cs.tweeter.model.net.response.GetFollowersResponse;
import edu.byu.cs.tweeter.model.net.response.RegisterResponse;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class GetFollowersTest {
    ServerFacade serverFacade;
    AuthToken testAuthtoken;

    @BeforeAll
    public void setup() {
        this.serverFacade = new ServerFacade();
        this.testAuthtoken = new AuthToken();
    }

    @Test
    public void GetFollowersPass() {
        GetFollowersRequest request = new GetFollowersRequest(testAuthtoken, "fAlias", 10, "lfAlias");
        String urlPath = "/getfollowers";
        try {
            GetFollowersResponse response = serverFacade.getFollowers(request, urlPath);
            assert (response.isSuccess());
            assertNotNull(response.getFollowers());
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }
    }

    @Test
    public void GetFollowersFail() {
        GetFollowersRequest request = new GetFollowersRequest(testAuthtoken, "fAlias", 10, "lfAlias");
        String urlPath = "/notgetfollowers";
        try {
            GetFollowersResponse response = serverFacade.getFollowers(request, urlPath);
            assert (!response.isSuccess());
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }
    }
}
