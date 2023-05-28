package edu.byu.cs.tweeter.model.net.request;

public class RegisterRequest {
    private String firstName;
    private String lastName;
    private String userAlias;
    private String password;
    private String image;

    private RegisterRequest() {}

    public RegisterRequest(String firstName, String lastName, String userAlias, String password, String image) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.userAlias = userAlias;
        this.password = password;
        this.image = image;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUserAlias() {
        return userAlias;
    }

    public void setUserAlias(String userAlias) {
        this.userAlias = userAlias;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
