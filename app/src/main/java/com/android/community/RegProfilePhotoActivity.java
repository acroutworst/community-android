package com.android.community;

import android.*;
import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.cocosw.bottomsheet.BottomSheet;
import com.mvc.imagepicker.ImagePicker;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Random;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

/**
 * Created by Billy on 1/11/2017.
 */

public class RegProfilePhotoActivity extends Activity{
    private static final String TAG = "RegProfilePhotoActivity";

    private static final int WRITE_EXTERNAL_STORAGE_PERMISSION = 7;

    private ImageButton mProfilePhoto;
    private Bitmap mBitmap;
    private Uri mImageUri;
    private Typeface mCopperplateFont;
    private Button mFinishButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_photo_reg);

        ImagePicker.setMinQuality(600, 600);

        mProfilePhoto = (ImageButton) findViewById(R.id.profile_photo);
        mProfilePhoto.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                requestPickImagePermissionAndPickImage();
            }
        });

        mCopperplateFont = Typeface.createFromAsset(getAssets(), "copperplate-regular.ttf");

        mFinishButton = (Button) findViewById(R.id.finish_button);
        mFinishButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(RegProfilePhotoActivity.this, HomeActivity.class);
                RegProfilePhotoActivity.this.startActivity(i);
            }
        });
        mFinishButton.setTypeface(mCopperplateFont);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ImagePicker.PICK_IMAGE_REQUEST_CODE) {
            if(checkSelfPermission(WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                mBitmap = ImagePicker.getImageFromResult(this, requestCode, resultCode, data);
                if (resultCode == RESULT_OK) {
                    Log.d(TAG, "ImagePicker's resultCode == RESULT_OK");
                    mImageUri = getImageUri(this, mBitmap);
                    CropImage.activity(mImageUri)
                            .setGuidelines(CropImageView.Guidelines.ON)
                            .start(this);
                } else if (resultCode != RESULT_OK) {
                    Log.e(TAG, "ImagePicker error");
                }
            }
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Log.d(TAG, "CropImage's resultCode == RESULT_OK");
                Uri resultUri = result.getUri();
                mProfilePhoto.setImageURI(resultUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Log.e(TAG, "CropImage error");
                Exception error = result.getError();
            }
        }
    }

    public void requestPickImagePermissionAndPickImage(){
        if (checkSelfPermission(WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            onPickImage(mProfilePhoto);
            return;
        } else {
            requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE}, WRITE_EXTERNAL_STORAGE_PERMISSION);
            if (ContextCompat.checkSelfPermission(this,
                    WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "WRITE_EXTERNAL_STORAGE permission granted");
                return;
            }
        }
        Log.e(TAG, "WRITE_EXTERNAL_STORAGE permission denied (inside requestPickImagePermissionAndPickImage())");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case WRITE_EXTERNAL_STORAGE_PERMISSION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "WRITE_EXTERNAL_STORAGE permission granted");
                    onPickImage(mProfilePhoto);
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {
                    Log.e(TAG, "WRITE_EXTERNAL_STORAGE permission denied (inside onRequestPermissionsResult())");
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    public void onPickImage(View view) {
        ImagePicker.pickImage(this, "Select your image:");
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }
}
