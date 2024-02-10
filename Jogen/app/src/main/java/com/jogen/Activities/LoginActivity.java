package com.jogen.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.jogen.R;
import com.jogen.Fragments.LoginFragment;
import com.jogen.ViewModels.LoginViewModel;

public class LoginActivity extends AppCompatActivity  {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        Uri uri = intent.getData();


        SharedPreferences pref=getSharedPreferences("AppPref", MODE_PRIVATE);
        if(pref.getBoolean("LoggedIn",false))
        {

            Intent myIntent = new Intent(this, MainActivity.class);
            startActivity(myIntent);
            finish();
        }
        else if(uri != null){
            boolean authComplete = false;

            if(uri.toString().contains("error=access_denied")){
                Log.i("", "ACCESS_DENIED_HERE");
                Toast.makeText(this, "Access denied, please re-login", Toast.LENGTH_SHORT).show();
                setContentView(R.layout.activity_login);

                LoginFragment login = LoginFragment.newInstance("", "");

                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, login)
                        .commit();
            }

            else if(uri.toString().startsWith("jogen://oauth2redirect")) {
                LoginViewModel LVM = new LoginViewModel(this.getApplication());
                String challenge = pref.getString("challenge", "");
                LVM.setCodeChallenge(challenge);

                SharedPreferences.Editor editor = pref.edit();
                editor.remove("challenge");
                editor.apply();

                boolean complete = LoginViewModel.weblogintask(uri.toString(), LVM, pref, this, authComplete);
                if (complete == true) {
                    Intent myIntent = new Intent(this, MainActivity.class);
                    startActivity(myIntent);
                    this.finish();
                }
            }

            else {
                Toast.makeText(this, "Unknown error occured, please try again", Toast.LENGTH_SHORT).show();
                setContentView(R.layout.activity_login);

                LoginFragment login = LoginFragment.newInstance("", "");

                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, login)
                        .commit();
            }
        }
        else {
            setContentView(R.layout.activity_login);

            LoginFragment login = LoginFragment.newInstance("", "");

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, login)
                    .commit();

        }



    }



    public boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        return (cm.getActiveNetworkInfo() != null);
    }



}