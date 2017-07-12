package com.ruppal.orbz.models;

import org.json.JSONObject;

import static com.ruppal.orbz.models.Song.GOOGLE_PLAY;
import static com.ruppal.orbz.models.Song.SOUNDCLOUD;
import static com.ruppal.orbz.models.Song.SPOTIFY;
import static com.ruppal.orbz.models.Song.YOUTUBE;

/**
 * Created by ruppal on 7/12/17.
 */

public class Artist {
    public String uid;
    public String name;

    public Artist fromJSON(String service, JSONObject object) {
        switch (service) {
            case SPOTIFY:
                return parseSpotifyJSON(object);
            break;
            case SOUNDCLOUD:
                return parseSoundcloudJSON(object);
            break;
            case GOOGLE_PLAY:
                return parseGooglePlayJSON(object);
            break;
            case YOUTUBE:
                return parseYoutubeJSON(object);
            break;
        }


    }
    private Song parseSpotifyJSON(JSONObject object){

    }
    private Song parseSoundcloudJSON(JSONObject object){

    }
    private Song parseGooglePlayJSON(JSONObject object){

    }
    private Song parseYoutubeJSON(JSONObject object){

    }
}
