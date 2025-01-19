package database.dao;

import database.core.GenericDAO;
import database.core.TimestampedDAO;
import database.tables.PlaylistScanTable;
import database.tables.PlaylistTable;
import spotify.Playlist;
import spotify.PlaylistScan;

public class PlaylistScanDAO extends TimestampedDAO<PlaylistScan, String> {
    public PlaylistScanDAO(PlaylistDAO playlistDAO) {
        super(new PlaylistScanTable(this, playlistDAO));
    }
}