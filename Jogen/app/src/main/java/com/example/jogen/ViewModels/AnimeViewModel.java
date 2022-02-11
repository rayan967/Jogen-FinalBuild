package com.example.jogen.ViewModels;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.app.Application;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.util.Pair;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import androidx.room.Room;

import com.example.jogen.Repositories.AnimeListDao;
import com.example.jogen.Repositories.AnimeListItem;
import com.example.jogen.Repositories.AnimeListRoomDatabase;
import com.example.jogen.Repositories.HomeRepository;
import com.kttdevelopment.mal4j.MyAnimeList;
import com.kttdevelopment.mal4j.anime.property.AnimeStatus;

import java.util.HashMap;

public class AnimeViewModel extends AndroidViewModel {

    MutableLiveData<Pair<HashMap<String, String>,byte[]>> animedetails;
    private HomeRepository mRepository;
    Application app;
    String watch_status, user_score, watched_eps, title , ranked, score, studio, rated, status, genre, description, episodes;
    Bitmap poster;


    public AnimeViewModel(Application app, String anime_id, Activity activity){
        super(app);

        this.app=app;
        mRepository= new HomeRepository(app, anime_id, activity);
        animedetails=mRepository.getAnimeDetails();
    }


    public MutableLiveData<Pair<HashMap<String, String>,byte[]>> getAnimeDetails()
    {
        return animedetails;
    }

    public String getWatch_status() {
        watch_status=animedetails.getValue().first.get("anime_status");
        return watch_status;
    }

    public String getUser_score() {

        user_score=animedetails.getValue().first.get("score");
        return user_score;
    }

    public String getWatched_eps() {
        watched_eps=animedetails.getValue().first.get("watched");
        return watched_eps;
    }

    public String getTitle() {
        title=animedetails.getValue().first.get("anime_name");
        return title;
    }

    public String getRanked() {
        ranked=animedetails.getValue().first.get("ranked");
        return ranked;
    }

    public String getScore() {
        score=animedetails.getValue().first.get("rating");
        return score;
    }

    public String getStudio() {
        studio=animedetails.getValue().first.get("studio");
        return studio;
    }

    public String getRated() {
        rated=animedetails.getValue().first.get("rated");
        return rated;
    }

    public String getStatus() {
        status=animedetails.getValue().first.get("stat");
        return status;
    }

    public String getGenre() {
        genre=animedetails.getValue().first.get("genre");
        return genre;
    }

    public String getDescription() {
        description=animedetails.getValue().first.get("description");
        return description;
    }

    public Bitmap getPoster() {
        Pair<HashMap<String, String>,byte[]> p = animedetails.getValue();
        byte[] bitmapdata=p.second;
        Bitmap bitmap = BitmapFactory.decodeByteArray(bitmapdata, 0, bitmapdata.length);
        poster=bitmap;
        return poster;
    }

    public byte[] getPoster_() {
        Pair<HashMap<String, String>,byte[]> p = animedetails.getValue();
        byte[] bitmapdata=p.second;
        return bitmapdata;
    }

    public String getEpisodes(){
        Pair<HashMap<String, String>,byte[]> p = animedetails.getValue();
        episodes=animedetails.getValue().first.get("episodes");
        return episodes;
    }



    public static void setNewStatus(Application app, String newStatus, String id)
    {
        SharedPreferences pref = app.getSharedPreferences("AppPref", MODE_PRIVATE);
        String token= pref.getString("token","");
        String new_Status=newStatus.replaceAll("\\s+","");
        if(new_Status.equalsIgnoreCase("PlanToWatch"))
            new_Status="PlanToWatch";

        new updateAsyncTask(id,token,new_Status).execute();

        AnimeListRoomDatabase INSTANCE = AnimeListRoomDatabase.getDatabase(app);





        if(new_Status.equalsIgnoreCase("OnHold")){
            new_Status="On Hold";}

        if(new_Status.equalsIgnoreCase("PlanToWatch")){
            new_Status="Plan to Watch";}


        INSTANCE.alDao().setStatus(newStatus, id);


    }


    public void setnNewStatus(Application app, String newStatus, String id)
    {
        SharedPreferences pref = app.getSharedPreferences("AppPref", MODE_PRIVATE);
        String token= pref.getString("token","");
        String new_Status=newStatus.replaceAll("\\s+","");
        if(new_Status.equalsIgnoreCase("PlanToWatch"))
            new_Status="PlanToWatch";

        if(new_Status.equalsIgnoreCase("OnHold"))
            new_Status="OnHold";

        new updateAsyncTask(id,token,new_Status).execute();

        AnimeListRoomDatabase INSTANCE = AnimeListRoomDatabase.getDatabase(app);

        INSTANCE.alDao().setStatus(newStatus, id);


        if(new_Status.equalsIgnoreCase("OnHold")){
            new_Status="On Hold";}
        if(new_Status.equalsIgnoreCase("PlanToWatch"))
            new_Status="Plan to Watch";
        AnimeListItem word = new AnimeListItem(getTitle(), new_Status, getUser_score(), getWatched_eps(), id, getPoster_());
        INSTANCE.alDao().insert(word);


    }

    public static void setNewScore(Application app, int score, String id){
        SharedPreferences pref = app.getSharedPreferences("AppPref", MODE_PRIVATE);
        String token= pref.getString("token","");


        new updateScoreTask(id,token,score).execute();

        AnimeListRoomDatabase INSTANCE = AnimeListRoomDatabase.getDatabase(app);

        INSTANCE.alDao().setScore(String.valueOf(score), id);

    }


    public static void setNewEps(Application app, int eps, String episodes, String id){
        SharedPreferences pref = app.getSharedPreferences("AppPref", MODE_PRIVATE);
        String token= pref.getString("token","");


        new updateEpsTask(id,token,eps).execute();

        AnimeListRoomDatabase INSTANCE = AnimeListRoomDatabase.getDatabase(app);

        INSTANCE.alDao().setEps(String.valueOf(eps)+"/"+episodes, id);

    }




    private static class updateAsyncTask extends AsyncTask<String, Void, Void> {

        private String id, token, new_Status;

        updateAsyncTask(String id, String token, String new_Status) {
            this.id = id;
            this.token = token;
            this.new_Status=new_Status;
        }

        @Override
        protected Void doInBackground(final String... params) {
            MyAnimeList mal = MyAnimeList.withToken(token);
            mal.updateAnimeListing(Integer.valueOf(id)).status(AnimeStatus.valueOf(new_Status)).update();
            return null;
        }
    }

    private static class updateScoreTask extends AsyncTask<String, Void, Void> {

        private String id, token;
        int score;

        updateScoreTask(String id, String token, int score) {
            this.id = id;
            this.token = token;
            this.score=score;
        }

        @Override
        protected Void doInBackground(final String... params) {
            MyAnimeList mal = MyAnimeList.withToken(token);
            mal.updateAnimeListing(Integer.valueOf(id)).score(score).update();
            return null;
        }
    }


    private static class updateEpsTask extends AsyncTask<String, Void, Void> {

        private String id, token;
        int eps;

        updateEpsTask(String id, String token, int eps) {
            this.id = id;
            this.token = token;
            this.eps=eps;
        }

        @Override
        protected Void doInBackground(final String... params) {
            MyAnimeList mal = MyAnimeList.withToken(token);
            mal.updateAnimeListing(Integer.valueOf(id)).episodesWatched(eps).update();
            return null;
        }
    }

}
