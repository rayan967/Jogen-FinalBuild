package com.example.jogen.ViewModels;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.app.Application;
import android.content.SharedPreferences;
import android.graphics.Picture;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.AndroidViewModel;

import com.example.jogen.util.MalInit;
import com.google.gson.Gson;
import com.kttdevelopment.mal4j.MyAnimeList;

import java.util.concurrent.ExecutionException;

public class LoginViewModel extends AndroidViewModel {

    String codeChallenge, authorization_url;
    MalInit mInit=new MalInit();
    Application app;
    public MyAnimeList mal;
    boolean authenticated=false;

    public LoginViewModel(Application application)
    {
        super(application);
        app=application;

    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public String getAuth(){
        authorization_url= mInit.getAuthUrl();
        return authorization_url;
    }

    public void setUser(String authCode) throws InterruptedException, ExecutionException
    {
        mal=mInit.getUser(authCode);
        SharedPreferences pref=app.getSharedPreferences("AppPref", MODE_PRIVATE);
        SharedPreferences.Editor edit = pref.edit();
        edit.putString("authcode", mInit.authCode);
        edit.putString("challenge", mInit.challenge);
        edit.putString("Username", mInit.getName());
        edit.putString("token", mInit.getToken());

        edit.putBoolean("LoggedIn",true);
        edit.commit();
        
    }

    public static boolean weblogintask(String url, LoginViewModel LVM, SharedPreferences pref, FragmentActivity activity, boolean authComplete){
        String authCode;
        if (url.contains("?code=") && authComplete != true) {
            Uri uri = Uri.parse(url);
            authCode = uri.getQueryParameter("code");
            authComplete = true;


            SharedPreferences.Editor edit = pref.edit();
            edit.putString("Code", authCode);
            edit.commit();
            activity.getSupportFragmentManager().popBackStack();

            try{
                LVM.setUser(authCode);
            }
            catch (InterruptedException e)
            {
                Log.e("Interrupted Exception: ", e.toString());
            }
            catch (ExecutionException e){
                Log.e("Execution Exception: ", e.toString());

            }

            LVM.authenticated=true;
            return LVM.authenticated;
        }else if(url.contains("error=access_denied")){
            Log.i("", "ACCESS_DENIED_HERE");
            authComplete = true;
            Toast.makeText(LVM.app.getApplicationContext(), "Error occured, please re-login", Toast.LENGTH_SHORT).show();
            activity.getSupportFragmentManager().popBackStack();
            return LVM.authenticated;
        }
        return LVM.authenticated;
    }
}
