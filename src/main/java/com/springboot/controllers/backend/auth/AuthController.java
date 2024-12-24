package com.springboot.controllers.backend.auth;

import com.springboot.constant.Template;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

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
                           Model model) {

        if (error != null) {
            model.addAttribute("error", error);
            return Template.CALLBACK_FAILURE;
        }

        RestTemplate restTemplate = new RestTemplate();

        // Exchange authorization code for access token
        Map<String, String> body = new HashMap<>();
        body.put("grant_type", "authorization_code");
        body.put("code", code);
        body.put("redirect_uri", redirectUri);
        body.put("client_id", clientId);
        body.put("client_secret", clientSecret);

        Map<String, String> response = restTemplate.postForObject(tokenUrl, body, Map.class);

        assert response != null;
        String accessToken = response.get("access_token");

        // Store access token in session (you may use Spring Session or similar)
        model.addAttribute("accessToken", accessToken);

        // Redirect to the original URL
        return "redirect:" + state;
    }
}
