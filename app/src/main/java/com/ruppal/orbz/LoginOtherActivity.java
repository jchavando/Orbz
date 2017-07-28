package com.ruppal.orbz;

import android.Manifest;
import android.accounts.Account;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.ruppal.orbz.clients.LastFMClient;
import com.ruppal.orbz.clients.SpotifyClient;
import com.ruppal.orbz.fragments.LoginLastFMFragment;
import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;
import com.spotify.sdk.android.player.ConnectionStateCallback;
import com.spotify.sdk.android.player.Error;
import com.spotify.sdk.android.player.Player;
import com.spotify.sdk.android.player.PlayerEvent;
import com.spotify.sdk.android.player.Spotify;
import com.spotify.sdk.android.player.SpotifyPlayer;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

import cz.msebera.android.httpclient.Header;

public class LoginOtherActivity extends AppCompatActivity implements SpotifyPlayer.NotificationCallback, ConnectionStateCallback, View.OnClickListener, GoogleApiClient.OnConnectionFailedListener,
LoginLastFMFragment.LastFMListener{


    // TODO: Replace with your client ID
    String spotifyClientId;
    // TODO: Replace with your redirect URI
    String spotifyRedirectUri;
    //openLastFMFragment();
    private Player mPlayer;
    private static final int REQUEST_CODE = 1337;
    private static final int RC_SIGN_IN = 9001;
    private static int PERMISSION_REQUEST_CODE= 1470;
    private String TAG = "loginOther";
    private String SCOPES = "https://www.googleapis.com/auth/youtube " + "https://www.googleapis.com/auth/youtube.readonly";
    Button btLoginSpotify;
    GoogleApiClient mGoogleApiClient;
    //SignInButton googleSignInButton;
    Button googleSignInButton;
    String spotifyAccessToken;
    String googleAccessToken;
    Button btLastFMLogin;
    String username;
    String password;
    Button btLocalLogin;
    LastFMClient lastFMClient;
    Context context;
    int colorTransparent = 0x80FFFFFF;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_other);

        spotifyClientId = getString(R.string.spotify_client_id);
        spotifyRedirectUri = getString(R.string.spotify_redirect_uri);
        //googleSignInButton = (SignInButton) findViewById(R.id.sign_in_button);
        googleSignInButton = (Button) findViewById(R.id.sign_in_button);
        btLastFMLogin = (Button) findViewById(R.id.btLastFMLogin);
        btLocalLogin = (Button) findViewById(R.id.btPlayer);


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail().requestIdToken(getString(R.string.googlePlay_client_id))
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        //btLocalLogin.setOnClickListener(this);
        googleSignInButton.setOnClickListener(this);
        //googleSignInButton.setColorScheme(COLOR_LIGHT);
        btLastFMLogin.setOnClickListener(this);
        lastFMClient = new LastFMClient();




    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        // Check if result comes from the correct activity
        if (requestCode == REQUEST_CODE) {
            AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, intent);
            if (response.getType() == AuthenticationResponse.Type.TOKEN) {
                spotifyAccessToken = response.getAccessToken();
                SpotifyClient spotifyClient = new SpotifyClient();
                spotifyClient.setAccessToken(spotifyAccessToken);
                btLoginSpotify = (Button) findViewById(R.id.btLoginSpotify);
                btLoginSpotify.setBackgroundResource(R.drawable.clicked_border);
                //googleSignInButton.setBackgroundResource(R.drawable.rounded);

                Toast.makeText(this, "Successfully logged in to Spotify!", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == RC_SIGN_IN && resultCode == Activity.RESULT_OK) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(intent);
            try {
                handleSignInResult(result);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (GoogleAuthException e) {
                e.printStackTrace();
            }
        }
    }

    public void openLastFMFragment() {
        FragmentManager fm = getSupportFragmentManager();
        LoginLastFMFragment lastFMLogin = LoginLastFMFragment.newInstance("some_title");
        lastFMLogin.show(fm, "lastfm_login");
    }

    @Override
    protected void onDestroy() {
        Spotify.destroyPlayer(this);
        super.onDestroy();
    }

    @Override
    public void onPlaybackEvent(PlayerEvent playerEvent) {
        Log.d("LoginOtherActivity", "Playback event received: " + playerEvent.name());
        switch (playerEvent) {
            // Handle event type as necessary
            default:
                break;
        }
    }

    @Override
    public void onPlaybackError(Error error) {
        Log.d("LoginOtherActivity", "Playback error received: " + error.name());
        switch (error) {
            // Handle error type as necessary
            default:
                break;
        }
    }

    @Override
    public void onLoggedIn() {
        Log.d("LoginOtherActivity", "User logged in");
        //on logged in, change button color
        btLoginSpotify = (Button) findViewById(R.id.btLoginSpotify);
        btLoginSpotify.setBackgroundResource(R.drawable.clicked_border);
        Toast.makeText(this, "signed in to Spotify", Toast.LENGTH_SHORT).show();

        //mPlayer.playUri(null, "spotify:track:2TpxZ7JUBn3uw46aR7qd6V", 0, 0);
    }

    @Override
    public void onLoggedOut() {
        Log.d("LoginOtherActivity", "User logged out");
    }

    @Override
    public void onLoginFailed(Error error) {
        Log.d("LoginOtherActivity", "Login failed");
    }

    @Override
    public void onTemporaryError() {
        Log.d("LoginOtherActivity", "Temporary error occurred");
    }

    @Override
    public void onConnectionMessage(String message) {
        Log.d("LoginOtherActivity", "Received connection message: " + message);
    }

    public void onClickSpotifyLogin(View view){
        AuthenticationRequest.Builder builder = new AuthenticationRequest.Builder(spotifyClientId,
                AuthenticationResponse.Type.TOKEN,
                spotifyRedirectUri);

        //TODO: if we want more permissions, need to modify scopes
        //https://developer.spotify.com/web-api/using-scopes/
        builder.setScopes(new String[]{"user-read-private", "streaming"});
        AuthenticationRequest request = builder.build();

        AuthenticationClient.openLoginActivity(this, REQUEST_CODE, request);
    }

    public void onClickDone(View view){
        if (checkPermission()) {
            Intent i = new Intent(this, MainActivity.class);
            i.putExtra(MainActivity.SPOTIFY_ACCESS_TOKEN, spotifyAccessToken);
            i.putExtra(MainActivity.GOOGLE_ACCESS_TOKEN, googleAccessToken);
//        i.putExtra(MainActivity.SPOTIFY_PLAYER, Parcels.wrap(mPlayer));
            startActivity(i);
        }
        else{
            Toast.makeText(context, "make sure you have granted the app storage permissions " +
                    "and that you are connected to the internet", Toast.LENGTH_LONG).show();
        }
    }

    public void onClickPlayer(View view) {
        //Intent player = new Intent(this, PlayerActivity.class);
        //startActivity(player);
        Toast.makeText(this, "accessed local music", Toast.LENGTH_SHORT).show();
        btLocalLogin.setBackgroundResource(R.drawable.clicked_border);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_button:
                signIn();
                break;
            case R.id.btLastFMLogin:
                openLastFMFragment();
                break;
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void handleSignInResult(GoogleSignInResult result) throws IOException, GoogleAuthException {
        Log.d("Google Sign in", "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            final Account account = acct.getAccount();
            AsyncTask<Void, Void, String> task = new AsyncTask<Void, Void, String>() {
                @Override
                protected String doInBackground(Void... params) {
                    String token = null;
                    try {
                        token = GoogleAuthUtil.getToken(LoginOtherActivity.this, account, "oauth2:" + SCOPES);
                    } catch (IOException transientEx) {
                        // Network or server error, try later
                        Log.e(TAG, transientEx.toString());
                    } catch (UserRecoverableAuthException e) {
                        // Recover (with e.getIntent())
                        Log.e(TAG, e.toString());
                        Intent recover = e.getIntent();
                        startActivityForResult(recover, RC_SIGN_IN);
                    } catch (GoogleAuthException authEx) {
                        // The call is not ever expected to succeed
                        // assuming you have already verified that
                        // Google Play services is installed.
                        Log.e(TAG, authEx.toString());
                    }

                    return token;
                }

                @Override
                protected void onPostExecute(String token) {
                    Log.i(TAG, "Access token retrieved:" + token);
                    googleAccessToken = token;
                }

            };
            task.execute();
            updateUI(true);

            //mStatusTextView.setText(getString(R.string.signed_in_fmt, acct.getDisplayName()));
        } else {
            // Signed out, show unauthenticated UI.
            updateUI(false);
        }
    }

    private void updateUI(boolean isLogin){
        if(isLogin){
            Toast.makeText(this, "signed in to Google", Toast.LENGTH_SHORT).show();
            googleSignInButton.setBackgroundResource(R.drawable.clicked_border);


        } else {
            Toast.makeText(this, "did not sign in", Toast.LENGTH_SHORT).show();

        }
    }

    @Override
    public void onFinishDialog(String username, String password) {
        this.username = username;
        this.password = password;
        loginToLastFMAccount();
    }

    public void loginToLastFMAccount(){
        lastFMClient.login(username, password, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("LastFM", "success");
                btLastFMLogin.setBackgroundResource(R.drawable.clicked_border);

                //Toast.makeText(this, "Successfully logged in to Last.fm!", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                Log.d("LastFM", "success");
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                Log.d("LastFM", "success");
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.d("LastFm", "failure");
                Log.e("LastFm", responseString);
                Log.e("LastFm", throwable.toString());
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("LastFm", "failure");
                Log.e("LastFm", errorResponse.toString());
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                Log.d("LastFm", "failure");
                Log.e("LastFm", errorResponse.toString());
            }
        });
    }


    private boolean checkPermission() {
        //Check for READ_EXTERNAL_STORAGE access, using ContextCompat.checkSelfPermission()//
        int readStorage = ContextCompat.checkSelfPermission(LoginOtherActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE);
        int internet = ContextCompat.checkSelfPermission(LoginOtherActivity.this, Manifest.permission.INTERNET);
        //If the app does have this permission, then return true//
        return readStorage == PackageManager.PERMISSION_GRANTED && internet == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.INTERNET}, PERMISSION_REQUEST_CODE);

    }


}
