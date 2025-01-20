package database.dao;

import database.core.TimestampedDAO;
import database.tables.AccessTokenTable;
import spotify.AccessToken;
import spotify.User;

public class AccessTokenDAO extends TimestampedDAO<AccessToken, String> {

    private final UserDAO userDAO;

    public AccessTokenDAO(UserDAO userDAO) {
        super(new AccessTokenTable(userDAO));
        this.userDAO = userDAO;
    }

    @Override
    public void add(AccessToken accessToken) {
        userDAO.add(accessToken.getUser());
        super.add(accessToken);
    }

    @Override
    public void update(AccessToken accessToken) {
        userDAO.update(accessToken.getUser());
        super.update(accessToken);
    }
}
