package edu.byu.cs.tweeter.server.util;

import java.util.ArrayList;
import java.util.List;

import edu.byu.cs.tweeter.server.dao.bean.UserBean;
import edu.byu.cs.tweeter.server.dao.factory.DynamoDBDAOFactory;
import edu.byu.cs.tweeter.server.dao.factory.iDAOFactory;
import edu.byu.cs.tweeter.server.service.Service;

public class Populate {

    private static final iDAOFactory factory = new DynamoDBDAOFactory();

    // How many follower users to add
    // We recommend you test this with a smaller number first, to make sure it works for you
    private final static int NUM_USERS = 10000;

    private final static String FOLLOW_TARGET = "@bossuser";

    public static void populateDatabase() {
        List<String> followers = new ArrayList<>();
        List<UserBean> users = new ArrayList<>();

        UserBean bossUser = new UserBean();
        bossUser.setUserAlias(FOLLOW_TARGET);
        bossUser.setFirstName("boss");
        bossUser.setLastName("user");
        bossUser.setImageURL("https://tweeterbucketvigynesh.s3.us-west-2.amazonaws.com/%40bossuser.jpg");
        bossUser.setFolloweeCount(0);
        bossUser.setFollowerCount(NUM_USERS);
        bossUser.setPasswordHash(Service.getSecurePassword("bosspassword"));
        users.add(bossUser);

        // Iterate over the number of users you will create
        for (int i = 1; i <= NUM_USERS; i++) {

            String name = "Guy " + i;
            String alias = "@guy" + i;

            String imageURL="https://tweeterbucketvigynesh.s3.us-west-2.amazonaws.com/%40guy.jpg";
            // Note that in this example, a UserDTO only has a name and an alias.
            // The url for the profile image can be derived from the alias in this example
            UserBean user = new UserBean();
            user.setUserAlias(alias);
            user.setFirstName(name);
            user.setLastName(name);
            user.setImageURL(imageURL);
            user.setFolloweeCount(1);
            user.setFollowerCount(0);
            user.setPasswordHash(Service.getSecurePassword("password"));
            users.add(user);

            // Note that in this example, to represent a follows relationship, only the aliases
            // of the two users are needed
            followers.add(alias);
        }

        // Call the DAOs for the database logic
//        if (users.size() > 0) {
//            factory.getUserDAO().batchWriteUsers(users);
//        }
        if (followers.size() > 0) {
            factory.getFollowDAO().batchWriteFollowers(followers, FOLLOW_TARGET);
        }
    }

    public static void main(String[] args){
        populateDatabase();
    }
}
