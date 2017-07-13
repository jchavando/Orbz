package com.ruppal.orbz.clients;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/**
 * Created by jchavando on 7/12/17.
 */

public class GooglePlayClient {

    private static  String BASE_URL = "https://play.google.com/music/services/"; //https://play.google.com/store/music/album/JAY_Z_4_44?id=Bhlerbl7hnpkofs7fdnt6ljnx64&hl=en
    private static AsyncHttpClient client = new AsyncHttpClient();


    private static String getApiUrl(String relativeUrl) {
        return BASE_URL + relativeUrl ;
    }


    public void loginToGoogleMusic(String accessToken, AsyncHttpResponseHandler handler){
        String apiUrl = "https://play.google.com/music/listen?hl=en&u=0";
        client.addHeader("Authorization", "GoogleLogin auth=" + accessToken);
        client.post(apiUrl, null, handler);

    }

    public void search (String accessToken, String query, AsyncHttpResponseHandler handler) {
        //REQUIRES: type is a comma separated string of search types.
        //for ex: type = "album, artist, playlist, track"
        String apiUrl = getApiUrl("search");
        RequestParams params = new RequestParams();
        params.put("q", query);
        client.addHeader("Authorization", "GoogleLogin auth=" + accessToken);
        client.post(apiUrl, params, handler);
    }
}
