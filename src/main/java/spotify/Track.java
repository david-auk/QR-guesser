package spotify;

import java.util.List;

public record Track(String id, String name, Album album, List<Artist> artists) {}
