package com.android.community;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.community.models.Event;

import java.util.ArrayList;

/**
 * Created by adamc on 2/17/17.
 */

public class DataAdapter extends RecyclerView.Adapter<DataAdapter.ViewHolder> {

	private ArrayList<Event> eventList;

	public DataAdapter(ArrayList<Event> eventList) {
		this.eventList = eventList;
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
		View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_row, viewGroup, false);
		return new ViewHolder(view);
	}

	@Override
	public void onBindViewHolder(ViewHolder viewHolder, int i) {

		viewHolder.event_name.setText(eventList.get(i).getTitle());
		viewHolder.event_desciption.setText(eventList.get(i).getDescription());
		viewHolder.event_location.setText(eventList.get(i).getLocation());
	}

	@Override
	public int getItemCount() {
		return eventList.size();
	}

	public class ViewHolder extends RecyclerView.ViewHolder {
		private TextView event_name, event_desciption, event_location;

		public ViewHolder(View view) {
			super(view);

			event_name = (TextView) view.findViewById(R.id.event_name);
			event_desciption = (TextView) view.findViewById(R.id.event_description);
			event_location = (TextView) view.findViewById(R.id.event_location);
		}
	}
}
