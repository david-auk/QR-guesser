package spotify;

public record User(String id, String name, String profilePictureImageUrl) {

    // TODO Implement
    public static User buildFromApi(String accessToken) {
        return new User(accessToken, "", "");
    }
}
