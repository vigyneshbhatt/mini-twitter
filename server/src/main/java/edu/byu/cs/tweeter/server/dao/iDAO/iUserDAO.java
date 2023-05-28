package edu.byu.cs.tweeter.server.dao.iDAO;

import java.util.List;

import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.server.dao.bean.UserBean;

public interface iUserDAO {
    public User login(String userAlias, String passwordHash);

    public User getUser(String userAlias);

    public Boolean isExistingUser(String userAlias);

    public User register(String userAlias, String passwordHash, String firstName, String lastName, String imageUrl);

    public void updateFollowerCount(String userAlias, int value);

    public void updateFolloweeCount(String userAlias, int value);

    public Integer getFollowerCount(String userAlias);

    public Integer getFolloweeCount(String userAlias);

    public void batchWriteUsers(List<UserBean> users);
}
