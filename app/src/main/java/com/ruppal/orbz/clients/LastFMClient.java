package com.ruppal.orbz.clients;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


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

    private String encodeUTF8(String s) {
        byte ptext[] = s.getBytes();
        String value="";
        try {
            value = new String(ptext, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return value;

    }

    public void login(String username, String password, AsyncHttpResponseHandler handler){
        API_SIG = md5("api_key"+ encodeUTF8(API_KEY) + "methodauth.getMobileSession" +
                    "password"+ encodeUTF8(password) +"username"+ encodeUTF8(username)+secret);

        String apiUrl = getApiUrl("auth.getMobileSession");
        RequestParams params = new RequestParams();
        //String authToken = md5(username + md5(password));
        params.put("api_key", API_KEY);
        params.put("method", "auth.getMobileSession");
        params.put("password", password);
        params.put("username", username);
        params.put("api_sig", API_SIG);
        //params.put("token", "8ATdA43CxzLtrSju6Nk0t68dYf7mIDcB");


      //  params.put("format", "json");
        client.post(apiUrl, params, handler);

    }
    private static final String md5(final String password) {
        try {

            MessageDigest digest = java.security.MessageDigest
                    .getInstance("MD5");
            digest.update(password.getBytes());
            byte messageDigest[] = digest.digest();

            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < messageDigest.length; i++) {
                String h = Integer.toHexString(0xFF & messageDigest[i]);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }


}
