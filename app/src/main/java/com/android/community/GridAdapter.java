package com.android.community;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.View.OnClickListener;

import com.airbnb.epoxy.EpoxyAdapter;
import com.airbnb.epoxy.EpoxyModel;
import com.android.community.models.ButtonModel;
import com.android.community.models.ButtonModel_;
import com.android.community.models.GroupModel_;
import com.android.community.models.HeaderModel;
import com.android.community.models.HeaderModel_;

import java.util.Collections;
import java.util.Random;

public class GridAdapter extends EpoxyAdapter {
  private static final Random RANDOM = new Random();

  // These models are saved as fields so they can easily be shown or hidden as needed
  private final ButtonModel_ clearButton = new ButtonModel_();
  private final ButtonModel_ shuffleButton = new ButtonModel_();
  public final ButtonModel_ changeColorsButton = new ButtonModel_();
	public final HeaderModel headerModel;

  GridAdapter() {
    // We are going to use automatic diffing, so we just have to enable it first
    enableDiffing();

    // We're using the generated subclasses of our models, which is indicated by the underscore
    // appended to the class name. These generated classes contain our setter methods, as well as
    // the hashcode methods that tell the diffing algorithm when a model has changed
    headerModel = new HeaderModel_()
        .title(R.string.epoxy)
        .caption(R.string.header_subtitle);

    ButtonModel addButton = new ButtonModel_()
        .text(R.string.button_add)
        .clickListener(onAddClicked);

    clearButton.text(R.string.button_clear)
        .clickListener(onClearClicked);

    shuffleButton.text(R.string.button_shuffle)
        .clickListener(onShuffleClicked);

    changeColorsButton.text(R.string.button_change)
        .clickListener(onChangeColorsClicked);

    addModels(
        headerModel,
        addButton.hide(),
        clearButton.hide(),
        shuffleButton.hide(),
        changeColorsButton.hide()
    );

    updateButtonVisibility();
  }

  private void updateButtonVisibility() {
    int colorCount = getAllModelsAfter(changeColorsButton).size();
  }

  private final OnClickListener onAddClicked = new OnClickListener() {
    @Override
    public void onClick(View v) {
      insertModelAfter(new GroupModel_().text(randomTitle()).image(randomPicture()), changeColorsButton);
      updateButtonVisibility();
    }
  };

  public final void onAddClicked() {
      insertModelAfter(new GroupModel_().text(randomTitle()).image(randomPicture()), headerModel);
      updateButtonVisibility();
  }

  private final OnClickListener onClearClicked = new OnClickListener() {
    @Override
    public void onClick(View v) {
      removeAllAfterModel(changeColorsButton);
      updateButtonVisibility();
    }
  };

  private final OnClickListener onShuffleClicked = new OnClickListener() {
    @Override
    public void onClick(View v) {
      Collections.shuffle(getAllModelsAfter(changeColorsButton));
      notifyModelsChanged();
    }
  };

  private final OnClickListener onChangeColorsClicked = new OnClickListener() {
    @Override
    public void onClick(View v) {
      for (EpoxyModel<?> model : getAllModelsAfter(changeColorsButton)) {
        ((GroupModel_) model).color(randomColor());
      }
      notifyModelsChanged();
    }
  };

  private int randomColor() {
    int r = RANDOM.nextInt(256);
    int g = RANDOM.nextInt(256);
    int b = RANDOM.nextInt(256);

    return Color.rgb(r, g, b);
  }

	private int randomTitle() {
		int title = RANDOM.nextInt(8);

		switch (title) {
			case 0:
				return R.string.group_1;
			case 1:
				return R.string.group_2;
			case 2:
				return R.string.group_3;
			case 3:
				return R.string.group_4;
			case 4:
				return R.string.group_5;
			case 5:
				return R.string.group_6;
			case 6:
				return R.string.group_7;
			default:
				return R.string.group_0;
		}
	}

	private int randomPicture() {
		int grid = RANDOM.nextInt(6);

		switch(grid) {
			case 0:
				return R.drawable.mountain;
			case 1:
				return R.drawable.cloud;
			case 2:
				return R.drawable.water;
			case 3:
				return R.drawable.fire;
			case 4:
				return R.drawable.trail;
			case 5:
				return R.drawable.tent;
			default:
				return R.drawable.tent;
		}
	}
}
