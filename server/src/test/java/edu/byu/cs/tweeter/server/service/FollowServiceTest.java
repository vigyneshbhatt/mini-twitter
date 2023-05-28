package edu.byu.cs.tweeter.server.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.List;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.GetFollowingRequest;
import edu.byu.cs.tweeter.model.net.response.GetFollowingResponse;
import edu.byu.cs.tweeter.server.dao.concreteDAO.DynamoDBFollowDAO;
import edu.byu.cs.tweeter.util.Pair;

public class FollowServiceTest {

    private GetFollowingRequest request;
    private Pair<List<String>, Boolean> expectedResponse;
    private DynamoDBFollowDAO mockDynamoDBFollowDAO;
    private FollowService followServiceSpy;

    @BeforeEach
    public void setup() {
        AuthToken authToken = new AuthToken();

        User currentUser = new User("FirstName", "LastName", null);

        User resultUser1 = new User("FirstName1", "LastName1",
                "https://faculty.cs.byu.edu/~jwilkerson/cs340/tweeter/images/donald_duck.png");
        User resultUser2 = new User("FirstName2", "LastName2",
                "https://faculty.cs.byu.edu/~jwilkerson/cs340/tweeter/images/daisy_duck.png");
        User resultUser3 = new User("FirstName3", "LastName3",
                "https://faculty.cs.byu.edu/~jwilkerson/cs340/tweeter/images/daisy_duck.png");

        // Setup a request object to use in the tests
        request = new GetFollowingRequest(authToken, currentUser.getAlias(), 3, null);

        // Setup a mock DynamoDBFollowDAO that will return known responses
        expectedResponse = new Pair<>(Arrays.asList(resultUser1.getAlias(), resultUser2.getAlias(), resultUser3.getAlias()), false);
        mockDynamoDBFollowDAO = Mockito.mock(DynamoDBFollowDAO.class);
        Mockito.when(mockDynamoDBFollowDAO.getFollowees(request)).thenReturn(expectedResponse);

        followServiceSpy = Mockito.spy(FollowService.class);
        Mockito.when(followServiceSpy.daoFactory.getFollowDAO()).thenReturn(mockDynamoDBFollowDAO);
    }

    /**
     * Verify that the {@link FollowService#getFollowees(GetFollowingRequest)}
     * method returns the same result as the {@link DynamoDBFollowDAO} class.
     */
    @Test
    public void testGetFollowees_validRequest_correctResponse() {
        GetFollowingResponse response = followServiceSpy.getFollowees(request);
        for (int i=0; i<response.getFollowees().size(); i++){
            Assertions.assertEquals(expectedResponse.getFirst().get(i), response.getFollowees().get(i).getAlias());
        }

    }
}
