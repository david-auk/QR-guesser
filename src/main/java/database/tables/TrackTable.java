package database.tables;

import database.core.Table;
import spotify.Track;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TrackTable extends Table<Track, String> {
    public TrackTable() {
        super("track", "id", String.class,
                "addQuery",
                "updateQuery"
        );
    }

    @Override
    public void prepareAddStatement(PreparedStatement unPreparedStatement, Track entity) throws SQLException {

    }

    @Override
    public void prepareUpdateStatement(PreparedStatement unPreparedStatement, Track entity) throws SQLException {

    }

    @Override
    public Track buildFromTableWildcardQuery(ResultSet resultSet) throws SQLException {
        return null;
    }

    @Override
    public String getPrimaryKey(Track entity) {
        return "";
    }
}
