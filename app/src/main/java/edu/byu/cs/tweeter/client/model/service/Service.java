package edu.byu.cs.tweeter.client.model.service;

import java.util.List;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class Service {

    public interface ServiceObserver {
        void handleFailure(String message);
        void handleException(Exception ex);
    }

    public interface GetPagedItemsObserver<T> extends ServiceObserver{
        void handleSuccess(List<T> items, boolean hasMorePages);
    }

    public interface AuthenticateObserver extends ServiceObserver{
        void handleSuccess(User user, AuthToken authToken);
    }

}
