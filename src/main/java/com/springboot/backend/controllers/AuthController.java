package com.springboot.backend.controllers;

// Import routing template
import com.springboot.constant.Template;

// Import springboot classes
import database.AccessTokenDAO;
import database.UserDAO;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.view.RedirectView;

// Import Http classes
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

// Rest
import spotify.AccessToken;
import spotify.User;

import static com.springboot.constant.GlobalVars.*;

@Controller
@RequestMapping("/backend/auth")
public class AuthController {

    // Set static API Routes/Logic


    @GetMapping("/authenticate")
    public RedirectView login(@RequestParam(value = "next", defaultValue = "/") String next) {
        String loginUrl = AUTH_URL + "?client_id=" + API_CLIENT_ID +
                "&response_type=code" +
                "&redirect_uri=" + API_REDIRECT_URI +
                "&scope=" + AUTH_SCOPE +
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

        AccessToken accessTokenObj;
        try {
            accessTokenObj = AccessToken.buildFromCode(code);
        } catch (Exception e) {
            model.addAttribute("error", "Failed to fetch access token: " + e.getMessage());
            return Template.CALLBACK_FAILURE;
        }

        // Add access token to DB
        try (UserDAO userDAO = new UserDAO()) {
            try (AccessTokenDAO accessTokenDAO = new AccessTokenDAO(userDAO)) {
                accessTokenDAO.add(accessTokenObj);
            }
        }

        // Create the cookie with the access token record id, so it can later be referenced to get the token and the associated user
        Cookie accessTokenCookie = getAccessTokenCookie(accessTokenObj);

        // Add the cookie to the response
        response.addCookie(accessTokenCookie);

        // Redirect to the original URL
        return "redirect:" + state;
    }

    private static Cookie getAccessTokenCookie(AccessToken accessTokenObj) {
        Cookie accessTokenCookie = new Cookie("access_token_id", accessTokenObj.getId());
        accessTokenCookie.setHttpOnly(true); // Ensure it's HTTP-only
        accessTokenCookie.setSecure(true); // Use secure cookies in production
        accessTokenCookie.setPath("/"); // Set the cookie path
        accessTokenCookie.setMaxAge(TOKEN_EXPIRATION_DURATION_S); // Set TTL
        return accessTokenCookie;
    }
}
