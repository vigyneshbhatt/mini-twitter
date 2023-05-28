package edu.byu.cs.tweeter.client.Presenter;

import edu.byu.cs.tweeter.client.model.service.UserService;

public class LoginPresenter extends AuthenticatePresenter{

    public LoginPresenter(AuthenticateView view) {
        super(view);
    }

    public void initiateLogin(String username, String password) {
        String validationMessage = validateLogin(username, password);

        if (validationMessage == null) {
            view.displayInfoMessage("Logging in...");
            UserService userService = new UserService();
            userService.login(username, password, new ConcreteAuthenticateObserver());
        } else {
            ((AuthenticateView) view).displayErrorMessage(validationMessage);
        }

    }

    public String validateLogin(String username, String password) {
        if (username.length() > 0 && username.charAt(0) != '@') {
            return "Alias must begin with @.";
        }
        if (username.length() < 2) {
            return "Alias must contain 1 or more characters after the @.";
        }
        if (password.length() == 0) {
            return "Password cannot be empty.";
        }
        return null;
    }

}
