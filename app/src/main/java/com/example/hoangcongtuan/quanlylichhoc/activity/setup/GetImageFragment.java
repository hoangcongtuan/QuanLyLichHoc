package com.example.hoangcongtuan.quanlylichhoc.activity.setup;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.example.hoangcongtuan.quanlylichhoc.R;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.File;
import java.io.IOException;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by hoangcongtuan on 9/25/17.
 */

public class GetImageFragment extends Fragment implements View.OnClickListener {

    private final static String TAG = GetImageFragment.class.getName();

    private static final int REQUEST_IMAGE_PICK = 0;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_IMAGE_CROP = 2;
    ImageButton btnPickCamera;
    ImageButton btnPickGallery;
    public ImageView ivImage;
    public Boolean isLoadImage;
    public Bitmap bitmap;
    private WelcomeFragInterface welcomeFragInterface;
    private Activity activity;
    AlertDialog.Builder alertBuilder;
    AlertDialog alertDialog;
    Button btnOk;
    private Uri imageUri;

    private String mCurrentPhotoPath;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_welcome_layout, container, false);
        btnPickCamera = (ImageButton) rootView.findViewById(R.id.btnPickCamera);
        btnPickGallery = (ImageButton)rootView.findViewById(R.id.btnPickGallery);
        ivImage = (ImageView)rootView.findViewById(R.id.ivImage);

        btnPickGallery.setOnClickListener(this);
        btnPickCamera.setOnClickListener(this);

        alertBuilder = new AlertDialog.Builder(getActivity());
        alertBuilder.setTitle("Crop Guide");
        LayoutInflater inflater1 = getActivity().getLayoutInflater();
        View dialogView = inflater1.inflate(R.layout.crop_guide_dialog_layout, null);
        alertBuilder.setView(dialogView);
        alertBuilder.setPositiveButton("Da hieu", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                cropImage(imageUri);
            }
        });

        alertDialog = alertBuilder.create();


        activity = getActivity();
        isLoadImage = false;

        return rootView;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnPickCamera:
                openCamera();

                break;
            case R.id.btnPickGallery:
                openGallery();
                break;
        }
    }

    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(Intent.createChooser(galleryIntent,"Chọn hình ảnh từ thư viện"),REQUEST_IMAGE_PICK);
    }

    private void openCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {

            }

            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(getActivity(),
                        getApplicationContext().getPackageName() + ".fileprovider",
                        photoFile);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private File createImageFile() throws IOException {
        String imageFileName = "HocPhan";
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode != Activity.RESULT_OK) {
            return;
        }
        switch(requestCode){
            case REQUEST_IMAGE_CAPTURE:
                imageUri = Uri.fromFile(new File(mCurrentPhotoPath));
                alertDialog.show();

                break;

            case REQUEST_IMAGE_PICK:
                if(data !=null)
                {
                    imageUri = data.getData();
                    alertDialog.show();
                }
                break;
            case REQUEST_IMAGE_CROP:
                if(data != null)
                {
                    Bundle bundle = data.getExtras();
                    if(bundle != null) {
                        Bitmap bitmap = bundle.getParcelable("data");
                        ivImage.setImageBitmap(bitmap);
                    }

                }
                break;
            case CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE:
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                Uri uri = result.getUri();
                try {
                    isLoadImage = true;
                    bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);
                    ivImage.setImageBitmap(bitmap);
                    welcomeFragInterface.onBitmapAvailable();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;

            default:
                break;
        }

    }


    private void cropImage(final Uri uri) {
        CropImage.activity(uri).start(getActivity(), this);
        //CropImage.activity(uri).start(getActivity());
    }

    public interface WelcomeFragInterface {
        void onBitmapAvailable();
    }

    public void setWelcomeFragInterface(WelcomeFragInterface welcomeFragInterface) {
        this.welcomeFragInterface = welcomeFragInterface;
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: ");
    }
}
