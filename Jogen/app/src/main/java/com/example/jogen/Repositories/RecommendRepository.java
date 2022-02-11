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
import android.view.View;
import android.widget.FrameLayout;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.jogen.R;
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


public class RecommendRepository {

    private RecommendListADao mAnimeListDao;
    private LiveData<List<RecommendListAItem>> mAllRows;
    RecommendADatabase db;
    Application app;

    String url="http://869c-88-209-32-73.ngrok.io/api/reca"; //Change this



    public RecommendRepository(Application application, Activity activity) {
        db = RecommendADatabase.getDatabase(application);
        app=application;
        mAnimeListDao = db.alDao();
        mAllRows = mAnimeListDao.getAllRows();
        List<RecommendListAItem> emptycheck = db.alDao().getAllRowsL();

        if(emptycheck.size()==0)
        new PopulateDbAsync(db,app,activity).execute();
    }

    public void refresh(Activity activity){
        db.alDao().deleteAll();
        new PopulateDbAsync(db,app,activity).execute();
    }


    public LiveData<List<RecommendListAItem>> getAllRows() {
        return mAllRows;
    }


    private class PopulateDbAsync extends AsyncTask<Void, Void, Void> {


        private final RecommendListADao mDao;
        String anime_name,type,score, anime_id;
        byte[] poster;
        SharedPreferences pref;
        Activity activity;
        Context context;
        ProgressDialog mProgressDialog;

        PopulateDbAsync(RecommendADatabase db, Context context, Activity activity) {
            mDao = db.alDao();
            this.activity=activity;
            this.context=context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            FrameLayout fl=activity.findViewById(R.id.container2);
            fl.setVisibility(View.INVISIBLE);
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
                FrameLayout fl=activity.findViewById(R.id.container2);
                fl.setVisibility(View.VISIBLE);
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





                JSONObject json=new JSONObject(jsonstring);
                JSONArray results;

                pref = context.getSharedPreferences("AppPref", MODE_PRIVATE);
                String token= pref.getString("token","");

                MyAnimeList mal = MyAnimeList.withToken(token);

                String array[]= {"art", "sound", "vibe" ,"humor"};

                for (String typ:array) {

                    results=json.getJSONArray(typ);
                    for(int i=0; i<results.length();i++){
                        anime_id=results.getString(i);
                        type=typ;
                        Anime anime=mal.getAnime(Integer.valueOf(anime_id));
                        anime_name=anime.getTitle();
                        score="Score: "+String.valueOf(anime.getMeanRating());
                        InputStream in = new java.net.URL(anime.getMainPicture().getMediumURL().replace("\\/","/")).openStream();


                        Bitmap icon = BitmapFactory.decodeStream(in);
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        icon.compress(Bitmap.CompressFormat.JPEG, 30, stream);
                        poster = stream.toByteArray();
                        RecommendListAItem word = new RecommendListAItem(anime_name, type, score, anime_id, poster);
                        mDao.insert(word);
                    }
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

}
