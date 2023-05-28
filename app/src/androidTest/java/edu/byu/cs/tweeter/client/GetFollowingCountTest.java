package edu.byu.cs.tweeter.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import edu.byu.cs.tweeter.client.model.net.ServerFacade;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.net.request.GetFollowingCountRequest;
import edu.byu.cs.tweeter.model.net.response.GetFollowingCountResponse;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class GetFollowingCountTest {
    ServerFacade serverFacade;
    AuthToken testAuthtoken;

    @BeforeAll
    public void setup() {
        this.serverFacade = new ServerFacade();
        this.testAuthtoken = new AuthToken();
    }

    @Test
    public void GetFollowingCountPass() {
        GetFollowingCountRequest request = new GetFollowingCountRequest("ua", testAuthtoken);
        String urlPath = "/getfollowingcount";
        try {
            GetFollowingCountResponse response = serverFacade.getFollowingCount(request, urlPath);
            assert (response.isSuccess());
            assertEquals(21, response.getFollowingCount());
        } catch (Exception e) {
            System.out.println("Exception: "+ e.getMessage());
        }
    }

    @Test
    public void GetFollowingCountFail() {
        GetFollowingCountRequest request = new GetFollowingCountRequest("ua", testAuthtoken);
        String urlPath = "/notgetfollowingcount";
        try {
            GetFollowingCountResponse response = serverFacade.getFollowingCount(request, urlPath);
            assert (!response.isSuccess());
        } catch (Exception e) {
            System.out.println("Exception: "+ e.getMessage());
        }
    }
}
