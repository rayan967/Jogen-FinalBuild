package com.example.jogen.util;

import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.kttdevelopment.mal4j.AccessToken;
import com.kttdevelopment.mal4j.Authorization;
import com.kttdevelopment.mal4j.MyAnimeList;
import com.kttdevelopment.mal4j.MyAnimeListAuthenticator;
import com.kttdevelopment.mal4j.anime.Anime;

import org.apache.http.auth.InvalidCredentialsException;
import org.json.JSONException;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ExecutionException;

public class MalInit {
    public String authCode, challenge, token;
    String CLIENT_ID="0d632dd13004c43ba139e22f792ef0e2";
    MyAnimeList Mal;
    String username;
    public MalInit(){

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public String getAuthUrl(){
        String codeVerifier, codeChallenge=new String();
        try {
            PkceUtil pkce = new PkceUtil();
            codeVerifier = pkce.generateCodeVerifier();

            codeChallenge = pkce.generateCodeChallange(codeVerifier);

        } catch (UnsupportedEncodingException | NoSuchAlgorithmException ex) {
            Log.e("Pkce: ",ex.toString());
        }
        challenge=codeChallenge;

        String authorization_url = MyAnimeListAuthenticator.getAuthorizationURL(CLIENT_ID, codeChallenge);
        return authorization_url;
    }

    public MyAnimeList getUser(String authCode) throws ExecutionException, InterruptedException
    {
        this.authCode=authCode;
        new Malobj().execute().get();
        return Mal;
    }

    public String getToken() {
        return token;
    }

    public String getName(){
        return username;
    }

    public class Malobj extends AsyncTask<String, Void, String> {


        @Override
        protected String doInBackground(String... params) {
            MyAnimeListAuthenticator authenticator = new MyAnimeListAuthenticator(new Authorization(CLIENT_ID, null, authCode, challenge));
            MyAnimeList mal = MyAnimeList.withOAuth2(authenticator);
            token = authenticator.getAccessToken().getToken();
            username=mal.getAuthenticatedUser().getName();
            Mal=mal;
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }
}


