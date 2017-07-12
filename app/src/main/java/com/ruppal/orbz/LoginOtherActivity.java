package com.ruppal.orbz;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;
import com.spotify.sdk.android.player.ConnectionStateCallback;
import com.spotify.sdk.android.player.Error;
import com.spotify.sdk.android.player.Player;
import com.spotify.sdk.android.player.PlayerEvent;
import com.spotify.sdk.android.player.Spotify;
import com.spotify.sdk.android.player.SpotifyPlayer;

import java.io.IOException;

public class LoginOtherActivity extends AppCompatActivity implements SpotifyPlayer.NotificationCallback, ConnectionStateCallback, View.OnClickListener, GoogleApiClient.OnConnectionFailedListener{

    // TODO: Replace with your client ID
    String spotifyClientId;
    // TODO: Replace with your redirect URI
    String spotifyRedirectUri;

    private Player mPlayer;
    private static final int REQUEST_CODE = 1337;
    private static final int RC_SIGN_IN = 9001;
    Button btLoginSpotify;
    GoogleApiClient mGoogleApiClient;
    SignInButton googleSignInButton;
    String spotifyAccessToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_other);
        spotifyClientId = getString(R.string.spotify_client_id);
        spotifyRedirectUri = getString(R.string.spotify_redirect_uri);
        googleSignInButton = (SignInButton) findViewById(R.id.sign_in_button);


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail().requestIdToken(getString(R.string.googlePlay_client_id))
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();


        googleSignInButton.setOnClickListener(this);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        // Check if result comes from the correct activity
        if (requestCode == REQUEST_CODE) {
            AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, intent);
            if (response.getType() == AuthenticationResponse.Type.TOKEN) {
                spotifyAccessToken = response.getAccessToken();
//                Config playerConfig = new Config(this, response.getAccessToken(), CLIENT_ID);
                btLoginSpotify = (Button) findViewById(R.id.btLoginSpotify);
                btLoginSpotify.setBackgroundColor(Color.GREEN);
                Toast.makeText(this, "Successfully logged in to Spotify!", Toast.LENGTH_LONG).show();
//                Spotify.getPlayer(playerConfig, this, new SpotifyPlayer.InitializationObserver() {
//                    @Override
//                    public void onInitialized(SpotifyPlayer spotifyPlayer) {
//                        mPlayer = spotifyPlayer;
//                        mPlayer.addConnectionStateCallback(LoginOtherActivity.this);
//                        mPlayer.addNotificationCallback(LoginOtherActivity.this);
//                    }
//
//                    @Override
//                    public void onError(Throwable throwable) {
//                        Log.e("LoginOtherActivity", "Could not initialize player: " + throwable.getMessage());
//                    }
//                });
            }
        } else if (requestCode == RC_SIGN_IN) {
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
        btLoginSpotify.setBackgroundColor(Color.GREEN);
        Toast.makeText(this, "Successfully logged in to Spotify!", Toast.LENGTH_LONG).show();

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

//    @Override
//    public void onLoginFailed(int i) {
//        Log.d("MainActivity", "Login failed");
//    }

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
        Intent i = new Intent(this, MainActivity.class);
        i.putExtra(MainActivity.SPOTIFY_ACCESS_TOKEN, spotifyAccessToken);
//        i.putExtra(MainActivity.SPOTIFY_PLAYER, Parcels.wrap(mPlayer));
        startActivity(i);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_button:
                signIn();
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
            String magicString ="oauth2:server:client_id" + getString(R.string.googlePlay_client_id);
            String authCOde = GoogleAuthUtil.getToken(this, acct.getEmail() , magicString);
            Log.d("GooglePlay", authCOde);

            //mStatusTextView.setText(getString(R.string.signed_in_fmt, acct.getDisplayName()));
            updateUI(true);
        } else {
            // Signed out, show unauthenticated UI.
            updateUI(false);
        }
    }

    private void updateUI(boolean isLogin){
        if(isLogin){
            Toast.makeText(this, "signed in", Toast.LENGTH_LONG).show();
            googleSignInButton.setBackgroundColor(Color.GREEN);

        } else {
            Toast.makeText(this, "did not sign in", Toast.LENGTH_LONG).show();

        }
    }
}
