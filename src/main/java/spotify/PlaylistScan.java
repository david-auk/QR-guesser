package spotify;

import org.json.JSONArray;
import org.json.JSONObject;
import utils.ProcessCallback;

import java.net.URI;
import java.net.URLDecoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    public static PlaylistScan buildFromApi(String playlistId, String accessToken, User requestedByUser, ProcessCallback processCallback) throws Exception {
        String apiUrl = "https://api.spotify.com/v1/playlists/" + playlistId;

        JSONObject data = getData(apiUrl, accessToken, null);

        // Build playlist record
        Playlist playlist = new Playlist(
                data.getString("id"),
                data.getString("name"),
                data.getJSONArray("images").getJSONObject(0).getString("url")
        );

        List<Track> tracks = new ArrayList<>();
        JSONObject tracksData = data.getJSONObject("tracks");

        String nextUrl = tracksData.optString("next", null);

        // Populate tracklist
        do {
            JSONArray items = tracksData.getJSONArray("items");

            for (int i = 0; i < items.length(); i++) {
                JSONObject item = items.getJSONObject(i);
                JSONObject trackJson = item.optJSONObject("track");

                if (trackJson == null || trackJson.optBoolean("is_local", false)) {
                    continue;
                }

                JSONObject albumJson = trackJson.optJSONObject("album");
                if (albumJson == null || !albumJson.has("release_date")) {
                    continue;
                }

                List<Artist> artists = new ArrayList<>();
                JSONArray artistsJson = trackJson.getJSONArray("artists");
                for (int j = 0; j < artistsJson.length(); j++) {
                    JSONObject artistJson = artistsJson.getJSONObject(j);
                    artists.add(new Artist(artistJson.getString("id"), artistJson.getString("name")));
                }

                Album album = new Album(
                        albumJson.getString("id"),
                        albumJson.getString("name"),
                        Integer.parseInt(albumJson.getString("release_date").substring(0, 4))
                );

                tracks.add(new Track(
                        trackJson.getString("id"),
                        trackJson.getString("name"),
                        album,
                        artists//,
                        //Timestamp.valueOf(item.getString("added_at").replace("T", " ").replace("Z", ""))
                ));
            }

            if (nextUrl != null) {
                tracksData = getData(nextUrl, accessToken, nextUrl);
                nextUrl = tracksData.optString("next", null);
            }
        } while (nextUrl != null);

        return new PlaylistScan(
                null,
                playlist,
                tracks,
                requestedByUser,
                false,
                new Timestamp(System.currentTimeMillis()),
                null
        );
    }

    public static PlaylistScan buildFromApi(String playlistId, String accessToken, User requestedByUser) throws Exception {
        return buildFromApi(playlistId, accessToken, requestedByUser, null);
    }

    private static JSONObject getData(String url, String accessToken, String next) throws Exception {
        String fields;
        Map<String, String> params;

        if (next != null) {
            URI uri = new URI(next);
            params = parseQuery(uri.getQuery());
            url = uri.getScheme() + "://" + uri.getHost() + uri.getPath();

            params.put("fields", "items(added_at,track(is_local,added_at,id,name,images,artists(name,id),album(id,name,release_date))),next");
        } else {
            fields = "id,name,images," +
                    "tracks.items(added_at,track(is_local,id,name,images,artists(name,id),album(id,name,release_date)))," +
                    "tracks.next";

            params = Map.of("fields", fields);
        }

        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(url + "?" + buildQueryString(params)))
                .header("Authorization", "Bearer " + accessToken)
                .GET()
                .build();

        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new Exception("Spotify API error: " + response.statusCode() + " - " + response.body());
        }

        return new JSONObject(response.body());
    }

    private static Map<String, String> parseQuery(String query) {
        return Arrays.stream(query.split("&"))
                .map(param -> param.split("="))
                .collect(Collectors.toMap(
                        parts -> URLDecoder.decode(parts[0], StandardCharsets.UTF_8),
                        parts -> URLDecoder.decode(parts[1], StandardCharsets.UTF_8)
                ));
    }

    private static String buildQueryString(Map<String, String> params) {
        return params.entrySet().stream()
                .map(entry -> entry.getKey() + "=" + entry.getValue())
                .collect(Collectors.joining("&"));
    }
}