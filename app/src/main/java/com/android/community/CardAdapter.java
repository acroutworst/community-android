package com.android.community;

import android.graphics.Color;

import com.airbnb.epoxy.EpoxyAdapter;
import com.android.community.models.GroupModel_;
import com.android.community.models.HeaderModel;
import com.android.community.models.HeaderModel_;
import com.android.community.models.MeetupModel_;

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
				.title(R.string.epoxy_meetups)
				.caption(R.string.header_subtitle);

		addModels(
				headerModel
		);
	}

	public final void onAddClicked() {
		insertModelAfter(new MeetupModel_().title(R.string.epoxy_meetups).subtitle(R.string.header_subtitle).color(randomColor()), headerModel);
	}

	private int randomColor() {
		int r = RANDOM.nextInt(360);
		int g = RANDOM.nextInt(256);
		int b = RANDOM.nextInt(256);

		return Color.HSVToColor(new float[] { r, 18f, 86f});
	}
}
