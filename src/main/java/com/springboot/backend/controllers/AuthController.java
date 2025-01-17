package com.springboot.backend.controllers;

// Import routing template and vars
import com.springboot.constant.Template;
import static com.springboot.constant.GlobalVars.*;

// Import springboot classes
import database.tables.AccessTokenDAO;
import database.tables.UserDAO;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.view.RedirectView;

// Import Http classes
import jakarta.servlet.http.HttpServletResponse;

// Rest
import spotify.AccessToken;

import static com.springboot.backend.utils.AccessTokenCookieUtil.setAccessTokenCookie;

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

        setAccessTokenCookie(accessTokenObj, response);

        // Redirect to the original URL
        return "redirect:" + state;
    }
}
