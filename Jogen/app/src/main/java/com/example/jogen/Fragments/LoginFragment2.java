package com.example.jogen.Fragments;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.example.jogen.Activities.MainActivity;
import com.example.jogen.R;
import com.example.jogen.ViewModels.LoginViewModel;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LoginFragment2#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoginFragment2 extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    WebView web;

    SharedPreferences pref;

    public LoginFragment2() {
        // Required empty public constructor
    }


    public static LoginFragment2 newInstance(String param1, String param2) {
        LoginFragment2 fragment = new LoginFragment2();
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
        return inflater.inflate(R.layout.fragment_login2, container, false);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onViewCreated(final View view,
                              Bundle savedInstanceState)
    {

        LoginViewModel LVM=new LoginViewModel(getActivity().getApplication());

        String authorization_url = LVM.getAuth();
        Log.d("URL: ",authorization_url);


        pref = getActivity().getSharedPreferences("AppPref", MODE_PRIVATE);



                web = getActivity().findViewById(R.id.webv);
                web.getSettings().setJavaScriptEnabled(true);
                web.loadUrl(authorization_url);

                web.setWebViewClient(new WebViewClient() {

                    boolean authComplete = false;

                    @Override
                    public void onPageStarted(WebView view, String url, Bitmap favicon){
                        super.onPageStarted(view, url, favicon);

                    }

                    @Override
                    public void onPageFinished(WebView View, String url) {
                        super.onPageFinished(View, url);
                        boolean complete=LoginViewModel.weblogintask(url,LVM,pref,getActivity(),authComplete);
                        if(complete==true){
                            view.setVisibility(android.view.View.INVISIBLE);
                            Intent myIntent = new Intent(getActivity(), MainActivity.class);
                            startActivity(myIntent);
                            getActivity().finish();
                        }
                        }
                });

            }




    }

