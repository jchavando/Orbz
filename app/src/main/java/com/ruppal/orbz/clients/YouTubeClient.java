package com.ruppal.orbz.clients;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/**
 * Created by jchavando on 7/12/17.
 */

public class YouTubeClient {

    public final static String YOUTUBE_BASE_URL = "https://www.youtube.com/watch?v=";
    private static  String API_BASE_URL = "https://www.googleapis.com/youtube/v3";
    private static AsyncHttpClient client = new AsyncHttpClient();
    public static String accessToken;

    public void setAccessToken(String accessToken) {this.accessToken = accessToken;}

    private static String getApiUrl(String relativeUrl) {

        return API_BASE_URL + relativeUrl;
    }

    public void search (String query, AsyncHttpResponseHandler handler) {
        //REQUIRES: type is a comma separated string of search types.
        //for ex: type = "album, artist, playlist, track"
        String apiUrl = getApiUrl("/search");
        RequestParams params = new RequestParams();
        params.put("part", "snippet");
        params.put("type", "video");
        params.put("maxResults", 20);
//        params.put("videoCategoryId", "Music");
        params.put("q", query);
//        params.put("videoDefinition", "any");
        client.addHeader("Authorization", "Bearer " + accessToken);
        client.get(apiUrl, params, handler);
    }
}
