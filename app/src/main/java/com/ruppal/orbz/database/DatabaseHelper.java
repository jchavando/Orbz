package com.ruppal.orbz.database;

import com.ruppal.orbz.models.Artist;
import com.ruppal.orbz.models.Owner;
import com.ruppal.orbz.models.Playlist;
import com.ruppal.orbz.models.Song;

import java.util.ArrayList;

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

    private static Playlist makeFakePlaylist(){
        Owner owner = new Owner();
        owner.setId("2");
        owner.setName("testName2");
        Playlist playlist = new Playlist();
        playlist.setOwner(owner);
        playlist.setPlaylistName("testPlaylistName2");
        playlist.setImage(null);
        playlist.setTracks(null);
        playlist.setTracksUrl(null);
        playlist.setPlaylistId("2");
        return playlist;
    }

    private static PlaylistTable makePlaylistTableRow(Playlist playlist){
        PlaylistTable playlistTable = new PlaylistTable();
        playlistTable.setPlaylistId(playlist.getPlaylistId());
        playlistTable.setPlaylistName(playlist.getPlaylistName());
        playlistTable.setOwnerName(playlist.getOwner().getName());
        playlistTable.setImage(playlist.getImage());
        playlistTable.setOwnerId(playlist.getOwner().getId());
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



}
