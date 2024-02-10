package com.jogen.Repositories;


import static android.content.Context.MODE_PRIVATE;
import com.jogen.R;

import android.app.Activity;
import android.app.Application;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.List;


public class SearchRepository {



    private SearchListDao mAnimeListDao;
    private LiveData<List<SearchListItem>> mAllRows;
    RecommendListDatabase db;
    Application app;

    String url="http://869c-88-209-32-73.ngrok.io/api/recb";  //Change this


    public SearchRepository(Application application, String query, Activity activity) {
        SearchListDatabase db = SearchListDatabase.getDatabase(application, query, activity);
        mAnimeListDao = db.alDao();
        mAllRows = mAnimeListDao.getAllRows();
    }

    public SearchRepository(Application application, Activity activity) {
        db = RecommendListDatabase.getDatabase(application);
        app=application;
        mAnimeListDao = db.alDao();
        mAllRows = mAnimeListDao.getAllRows();
        List<SearchListItem> emptycheck = db.alDao().getAllRowsL();

        if(emptycheck.size()==0) {
            new PopulateDbAsync(db, application, activity).execute();
        }
    }

    public void refresh(Activity activity){
        db.alDao().deleteAll();
        new PopulateDbAsync(db,app,activity).execute();
    }

    private boolean isNetworkAvailable() {
        Context context = app;
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    public LiveData<List<SearchListItem>> getAllRows() {
        return mAllRows;
    }


    private class PopulateDbAsync extends AsyncTask<Void, Void, Void> {


        private final SearchListDao mDao;
        String anime_name,episodes,score, anime_id;
        byte[] poster;
        SharedPreferences pref;
        Activity activity;
        Context context;
        ProgressDialog mProgressDialog;


        PopulateDbAsync(RecommendListDatabase db, Context context, Activity activity) {
            mDao = db.alDao();
            this.activity=activity;
            this.context=context;
        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            mProgressDialog = ProgressDialog.show(activity, "Generating Recommendations", "Please wait ...");
            mProgressDialog.setCanceledOnTouchOutside(false); // main method that force user cannot click outside
        }

        @Override
        protected void onPostExecute(Void result) {
            if (this.isCancelled()) {
                result = null;
                return;
            }

            if (mProgressDialog != null) {
                mProgressDialog.dismiss();
            }

        }

        @Override
        protected Void doInBackground(final Void... params) {

            if (isNetworkAvailable()) {
                try {
                    pref = context.getSharedPreferences("AppPref", MODE_PRIVATE);
                    String token = pref.getString("token", "");
                    OkHttpClient client = new OkHttpClient();


                    String url = "https://api.myanimelist.net/v2/anime/suggestions?fields=num_episodes,mean,media_type&offset=0&limit=20";

                    Request request = new Request.Builder()
                            .url(url)
                            .addHeader("Authorization", token)
                            .build();

                    Response response = client.newCall(request).execute();

                    if (!response.isSuccessful()) {
                        // Handle different HTTP errors here. For example:
                        switch (response.code()) {
                            case 401:
                                activity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(context, "Session expired, please log in again.", Toast.LENGTH_SHORT).show();
                                        NavController navController = Navigation.findNavController(activity, R.id.nav_host_fragment_content_main);
                                        navController.navigate(R.id.nav_logout);
                                    }
                                });
                                break;
                            case 503:
                                activity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(context, "MyAnimeList is down, please try later.", Toast.LENGTH_SHORT).show();
                                    }
                                });

                                break;
                            default:
                                activity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(context, "Unexpected error occurred, please try later.", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                break;
                        }
                        return null;
                    }


                    String jsonString = response.body().string();


                    JSONObject js = new JSONObject(jsonString);
                    JSONArray results = js.getJSONArray("data");

                    for (int i = 0; i < results.length(); i++) {
                        JSONObject node = results.getJSONObject(i).getJSONObject("node");
                        String animeName = node.getString("title");
                        String animeId = String.valueOf(node.getInt("id"));
                        int numEpisodes = node.getInt("num_episodes");

                        double mean;
                        if(node.has("mean"))
                            mean = node.getDouble("mean");
                        else
                            continue;

                        String mediaType = node.getString("media_type").toUpperCase();
                        String episodeText = numEpisodes == 1 ? "Episode" : "Episodes";
                        String episodes = mediaType + " (" + numEpisodes + " " + episodeText + ")";
                        String score = "Score: " + mean;

                        String imageUrl = node.getJSONObject("main_picture").getString("medium");
                        InputStream in = new URL(imageUrl.replace("\\/", "/")).openStream();
                        Bitmap icon = BitmapFactory.decodeStream(in);
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        icon.compress(Bitmap.CompressFormat.WEBP, 30, stream);
                        byte[] poster = stream.toByteArray();

                        SearchListItem word = new SearchListItem(animeName, episodes, score, animeId, poster);
                        mDao.insert(word);
                    }
                } catch (Exception e) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(context, "Unexpected error occurred, please try later.", Toast.LENGTH_SHORT).show();
                        }
                    });
                    e.printStackTrace();
                }
                return null;
            } else{
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, "Please connect to the internet and try again.", Toast.LENGTH_SHORT).show();
                    }
                });
                return null;}
        }
    }



}
