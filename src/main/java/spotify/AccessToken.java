package spotify;

// Import springboot classes
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

// Rest
import java.sql.Timestamp;
import java.util.Map;
import java.util.UUID;

// Import global static vars
import static com.springboot.constant.GlobalVars.TOKEN_URL;
import static com.springboot.constant.GlobalVars.TOKEN_EXPIRATION_DURATION_MS;
import static com.springboot.constant.GlobalVars.API_CLIENT_ID;
import static com.springboot.constant.GlobalVars.API_CLIENT_SECRET;
import static com.springboot.constant.GlobalVars.API_REDIRECT_URI;

public class AccessToken {

    private final String id;
    private String accessToken;
    private String refreshToken;
    private final User user;
    private Timestamp expiresAt;

    // Constructor for DAO
    public AccessToken(String id, String accessToken, String refreshToken, User user, Timestamp createdAt) {
        this.id = id;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.user = user;
        this.expiresAt = new Timestamp(createdAt.getTime() + TOKEN_EXPIRATION_DURATION_MS);
    }

    // Constructor for Creation to add to DAO
    public AccessToken(String accessToken, String refreshToken, User user) {
        this.id = UUID.randomUUID().toString();
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.user = user;
        this.expiresAt = new Timestamp(System.currentTimeMillis() + TOKEN_EXPIRATION_DURATION_MS);
    }

    public String getId() {
        return id;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public User getUser() {
        return user;
    }

    public boolean isExpired() {
        return new Timestamp(System.currentTimeMillis()).after(expiresAt); // TODO Verify working
    }

    public void refresh() {
        Map<String, String> responseMap = fetchAccessToken("grant_type=refresh_token&refresh_token=" + refreshToken);

        // Update fields based on the new token
        this.accessToken = responseMap.get("access_token");
        this.refreshToken = responseMap.get("refresh_token");
        this.expiresAt = new Timestamp(System.currentTimeMillis() + TOKEN_EXPIRATION_DURATION_MS);
    }

    public static AccessToken buildFromCode(String authorizationCode) {
        Map<String, String> responseMap = fetchAccessToken("grant_type=authorization_code"
                + "&code=" + authorizationCode
                + "&redirect_uri=" + API_REDIRECT_URI);

        String accessToken = responseMap.get("access_token");
        String refreshToken = responseMap.get("refresh_token");
        User user = User.buildFromApi(accessToken);

        return new AccessToken(accessToken, refreshToken, user);
    }

    private static Map<String, String> fetchAccessToken(String body) {
        RestTemplate restTemplate = new RestTemplate();

        // Set up headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setBasicAuth(API_CLIENT_ID, API_CLIENT_SECRET);

        // Make the POST request
        HttpEntity<String> request = new HttpEntity<>(body, headers);
        return restTemplate.postForObject(TOKEN_URL, request, Map.class);
    }
}