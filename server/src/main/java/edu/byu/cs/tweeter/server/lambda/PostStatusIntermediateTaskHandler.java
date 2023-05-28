package edu.byu.cs.tweeter.server.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageResult;

import java.util.List;

import edu.byu.cs.tweeter.model.net.request.GetFollowersRequest;
import edu.byu.cs.tweeter.model.net.request.PostStatusRequest;
import edu.byu.cs.tweeter.server.dao.factory.DynamoDBDAOFactory;
import edu.byu.cs.tweeter.server.service.FollowService;
import edu.byu.cs.tweeter.server.util.JsonSerializer;
import edu.byu.cs.tweeter.server.util.UpdateFeedRequest;
import edu.byu.cs.tweeter.util.Pair;

public class PostStatusIntermediateTaskHandler implements RequestHandler<SQSEvent, Void> {

    @Override
    public Void handleRequest(SQSEvent event, Context context) {
        for (SQSEvent.SQSMessage msg : event.getRecords()) {
            System.out.println("### 1st Stage Success: There are messages in PostStatus SQS Queue");
            PostStatusRequest request = JsonSerializer.deserialize(msg.getBody(), PostStatusRequest.class);

            FollowService followService = new FollowService(new DynamoDBDAOFactory());

            Pair<List<String>, Boolean> response = followService.daoFactory.
                    getFollowDAO().getFollowers(new GetFollowersRequest(
                            request.getAuthToken(), request.getStatus().getUser().getAlias(),
                            250, null));

            List<String> followers = response.getFirst();
            sendToUpdateFeedQueue(new UpdateFeedRequest(followers, request.getStatus()));

            while(response.getSecond()){

                response = followService.daoFactory.
                        getFollowDAO().getFollowers(new GetFollowersRequest(
                                request.getAuthToken(), request.getStatus().getUser().getAlias(),
                                250, followers.get(followers.size()-1)));

                followers = response.getFirst();
                sendToUpdateFeedQueue(new UpdateFeedRequest(followers, request.getStatus()));
            }

        }
        return null;
    }


    public void sendToUpdateFeedQueue(UpdateFeedRequest request){
        String queueUrl = "https://sqs.us-west-2.amazonaws.com/250765046656/UpdateFeedQueue";

        try {
            SendMessageRequest sendMessageRequest = new SendMessageRequest()
                    .withQueueUrl(queueUrl)
                    .withMessageBody(JsonSerializer.serialize(request));

            AmazonSQS sqs = AmazonSQSClientBuilder.defaultClient();
            SendMessageResult sendMessageResult = sqs.sendMessage(sendMessageRequest);

        } catch (Exception e) {
            System.out.println("### Error: Failed to put updateFeedRequest in UpdateFeed SQS Queue");
            throw new RuntimeException("[Internal Error] Failed to put updateFeedRequest in UpdateFeed SQS Queue");
        }
    }
}
