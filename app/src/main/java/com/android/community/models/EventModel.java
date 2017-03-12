package com.android.community.models;

import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.TextView;

import com.airbnb.epoxy.EpoxyAttribute;
import com.airbnb.epoxy.EpoxyModelClass;
import com.airbnb.epoxy.EpoxyModelWithHolder;
import com.android.community.R;
import com.android.community.models.EventModel.EventHolder;

import butterknife.BindView;


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


import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnItemClick;

@EpoxyModelClass(layout = R.layout.model_event)
public abstract class EventModel extends EpoxyModelWithHolder<EventModel.EventHolder> {
    @EpoxyAttribute @ColorInt int color;
    @EpoxyAttribute String title;
    @EpoxyAttribute String subtitle;
    @EpoxyAttribute OnClickListener clickListener;

    @Override
    public void bind(EventHolder holder) {
        holder.cardView.setBackgroundColor(color);
//		holder.cardView.setBackgroundResource(image);
        holder.textView.setText(title);
        holder.subtitleTextView.setText(subtitle);
        holder.cardView.setOnClickListener(clickListener);
    }

    static class EventHolder extends BaseEpoxyHolder {
        @BindView(R.id.card_view_event)
        CardView cardView;
        @BindView(R.id.text_view_event)
        TextView textView;
        @BindView(R.id.subtitle_text_view_event) TextView subtitleTextView;
    }
}

