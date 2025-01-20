package database.dao;

import database.core.GenericDAO;
import database.tables.TrackTable;
import spotify.Artist;
import spotify.Track;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class TrackDAO extends GenericDAO<Track, String> {

    private final AlbumDAO albumDAO;
    private final ArtistDAO artistDAO;

    public TrackDAO(AlbumDAO albumDAO, ArtistDAO artistDAO) {
        super(new TrackTable(albumDAO, artistDAO));
        this.albumDAO = albumDAO;
        this.artistDAO = artistDAO;
    }

    private void addArtistToTrack(Artist artist, Track track) {
        // Add the artist if not exists
        artistDAO.add(artist);

        String query = "INSERT INTO artist_track (artist_id, track_id) VALUES (?, ?)";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, artist.id());
            preparedStatement.setString(2, track.id());
            preparedStatement.executeUpdate();
        } catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public void add(Track track) {
        // Add the album
        albumDAO.add(track.album());

        // Add the actual track
        super.add(track);

        // Add the associated artists to the database
        for (Artist artist : track.artists()) {
            addArtistToTrack(artist, track);
        }
    }

    @Override
    public void update(Track track) {
        // Update the associated album
        albumDAO.update(track.album());

        // Update the associated artists
        for (Artist artist : track.artists()) {
            artistDAO.update(artist);
        }
        super.update(track);
    }
}
