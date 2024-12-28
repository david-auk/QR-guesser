package spotify;

import org.json.JSONArray;
import org.json.JSONObject;
import utils.AssertCallback;

import java.net.URI;
import java.net.URLDecoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

public class PlaylistScan {

    String id;
    Playlist playlist;
    List<Map<Track, Timestamp>> tracks;
    User requestedByUser;
    boolean exportComplete;
    Timestamp timestamp;
    PlaylistScan extendsPlaylistScan;

    public PlaylistScan(String id, Playlist playlist, List<Map<Track, Timestamp>> tracks, User requestedByUser, boolean exportComplete, Timestamp timestamp, PlaylistScan extendsPlaylistScan) {
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

    public List<Map<Track, Timestamp>> getTracks() {
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

    public static PlaylistScan buildFromApi(String playlistId, AccessToken accessToken, AssertCallback callback) {
        String apiUrl = "https://api.spotify.com/v1/playlists/" + playlistId;

        Map<String, String> status = new HashMap<>();

        status.put("code", "100");
        status.put("description", "Retrieving playlist items from Spotify API");
        callback.update(status);

        JSONObject data;
        try {
            data = getData(apiUrl, accessToken, null);
        } catch (Exception e) {
            status.put("code", "400");
            status.put("description", "Failed to Retrieve playlist items from Spotify API. ERROR: " + e.getMessage());
            callback.update(status);
            return null;
        }
        // Build playlist record
        Playlist playlist = new Playlist(
                data.getString("id"),
                data.getString("name"),
                data.getJSONArray("images").getJSONObject(0).getString("url")
        );

        List<Map<Track, Timestamp>> tracks = new ArrayList<>();
        JSONObject tracksData = data.getJSONObject("tracks");

        String nextUrl = tracksData.optString("next", null);

        status.put("description", "Building playlist items");
        callback.update(status);

        // Populate tracklist
        do {
            JSONArray items = tracksData.getJSONArray("items");

            for (int i = 0; i < items.length(); i++) {

                // TODO implement cancel logic

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

                Track track = new Track(
                        trackJson.getString("id"),
                        trackJson.getString("name"),
                        album,
                        artists
                );

                Timestamp addedAt = Timestamp.valueOf(item.getString("added_at").replace("T", " ").replace("Z", ""));

                // Create a new map
                Map<Track, Timestamp> trackEntry = new HashMap<>();

                // Add track and time to this map
                trackEntry.put(track, addedAt);

                // Add this map to the tracklist
                tracks.add(trackEntry);

            }

            if (nextUrl != null) {
                try {
                    tracksData = getData(nextUrl, accessToken, nextUrl);
                } catch (Exception e) {
                    status.put("code", "400");
                    status.put("description", "Failed to Retrieve items from Spotify API NEXT URL. ERROR: " + e.getMessage());
                    callback.update(status);
                    return null;
                }
                nextUrl = tracksData.optString("next", null);
            }
        } while (nextUrl != null);

        status.put("code", "200");
        status.put("description", "Scan completed");

        callback.update(status);

        return new PlaylistScan(
                null,
                playlist,
                tracks,
                accessToken.getUser(),
                false,
                new Timestamp(System.currentTimeMillis()),
                null
        );
    }

    public static PlaylistScan buildFromApi(String playlistId, AccessToken accessToken) throws Exception {
        return buildFromApi(playlistId, accessToken, null);
    }

    private static JSONObject getData(String url, AccessToken accessToken, String next) throws Exception {
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
                .header("Authorization", "Bearer " + accessToken.getAccessToken())
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