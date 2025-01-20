package database.dao;

import database.core.GenericDAO;
import database.tables.ArtistTable;
import spotify.Artist;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class ArtistDAO extends GenericDAO<Artist, String> {
    public ArtistDAO() {
        super(new ArtistTable());
    }

    public List<Artist> getArtistsByTrackId(String trackId) {
        List<Artist> artists;
        String query = "SELECT * FROM artist_track WHERE track_id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, trackId);
            artists = getEntities(preparedStatement.executeQuery());
        } catch (SQLException e){
            throw new RuntimeException(e);
        }
        return artists;
    }
}