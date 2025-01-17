package com.springboot.backend.controllers;

import com.springboot.backend.progress.ProgressService;
import com.springboot.backend.utils.AccessTokenCookieUtil;
import com.springboot.backend.utils.AsyncInteractive;
import com.springboot.backend.utils.RedisInteractive;
import com.springboot.exceptions.QrGuesserUserShouldLoginException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import spotify.AccessToken;
import spotify.PlaylistScan;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/backend/export")
public class ExportController {

    @Autowired
    private ProgressService progressService;

    @GetMapping("/start-scan")
    public Map<String, String> startScan(HttpServletRequest request) throws QrGuesserUserShouldLoginException {
        AccessToken accessToken = AccessTokenCookieUtil.getVailidAccessToken(request);

        RedisInteractive redisInteractive = new RedisInteractive(progressService);

        System.out.println(redisInteractive.getProgressUUID());

        // Build the scan and add to DAO with threading
        buildScan(accessToken, redisInteractive);

        return Map.of("task_id", redisInteractive.getProgressUUID().toString());
    }


    @Async
    protected void buildScan(AccessToken accessToken, AsyncInteractive asyncInteractive) {
        // Start the asynchronous task
        PlaylistScan playlistScan = PlaylistScan.buildFromApi("placeHolder", accessToken, asyncInteractive);

        // TODO add to DAO
    }
}
