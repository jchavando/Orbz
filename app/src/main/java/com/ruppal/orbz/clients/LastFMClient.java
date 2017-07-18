package com.ruppal.orbz.clients;

import android.content.Context;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import static de.umass.util.StringUtilities.md5;

/**
 * Created by jchavando on 7/17/17.
 */

public class LastFMClient extends JsonHttpResponseHandler {

    private static  String BASE_URL = "http://ws.audioscrobbler.com/";
    private static AsyncHttpClient client = new AsyncHttpClient();
    private static String API_KEY = "b08816ba8eca0e4591fad667a6aea410";
    private static String API_SIG;

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
        API_SIG = md5("api_keyb08816ba8eca0e4591fad667a6aea410" + "methodauth.getMobileSession" +
                "password"+password+"username"+username+"8e2a01466c4f4d03d6d781854c8ebf7a");
        String apiUrl = getApiUrl("auth.getMobileSession");
        RequestParams params = new RequestParams();
        params.put("username", username);
        params.put("password", password);
        params.put("api_key", API_KEY);
        params.put("api_sig", API_SIG);
        client.get(apiUrl, params, handler);
    }

}
