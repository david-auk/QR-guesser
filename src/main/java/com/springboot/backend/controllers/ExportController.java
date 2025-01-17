package com.springboot.backend.controllers;

import com.springboot.backend.progress.ProgressService;
import com.springboot.backend.utils.AccessTokenCookieUtil;
import com.springboot.backend.utils.AsyncInteractive;
import com.springboot.backend.utils.RedisInteractive;
import com.springboot.backend.utils.RequestBodyUtil;
import com.springboot.exceptions.JsonErrorResponseException;
import com.springboot.exceptions.UserUnauthenticatedException;
import database.dao.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.*;
import spotify.AccessToken;
import spotify.Playlist;
import spotify.PlaylistScan;

import java.util.Map;

@RestController
@RequestMapping("/backend/export")
public class ExportController {

    @Autowired
    private ProgressService progressService;

    @PostMapping("/start-scan")
    public Map<String, String> startScan(HttpServletRequest request, @RequestBody Map<String, ?> requestBodyMap) throws UserUnauthenticatedException, JsonErrorResponseException {
        AccessToken accessToken = AccessTokenCookieUtil.getVailidAccessToken(request);
        RequestBodyUtil requestBodyUtil = new RequestBodyUtil(requestBodyMap);

        // Get the playlist_id
        String id = requestBodyUtil.getField("playlist_id", String.class);
        String title = requestBodyUtil.getField("playlist_title", String.class);
        String imageUrl = requestBodyUtil.getField("playlist_cover_image_url", String.class);

        RedisInteractive redisInteractive = new RedisInteractive(progressService);

        // Get the playlist object
        Playlist playlist = new Playlist(id, title, imageUrl);

        // Build the scan and add to DAO with threading
        buildScan(accessToken, playlist, redisInteractive);

        return Map.of("task_id", redisInteractive.getProgressUUID().toString());
    }

    @Async
    protected void buildScan(AccessToken accessToken, Playlist playlist, AsyncInteractive asyncInteractive) {
        // Start the asynchronous task

        PlaylistScan playlistScan = new PlaylistScan(playlist, accessToken.getUser(), null);

        // Populate the playlistScan track list with API
        playlistScan.scan(accessToken, asyncInteractive);

        // Add Scraped info to the database
        try (AlbumDAO albumDAO = new AlbumDAO()){
            try (ArtistDAO artistDAO = new ArtistDAO()){
                try (TrackDAO trackDAO = new TrackDAO(albumDAO, artistDAO)){
                    try (PlaylistDAO playlistDAO = new PlaylistDAO()){
                        try (UserDAO userDAO = new UserDAO()){
                            try (PlaylistScanDAO playlistScanDAO = new PlaylistScanDAO(playlistDAO, userDAO, trackDAO)){
                                playlistScanDAO.add(playlistScan);
                            }
                        }
                    }
                }
            }
        }
    }
}
