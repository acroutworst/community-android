package com.croutworst.community;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.community.R;
import com.mvc.imagepicker.ImagePicker;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;

import de.hdodenhof.circleimageview.CircleImageView;
import timber.log.Timber;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class RegProfilePhotoActivity extends Activity{
    // Debug purposes
    private static final String TAG = "RegProfilePhotoActivity";

    // UI references
    private CircleImageView mProfilePhoto;
    private Button mFinishButton;

    // Utilities
    private Bitmap mBitmap;
    private Uri mImageUri;
    private Drawable mDrawable;
    private Typeface mCopperplateFont;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_photo_reg);

        ImagePicker.setMinQuality(600, 600);

        mProfilePhoto = (CircleImageView) findViewById(R.id.profile_photo_container);
        mProfilePhoto.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                requestPickImagePermissionAndPickImage();
            }
        });

        mCopperplateFont = Typeface.createFromAsset(getAssets(), "copperplate-regular.ttf");

        mFinishButton = (Button) findViewById(R.id.finish_button);
        mFinishButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //TODO: Register user. If successful, start HomeActivity.class, else create a Toast saying registration was unsuccessful
                Intent i = new Intent(RegProfilePhotoActivity.this, HomeActivity.class);
                RegProfilePhotoActivity.this.startActivity(i);
            }
        });
        mFinishButton.setTypeface(mCopperplateFont);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ImagePicker.PICK_IMAGE_REQUEST_CODE && resultCode == RESULT_OK) {
            if(checkSelfPermission(WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                mBitmap = ImagePicker.getImageFromResult(this, requestCode, resultCode, data);
                mImageUri = fromBitmapToUri(this, mBitmap);
                cropImage(mImageUri);
            }
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK && result != null) {
                Timber.d("CropImage's resultCode == RESULT_OK");
                Uri resultUri = result.getUri();
                mProfilePhoto.setImageURI(resultUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Timber.e("CropImage error");
                Exception error = result.getError();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case ImagePicker.PICK_IMAGE_REQUEST_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    onPickImage(mProfilePhoto);
                } else {
                    Toast.makeText(this, "Cancelling, required permissions are not granted", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }

    public void requestPickImagePermissionAndPickImage(){
        if (checkSelfPermission(WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            onPickImage(mProfilePhoto);
        } else {
            requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE}, ImagePicker.PICK_IMAGE_REQUEST_CODE);
        }
    }

    public void onPickImage(View view) {
        ImagePicker.pickImage(this, "Select your image:");
    }

    public void cropImage(Uri imageUri){
        CropImage.activity(imageUri)
                .setCropShape(CropImageView.CropShape.OVAL)
                .setGuidelines(CropImageView.Guidelines.ON_TOUCH)
                .setFixAspectRatio(true)
                .setOutputUri(imageUri)
                .start(this);
    }

    public Uri fromBitmapToUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }
}