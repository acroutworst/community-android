package com.android.community.models;

import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import android.support.v7.widget.CardView;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.airbnb.epoxy.EpoxyAttribute;
import com.airbnb.epoxy.EpoxyModelClass;
import com.airbnb.epoxy.EpoxyModelWithHolder;
import com.android.community.R;
import com.android.community.models.MeetupModel.MeetupHolder;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnItemClick;

/**
 * Created by adamc on 2/16/17.
 */

@EpoxyModelClass(layout = R.layout.model_meetup)
public abstract class MeetupModel extends EpoxyModelWithHolder<MeetupHolder> {
	@EpoxyAttribute @ColorInt int color;
	@EpoxyAttribute String title;
	@EpoxyAttribute String subtitle;
//	@EpoxyAttribute @DrawableRes int image;
	@EpoxyAttribute OnClickListener clickListener;

	@Override
	public void bind(MeetupHolder holder) {
		holder.cardView.setBackgroundColor(color);
		holder.textView.setText(title);
		holder.subtitleTextView.setText(subtitle);
//		holder.cardView.setBackgroundResource(image);
		holder.cardView.setOnClickListener(clickListener);
	}

	static class MeetupHolder extends BaseEpoxyHolder {
		@BindView(R.id.card_view_meetup) CardView cardView;
		@BindView(R.id.text_view_meetup) TextView textView;
		@BindView(R.id.subtitle_text_view_meetup) TextView subtitleTextView;
	}
}

