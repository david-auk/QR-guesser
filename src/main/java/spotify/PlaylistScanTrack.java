package spotify;

import java.sql.Timestamp;

public record PlaylistScanTrack(String id, PlaylistScan playlistScan, Track track, Integer index, Timestamp addedAt) {}