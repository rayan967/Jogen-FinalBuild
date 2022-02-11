package com.example.jogen.Repositories;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.kttdevelopment.mal4j.MyAnimeList;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

@Database(entities = {SearchListItem.class}, version = 1, exportSchema = false)
public abstract class SearchListDatabase extends RoomDatabase {
    public abstract SearchListDao alDao();

    private static SearchListDatabase INSTANCE;

    public static SearchListDatabase getDatabase(final Context context, String query) {
            synchronized (SearchListDatabase.class) {

                RoomDatabase.Callback sRoomDatabaseCallback =
                        new RoomDatabase.Callback() {

                            @Override
                            public void onOpen(@NonNull SupportSQLiteDatabase db) {
                                super.onOpen(db);
                                new PopulateDbAsync(INSTANCE, context, query).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
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

        PopulateDbAsync(SearchListDatabase db, Context context, String query) {
            mDao = db.alDao();
            this.query=query;
            this.context=context;
        }

        @Override
        protected Void doInBackground(final Void... params) {
            try {

                String url="https://api.jikan.moe/v3/search/anime?q="+query+"&page=1";
                Log.d("Request: ",url);

                OkHttpClient client = new OkHttpClient();

                Request request = new Request.Builder()
                        .get()
                        .url(url)
                        .build();

                Response response = client.newCall(request).execute();
                String jsonstring=response.body().string();
                Log.d("Response ",jsonstring);

                JSONObject js=new JSONObject(jsonstring);
                JSONArray results=js.getJSONArray("results");

                for(int i=0; i<results.length();i++){
                    JSONObject anime=results.getJSONObject(i);
                    anime_name=anime.getString("title");
                    episodes="TV("+anime.getString("episodes")+" Episodes)";
                    score="Score: "+anime.getString("score");
                    anime_id=anime.getString("mal_id");
                    InputStream in = new java.net.URL(anime.getString("image_url").replace("\\/","/")).openStream();


                    Bitmap icon = BitmapFactory.decodeStream(in);
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    icon.compress(Bitmap.CompressFormat.JPEG, 30, stream);
                    poster = stream.toByteArray();
                    SearchListItem word = new SearchListItem(anime_name, episodes, score, anime_id, poster);
                    mDao.insert(word);
                }
            }

            catch(Exception e) {


                e.printStackTrace();
            }
            return null;
        }
    }

}