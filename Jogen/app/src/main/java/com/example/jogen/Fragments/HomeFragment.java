package com.example.jogen.Fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.jogen.Adapters.AnimeListAdapter;
import com.example.jogen.R;
import com.example.jogen.Repositories.AnimeListItem;
import com.example.jogen.databinding.FragmentHomeBinding;
import com.example.jogen.ViewModels.HomeViewModel;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment  {

    private FragmentHomeBinding binding;
    private List<AnimeListItem> alList=new ArrayList<>();;
    AnimeListAdapter mAdapter;
    private HomeViewModel mViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        return root;
    }

    @Override
    public void onViewCreated(final View view,
                              Bundle savedInstanceState)
    {


        RecyclerView recyclerView = (RecyclerView) getView().findViewById(R.id.recyclerview);

        mAdapter = new AnimeListAdapter(alList,getActivity());
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        mViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        mViewModel.populate(getActivity());
        mViewModel.getAllRows().observe(getViewLifecycleOwner(), new Observer<List<AnimeListItem>>() {
            @Override
            public void onChanged(@Nullable final List<AnimeListItem> words) {
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }



}