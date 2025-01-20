package database.dao;

import database.core.GenericDAO;
import database.tables.PlaylistTable;
import spotify.Playlist;

public class PlaylistDAO extends GenericDAO<Playlist, String> {
    public PlaylistDAO() {
        super(new PlaylistTable());
    }
}