package spotify;

import utils.ProcessCallback;

import java.sql.Timestamp;
import java.util.List;

public class PlaylistScan {

    String id;
    Playlist playlist;
    List<Track> tracks;
    User requestedByUser;
    boolean exportComplete;
    Timestamp timestamp;
    PlaylistScan extendsPlaylistScan;

    public PlaylistScan(String id, Playlist playlist, List<Track> tracks, User requestedByUser, boolean exportComplete, Timestamp timestamp, PlaylistScan extendsPlaylistScan) {
        this.id = id;
        this.playlist = playlist;
        this.tracks = tracks;
        this.requestedByUser = requestedByUser;
        this.exportComplete = exportComplete;
        this.timestamp = timestamp;
        this.extendsPlaylistScan = extendsPlaylistScan;
    }

    public String getId() {
        return id;
    }

    public Playlist getPlaylist() {
        return playlist;
    }

    public List<Track> getTracks() {
        return tracks;
    }

    public User getRequestedByUser() {
        return requestedByUser;
    }

    public boolean isExportComplete() {
        return exportComplete;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public PlaylistScan getExtendsPlaylistScan() {
        return extendsPlaylistScan;
    }

    public PlaylistScan buildFromApi(String apiKey, ProcessCallback processCallback) {
        return new PlaylistScan(
                null,
                null,
                null,
                null,
                false,
                null,
                null
        );
    }

    public PlaylistScan buildFromApi(String apiKey) {
        return buildFromApi(apiKey, null);
    }
}