package database.dao;

import database.core.GenericDAO;
import database.tables.TrackTable;
import spotify.Track;

public class TrackDAO extends GenericDAO<Track, String> {
    public TrackDAO() {
        super(new TrackTable());
    }
}
