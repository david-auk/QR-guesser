package com.springboot.constant;

public class GlobalVars {

    // Token vars
    public static final int TOKEN_EXPIRATION_DURATION_S = 3600;  // Set expiration time to one hour afer the creation as the spotify api accessToken logic is implemented
    public static final int TOKEN_EXPIRATION_DURATION_MS = TOKEN_EXPIRATION_DURATION_S * 1000; // Convert to MS
    public static final String TOKEN_URL = "https://accounts.spotify.com/api/token";
    public static final String TOKEN_ID_COOKIE_NAME = "access_token_id";

    // API vars
    public static final String API_CLIENT_ID = System.getenv("SPOTIFY_CLIENT_ID");
    public static final String API_CLIENT_SECRET = System.getenv("SPOTIFY_CLIENT_SECRET");
    public static final String API_REDIRECT_URI = System.getenv("SPOTIFY_REDIRECT_URL") + "/backend/auth/callback";

    // Auth vars
    public static final String AUTH_URL = "https://accounts.spotify.com/authorize";
    public static final String AUTH_SCOPE = "user-modify-playback-state " + // scope needed to control playback
            "user-read-playback-state " + // Needed for pause functionality in scanner
            "user-read-private playlist-read-private playlist-read-collaborative"; // Needed for scraping userdata

    // User vars
    public static final String SPOTIFY_USER_PROFILE_URL = "https://api.spotify.com/v1/me";

}
