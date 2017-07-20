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
    String songId;  //the uid of the song

    @Column
    String songName;

    @Column
    String songService;

    @Column
    @ForeignKey(saveForeignKeyModel = false) //todo can make this true
    PlaylistTable playlistTable;

    public void setSongId(String songId) {
        this.songId = songId;
    }

    public void setSongName(String songName) {
        this.songName = songName;
    }

    public void setSongService(String songService) {
        this.songService = songService;
    }

    public void setPlaylistTable(PlaylistTable playlistTable) {
        this.playlistTable = playlistTable;
    }
}