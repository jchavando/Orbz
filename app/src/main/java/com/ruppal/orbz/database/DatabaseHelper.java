package com.ruppal.orbz.database;

import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.ruppal.orbz.models.Artist;
import com.ruppal.orbz.models.Owner;
import com.ruppal.orbz.models.Playlist;
import com.ruppal.orbz.models.Song;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ruppal on 7/20/17.
 */

public class DatabaseHelper {
//    public static void addSongToTestPlaylist(Song song){
//        Playlist fakePlaylist = makeFakePlaylist();
//        PlaylistTable playlistTable = makePlaylistTableRow(fakePlaylist);
//        playlistTable.save();
//        SongTable songTable = makeSongTableRow(song, playlistTable);
//        songTable.save();
//    }


    private static Playlist makeFakePlaylist(){
        Owner owner = new Owner();
        owner.setId("3");
        owner.setName("testName3");
        Playlist playlist = new Playlist();
        playlist.setOwner(owner);
        playlist.setPlaylistName("testPlaylistName3");
        playlist.setImage(null);
        playlist.setTracks(null);
        playlist.setTracksUrl(null);
        playlist.setPlaylistId("3");
        playlist.setPlaylistService(Song.LOCAL);
        return playlist;
    }


//    private static SongTable makeSongTableRow(Song song, PlaylistTable playlistTable){
//        SongTable songTable = new SongTable();
//        songTable.setAlbum(song.getAlbum());
//        songTable.setTitle(song.getTitle());
//        songTable.setUid(song.getUid());
//        Artist artist= song.getArtists().get(0);
//        songTable.setArtistName(artist.getName());
//        songTable.setAlbumCoverUrl(song.getAlbumCoverUrl());
//        songTable.setPlaying(song.isPlaying());
//        songTable.setPopularity(song.getPopularity());
//        songTable.setService(song.getService());
//        songTable.setDuration_ms(song.getDuration_ms());
//        songTable.set
//        return songTable;
//    }

    public static Playlist playlistFromPlaylistTable(PlaylistTable playlistTable){
        Playlist playlist = new Playlist();
        Owner owner = new Owner();
        owner.setName(playlistTable.getOwnerName());
        owner.setId(playlistTable.getOwnerId());
        playlist.setOwner(owner);
        playlist.setPlaylistName(playlistTable.getPlaylistName());
        playlist.setImage(playlistTable.getImage());
        playlist.setPlaylistId(playlistTable.getPlaylistId());
        playlist.setPlaylistService(playlistTable.getPlaylistService());
        return playlist;
    }

    public static Song songFromSongTable(SongTable songTable){
        Song song = new Song();
        ArrayList<Artist> artists= new ArrayList<>();
        Artist artist = new Artist();
        artist.setName(songTable.getArtistName());
        artist.setUid(null);
        artists.add(artist);
        song.setArtists(artists);
        song.setTitle(songTable.getTitle());
        song.setAlbumCoverUrl(songTable.getAlbumCoverUrl());
        song.setUid(songTable.getUid());
        song.setPlaying(songTable.isPlaying());
        song.setAlbum(songTable.getAlbum());
        song.setPopularity(songTable.getPopularity());
        song.setService(songTable.getService());
        song.setDuration_ms(songTable.getDuration_ms());
        return song;
    }

    public static SongTable songTablefromSong(Song song){
        SongTable songTable = new SongTable();
        songTable.setUid(song.getUid());
        songTable.setTitle(song.getTitle());
        songTable.setArtistName(song.getArtists().get(0).getName());
        songTable.setAlbumCoverUrl(song.getAlbumCoverUrl());
        songTable.setPlaying(song.isPlaying());
        songTable.setAlbum(song.getAlbum());
        songTable.setPopularity(song.getPopularity());
        songTable.setService(song.getService());
        songTable.setPlaylistTable(null);
        return songTable;
    }

    public static ArrayList<Playlist> getLocalPlaylists (){
        //is there a faster way to do this?
        List<PlaylistTable> playlistTableList = SQLite.select().
                from(PlaylistTable.class).queryList();
        ArrayList<Playlist> playlists = new ArrayList<>();
        for (int i =0; i < playlistTableList.size(); i++){
            PlaylistTable playlistTable = playlistTableList.get(i);
            //search songs in this playlist table
            List<SongTable> songTableList = SQLite.select().
                    from(SongTable.class).
                    where(SongTable_Table.playlistTable_playlistId.is(playlistTable.getPlaylistId())).
                    queryList();
            ArrayList<Song> songsInPlaylist = new ArrayList<>();
            for (int j=0; j< songTableList.size(); j++){
                SongTable songTable = songTableList.get(j);
                Song song = DatabaseHelper.songFromSongTable(songTable);
                songsInPlaylist.add(song);
            }
            Playlist playlist = DatabaseHelper.playlistFromPlaylistTable(playlistTable);
            playlist.setTracks(songsInPlaylist);
//            songs.add(playlist);
            playlists.add(playlist);
        }
        return playlists;
    }


    public static Playlist makeNewLocalPlaylist(String playlistName){
        Playlist playlist = new Playlist();
        playlist.setPlaylistId(playlistName);
        playlist.setPlaylistName(playlistName);
        playlist.setTracks(new ArrayList<Song>());
        Owner owner = new Owner();
        String name = "me";
        owner.setId(name);
        owner.setName(name);
        playlist.setOwner(owner);
        playlist.setPlaylistService(Song.LOCAL);
        PlaylistTable playlistTable = makePlaylistTableRow(playlist);
        playlistTable.save();
        return playlist;
    }

    private static PlaylistTable makePlaylistTableRow(Playlist playlist){
        PlaylistTable playlistTable = new PlaylistTable();
        playlistTable.setPlaylistId(playlist.getPlaylistId());
        playlistTable.setPlaylistName(playlist.getPlaylistName());
        playlistTable.setOwnerName(playlist.getOwner().getName());
        playlistTable.setOwnerId(playlist.getOwner().getId());
        playlistTable.setImage(playlist.getImage());
        playlistTable.setPlaylistService(playlist.getPlaylistService());
        return playlistTable;
    }


    public static List<PlaylistTable> getAllPlaylists (){
        List<PlaylistTable> playlistTableList = SQLite.select().
                from(PlaylistTable.class).queryList();
        return playlistTableList;
    }

    private static List<SongTable> getAllSongs(){
        List<SongTable> songTableList = SQLite.select().
                from(SongTable.class).queryList();
        return songTableList;
    }

    public static SongTable makeNewSongTable(Song song, PlaylistTable playlistTable){
        //check that song is not already in the playlist
       SongTable songTable = songTablefromSong(song);
        songTable.setPlaylistTable(playlistTable);
        songTable.save();
        return songTable;
    }

    public void updateTestPlaylist(){
        //search for test playlist
    }

    public static void setDatabasePlayingFalse(){
        List <SongTable> songTableList = getAllSongs();
        for (int i=0; i<songTableList.size();i++){
            SongTable songTable = songTableList.get(i);
            songTable.setPlaying(false);
        }
    }

    public static void compareRowSetPlaying(Song song, boolean playing){
        if (song.getService() != Song.LOCAL) {
            SongTable songTable = songTablefromSong(song);
            List<SongTable> songTableList = getAllSongs();
            for (int i = 0; i < songTableList.size(); i++) {
                if (songTable.getUid().equals(songTableList.get(i).getUid())) {
                    songTableList.get(i).setPlaying(playing);
                    return;
                }
            }
        }
    }
}
