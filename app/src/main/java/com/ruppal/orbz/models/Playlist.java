package com.ruppal.orbz.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.ArrayList;

import static com.ruppal.orbz.models.Song.SPOTIFY;

/**
 * Created by ruppal on 7/19/17.
 */

@Parcel
public class Playlist {
    Owner owner;
    String playlistName;
    String image;
    String tracksUrl;
    ArrayList<Song> tracks;
    String playlistId;
    String playlistService;

    public static Playlist fromJSON(String service, JSONObject object) throws JSONException {
        switch (service) {
            case SPOTIFY:
                return parseSpotifyJSON(object);
            //break;
//            case SOUNDCLOUD:
//                return parseSoundcloudJSON(object);
//                break;
//            case GOOGLE_PLAY:
//                return parseGooglePlayJSON(object);
//            //break;
//            case YOUTUBE:
//                return parseYoutubeJSON(object);
            //break;
            default:
                return null;
        }
    }

    public void setSongTracks (ArrayList<Song> songs){
        this.tracks = songs;
    }

    private static Playlist parseSpotifyJSON(JSONObject object) throws JSONException {
        //REQUIRES: passing in an item from "items"
        Playlist playlist = new Playlist();
        playlist.playlistId = object.getString("id");
        JSONArray images = object.getJSONArray("images");
        playlist.image = images.getJSONObject(0).getString("url");
        JSONObject ownerObj = object.getJSONObject("owner");
        playlist.owner = Owner.fromJSON(SPOTIFY, ownerObj);
        playlist.playlistName = object.getString("name");
        playlist.tracksUrl = object.getJSONObject("tracks").getString("href");
        playlist.playlistService = SPOTIFY;
        return playlist;
    }

    private static ArrayList<String> addSpotifyImages(JSONArray images) throws JSONException {
        ArrayList<String> imagesUrls = new ArrayList<>();
        for (int i=0; i<images.length(); i++){
            JSONObject image = images.getJSONObject(i);
            String url = image.getString("url");
            imagesUrls.add(url);
        }
        return imagesUrls;
    }

    public Owner getOwner() {
        return owner;
    }

    public String getPlaylistName() {
        return playlistName;
    }

    public String getImage() {
        return image;
    }

    public String getPlaylistService() {
        return playlistService;
    }

    public void setPlaylistService(String playlistService) {this.playlistService = playlistService;}

    public String getTracksUrl() {
        return tracksUrl;
    }

    public ArrayList<Song> getTracks() {
        return tracks;
    }

    public String getPlaylistId() {
        return playlistId;
    }

    public void setOwner(Owner owner) {
        this.owner = owner;
    }

    public void setPlaylistName(String playlistName) {
        this.playlistName = playlistName;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setTracksUrl(String tracksUrl) {
        this.tracksUrl = tracksUrl;
    }

    public void setTracks(ArrayList<Song> tracks) {
        this.tracks = tracks;
    }

    public void setPlaylistId(String playlistId) {
        this.playlistId = playlistId;
    }
}