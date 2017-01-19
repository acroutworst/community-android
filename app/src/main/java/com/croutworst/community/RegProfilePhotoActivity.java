package com.croutworst.community;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.cocosw.bottomsheet.BottomSheet;

/**
 * Created by Billy on 1/11/2017.
 */

public class RegProfilePhotoActivity extends Activity{
    private static final String TAG = "RegProfilePhotoActivity";

    private ImageButton mUploadPhoto;
    private ImageView mImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_photo_reg);

        mUploadPhoto = (ImageButton) findViewById(R.id.upload_photo);
        mUploadPhoto.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                new BottomSheet.Builder(RegProfilePhotoActivity.this, R.style.BottomSheet_Dialog)
                        .title("Profile photo")
                        .grid() // <-- important part
                        .sheet(R.menu.upload_photo_bottom_sheet)
                        .listener(new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case R.id.take_photo:
                                        Log.d(TAG, "take photo");
//                                        Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                                        startActivityForResult(takePicture, 0);//zero can be replaced with any action code
                                        break;
                                    case R.id.upload_photo:
                                        Log.d(TAG, "upload photo");
//                                        Intent pickPhoto = new Intent(Intent.ACTION_PICK,
//                                                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                                        startActivityForResult(pickPhoto , 1);//one can be replaced with any action code
                                        break;
                                    case R.id.download_photo:
                                        Log.d(TAG, "download photo");
                                        break;
                                    case R.id.remove_photo:
                                        Log.d(TAG, "remove photo");
                                        break;
                                    default:
                                        //do nothing
                                        break;
                                }
                            }
                        }).show();
            }
        });

        Button finishButton = (Button) findViewById(R.id.finish_button);
        finishButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(RegProfilePhotoActivity.this, HomeActivity.class);
                RegProfilePhotoActivity.this.startActivity(i);
            }
        });
    }

/*    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        switch(requestCode) {
            case 0:
                if(resultCode == RESULT_OK){
                    Uri selectedImage = imageReturnedIntent.getData();
                    mImageView.setImageURI(selectedImage);
                }
                break;
            case 1:
                ImageView imageview;
                if(resultCode == RESULT_OK){
                    Uri selectedImage = imageReturnedIntent.getData();
                    mImageView.setImageURI(selectedImage);
                }
                break;
        }
    }*/
}
