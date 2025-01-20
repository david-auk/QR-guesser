package database.dao;

import database.core.GenericDAO;
import database.tables.AlbumTable;
import spotify.Album;

public class AlbumDAO extends GenericDAO<Album, String> {
    public AlbumDAO() {
        super(new AlbumTable());
    }
}