package edu.byu.cs.tweeter.server.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageResult;

import edu.byu.cs.tweeter.model.net.request.PostStatusRequest;
import edu.byu.cs.tweeter.model.net.response.PostStatusResponse;
import edu.byu.cs.tweeter.server.dao.factory.DynamoDBDAOFactory;
import edu.byu.cs.tweeter.server.service.StatusService;
import edu.byu.cs.tweeter.server.util.JsonSerializer;

public class PostStatusHandler implements RequestHandler<PostStatusRequest, PostStatusResponse> {
    @Override
    public PostStatusResponse handleRequest(PostStatusRequest request, Context context) {

        StatusService statusService = new StatusService(new DynamoDBDAOFactory());
        PostStatusResponse postStatusStoryResponse = statusService.postStatusStory(request);

        System.out.println("### Test1: postStatusStoryResponse :"+ postStatusStoryResponse.getMessage());

        if (postStatusStoryResponse.getMessage()==null) {
            System.out.println("### Test2: trying to put in postfeed queue");
            String queueUrl = "https://sqs.us-west-2.amazonaws.com/250765046656/PostStatusQueue";

            try {
                SendMessageRequest sendMessageRequest = new SendMessageRequest()
                        .withQueueUrl(queueUrl)
                        .withMessageBody(JsonSerializer.serialize(request));

                AmazonSQS sqs = AmazonSQSClientBuilder.defaultClient();
                SendMessageResult sendMessageResult = sqs.sendMessage(sendMessageRequest);

                return new PostStatusResponse();

            } catch (Exception e) {
                System.out.println("### Error: Failed to put PostStatusRequest in PostStatus SQS Queue");
                throw new RuntimeException("[Internal Error] Failed to put PostStatusRequest in PostStatus SQS Queue");
            }
        } else return postStatusStoryResponse;
    }
}
