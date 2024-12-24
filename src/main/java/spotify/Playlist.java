package spotify;

public record Playlist(String id, String title, String coverImageUrl) {
    public String url(){
        return "https://open.spotify.com/playlist/" + id;
    }
}
