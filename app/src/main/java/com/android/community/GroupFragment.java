package com.android.community;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.community.GridAdapter;
import com.android.community.R;
import com.android.community.VerticalGridCardSpacingDecoration;

import butterknife.BindView;
import butterknife.ButterKnife;


public class GroupFragment extends Fragment {
    @BindView(R.id.recycler_view) RecyclerView recyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_group, container, false);
        ButterKnife.bind(this.getActivity());

        int spanCount = getSpanCount();

        GridAdapter adapter = new GridAdapter();

        // We are using a multi span grid to show many color models in each row. To set this up we need
        // to set our span count on the adapter and then get the span size lookup object from
        // the adapter. This look up object will delegate span size lookups to each model.
        adapter.setSpanCount(spanCount);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this.getActivity().getApplicationContext(), spanCount);
        gridLayoutManager.setSpanSizeLookup(adapter.getSpanSizeLookup());

        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new VerticalGridCardSpacingDecoration());
        recyclerView.setAdapter(adapter);

        // Many color models are shown on screen at once. The default recycled view pool size is
        // only 5, so we manually set the pool size to avoid constantly creating new views when
        // the colors are randomized
        recyclerView.getRecycledViewPool().setMaxRecycledViews(R.layout.model_color, 50);

        return view;
    }

    private int getSpanCount() {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        return (int) (dpWidth / 100);
    }
}
