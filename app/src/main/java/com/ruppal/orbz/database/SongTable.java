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
    @ForeignKey(saveForeignKeyModel = false) //todo can make this true
    PlaylistTable playlistTable;
}