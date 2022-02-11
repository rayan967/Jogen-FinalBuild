package com.example.jogen.Fragments;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
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

import com.example.jogen.Adapters.SearchListAdapter;
import com.example.jogen.R;
import com.example.jogen.Repositories.SearchListItem;
import com.example.jogen.ViewModels.DiscoverViewModel;
import com.example.jogen.databinding.FragmentGalleryBinding;
import com.example.jogen.databinding.FragmentHomeBinding;
import com.example.jogen.databinding.FragmentRecommendBBinding;

import java.util.ArrayList;
import java.util.List;


public class RecommendBFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private List<SearchListItem> alList=new ArrayList<>();
    SearchListAdapter mAdapter;
    private DiscoverViewModel mViewModel;
    private FragmentRecommendBBinding binding;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentRecommendBBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


    @Override
    public void onViewCreated(final View view,
                              Bundle savedInstanceState) {


        SearchView searchView = view.findViewById(R.id.search);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerview);
        mAdapter = new SearchListAdapter(alList, getActivity());
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);



        mViewModel = new ViewModelProvider(getActivity()).get(DiscoverViewModel.class);
        mViewModel.runRQuery(getActivity());
        mViewModel.getAllRows().observe(getViewLifecycleOwner(), new Observer<List<SearchListItem>>() {
            @Override
            public void onChanged(@Nullable final List<SearchListItem> words) {
                mAdapter.setRows(words);
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