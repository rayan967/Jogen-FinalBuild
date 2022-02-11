package com.example.jogen.Fragments;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.jogen.Adapters.RecommendListAAdapter;
import com.example.jogen.Adapters.SearchListAdapter;
import com.example.jogen.R;
import com.example.jogen.Repositories.RecommendListAItem;
import com.example.jogen.Repositories.SearchListItem;
import com.example.jogen.ViewModels.DiscoverViewModel;
import com.example.jogen.ViewModels.RecommendAViewModel;
import com.example.jogen.databinding.FragmentGalleryBinding;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RecommendAFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RecommendAFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private FragmentGalleryBinding binding;


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private RecommendAViewModel mViewModel;

    public RecommendAFragment() {
        // Required empty public constructor
    }

    public static RecommendAFragment newInstance(String param1, String param2) {
        RecommendAFragment fragment = new RecommendAFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_recommend_a, container, false);
    }


    @Override
    public void onViewCreated(final View view,
                              Bundle savedInstanceState) {



        List<RecommendListAItem> alList=new ArrayList<>();

        RecommendListAAdapter artAdapter;
        RecyclerView artView = view.findViewById(R.id.art);
        artAdapter = new RecommendListAAdapter(alList, getActivity());
        artView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        artView.setItemAnimator(new DefaultItemAnimator());
        artView.setAdapter(artAdapter);

        RecommendListAAdapter soundAdapter;
        RecyclerView soundView = view.findViewById(R.id.sound);
        soundAdapter = new RecommendListAAdapter(alList, getActivity());
        soundView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        soundView.setItemAnimator(new DefaultItemAnimator());
        soundView.setAdapter(soundAdapter);

        RecommendListAAdapter vibeAdapter;
        RecyclerView vibeView = view.findViewById(R.id.vibe);
        vibeAdapter = new RecommendListAAdapter(alList, getActivity());
        vibeView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        vibeView.setItemAnimator(new DefaultItemAnimator());
        vibeView.setAdapter(vibeAdapter);

        RecommendListAAdapter humorAdapter;
        RecyclerView humorView = view.findViewById(R.id.humor);
        humorAdapter = new RecommendListAAdapter(alList, getActivity());
        humorView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        humorView.setItemAnimator(new DefaultItemAnimator());
        humorView.setAdapter(humorAdapter);




        mViewModel = new ViewModelProvider(getActivity()).get(RecommendAViewModel.class);
        mViewModel.runQuery(getActivity());
        mViewModel.getAllRows().observe(getViewLifecycleOwner(), new Observer<List<RecommendListAItem>>() {
            @Override
            public void onChanged(@Nullable final List<RecommendListAItem> words) {
                artAdapter.setRows(mViewModel.getArtRows());
                soundAdapter.setRows(mViewModel.getSoundRows());
                vibeAdapter.setRows(mViewModel.getVibeRows());
                humorAdapter.setRows(mViewModel.getHumorRows());
            }
        });

        SwipeRefreshLayout pullToRefresh = view.findViewById(R.id.pullToRefresh);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mViewModel.refresh(getActivity());
                pullToRefresh.setRefreshing(false);
            }
        });


    }
}