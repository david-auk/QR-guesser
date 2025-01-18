package database.dao;

import database.core.GenericDAO;
import database.tables.AlbumTable;
import database.tables.ArtistTable;
import spotify.Album;
import spotify.Artist;

public class ArtistDAO extends GenericDAO<Artist, String> {
    public ArtistDAO() {
        super(new ArtistTable());
    }
}