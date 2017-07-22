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
    public static void addSongToTestPlaylist(Song song){
        Playlist fakePlaylist = makeFakePlaylist();
        PlaylistTable playlistTable = makePlaylistTableRow(fakePlaylist);
        playlistTable.save();
        SongTable songTable = makeSongTableRow(song, playlistTable);
        songTable.save();
    }

    public static void makeNewLocalPlaylist(String playlistName){
        Playlist playlist = new Playlist();
        playlist.setPlaylistId(playlistName);
        playlist.setPlaylistName(playlistName);
        playlist.setPlaylistService(Song.LOCAL);
        PlaylistTable playlistTable = makePlaylistTableRow(playlist);
        playlistTable.save();
    }



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

    private static PlaylistTable makePlaylistTableRow(Playlist playlist){
        PlaylistTable playlistTable = new PlaylistTable();
        playlistTable.setPlaylistId(playlist.getPlaylistId());
        playlistTable.setPlaylistName(playlist.getPlaylistName());
        if (playlist.getOwner() != null){
            playlistTable.setOwnerName(playlist.getOwner().getName());
            playlistTable.setOwnerId(playlist.getOwner().getId());
        }
        playlistTable.setImage(playlist.getImage());
        playlistTable.setPlaylistService(playlist.getPlaylistService());
        return playlistTable;
    }

    private static SongTable makeSongTableRow(Song song, PlaylistTable playlistTable){
        SongTable songTable = new SongTable();
        songTable.setAlbum(song.getAlbum());
        songTable.setTitle(song.getTitle());
        songTable.setUid(song.getUid());
        Artist artist= song.getArtists().get(0);
        songTable.setArtistName(artist.getName());
        songTable.setAlbumCoverUrl(song.getAlbumCoverUrl());
        songTable.setPlaying(song.isPlaying());
        songTable.setPopularity(song.getPopularity());
        songTable.setService(song.getService());
        songTable.setDuration_ms(song.getDuration_ms());
        songTable.setPlaylistTable(playlistTable);
        return songTable;
    }

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

    public static void getLocalPlaylists (ArrayList<Object> songs){
        //is there a faster way to do this?
        List<PlaylistTable> playlistTableList = SQLite.select().
                from(PlaylistTable.class).queryList();
        for (int i =0; i < playlistTableList.size(); i++){
            PlaylistTable playlistTable = playlistTableList.get(i);
            //search songs in this playlist table
            List<SongTable> songTableList = SQLite.select().
                    from(SongTable.class).
//                    where(PlaylistTable_Table.playlistName.is(playlistTable.getPlaylistName())).
        queryList();
            ArrayList<Song> songsInPlaylist = new ArrayList<>();
            for (int j=0; j< songTableList.size(); j++){
                SongTable songTable = songTableList.get(j);
                Song song = DatabaseHelper.songFromSongTable(songTable);
                songsInPlaylist.add(song);
            }
            Playlist playlist = DatabaseHelper.playlistFromPlaylistTable(playlistTable);
            playlist.setTracks(songsInPlaylist);
            songs.add(playlist);
        }
    }

    public static List<PlaylistTable> getAllPlaylists (){
        List<PlaylistTable> playlistTableList = SQLite.select().
                from(PlaylistTable.class).queryList();
        return playlistTableList;
    }



    public void updateTestPlaylist(){
        //search for test playlist


    }

}
