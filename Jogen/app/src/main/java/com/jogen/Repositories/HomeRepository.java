package com.jogen.Repositories;

import static android.content.Context.MODE_PRIVATE;

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
import android.util.Log;
import android.util.Pair;
import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.jogen.R;
import com.kttdevelopment.mal4j.MyAnimeList;
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
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


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


    private boolean isNetworkAvailable() {
        Context context = app;
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
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

            if(isNetworkAvailable())
            {

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
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(activity, "Unexpected error, try again later.", Toast.LENGTH_SHORT).show();
                            NavController navController = Navigation.findNavController(activity, R.id.nav_host_fragment_content_main);
                            navController.navigate(R.id.nav_home);
                        }
                    });
                }
                return null;
            }

            else {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(activity, "Please connect to the internet and try again.", Toast.LENGTH_SHORT).show();
                    }
                });
                return null;
            }

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



    private class PopulateDbAsync extends AsyncTask<Void, Void, Void> {


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

            if(isNetworkAvailable())
            {
                try {
                    pref = context.getSharedPreferences("AppPref", MODE_PRIVATE);
                    String token = pref.getString("token", "");
                    int offset = 0;
                    int limit = 100;
                    boolean morePagesAvailable = true;

                    OkHttpClient client = new OkHttpClient();
                    Map<String, String> statusMap = new HashMap<>();
                    statusMap.put("watching", "Watching");
                    statusMap.put("completed", "Completed");
                    statusMap.put("on_hold", "On Hold");
                    statusMap.put("dropped", "Dropped");
                    statusMap.put("plan_to_watch", "Plan to Watch");

                    while (morePagesAvailable) {
                        String url = "https://api.myanimelist.net/v2/users/@me/animelist?fields=list_status,num_episodes&offset=" + offset + "&limit=" + limit;

                        Request request = new Request.Builder()
                                .get()
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
                        Log.d("tag", jsonString);
                        JSONObject js = new JSONObject(jsonString);
                        JSONArray jsonArray = js.getJSONArray("data");

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject animeObj = jsonArray.getJSONObject(i).getJSONObject("node");
                            JSONObject statusObj = jsonArray.getJSONObject(i).getJSONObject("list_status");

                            String animeName = animeObj.getString("title");
                            String animeId = String.valueOf(animeObj.getInt("id"));
                            String status = statusMap.get(statusObj.getString("status"));
                            int scoreValue = statusObj.getInt("score");
                            String score = scoreValue == 0 ? "-" : String.valueOf(scoreValue);
                            int totalEpisodes = animeObj.getInt("num_episodes");
                            String watched = statusObj.getInt("num_episodes_watched") + "/" + totalEpisodes;
                            String imageUrl = animeObj.getJSONObject("main_picture").getString("medium");

                            InputStream in = new URL(imageUrl.replace("\\/", "/")).openStream();
                            Bitmap icon = BitmapFactory.decodeStream(in);
                            ByteArrayOutputStream stream = new ByteArrayOutputStream();
                            icon.compress(Bitmap.CompressFormat.WEBP, 30, stream);
                            byte[] poster = stream.toByteArray();

                            // Constructing the AnimeListItem object
                            AnimeListItem word = new AnimeListItem(animeName, status, score, watched, animeId, poster);
                            mDao.insert(word);
                        }

                        // Check if there is a next page
                        JSONObject paging = js.optJSONObject("paging");
                        if (paging != null && paging.has("next")) {
                            offset += limit;
                        } else {
                            morePagesAvailable = false;
                        }
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
            }
            else
            {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(activity, "Please connect to the internet and try again.", Toast.LENGTH_SHORT).show();
                    }
                });
                return null;
            }

        }
    }

}