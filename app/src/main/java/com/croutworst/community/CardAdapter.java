package com.croutworst.community;

import android.graphics.Color;
import android.view.View;
import android.widget.Toast;

import com.airbnb.epoxy.EpoxyAdapter;
import com.android.community.R;
import com.croutworst.community.models.HeaderModel;
import com.croutworst.community.models.HeaderModel_;
import com.croutworst.community.models.Meetup;
import com.croutworst.community.models.MeetupModel_;

import java.util.Random;

/**
 * Created by adamc on 2/16/17.
 */

public class CardAdapter extends EpoxyAdapter {

	private static final Random RANDOM = new Random();

	public final HeaderModel headerModel;

	public CardAdapter() {
		enableDiffing();

		headerModel = new HeaderModel_()
				.title(R.string.epoxy_meetups);
//				.caption(R.string.empty);

		addModels(
				headerModel
		);
	}

	private void updateVisibility() {
		if(headerModel.isShown()) {
			headerModel.hide();
		}
	}

	public final void onAddClicked() {
//		insertModelAfter(new MeetupModel_().title("Meetup").subtitle("Welcome to Community Groups!").color(randomColor()), headerModel);
//		updateVisibility();
		addModel(new MeetupModel_().title("Meetup").subtitle("Welcome to Community Groups!").color(randomColor()));
	}

	public void addMeetup(Meetup meetup) {
//		insertModelAfter(new MeetupModel_().title(meetup.getName()).subtitle(meetup.getDescription()).color(randomColor()), headerModel);
//		updateVisibility();

		// instead of this
		insertModelAfter(new MeetupModel_()
				.title(meetup.getName())
				.subtitle(meetup.getDescription())
				.color(randomColor())
				.clickListener(onMeetupClicked), headerModel);
	}

	public View.OnClickListener onMeetupClicked = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			Toast.makeText(v.getContext(), "Meetup clicked", Toast.LENGTH_LONG).show();
		}
	};

	public void removeMeetup() {
//		removeAllAfterModel(headerModel);
		removeAllModels();

		addModels(
				headerModel
		);

		headerModel.show();
	}

	private int randomColor() {
		int r = RANDOM.nextInt(8);

		switch (r) {
			case 0:
				return Color.parseColor("#B4B7DC");
			case 1:
				return Color.parseColor("#DCB4C3");
			case 2:
				return Color.parseColor("#B4D3DC");
			case 3:
				return Color.parseColor("#B4DCCD");
			case 4:
				return Color.parseColor("#D3DCB4");
			case 5:
				return Color.parseColor("#DCC5B4");
			case 6:
				return Color.parseColor("#DCB4B4");
			default:
				return Color.parseColor("#D0B4DC");
		}
	}
}
