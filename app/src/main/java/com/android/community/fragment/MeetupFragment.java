package com.android.community.fragment;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.community.CardAdapter;
import com.android.community.R;

public class MeetupFragment extends Fragment {

    private RecyclerView recyclerView;
    private ViewGroup view;
    private CardAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = (ViewGroup) inflater.inflate(R.layout.fragment_meetup, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view_meetup);

        adapter = new CardAdapter();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this.getContext());
        linearLayoutManager.setRecycleChildrenOnDetach(true);

        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);

        recyclerView.getRecycledViewPool().setMaxRecycledViews(R.layout.model_meetup, 50);

        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab_meetup);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adapter.onAddClicked();
            }
        });


        return view;
    }
}