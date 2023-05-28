package edu.byu.cs.tweeter.client.Presenter;

import edu.byu.cs.tweeter.client.model.service.Service;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public abstract class AuthenticatePresenter extends Presenter{

    public AuthenticatePresenter(AuthenticateView view) {
        super(view);
    }

    public interface AuthenticateView extends View {
        void displayErrorMessage(String message);

        void actionSuccessful(User user, AuthToken authToken);
    }

    protected class ConcreteAuthenticateObserver extends  ConcreteServiceObserver implements Service.AuthenticateObserver{
        @Override
        public void handleSuccess(User user, AuthToken authToken) {
            ((AuthenticateView) view).actionSuccessful(user, authToken);
        }

        @Override
        void handleFailureExtra(String message) {

        }

        @Override
        void handleExceptionExtra(Exception ex) {

        }
    }


}
