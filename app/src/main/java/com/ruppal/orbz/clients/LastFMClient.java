package com.ruppal.orbz.clients;

import android.content.Context;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/**
 * Created by jchavando on 7/17/17.
 */

public class LastFMClient extends JsonHttpResponseHandler {

    private static  String BASE_URL = "http://ws.audioscrobbler.com/";
    private static AsyncHttpClient client = new AsyncHttpClient();

    private static String getApiUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }
    public LastFMClient(Context context) {

    }

    //TODO
    public void search (String title, AsyncHttpResponseHandler handler) {
        //REQUIRES: type is a comma separated string of search types.
        //for ex: type = "album, artist, playlist, track"
        String apiUrl = getApiUrl("track.search");
        RequestParams params = new RequestParams();
        params.put("track", title);
        //client.addHeader("Authorization", "Bearer " + accessToken );
        client.get(apiUrl, params, handler);
    }

    public void login(String username, String password, AsyncHttpResponseHandler handler){
        String apiUrl = getApiUrl("auth.getMobileSession");
        RequestParams params = new RequestParams();
        params.put("username", username);
        params.put("password", password);
        client.get(apiUrl, params, handler);
    }

}
