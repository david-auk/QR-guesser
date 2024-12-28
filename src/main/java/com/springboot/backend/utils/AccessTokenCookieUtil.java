package com.springboot.backend.utils;

import com.springboot.exceptions.QrGuesserUserShouldLoginException;
import database.AccessTokenDAO;
import database.UserDAO;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.util.WebUtils;
import spotify.AccessToken;

import static com.springboot.constant.GlobalVars.TOKEN_EXPIRATION_DURATION_S;
import static com.springboot.constant.GlobalVars.TOKEN_ID_COOKIE_NAME;

public class AccessTokenCookieUtil {
    public static AccessToken getVailidAccessToken(HttpServletRequest request) throws QrGuesserUserShouldLoginException {

        String accessTokenId = getAccessTokenId(request);

        AccessToken accessToken;
        try (UserDAO userDAO = new UserDAO()) {
            try (AccessTokenDAO accessTokenDAO = new AccessTokenDAO(userDAO)) {
                accessToken = accessTokenDAO.get(accessTokenId);

                if (accessToken == null) {
                    throw new QrGuesserUserShouldLoginException("No record found in DB");
                    // This exception can occur if a cookie is older than the delete scheduler option in the DB
                }

                if (accessToken.isExpired()) {
                    // Refresh expired Token
                    accessToken.refresh();

                    // Update the existing record with the new tokens
                    accessTokenDAO.update(accessToken);
                }
            }
        }
        return accessToken;
    }

    private static String getAccessTokenId(HttpServletRequest request) throws QrGuesserUserShouldLoginException {
        Cookie accessTokenCookie = WebUtils.getCookie(request, TOKEN_ID_COOKIE_NAME);
        if (accessTokenCookie == null) {
            throw new QrGuesserUserShouldLoginException("Cookie not found in browser");
        } else {
            return accessTokenCookie.getValue();
        }
    }

    public static void setAccessTokenCookie(AccessToken accessTokenObj, HttpServletResponse response) {
        if (accessTokenObj != null) {
            // Create the cookie with the access token record id, so it can later be referenced to get the token and the associated user
            Cookie accessTokenCookie = generateAccessTokenCookie(accessTokenObj);

            // Add the cookie to the response
            response.addCookie(accessTokenCookie);
        }
    }

    private static Cookie generateAccessTokenCookie(AccessToken accessTokenObj) {
        Cookie accessTokenCookie = new Cookie(TOKEN_ID_COOKIE_NAME, accessTokenObj.getId());
        accessTokenCookie.setHttpOnly(true); // Ensure it's HTTP-only
        accessTokenCookie.setSecure(true); // Use secure cookies in production
        accessTokenCookie.setPath("/"); // Set the cookie path
        accessTokenCookie.setMaxAge(TOKEN_EXPIRATION_DURATION_S); // Set TTL
        return accessTokenCookie;
    }
}
