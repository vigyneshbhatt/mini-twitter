package edu.byu.cs.tweeter.server.dao.factory;

import edu.byu.cs.tweeter.server.dao.iDAO.iAuthTokenDAO;
import edu.byu.cs.tweeter.server.dao.iDAO.iFeedDAO;
import edu.byu.cs.tweeter.server.dao.iDAO.iFollowDAO;
import edu.byu.cs.tweeter.server.dao.iDAO.iStoryDAO;
import edu.byu.cs.tweeter.server.dao.iDAO.iUserDAO;

public interface iDAOFactory {
    public iUserDAO getUserDAO();

    public iFeedDAO getFeedDAO();

    public iStoryDAO getStoryDAO();

    public iFollowDAO getFollowDAO();

    public iAuthTokenDAO getAuthTokenDAO();
}
