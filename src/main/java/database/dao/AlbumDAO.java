package database.dao;

import database.core.GenericDAO;
import database.tables.AlbumTable;
import database.tables.UserTable;
import spotify.Album;
import spotify.User;

public class AlbumDAO extends GenericDAO<Album, String> {
    public AlbumDAO() {
        super(new AlbumTable());
    }
}