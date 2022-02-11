package com.example.jogen.ui.gallery;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import com.example.jogen.Adapters.AnimeListAdapter;
import com.example.jogen.R;
import com.example.jogen.Repositories.AnimeListItem;
import com.example.jogen.ViewModels.HomeViewModel;
import com.example.jogen.databinding.FragmentGalleryBinding;

import java.util.ArrayList;
import java.util.List;

public class WatchingFragment extends Fragment {

    private GalleryViewModel galleryViewModel;
    private FragmentGalleryBinding binding;
    private List<AnimeListItem> alList=new ArrayList<>();;
    AnimeListAdapter mAdapter;
    private HomeViewModel mViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        galleryViewModel =
                new ViewModelProvider(this).get(GalleryViewModel.class);

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

        mViewModel.getList("Watching").observe(getViewLifecycleOwner(), new Observer<List<AnimeListItem>>() {
            @Override
            public void onChanged(@Nullable final List<AnimeListItem> words) {
                mAdapter.setRows(words);

            }
        });

    }
}