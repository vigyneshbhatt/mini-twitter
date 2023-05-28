package edu.byu.cs.tweeter.client.Presenter;

import edu.byu.cs.tweeter.client.model.service.Service;

public class Presenter {
    View view;
    public Presenter(View view) {
        this.view = view;
    }

    public interface View {
        void displayInfoMessage(String message);
    }

    protected abstract class ConcreteServiceObserver implements Service.ServiceObserver {

        @Override
        public void handleFailure(String message) {
            handleFailureExtra(message);
            view.displayInfoMessage(message);
        }

        @Override
        public void handleException(Exception ex) {
            handleExceptionExtra(ex);
            view.displayInfoMessage(ex.getMessage());
        }

        abstract void handleFailureExtra(String message);

        abstract void handleExceptionExtra(Exception ex);


    }
}
