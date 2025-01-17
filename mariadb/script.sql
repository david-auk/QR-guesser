-- Turn on scheduler
SET GLOBAL event_scheduler = ON;

-- Create the `user` table
CREATE TABLE user (
    id VARCHAR(255) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    profile_picture_image_url VARCHAR(255) DEFAULT NULL
);

-- Create the `playlist` table
CREATE TABLE playlist (
    id VARCHAR(255) PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    cover_image_url VARCHAR(255) NOT NULL
);

-- Create the `playlist_scan` table
CREATE TABLE playlist_scan (
    id UUID NOT NULL DEFAULT UUID() PRIMARY KEY,
    playlist_id VARCHAR(255) NOT NULL,
    requested_by_user_id VARCHAR(255) NOT NULL,
    export_completed BOOLEAN NOT NULL,
    extends_playlist_scan UUID DEFAULT NULL,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (extends_playlist_scan) REFERENCES playlist_scan(id) ON DELETE CASCADE,
    FOREIGN KEY (requested_by_user_id) REFERENCES user(id) ON DELETE CASCADE,
    FOREIGN KEY (playlist_id) REFERENCES playlist(id) ON DELETE CASCADE
);

-- Create the `artist` table
CREATE TABLE artist (
    id VARCHAR(255) PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);

-- Create the `album` table
CREATE TABLE album (
    id VARCHAR(255) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    release_year INT NOT NULL
);

-- Create the `track` table
CREATE TABLE track (
    id VARCHAR(255) PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    album_id VARCHAR(255) NOT NULL,
    FOREIGN KEY (album_id) REFERENCES album(id) ON DELETE CASCADE
);

-- Create the relationship between `artist` and `track` (many-to-many)
CREATE TABLE artist_track (
    artist_id VARCHAR(255) NOT NULL,
    track_id VARCHAR(255) NOT NULL,
    PRIMARY KEY (artist_id, track_id),
    FOREIGN KEY (artist_id) REFERENCES artist(id) ON DELETE CASCADE,
    FOREIGN KEY (track_id) REFERENCES track(id) ON DELETE CASCADE
);

-- Create the relationship between `playlist_scan` and `track` (many-to-many)
CREATE TABLE playlist_scan_track (
    id UUID NOT NULL DEFAULT UUID() PRIMARY KEY,
    playlist_scan_id UUID NOT NULL,
    track_id VARCHAR(255) NOT NULL,
    track_playlist_scan_index INT NOT NULL,
    track_added_at TIMESTAMP NOT NULL,
    FOREIGN KEY (playlist_scan_id) REFERENCES playlist_scan(id) ON DELETE CASCADE,
    FOREIGN KEY (track_id) REFERENCES track(id) ON DELETE CASCADE
);

-- Create the `access_token` table
CREATE TABLE access_token (
    id UUID NOT NULL DEFAULT UUID() PRIMARY KEY,
    access_token VARCHAR(255) NOT NULL,
    refresh_token VARCHAR(255) NOT NULL,
    user_id VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    expires_at TIMESTAMP DEFAULT (CURRENT_TIMESTAMP + INTERVAL 1 HOUR),
    FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE
);

CREATE EVENT auto_delete_old_access_tokens
ON SCHEDULE EVERY 1 DAY
DO
DELETE FROM access_token
WHERE expires_at < NOW() - INTERVAL 7 DAY;