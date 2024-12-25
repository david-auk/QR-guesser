package com.springboot.controllers.backend.auth;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.ui.Model;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Map;

import com.springboot.constant.Template;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.view.RedirectView;

@Controller
@RequestMapping("/backend/auth")
public class AuthController {

    private final String clientId = System.getenv("SPOTIFY_CLIENT_ID");
    private final String clientSecret = System.getenv("SPOTIFY_CLIENT_SECRET");
    private final String redirectUri = System.getenv("SPOTIFY_REDIRECT_URL") + "/backend/auth/callback";
    private final String authUrl = "https://accounts.spotify.com/authorize";
    private final String tokenUrl = "https://accounts.spotify.com/api/token";
    private final String scope = "user-modify-playback-state " + // scope needed to control playback
            "user-read-playback-state " + // Needed for pause functionality in scanner
            "user-read-private playlist-read-private playlist-read-collaborative"; // Needed for scraping userdata

    @GetMapping("/authenticate")
    public RedirectView login(@RequestParam(value = "next", defaultValue = "/") String next) {
        String loginUrl = authUrl + "?client_id=" + clientId +
                "&response_type=code" +
                "&redirect_uri=" + redirectUri +
                "&scope=" + scope +
                "&state=" + next;
        return new RedirectView(loginUrl);
    }

    @GetMapping("/callback")
    public String callback(@RequestParam(value = "code", required = false) final String code,
                           @RequestParam(value = "error", required = false) final String error,
                           @RequestParam(value = "state", defaultValue = "/") String state,
                           Model model,
                           HttpServletResponse response) { // Inject HttpServletResponse

        if (error != null) {
            model.addAttribute("error", error);
            return Template.CALLBACK_FAILURE;
        }

        RestTemplate restTemplate = new RestTemplate();

        // Set up headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        // Build the request body
        String body = "grant_type=authorization_code"
                + "&code=" + code
                + "&redirect_uri=" + redirectUri
                + "&client_id=" + clientId
                + "&client_secret=" + clientSecret;

        // Combine headers and body
        HttpEntity<String> request = new HttpEntity<>(body, headers);

        // Make the POST request
        try {
            Map<String, String> responseMap = restTemplate.postForObject(tokenUrl, request, Map.class);

            assert responseMap != null;
            String accessToken = responseMap.get("access_token");

            System.out.println(accessToken);

            // Create the cookie with the access token
            Cookie accessTokenCookie = new Cookie("accessToken", accessToken);
            accessTokenCookie.setHttpOnly(true); // Ensure it's HTTP-only
            accessTokenCookie.setSecure(true); // Use secure cookies in production
            accessTokenCookie.setPath("/"); // Set the cookie path
            accessTokenCookie.setMaxAge(10); // TTL of 1 hour in seconds

            // Add the cookie to the response
            response.addCookie(accessTokenCookie);

            // Redirect to the original URL
            return "redirect:" + state;

        } catch (HttpClientErrorException e) {
            model.addAttribute("error", "Failed to fetch access token: " + e.getMessage());
            return Template.CALLBACK_FAILURE;
        }
    }
}
