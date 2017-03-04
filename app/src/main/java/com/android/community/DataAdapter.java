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

	private ArrayList<Event> eventList = new ArrayList<>();

	public DataAdapter() {}

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
		Event event = eventList.get(i);

		viewHolder.event_name.setText(event.getTitle());
		viewHolder.event_desciption.setText(event.getDescription());
		viewHolder.event_location.setText(event.getLocation());
	}

	@Override
	public int getItemCount() {
		return eventList.size();
	}

	public void addEvent(Event post) {
		this.eventList.add(post);
		this.notifyDataSetChanged();
	}

	public void removeEvent() {
		int size = this.eventList.size();
		this.eventList.clear();
		notifyItemRangeRemoved(0, size);
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
