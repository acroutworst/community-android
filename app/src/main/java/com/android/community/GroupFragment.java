package com.android.community;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class GroupFragment extends Fragment {

    private RecyclerView recyclerView;
    private ViewGroup view;
    private GridAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        view = (ViewGroup) inflater.inflate(R.layout.fragment_group, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);

        int spanCount = getSpanCount();

        adapter = new GridAdapter();

        // We are using a multi span grid to show many color models in each row. To set this up we need
        // to set our span count on the adapter and then get the span size lookup object from
        // the adapter. This look up object will delegate span size lookups to each model.
        adapter.setSpanCount(spanCount);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this.getContext(), spanCount);
        gridLayoutManager.setSpanSizeLookup(adapter.getSpanSizeLookup());
        gridLayoutManager.setRecycleChildrenOnDetach(true);

        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new VerticalGridCardSpacingDecoration());
        recyclerView.setAdapter(adapter);

        // Many color models are shown on screen at once. The default recycled view pool size is
        // only 5, so we manually set the pool size to avoid constantly creating new views when
        // the colors are randomized
        recyclerView.getRecycledViewPool().setMaxRecycledViews(R.layout.model_group, 50);

        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab_group);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adapter.onAddClicked();
            }
        });

        return view;
    }

    private int getSpanCount() {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        return (int) (dpWidth / 100);
    }
}
