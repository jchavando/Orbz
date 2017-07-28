package com.ruppal.orbz;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseObject;
import com.raizlabs.android.dbflow.config.FlowConfig;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.ruppal.orbz.models.Message;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

/**
 * Created by ruppal on 7/20/17.
 */

public class OrbzApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        ParseObject.registerSubclass(Message.class);

        // Use for monitoring Parse network traffic
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        // Can be Level.BASIC, Level.HEADERS, or Level.BODY
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        builder.networkInterceptors().add(httpLoggingInterceptor);

        // set applicationId and server based on the values in the Heroku settings.
        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("orbzqueue") // Replaced `myAppId`
                .clientKey("8344d7de-1b6f-4dfb-8d84-61941a182577")  // set explicitly unless clientKey is explicitly configured on Parse server
                .clientBuilder(builder) // this builder comes from the OkHttpClient.
                .server("http://orbz.herokuapp.com/parse/").build()); // Replace https://my-parse-app-url.herokuapp.com/parse

    // This instantiates DBFlow
        FlowManager.init(new FlowConfig.Builder(this).build());
        // add for verbose logging
        // FlowLog.setMinimumLoggingLevel(FlowLog.Level.V);
    }
}
