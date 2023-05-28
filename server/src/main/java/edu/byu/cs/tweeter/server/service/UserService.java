package edu.byu.cs.tweeter.server.service;

import java.text.ParseException;
import java.util.List;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.GetFeedRequest;
import edu.byu.cs.tweeter.model.net.request.GetStoryRequest;
import edu.byu.cs.tweeter.model.net.request.GetUserRequest;
import edu.byu.cs.tweeter.model.net.request.LoginRequest;
import edu.byu.cs.tweeter.model.net.request.LogoutRequest;
import edu.byu.cs.tweeter.model.net.request.RegisterRequest;
import edu.byu.cs.tweeter.model.net.response.GetFeedResponse;
import edu.byu.cs.tweeter.model.net.response.GetStoryResponse;
import edu.byu.cs.tweeter.model.net.response.GetUserResponse;
import edu.byu.cs.tweeter.model.net.response.LoginResponse;
import edu.byu.cs.tweeter.model.net.response.LogoutResponse;
import edu.byu.cs.tweeter.model.net.response.RegisterResponse;
import edu.byu.cs.tweeter.server.dao.factory.iDAOFactory;
import edu.byu.cs.tweeter.util.Pair;
import software.amazon.awssdk.services.dynamodb.endpoints.internal.Value;

public class UserService extends Service {

    public UserService(iDAOFactory daoFactory){
        super (daoFactory);
    }

    public LoginResponse login(LoginRequest request) {
        if(request.getUserAlias() == null){
            throw new RuntimeException("[Bad Request] Missing a username");
        } else if(request.getPassword() == null) {
            throw new RuntimeException("[Bad Request] Missing a password");
        }

        String passwordHash = getSecurePassword(request.getPassword());
        User user = daoFactory.getUserDAO().login(request.getUserAlias(), passwordHash);
        if (user!=null){
            AuthToken authToken = daoFactory.getAuthTokenDAO().generateToken();
            return new LoginResponse(user, authToken);
        } else return new LoginResponse("Authentication failed: Invalid Password");
    }

    public RegisterResponse register(RegisterRequest request) {
        if(request.getUserAlias() == null){
            throw new RuntimeException("[Bad Request] Missing a username");
        } else if(request.getPassword() == null) {
            throw new RuntimeException("[Bad Request] Missing a password");
        } else if(request.getFirstName() == null) {
            throw new RuntimeException("[Bad Request] Missing a first name");
        } else if(request.getLastName() == null) {
            throw new RuntimeException("[Bad Request] Missing a last name");
        } else if(request.getImage() == null) {
            throw new RuntimeException("[Bad Request] Missing an image");
        }

        if (daoFactory.getUserDAO().isExistingUser(request.getUserAlias())) {
            return new RegisterResponse("This userHandle is already taken");

        } else {
            System.out.println("## UserService Register - "+ request.getUserAlias());

            String imageURL = putS3Image(request);
            String passwordHash = getSecurePassword(request.getPassword());

            User user = daoFactory.getUserDAO().register(request.getUserAlias(), passwordHash, request.getFirstName(), request.getLastName(), imageURL);
            AuthToken authToken = daoFactory.getAuthTokenDAO().generateToken();

            followOtherUsers(request.getUserAlias());
            getFollowedByOtherUsers(request.getUserAlias());

            return new RegisterResponse(user, authToken);
        }
    }


    public GetFeedResponse getFeed(GetFeedRequest request){

        if(request.getUserAlias() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have a userAlias");
        } else if(request.getLimit() <= 0) {
            throw new RuntimeException("[Bad Request] Request needs to have a positive limit");
        }

        if (daoFactory.getAuthTokenDAO().validateAuthToken(request.getAuthToken())!=null) {
            Pair<List<Status>, Boolean> result = daoFactory.getFeedDAO().getFeed(request.getUserAlias(), request.getLastStatus(), request.getLimit());
            return new GetFeedResponse(result.getFirst(), result.getSecond());
        }
        return new GetFeedResponse("Failed authToken validation");
    }

    public GetStoryResponse getStory(GetStoryRequest request) {
        if(request.getUserAlias() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have a userAlias");
        } else if(request.getLimit() <= 0) {
            throw new RuntimeException("[Bad Request] Request needs to have a positive limit");
        }

        if (daoFactory.getAuthTokenDAO().validateAuthToken(request.getAuthToken())!=null) {
            User user = daoFactory.getUserDAO().getUser(request.getUserAlias());
            Pair<List<Status>, Boolean> result = daoFactory.getStoryDAO().getStory(user, request.getLastStatus(), request.getLimit());
            return new GetStoryResponse(result.getFirst(), result.getSecond());
        }
        return new GetStoryResponse("Failed authToken validation");
    }

    public GetUserResponse getUser(GetUserRequest request) throws ParseException {
        if(request.getUserAlias() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have a userAlias");
        }
        if (daoFactory.getAuthTokenDAO().validateAuthToken(request.getAuthToken())!=null) {
            User user = daoFactory.getUserDAO().getUser(request.getUserAlias());
            return  new GetUserResponse(user);
        }else return new GetUserResponse("Failed authToken validation");

    }

    public LogoutResponse logout(LogoutRequest request)
    {
        if (daoFactory.getAuthTokenDAO().validateAuthToken(request.getAuthToken())!=null){
            daoFactory.getAuthTokenDAO().deleteToken(request.getAuthToken());
            return new LogoutResponse();

        } else return new LogoutResponse("Failed authToken validation");
    }

    public void followOtherUsers(String userAlias){
        super.follow(userAlias, "@shivam");
        super.follow(userAlias, "@vigynesh");
        super.follow(userAlias, "@gola");
        super.follow(userAlias, "@atul");
    }

    public void getFollowedByOtherUsers (String userAlias){
        super.follow("@shivam", userAlias);
        super.follow("@vigynesh", userAlias);
        super.follow("@gola", userAlias);
        super.follow("@atul", userAlias);
    }
}
