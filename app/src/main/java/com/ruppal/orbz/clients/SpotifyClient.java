package com.ruppal.orbz.clients;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/**
 * Created by ruppal on 7/12/17.
 */

public class SpotifyClient {
    private static  String BASE_URL = "https://api.spotify.com/v1/";
    private static AsyncHttpClient client = new AsyncHttpClient();

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
        client.get(apiUrl, params, handler);
    }



}
