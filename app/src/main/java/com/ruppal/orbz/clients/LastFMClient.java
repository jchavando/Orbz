package com.ruppal.orbz.clients;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import static de.umass.util.StringUtilities.md5;

/**
 * Created by jchavando on 7/17/17.
 */

public class LastFMClient extends JsonHttpResponseHandler {

    private static  String BASE_URL = "https://ws.audioscrobbler.com/2.0/?";
    private static AsyncHttpClient client = new AsyncHttpClient();
    private static String API_KEY = "b08816ba8eca0e4591fad667a6aea410";
    private static String API_SIG;
    private static String secret = "8e2a01466c4f4d03d6d781854c8ebf7a";

    private static String getApiUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }
//    public LastFMClient(Context context) {
//
//    }

    //TODO
    public void search (String title, AsyncHttpResponseHandler handler) {
        //REQUIRES: type is a comma separated string of search types.
        //for ex: type = "album, artist, playlist, track"
        String apiUrl = getApiUrl("track.search");
        RequestParams params = new RequestParams();
        params.put("track", title);
        params.put("api_key", API_KEY);
        //client.addHeader("Authorization", "Bearer " + accessToken );
        client.get(apiUrl, params, handler);
    }

    public void login(String username, String password, AsyncHttpResponseHandler handler){
        try {
            API_SIG = md5("api_key"+URLEncoder.encode(API_KEY, "utf-8") + "methodauth.getMobileSession" +
                    "password"+ URLEncoder.encode(password, "utf-8")+"username"+
                    URLEncoder.encode(username, "utf-8")+secret);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String apiUrl = getApiUrl("auth.getSession");
        RequestParams params = new RequestParams();
        //String authToken = md5(username + md5(password));
        params.put("username", username);
        params.put("password", password);
        //params.put("token", "8ATdA43CxzLtrSju6Nk0t68dYf7mIDcB");
        params.put("api_key", API_KEY);
        params.put("api_sig", API_SIG);
      //  params.put("format", "json");
        client.post(apiUrl, params, handler);

    }

}
