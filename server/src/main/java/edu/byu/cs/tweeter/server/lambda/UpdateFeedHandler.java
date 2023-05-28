package edu.byu.cs.tweeter.server.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;

import edu.byu.cs.tweeter.model.net.request.PostStatusRequest;
import edu.byu.cs.tweeter.server.dao.factory.DynamoDBDAOFactory;
import edu.byu.cs.tweeter.server.service.FollowService;
import edu.byu.cs.tweeter.server.service.StatusService;
import edu.byu.cs.tweeter.server.util.JsonSerializer;
import edu.byu.cs.tweeter.server.util.UpdateFeedRequest;

public class UpdateFeedHandler implements RequestHandler<SQSEvent, Void> {

    @Override
    public Void handleRequest(SQSEvent event, Context context) {

        for (SQSEvent.SQSMessage msg : event.getRecords()) {
            System.out.println("### 2nd Stage Success: There are messages in UpdateFeed SQS Queue");
            UpdateFeedRequest request = JsonSerializer.deserialize(msg.getBody(), UpdateFeedRequest.class);

            StatusService statusService = new StatusService(new DynamoDBDAOFactory());
            statusService.batchWriteStatus(request);
        }

        return null;
    }

}
