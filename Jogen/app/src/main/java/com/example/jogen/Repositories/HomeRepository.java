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
import android.widget.ExpandableListAdapter;

import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.kttdevelopment.mal4j.Authorization;
import com.kttdevelopment.mal4j.MyAnimeList;
import com.kttdevelopment.mal4j.MyAnimeListAuthenticator;
import com.kttdevelopment.mal4j.anime.Anime;
import com.kttdevelopment.mal4j.anime.AnimeListStatus;
import com.kttdevelopment.mal4j.anime.property.Studio;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;


public class HomeRepository {
    private AnimeListDao mAnimeListDao;
    private LiveData<List<AnimeListItem>> mAllRows;
    private MutableLiveData<Pair<HashMap<String,String>,byte[] >> animedetails;
    public MutableLiveData<String> Fare;
    Application app;
    AnimeListRoomDatabase db;
    String anime_name, anime_status, rating, eps_watched, anime_id;
    byte[] poster;

    public HomeRepository(Application application, Activity activity) {
        db = AnimeListRoomDatabase.getDatabase(application);
        app=application;
        mAnimeListDao = db.alDao();
        mAllRows = mAnimeListDao.getAllRows();

        List<AnimeListItem> emptycheck=db.alDao().getAllRowsL();

        if(emptycheck.size()==0)
        new PopulateDbAsync(db,app,activity).execute();
    }

    public void refresh(Activity activity)
    {
        mAnimeListDao.deleteAll();
        new PopulateDbAsync(db,app,activity).execute();
    }

    public HomeRepository(String anime_name, String anime_status, String rating, String eps_watched, String anime_id, byte[] poster ) {
        this.anime_name = anime_name;
        this.anime_status = anime_status;
        this.rating = rating;
        this.eps_watched = eps_watched;
        this.poster = poster;
        this.anime_id=anime_id;
    }

    public HomeRepository(Application app, String anime_id, Activity activity)
    {
        this.app=app;
        this.anime_id=anime_id;
        animedetails =  new MutableLiveData<>();
        insertAnimeDetails(anime_id, activity);
    }


    public LiveData<List<AnimeListItem>> getAllRows() {
        return mAllRows;
    }

    public void insert(AnimeListItem tt) {
        new insertAsyncTask(mAnimeListDao).execute(tt);
    }

    public void insertAnimeDetails(String id, Activity activity)
    {
        new AnimeDetails(activity).execute();

    }

    public MutableLiveData<Pair<HashMap<String, String>,byte[]>> getAnimeDetails()
    {
        return animedetails;
    }




    public class AnimeDetails extends AsyncTask<String, Void, String> {

        String anime_name,status,score, watched;
        String ranked, rating, studio, rated, stat, genre, description, episodes;
        HashMap<String,String> animedetail;
        Pair<HashMap<String,String>,byte[] > pair;
        byte[] poster;
        private ProgressDialog mProgressDialog;
        SharedPreferences pref;
        Activity activity;

        public AnimeDetails(Activity activity){
            this.activity=activity;
        }


        @Override
        protected String doInBackground(String... params) {
            try {

                pref = app.getSharedPreferences("AppPref", MODE_PRIVATE);

                String token= pref.getString("token","");


                MyAnimeList mal = MyAnimeList.withToken(token);


                Anime anime=mal.getAnime(Integer.valueOf(anime_id));
                AnimeListStatus al=anime.getListStatus();

                anime_name=anime.getTitle();


                if(al.getStatus()!=null)
                    status=al.getStatus().toString();
                else
                    status="Add to List";
                if(al.getScore()!=null)
                    score=al.getScore().toString();
                else
                    score="-";

                if(al.getWatchedEpisodes()!=null)
                    watched=al.getWatchedEpisodes().toString()+"/"+anime.getEpisodes().toString();
                else
                    watched="0"+"/"+anime.getEpisodes().toString();

                episodes=anime.getEpisodes().toString();

                InputStream in = new java.net.URL(anime.getMainPicture().getMediumURL()).openStream();
                Bitmap icon = BitmapFactory.decodeStream(in);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                icon.compress(Bitmap.CompressFormat.PNG, 100, stream);
                poster = stream.toByteArray();

                ranked="#"+anime.getRank().toString();
                rating=anime.getMeanRating().toString();

                Studio[] st=anime.getStudios();
                studio=st[0].getName();


                rated=anime.getRating().toString();
                stat=anime.getStatus().toString();
                Log.d("Anime stat:",stat);



                if(anime.getGenres().length>3)
                {
                    genre=anime.getGenres()[0].getName()+", "+anime.getGenres()[1].getName()+", "+anime.getGenres()[2].getName();                }

                else {
                    genre=anime.getGenres()[0].getName();
                }

                description=anime.getSynopsis();



                animedetail =new HashMap<>();
                animedetail.put("anime_name",anime_name);
                animedetail.put("anime_status",status);
                animedetail.put("score",score);
                animedetail.put("watched",watched);
                animedetail.put("ranked",ranked);
                animedetail.put("rating",rating);
                animedetail.put("studio",studio);
                animedetail.put("rated",rated);
                animedetail.put("stat",stat);
                animedetail.put("genre",genre);
                animedetail.put("description",description);
                animedetail.put("episodes",episodes);


                pair=new Pair<>(animedetail,poster);







            }

            catch(Exception e) {

                e.printStackTrace();
            }
            return null;

        }

        @Override
        protected void onPostExecute(String result) {
            animedetails.setValue(pair);
            if (this.isCancelled()) {
                result = null;
                return;
            }

            if (mProgressDialog != null) {
                mProgressDialog.dismiss();
            }

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            mProgressDialog = ProgressDialog.show(activity, "Retrieving Data", "Please wait ...");
            mProgressDialog.setCanceledOnTouchOutside(false); // main method that force user cannot click outside
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }




    private static class insertAsyncTask extends AsyncTask<AnimeListItem, Void, Void> {

        private AnimeListDao mAsyncTaskDao;

        insertAsyncTask(AnimeListDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final AnimeListItem... params) {
            mAsyncTaskDao.insert(params[0]);
            return null;
        }
    }



    private static class PopulateDbAsync extends AsyncTask<Void, Void, Void> {


        private final AnimeListDao mDao;
        String anime_name,status,rating, watched, anime_id;
        byte[] poster;
        SharedPreferences pref;
        Context context;
        Activity activity;
        private ProgressDialog mProgressDialog;


        PopulateDbAsync(AnimeListRoomDatabase db, Context context, Activity activity) {
            mDao = db.alDao();
            this.context=context;
            this.activity=activity;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            mProgressDialog = ProgressDialog.show(activity, "Retrieving Data", "Please wait ...");
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


                pref = context.getSharedPreferences("AppPref", MODE_PRIVATE);
                String token= pref.getString("token","");

                MyAnimeList mal = MyAnimeList.withToken(token);

                int j=0;
                while(true)
                {
                    j++;
                    String url = "https://api.jikan.moe/v3/user/" + mal.getAuthenticatedUser().getName() + "/animelist/all/"+String.valueOf(j);



                    OkHttpClient client = new OkHttpClient();

                    Request request = new Request.Builder()
                            .get()
                            .url(url)
                            .build();

                    Response response = client.newCall(request).execute();
                    String jsonstring = response.body().string();





                    JSONObject js = new JSONObject(jsonstring);
                    JSONArray jsonArray = new JSONArray();


                    jsonArray = js.getJSONArray("anime");
                    if(jsonArray.isNull(0))
                        throw new Exception("End of Page");


                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsa = jsonArray.getJSONObject(i);
                        status = jsa.getString("watching_status");
                        switch (status) {
                            case "1":
                                status = "Watching";
                                break;
                            case "2":
                                status = "Completed";
                                break;
                            case "3":
                                status = "On Hold";
                                break;
                            case "4":
                                status = "Dropped";
                                break;
                            case "6":
                                status = "Plan to Watch";
                                break;
                        }
                        rating = jsa.getString("score");
                        if (rating.equals("0"))
                            rating = "-";
                        JSONObject anime = jsa;
                        anime_name = anime.getString("title");
                        anime_id = anime.getString("mal_id");
                        watched = jsa.getString("watched_episodes") + "/" + anime.getString("total_episodes");
                        InputStream in = new java.net.URL(anime.getString("image_url").replace("\\/", "/")).openStream();


                        Bitmap icon = BitmapFactory.decodeStream(in);
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        icon.compress(Bitmap.CompressFormat.WEBP, 30, stream);
                        poster = stream.toByteArray();

                        AnimeListItem word = new AnimeListItem(anime_name, status, rating, watched, anime_id, poster);
                        mDao.insert(word);
                    }
                }
            }

            catch(Exception e) {
                e.printStackTrace();
                return null;
            }

        }
    }

}