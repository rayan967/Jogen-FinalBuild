package com.example.jogen.Fragments;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;

import com.example.jogen.Activities.LoginActivity;
import com.example.jogen.R;
import com.example.jogen.ViewModels.AnimeViewModel;
import com.example.jogen.util.PkceUtil;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.kttdevelopment.mal4j.MyAnimeListAuthenticator;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LoginFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoginFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    Button auth;

    private String mParam1;
    private String mParam2;

    public LoginFragment() {
    }


    public static LoginFragment newInstance(String param1, String param2) {
        LoginFragment fragment = new LoginFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onViewCreated(final View view,
                              Bundle savedInstanceState)
    {

        LoginActivity lg=(LoginActivity) getActivity();
        auth = getActivity().findViewById(R.id.login);
        auth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                if(!lg.isNetworkConnected()){
                    AlertDialog builder = new MaterialAlertDialogBuilder(getContext())
                            .setTitle("Network Error")
                            .setMessage("Please connect to the internet and try again")
                            .setPositiveButton("Okay", null)
                            .show();
                    return;
                }

                LoginFragment2 login = LoginFragment2.newInstance("","");

                FragmentTransaction ft = getParentFragmentManager().beginTransaction()
                        .replace(R.id.container, login)
                        .addToBackStack("Login");
                ft.commit();



        }



    });


}
}

