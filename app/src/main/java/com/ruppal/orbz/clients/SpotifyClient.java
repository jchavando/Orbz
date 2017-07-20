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
    public static String accessToken;

    public void setAccessToken(String accessToken) {this.accessToken = accessToken;}


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

    public void getPlaylistTimeline(AsyncHttpResponseHandler handler) {
        //fill in with playlist api calls
        String apiUrl = getApiUrl("statuses/mentions_timeline.json");
        // Can specify query string params directly or through RequestParams.
        RequestParams params = new RequestParams();
        params.put("count", 25); //specifies number of records to retrieve
        params.put("since_id", 1); //returns results with an ID greater than 1

        client.get(apiUrl, params, handler);
    }

    public void getMyPlaylists(AsyncHttpResponseHandler handler){
        String apiUrl = getApiUrl("me/playlists");
        //can add params to get different number of results
        client.addHeader("Authorization", "Bearer " + accessToken );
        client.get(apiUrl, null, handler);
    }

    public void getPlayListTracks (String fullUrl, AsyncHttpResponseHandler handler){
        //REQUIRES FULL URL
        client.addHeader("Authorization", "Bearer " + accessToken );
        client.get(fullUrl, null, handler);
    }
}
