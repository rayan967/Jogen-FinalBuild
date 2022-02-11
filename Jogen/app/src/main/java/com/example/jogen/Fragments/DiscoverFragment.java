package com.example.jogen.Fragments;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.jogen.Adapters.AnimeListAdapter;
import com.example.jogen.Adapters.SearchListAdapter;
import com.example.jogen.R;
import com.example.jogen.Repositories.AnimeListItem;
import com.example.jogen.Repositories.SearchListItem;
import com.example.jogen.ViewModelFactories.AnimeViewModelFactory;
import com.example.jogen.ViewModelFactories.DiscoverViewModelFactory;
import com.example.jogen.ViewModels.AnimeViewModel;
import com.example.jogen.ViewModels.DiscoverViewModel;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DiscoverFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DiscoverFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private List<SearchListItem> alList=new ArrayList<>();
    SearchListAdapter mAdapter;
    private DiscoverViewModel mViewModel;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;



    public DiscoverFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DiscoverFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DiscoverFragment newInstance(String param1, String param2) {
        DiscoverFragment fragment = new DiscoverFragment();
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
        return inflater.inflate(R.layout.fragment_discover, container, false);
    }

    @Override
    public void onViewCreated(final View view,
                              Bundle savedInstanceState) {
        SearchView searchView = view.findViewById(R.id.search);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerview);
        mAdapter = new SearchListAdapter(alList,getActivity());
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);



        mViewModel = new ViewModelProvider(getActivity()).get(DiscoverViewModel.class);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener(){
            @Override
            public boolean onQueryTextChange(String newText) {
                // TODO Auto-generated method stub
                return false;
            }


            @Override
            public boolean onQueryTextSubmit(String query) {

                mViewModel.runQuery(query);
                mViewModel.getAllRows().observe(getViewLifecycleOwner(), new Observer<List<SearchListItem>>() {
                    @Override
                    public void onChanged(@Nullable final List<SearchListItem> words) {
                        mAdapter.setRows(words);
                    }
                });
                return false;
            }
        });



    }

}
