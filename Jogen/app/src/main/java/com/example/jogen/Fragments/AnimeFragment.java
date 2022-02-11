package com.example.jogen.Fragments;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.room.Room;

import android.text.Html;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.PopupMenu.OnMenuItemClickListener;
import android.widget.Toast;

import com.example.jogen.R;
import com.example.jogen.Repositories.AnimeListRoomDatabase;
import com.example.jogen.ViewModelFactories.AnimeViewModelFactory;
import com.example.jogen.ViewModels.AnimeViewModel;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AnimeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AnimeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private AnimeViewModel mViewModel;


    // TODO: Rename and change types of parameters
    private String mParam1;


    public AnimeFragment() {
        // Required empty public constructor
    }

    public static AnimeFragment newInstance(String id) {
        AnimeFragment fragment = new AnimeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, id);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.anime_description, container, false);
    }

    @Override
    public void onViewCreated(final View view,
                              Bundle savedInstanceState) {


        mParam1=getArguments().getString("ID");
        Log.d("ANimeIDinfrag:",mParam1);
        view.setVisibility(View.INVISIBLE);

        ImageView poster =  view.findViewById(R.id.poster);
        Button watch_status = view.findViewById(R.id.containedButton);
        Button user_score = view.findViewById(R.id.containedButton2);
        Button watched_eps = view.findViewById(R.id.containedButton3);
        TextView title = view.findViewById(R.id.title);
        TextView ranked = view.findViewById(R.id.rank);
        TextView score = view.findViewById(R.id.score);
        TextView studio = view.findViewById(R.id.studio);
        TextView rated = view.findViewById(R.id.rated);
        TextView status = view.findViewById(R.id.stat);
        TextView genre = view.findViewById(R.id.genre);
        TextView description = view.findViewById(R.id.Description);



        AnimeViewModelFactory factory = new AnimeViewModelFactory(getActivity().getApplication(), mParam1, getActivity());

        mViewModel = new ViewModelProvider(this,factory).get(AnimeViewModel.class);

        mViewModel.getAnimeDetails().observe(getViewLifecycleOwner(), new Observer<Pair<HashMap<String, String>, byte[]>>() {
            @Override
            public void onChanged(@Nullable final Pair<HashMap<String, String>, byte[]> words) {
                if(words!=null) {
                    view.setVisibility(View.VISIBLE);
                    watch_status.setText(mViewModel.getWatch_status());
                    user_score.setText("Score: "+mViewModel.getUser_score());
                    watched_eps.setText("Watched: "+mViewModel.getWatched_eps());
                    title.setText(mViewModel.getTitle());
                    ranked.setText(mViewModel.getRanked());
                    score.setText(mViewModel.getScore());
                    studio.setText(mViewModel.getStudio());
                    rated.setText(mViewModel.getRated());
                    status.setText(mViewModel.getStatus());
                    genre.setText(mViewModel.getGenre());

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        description.setText(Html.fromHtml(mViewModel.getDescription(), Html.FROM_HTML_MODE_COMPACT));
                    } else {
                        description.setText(Html.fromHtml(mViewModel.getDescription()));
                    }
                    poster.setImageBitmap(mViewModel.getPoster());
                }
            }
        });


        watch_status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup=new PopupMenu(getContext(),v);
                popup.getMenuInflater().inflate(R.menu.watch_status_menu, popup.getMenu());
                popup.show();


                popup.setOnMenuItemClickListener(new OnMenuItemClickListener(){

                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem){


                        String new_status=menuItem.getTitle().toString();

                        if(!watch_status.getText().toString().equalsIgnoreCase("ADD TO LIST")) {
                            AnimeViewModel.setNewStatus(getActivity().getApplication(), new_status, mParam1);
                        }
                        else
                        {
                            mViewModel.setnNewStatus(getActivity().getApplication(), new_status, mParam1);

                        }
                        watch_status.setText(new_status);


                        return false;
                    }
                });
            }
        });



        user_score.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(watch_status.getText().toString().equalsIgnoreCase("ADD TO LIST"))
                    return;
                PopupMenu popup=new PopupMenu(getContext(),v);
                popup.getMenuInflater().inflate(R.menu.score_menu, popup.getMenu());
                popup.show();
                popup.setOnMenuItemClickListener(new OnMenuItemClickListener(){

                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem){
                        int score=0;
                        String new_score=menuItem.getTitle().toString();
                        switch (new_score)
                        {
                            case "- / No score": score=0;
                                break;
                            case "10 / Masterpiece": score=10;
                                break;
                            case "9 / Great": score=9;
                                break;
                            case "8 /  Very good": score=8;
                                break;
                            case "7 / Good": score=7;
                                break;
                            case "6 / Fine": score=6;
                                break;
                            case "5 / Average": score=5;
                                break;
                            case "4 / Bad": score=4;
                                break;
                            case "3 / Very Bad": score=3;
                                break;
                            case "2 / Horrible": score=2;
                                break;
                            case "1 / Appalling": score=1;
                                break;
                        }

                        AnimeViewModel.setNewScore(getActivity().getApplication(),score,mParam1);
                        user_score.setText("SCORE: "+String.valueOf(score));

                        return false;
                    }
                });
            }
        });

        watched_eps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(watch_status.getText().toString().equalsIgnoreCase("Plantowatch")||watch_status.getText().toString().equalsIgnoreCase("Plan to watch")||watch_status.getText().toString().equalsIgnoreCase("Add to list")){
                    Toast.makeText(getContext(),"You can only set episodes for anime you are currently watching or have completed", Toast.LENGTH_SHORT).show();
                    return;}

                LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(LAYOUT_INFLATER_SERVICE);
                final View layout = inflater.inflate(R.layout.eps_dialog, null);
                TextInputEditText eps = layout.findViewById(R.id.new_eps);

                String total=watched_eps.getText().toString().split("/")[1];
                TextView totaltext= layout.findViewById(R.id.total);
                totaltext.setText("/"+total);


                eps.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    public void onFocusChange(View v, boolean hasFocus) {
                        if (hasFocus)
                            eps.setHint(null);
                        else
                            eps.setHint("#/");
                    }
                });

                AlertDialog builder = new MaterialAlertDialogBuilder(getContext())
                        .setTitle("Episodes Watched")
                        .setView(layout)
                        .setNegativeButton("Cancel",null)
                        .setPositiveButton("Confirm", new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if(!eps.getText().toString().equals("")) {
                                    int new_eps = Integer.valueOf(eps.getText().toString());
                                    if (new_eps > Integer.valueOf(total)) {
                                        AlertDialog builder = new MaterialAlertDialogBuilder(getContext())
                                                .setTitle("Error")
                                                .setMessage("Please input valid episodes")
                                                .setPositiveButton("OK", null)
                                                .show();

                                    }
                                    else{

                                        int newEps = Integer.valueOf(eps.getText().toString());
                                        AnimeViewModel.setNewEps(getActivity().getApplication(),newEps,mViewModel.getEpisodes(),mParam1);
                                        watched_eps.setText("WATCHED: "+String.valueOf(newEps)+"/"+total);

                                    }
                                }
                                else{
                                    AlertDialog builder = new MaterialAlertDialogBuilder(getContext())
                                            .setTitle("Error")
                                            .setMessage("Please input valid episodes")
                                            .setPositiveButton("OK", null)
                                            .show();}
                            }
                        })
                        .show();
            }
        });


    }

}