package database.dao;

import database.core.TimestampedDAO;
import database.tables.PlaylistScanTable;
import spotify.PlaylistScan;
import spotify.PlaylistScanTrack;

public class PlaylistScanDAO extends TimestampedDAO<PlaylistScan, String> {

    // Used for close method logic
    private final boolean openedNewPlaylistScanTrackDAOConnection;

    // Dependency DAO's
    private final PlaylistScanTrackDAO playlistScanTrackDAO;

    public PlaylistScanDAO(PlaylistDAO playlistDAO, PlaylistScanTrackDAO playlistScanTrackDAO, UserDAO userDAO) {
        // Call the superclass constructor with a temporary null value
        super(null);
        openedNewPlaylistScanTrackDAOConnection = false;

        // Set the DAO's
        this.playlistScanTrackDAO = playlistScanTrackDAO;

        // Initialize the PlaylistScanTable after the superclass constructor has been called (to reference "this")
        PlaylistScanTable playlistScanTable = new PlaylistScanTable(this, playlistDAO, playlistScanTrackDAO, userDAO);

        // Set the table in the superclass
        setTable(playlistScanTable);
    }

    /**
     * Second constructor that's needed because playlistScanDAO and playlistScanTrackDAO both need each-other.
     * This constructor is there as an option to automatically create the playlistScanTrackDAO instance within the constructor
     * @param trackDAO The DAO playlistScanTrackDAO is dependent on.
     */
    public PlaylistScanDAO(PlaylistDAO playlistDAO, UserDAO userDAO, TrackDAO trackDAO) {
        // Call the superclass constructor with a temporary null value
        super(null);

        // Set the DAO's
        this.playlistScanTrackDAO = new PlaylistScanTrackDAO(this, trackDAO);
        openedNewPlaylistScanTrackDAOConnection = true;

        // Initialize the PlaylistScanTable after the superclass constructor has been called (to reference "this")
        PlaylistScanTable playlistScanTable = new PlaylistScanTable(this, playlistDAO, playlistScanTrackDAO, userDAO);

        // Set the table in the superclass
        setTable(playlistScanTable);
    }

    public PlaylistScanTrackDAO getPlaylistScanTrackDAO() {
        return playlistScanTrackDAO;
    }

    @Override
    public void add(PlaylistScan playlistScan) {

        // Add all the associated tracks.
        for (PlaylistScanTrack playlistScanTrack : playlistScan.getPlaylistScanTracks()) {
            playlistScanTrackDAO.add(playlistScanTrack);
        }
        super.add(playlistScan);
    }

    @Override
    public void close(){

        if (openedNewPlaylistScanTrackDAOConnection) {
            playlistScanTrackDAO.close();
        }

        super.close();
    }
}