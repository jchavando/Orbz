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

    public void setPlaylistId(String playlistId) {
        this.playlistId = playlistId;
    }

    public void setPlaylistName(String playlistName) {
        this.playlistName = playlistName;
    }
}