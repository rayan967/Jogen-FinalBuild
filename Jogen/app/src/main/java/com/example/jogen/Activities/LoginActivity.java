package com.example.jogen.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;

import com.example.jogen.R;
import com.example.jogen.Fragments.LoginFragment;

public class LoginActivity extends AppCompatActivity  {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences pref=getSharedPreferences("AppPref", MODE_PRIVATE);
        if(pref.getBoolean("LoggedIn",false))
        {

            Intent myIntent = new Intent(this, MainActivity.class);
            startActivity(myIntent);
            finish();
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