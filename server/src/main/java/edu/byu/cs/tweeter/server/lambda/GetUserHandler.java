package edu.byu.cs.tweeter.server.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import java.text.ParseException;

import edu.byu.cs.tweeter.model.net.request.GetUserRequest;
import edu.byu.cs.tweeter.model.net.response.GetUserResponse;
import edu.byu.cs.tweeter.server.dao.factory.DynamoDBDAOFactory;
import edu.byu.cs.tweeter.server.service.UserService;

public class GetUserHandler implements RequestHandler<GetUserRequest, GetUserResponse> {

    @Override
    public GetUserResponse handleRequest(GetUserRequest input, Context context) {
        UserService userService = new UserService(new DynamoDBDAOFactory());
        try {
            return userService.getUser(input);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return new GetUserResponse("Error in GetUserHandler");
    }
}