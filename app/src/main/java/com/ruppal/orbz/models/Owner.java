package com.ruppal.orbz.models;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

/**
 * Created by ruppal on 7/19/17.
 */

@Parcel
public class Owner {
    String name;
    String id;

    public static Owner fromJSON(String service, JSONObject object) throws JSONException{
        switch (service){
            case Song.SPOTIFY:
                return parseSpotifyJson(object);
            default:
                return null;
        }
    }

    private static Owner parseSpotifyJson(JSONObject object) throws JSONException {
        //REQUIRES: an owner object from spotify
        Owner owner = new Owner();
        owner.name = object.getString("display_name");
        owner.id = object.getString("id");
        return owner;

    }
}
