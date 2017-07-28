package com.ruppal.orbz.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;

/**
 * Created by jchavando on 7/3/17.
 */

@ParseClassName("Message")
public class Message extends ParseObject {
    public static final String USER_ID_KEY = "userId";
    public static final String TITLE = "title";
    public static final String ARTISTNAME = "artistname";
    public static final String SERVICE = "service";
    public static final String UID = "uid";
    public static final String ARTISTID = "artistID";
    public static final String ALBUM = "album";
    public static final String POPULARITY = "popularity";
    public static final String DURATION = "duration";

    public void setUserId(String userId) {
        put(USER_ID_KEY, userId);
    }

    public void setTitle(String title) {put(TITLE, title);}

    public void setArtistName(String artist) {put(ARTISTNAME, artist);}

    public void setService(String service) {put(SERVICE, service);}

    public void setUid(String uid) {put(UID, uid);}

    public void setArtistId(String artistId) {put(ARTISTID, artistId);}

    public void setAlbum(String album) {put(ALBUM, album);}

    public void setPopularity(int popularity) {put(POPULARITY, popularity);}

    public  void setDuration(int duration) {put(DURATION, duration);}

    public String getUSERID() {
        return getString(USER_ID_KEY);
    }

    public String getTITLE() {return getString(TITLE);}

    public String getARTISTNAME() {return getString(ARTISTNAME);}

    public String getSERVICE() {return getString(SERVICE);}

    public String getUID() {return getString(UID);}

    public String getARTISTID() {return getString(ARTISTID);}

    public String getALBUM() {return getString(ALBUM);}

    public int getDURATION() {return getInt(DURATION);}

    public int getPopularity() {return getInt(POPULARITY);}

}
