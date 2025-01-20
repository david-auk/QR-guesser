package database.dao;

import database.core.GenericDAO;
import database.tables.UserTable;
import spotify.User;

public class UserDAO extends GenericDAO<User, String> {
    public UserDAO() {
        super(new UserTable());
    }
}