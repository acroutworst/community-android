package com.android.community;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.community.models.Community;

import java.util.ArrayList;

/**
 * Created by adamc on 3/14/17.
 */

public class CommunityAdapter extends RecyclerView.Adapter<CommunityAdapter.ViewHolder> {

	private ArrayList<Community> communityList = new ArrayList<>();

	public CommunityAdapter() {}

	public CommunityAdapter(ArrayList<Community> communityList) {
		this.communityList = communityList;
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
		View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.community_row, viewGroup, false);
		return new ViewHolder(view);
	}

	@Override
	public void onBindViewHolder(ViewHolder viewHolder, int i) {
		Community community = communityList.get(i);

		viewHolder.community_name.setText(community.getTitle());
	}

	@Override
	public int getItemCount() {
		return communityList.size();
	}

	public void addCommunity(Community post) {
		this.communityList.add(post);
		this.notifyDataSetChanged();
	}

	public void removeCommunity() {
		int size = this.communityList.size();
		this.communityList.clear();
		notifyItemRangeRemoved(0,size);
	}

	public class ViewHolder extends RecyclerView.ViewHolder {
		private TextView community_name;

		public ViewHolder(View view) {
			super(view);

			community_name = (TextView) view.findViewById(R.id.community_name);
		}
	}
}
