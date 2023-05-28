package edu.byu.cs.tweeter.model.net.request;

/**
 * Contains all the information needed to make a login request.
 */
public class LoginRequest {

    private String userAlias;
    private String password;

    /**
     * Allows construction of the object from Json. Private so it won't be called in normal code.
     */
    private LoginRequest() {}

    /**
     * Creates an instance.
     *
     * @param userAlias the username of the user to be logged in.
     * @param password the password of the user to be logged in.
     */
    public LoginRequest(String userAlias, String password) {
        this.userAlias = userAlias;
        this.password = password;
    }

    /**
     * Returns the username of the user to be logged in by this request.
     *
     * @return the username.
     */
    public String getUserAlias() {
        return userAlias;
    }

    /**
     * Sets the username.
     *
     * @param userAlias the username.
     */
    public void setUserAlias(String userAlias) {
        this.userAlias = userAlias;
    }
    /**
     * Returns the password of the user to be logged in by this request.
     *
     * @return the password.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the password.
     *
     * @param password the password.
     */
    public void setPassword(String password) {
        this.password = password;
    }
}
