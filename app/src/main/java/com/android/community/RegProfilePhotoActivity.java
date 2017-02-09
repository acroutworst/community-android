package com.android.community;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.cocosw.bottomsheet.BottomSheet;
import com.mvc.imagepicker.ImagePicker;

/**
 * Created by Billy on 1/11/2017.
 */

public class RegProfilePhotoActivity extends Activity{
    private static final String TAG = "RegProfilePhotoActivity";

    private ImageButton mUploadPhoto;
    private ImageView mImageView;
    private Typeface mCopperplateFont;
    private Button mFinish;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_photo_reg);

        ImagePicker.setMinQuality(600, 600);

        mUploadPhoto = (ImageButton) findViewById(R.id.upload_photo);
        mUploadPhoto.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                onPickImage(v);
            }
        });

        mCopperplateFont = Typeface.createFromAsset(getAssets(), "copperplate-regular.ttf");

        mFinish = (Button) findViewById(R.id.finish_button);
        mFinish.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(RegProfilePhotoActivity.this, HomeActivity.class);
                RegProfilePhotoActivity.this.startActivity(i);
            }
        });
        mFinish.setTypeface(mCopperplateFont);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        Bitmap bitmap = ImagePicker.getImageFromResult(this, requestCode, resultCode, imageReturnedIntent);
        // TODO do something with the bitmap
    }

    public void onPickImage(View view) {
        // Click on image button
        ImagePicker.pickImage(this, "Select your image:");
    }
}
