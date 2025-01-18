package database.dao;

import database.core.GenericTimestampedDAO;
import database.tables.AccessTokenTable;
import spotify.AccessToken;
import spotify.User;

public class AccessTokenDAO extends GenericTimestampedDAO<AccessToken, String> {

    private final UserDAO userDAO;

    public AccessTokenDAO(UserDAO userDAO) {
        super(new AccessTokenTable(userDAO));
        this.userDAO = userDAO;
    }

    // Handle user does not exist yet
    @Override
    public void add(AccessToken accessToken) {
        User user = accessToken.getUser();
        if (!userDAO.exists(user)) {
            userDAO.add(user);
        }
        super.add(accessToken);
    }
}
