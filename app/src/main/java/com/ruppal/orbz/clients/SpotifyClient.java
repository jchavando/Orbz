package com.ruppal.orbz.clients;

import android.support.annotation.Nullable;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;

/**
 * Created by ruppal on 7/12/17.
 */

public class SpotifyClient extends JsonHttpResponseHandler {
    private static  String BASE_URL = "https://api.spotify.com/v1/";
    private static AsyncHttpClient client = new AsyncHttpClient();
    private String accessToken;

    public SpotifyClient (String accessToken){
        this.accessToken = accessToken;
    }

    private static String getApiUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }





    public void search (String query, String type, AsyncHttpResponseHandler handler) {
        //REQUIRES: type is a comma separated string of search types.
        //for ex: type = "album, artist, playlist, track"
        String apiUrl = getApiUrl("search");
        RequestParams params = new RequestParams();
        params.put("q", query);
        params.put("type", type);
        client.addHeader("Authorization", "Bearer " + accessToken );
        client.get(apiUrl, params, handler);
    }


    public void startAndResume(@Nullable String spotify_uri, @Nullable JSONArray uris, @Nullable Object offset, AsyncHttpResponseHandler handler){
        String apiUrl = getApiUrl("me/player/play");
        RequestParams params = new RequestParams();
        if (spotify_uri != null){
            params.put("context_uri", spotify_uri);
        }
        if (uris != null){
            params.put("uris", uris);
        }
        if (offset != null){
            params.put("offset", offset);
        }
        client.addHeader("Authorization", "Bearer " + accessToken );
        client.put(apiUrl, params, handler);
    }
}