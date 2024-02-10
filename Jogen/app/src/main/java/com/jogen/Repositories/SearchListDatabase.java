package com.jogen.Repositories;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.jogen.R;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;

@Database(entities = {SearchListItem.class}, version = 1, exportSchema = false)
public abstract class SearchListDatabase extends RoomDatabase {
    public abstract SearchListDao alDao();

    private static SearchListDatabase INSTANCE;

    public static SearchListDatabase getDatabase(final Context context, String query, Activity activity) {
            synchronized (SearchListDatabase.class) {

                RoomDatabase.Callback sRoomDatabaseCallback =
                        new RoomDatabase.Callback() {

                            @Override
                            public void onOpen(@NonNull SupportSQLiteDatabase db) {
                                super.onOpen(db);
                                new PopulateDbAsync(INSTANCE, context, activity, query).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                            }
                        };

                INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                        SearchListDatabase.class, "searchlist_database")
                        .allowMainThreadQueries()
                        .fallbackToDestructiveMigration()
                        .addCallback(sRoomDatabaseCallback)
                        .build();
                INSTANCE.alDao().deleteAll();
            }
            return INSTANCE;

    }


    private static class PopulateDbAsync extends AsyncTask<Void, Void, Void> {


        private final SearchListDao mDao;
        String query;
        String anime_name,episodes,score, rank, anime_id;
        byte[] poster;
        SharedPreferences pref;
        Context context;
        Activity activity;

        PopulateDbAsync(SearchListDatabase db, Context context, Activity activity, String query) {
            mDao = db.alDao();
            this.query=query;
            this.context=context;
            this.activity = activity;

        }

        private boolean isNetworkAvailable() {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }

        @Override
        protected Void doInBackground(final Void... params) {

            if(isNetworkAvailable())
            {
            try {
                pref = context.getSharedPreferences("AppPref", MODE_PRIVATE);

                String token= pref.getString("token","");
                String url = "https://api.myanimelist.net/v2/anime?fields=num_episodes,mean,media_type&q=" + query + "&limit=10";

                OkHttpClient client = new OkHttpClient();

                Request request = new Request.Builder()
                        .url(url)
                        .addHeader("Authorization", token)
                        .build();

                Response response = client.newCall(request).execute();


                if (!response.isSuccessful()) {
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
                Log.d("Response ", jsonString);

                JSONObject js = new JSONObject(jsonString);
                JSONArray results = js.getJSONArray("data");

                for (int i = 0; i < results.length(); i++) {
                    JSONObject node = results.getJSONObject(i).getJSONObject("node");
                    String animeName = node.getString("title");
                    double mean;
                    if(node.has("mean"))
                        mean = node.getDouble("mean");
                    else
                        continue;
                    String animeId = String.valueOf(node.getInt("id"));
                    Log.d("id: ",animeId);
                    int numEpisodes = node.getInt("num_episodes");

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
                        Toast.makeText(context, "Unexpected error occurred.", Toast.LENGTH_SHORT).show();
                    }
                });
                e.printStackTrace();
            }
            return null;
        }
            else
            {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, "Please connect to the internet and try again.", Toast.LENGTH_SHORT).show();
                    }
                });
                return null;

            }
        }

    }

}