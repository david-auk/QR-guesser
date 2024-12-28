package spotify;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.springboot.constant.GlobalVars.SPOTIFY_USER_PROFILE_URL;

public record User(String id, String name, String profilePictureImageUrl) {
    public static User buildFromApi(String accessToken) {
        try {
            // Create the HTTP client
            HttpClient client = HttpClient.newHttpClient();

            // Set the Authorization header
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(SPOTIFY_USER_PROFILE_URL))  // Use the Spotify User Profile URL here
                    .header("Authorization", "Bearer " + accessToken)
                    .build();

            // Send the request and get the response
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // Check for a successful response
            if (response.statusCode() != 200) {
                throw new RuntimeException("Error fetching user profile: " + response.statusCode() + ", " + response.body());
            }

            // Parse the JSON response
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode userData = objectMapper.readTree(response.body());

            // Extract the fields from the JSON
            String id = userData.get("id").asText();
            String name = userData.get("display_name").asText();
            String profilePictureImageUrl = userData.get("images").get(0).get("url").asText();

            // Return the User object
            return new User(id, name, profilePictureImageUrl);

        } catch (Exception e) {
            Logger.getLogger(User.class.getName()).log(Level.SEVERE, "Error fetching user profile", e);
            return new User("", "", "");
        }
    }
}