package com.jogen.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jogen.Adapters.AnimeListAdapter;
import com.jogen.R;
import com.jogen.Repositories.AnimeListItem;
import com.jogen.ViewModels.HomeViewModel;
import com.jogen.databinding.FragmentGalleryBinding;

import java.util.ArrayList;
import java.util.List;

public class DroppedFragment extends Fragment {

    private FragmentGalleryBinding binding;
    private List<AnimeListItem> alList=new ArrayList<>();;
    AnimeListAdapter mAdapter;
    private HomeViewModel mViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentGalleryBinding.inflate(inflater, container, false);
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



        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Anime List");
        RecyclerView recyclerView = (RecyclerView) getView().findViewById(R.id.recyclerview);

        mAdapter = new AnimeListAdapter(alList,getActivity());
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        mViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        mViewModel.getList("Dropped").observe(getViewLifecycleOwner(), new Observer<List<AnimeListItem>>() {
            @Override
            public void onChanged(@Nullable final List<AnimeListItem> words) {
                mAdapter.setRows(words);

            }
        });

    }
}