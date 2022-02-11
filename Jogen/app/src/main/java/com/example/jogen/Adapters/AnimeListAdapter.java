package com.example.jogen.Adapters;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;
import androidx.navigation.NavController;
import androidx.recyclerview.widget.RecyclerView;


import com.example.jogen.Fragments.AnimeFragment;
import com.example.jogen.Fragments.HomeFragment;
import com.example.jogen.Fragments.LoginFragment;
import com.example.jogen.R;
import com.example.jogen.Repositories.AnimeListItem;

import java.util.List;

public class AnimeListAdapter extends RecyclerView.Adapter<AnimeListAdapter.MyViewHolder> {

    private List<AnimeListItem> alList;
    FragmentActivity activity;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView anime_name, status, rating, eps_watched, anime_id;
        public ImageView poster;
        public CardView card;



        public MyViewHolder(View view) {
            super(view);
            anime_name = (TextView) view.findViewById(R.id.anime_name);
            status = (TextView) view.findViewById(R.id.status);
            rating = (TextView) view.findViewById(R.id.rating);
            eps_watched = (TextView) view.findViewById(R.id.eps_watched);
            anime_id= view.findViewById(R.id.anime_id);
            poster = view.findViewById((R.id.poster));
            card = view.findViewById(R.id.animecard);
            card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {



                    Bundle bundle = new Bundle();
                    bundle.putString("ID", anime_id.getText().toString());
                    Navigation.findNavController(activity, R.id.nav_host_fragment_content_main).navigate(R.id.nav_anime, bundle);

                }
            });




        }
    }



    public AnimeListAdapter(List<AnimeListItem> alList, FragmentActivity activity) {
        this.alList = alList;
        this.activity=activity;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.watch_list_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        AnimeListItem TT = alList.get(position);
        holder.anime_name.setText(TT.getAnimeName());
        holder.status.setText(TT.getStatus());

        if(TT.getRating().equals("0"))
            TT.setRating("-");
        holder.rating.setText(TT.getRating());
        holder.eps_watched.setText(TT.getEpsWatched());

        String id=TT.getAnime_id();
        holder.anime_id.setText(TT.getAnime_id());

        byte[] bitmapdata = TT.getPoster();
        Bitmap bitmap = BitmapFactory.decodeByteArray(bitmapdata, 0, bitmapdata.length);
        holder.poster.setImageBitmap(bitmap);




    }

    @Override
    public int getItemCount() {
        if (alList != null)
            return alList.size();
        else
            return 0;
    }


    public void setRows(List<AnimeListItem> alList) {
        this.alList = alList;

        notifyDataSetChanged();
    }





}