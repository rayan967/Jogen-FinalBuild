package com.example.jogen.Repositories;


import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.app.Application;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.util.Pair;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.kttdevelopment.mal4j.Authorization;
import com.kttdevelopment.mal4j.MyAnimeList;
import com.kttdevelopment.mal4j.MyAnimeListAuthenticator;
import com.kttdevelopment.mal4j.anime.Anime;
import com.kttdevelopment.mal4j.anime.AnimeListStatus;
import com.kttdevelopment.mal4j.anime.property.Studio;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;


public class SearchRepository {



    private SearchListDao mAnimeListDao;
    private LiveData<List<SearchListItem>> mAllRows;
    RecommendListDatabase db;
    Application app;

    String url="http://869c-88-209-32-73.ngrok.io/api/recb";  //Change this


    public SearchRepository(Application application, String query) {
        SearchListDatabase db = SearchListDatabase.getDatabase(application, query);
        mAnimeListDao = db.alDao();
        mAllRows = mAnimeListDao.getAllRows();
    }

    public SearchRepository(Application application, Activity activity) {
        db = RecommendListDatabase.getDatabase(application);
        app=application;
        mAnimeListDao = db.alDao();
        mAllRows = mAnimeListDao.getAllRows();
        List<SearchListItem> emptycheck = db.alDao().getAllRowsL();

        if(emptycheck.size()==0)
        new PopulateDbAsync(db,application,activity).execute();
    }

    public void refresh(Activity activity){
        db.alDao().deleteAll();
        new PopulateDbAsync(db,app,activity).execute();
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
            try {

                AnimeListRoomDatabase INSTANCE = AnimeListRoomDatabase.getDatabase(context);
                List<AnimeListItem> list =INSTANCE.alDao().getList();
                JSONObject requestbody=jsonify(list);


                OkHttpClient client = new OkHttpClient();
                client.setConnectTimeout(60, TimeUnit.SECONDS);
                client.setWriteTimeout(60, TimeUnit.SECONDS);
                client.setReadTimeout(60, TimeUnit.SECONDS);
                MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                RequestBody body = RequestBody.create(JSON, requestbody.toString());
                Request request = new Request.Builder()
                        .get()
                        .post(body)
                        .url(url)
                        .build();

                Response response = client.newCall(request).execute();
                String jsonstring=response.body().string();



                JSONObject js=new JSONObject(jsonstring);
                JSONArray results=js.getJSONArray("Recommendations");

                pref = context.getSharedPreferences("AppPref", MODE_PRIVATE);
                String token= pref.getString("token","");

                MyAnimeList mal = MyAnimeList.withToken(token);



                for(int i=0; i<results.length();i++){
                    anime_id=results.getString(i);
                    Anime anime=mal.getAnime(Integer.valueOf(anime_id));
                    anime_name=anime.getTitle();
                    episodes="TV("+anime.getEpisodes()+" Episodes)";
                    score="Score: "+String.valueOf(anime.getMeanRating());
                    InputStream in = new java.net.URL(anime.getMainPicture().getMediumURL().replace("\\/","/")).openStream();


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

    public static JSONObject jsonify(List<AnimeListItem> list)
    {
        try {
            if(list!=null){
                JSONObject js=new JSONObject();
                for (AnimeListItem item:
                        list) {
                    String id=item.getAnime_id();
                    String score=item.getRating();
                    if(score.equals("-"))
                        score="7";
                    if(score.equals("0"))
                        score="7";
                    js.put(id,score);

                }
                return js;
            }
            else{
                Log.e("JSONError", "Null List");
                return null;}
        }
        catch (JSONException je)
        {
            Log.e("JsonException: ",je.getMessage());
            return null;
        }
    }
    public static String convertStandardJSONString(String data_json) {
        data_json = data_json.replaceAll("\\\\r\\\\n", "");
        data_json = data_json.replace("\"{", "{");
        data_json = data_json.replace("}\",", "},");
        data_json = data_json.replace("}\"", "}");
        return data_json;
    }

}
