package com.ruppal.orbz.database;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

/**
 * Created by ruppal on 7/20/17.
 */

@Table(database = OrbzDatabase.class)
//@Parcel(analyze={SongTable.class})   // add Parceler annotation here
public class SongTable extends BaseModel {
    // ... field definitions that map to columns go here ...

    @Column
    @PrimaryKey
    String uid;  //the uid of the song

    @Column
    String title;

    @Column
    String artistName;

    @Column
    String albumCoverUrl;

    @Column
    boolean playing;

    @Column
    String album;

    @Column
    int popularity;

    @Column
    String service;

    @Column
    int duration_ms;

    @Column
    String data;

    @Column
    @ForeignKey(saveForeignKeyModel = false)
    public PlaylistTable playlistTable;


    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public void setAlbumCoverUrl(String albumCoverUrl) {
        this.albumCoverUrl = albumCoverUrl;
    }

    public void setPlaying(boolean playing) {
        this.playing = playing;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public void setPopularity(int popularity) {
        this.popularity = popularity;
    }

    public void setService(String service) {
        this.service = service;
    }

    public void setDuration_ms(int duration_ms) {
        this.duration_ms = duration_ms;
    }

    public PlaylistTable getPlaylistTable() {
        return playlistTable;
    }

    public void setPlaylistTable(PlaylistTable playlistTable) {
        this.playlistTable = playlistTable;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getUid() {
        return uid;
    }

    public String getTitle() {
        return title;
    }

    public String getArtistName() {
        return artistName;
    }

    public String getAlbumCoverUrl() {
        return albumCoverUrl;
    }

    public boolean isPlaying() {
        return playing;
    }

    public String getAlbum() {
        return album;
    }

    public int getPopularity() {
        return popularity;
    }

    public String getService() {
        return service;
    }

    public int getDuration_ms() {
        return duration_ms;
    }

}