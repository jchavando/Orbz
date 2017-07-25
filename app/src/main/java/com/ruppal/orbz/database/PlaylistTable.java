package com.ruppal.orbz.database;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

/**
 * Created by ruppal on 7/20/17.
 */

@Table(database = OrbzDatabase.class)
//@Parcel(analyze={PlaylistTable.class})   // add Parceler annotation here
public class PlaylistTable extends BaseModel {
    @Column
    @PrimaryKey
    String playlistId;

    @Column
    String playlistName;

    @Column
    String ownerName;

    @Column
    String ownerId;

    @Column
    String image;

    @Column
    String playlistService;

    //dont refrence tracks like in the playlist class beacuse the foregin key handles that


    public void setPlaylistId(String playlistId) {
        this.playlistId = playlistId;
    }

    public void setPlaylistName(String playlistName) {
        this.playlistName = playlistName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getPlaylistService() {
        return playlistService;
    }

    public void setPlaylistService(String playlistService) {
        this.playlistService = playlistService;
    }

    public String getPlaylistId() {
        return playlistId;
    }

    public String getPlaylistName() {
        return playlistName;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public String getImage() {
        return image;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }
}