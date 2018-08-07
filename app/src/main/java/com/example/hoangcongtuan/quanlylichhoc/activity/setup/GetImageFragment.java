package com.example.hoangcongtuan.quanlylichhoc.activity.setup;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.example.hoangcongtuan.quanlylichhoc.R;
import com.example.hoangcongtuan.quanlylichhoc.exception.AppException;
import com.example.hoangcongtuan.quanlylichhoc.utils.Utils;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.File;
import java.io.IOException;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by hoangcongtuan on 9/25/17.
 */

public class GetImageFragment extends Fragment implements View.OnClickListener{
    private final static String TAG = GetImageFragment.class.getName();
    public final static int RQ_PER_CAMERA = 0;
    private static final int REQUEST_IMAGE_GALLERY = 0;
    private static final int REQUEST_IMAGE_CAMERA = 1;
    private static final int REQUEST_IMAGE_CROP = 2;
    public ImageView ivImage;
    public Boolean isLoadImage;
    public Bitmap bitmap;
    private GetImageFragCallBack getImageFragCallBack;
    private AlertDialog cropTipDialog;
    private Uri imageUri;

    private String mCurrentPhotoPath;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_welcome_layout, container, false);
        ImageButton btnPickCamera = rootView.findViewById(R.id.btnPickCamera);
        ImageButton btnPickGallery = rootView.findViewById(R.id.btnPickGallery);
        ivImage = rootView.findViewById(R.id.ivImage);

        btnPickGallery.setOnClickListener(this);
        btnPickCamera.setOnClickListener(this);

        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getActivity());
        alertBuilder.setTitle(getResources().getString(R.string.crop_dialog_title));
        alertBuilder.setMessage(getResources().getString(R.string.crop_dialog_message));
        LayoutInflater inflater1 = getActivity().getLayoutInflater();
        @SuppressLint("InflateParams")
        View dialogView = inflater1.inflate(R.layout.layout_crop_guide_dialog, null);
        alertBuilder.setView(dialogView);
        alertBuilder.setPositiveButton(getResources().getString(R.string.understand), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                cropImage(imageUri);
            }
        });

        cropTipDialog = alertBuilder.create();
        isLoadImage = false;
        return rootView;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnPickCamera:
                try {
                    openCamera();
                } catch (AppException e) {
                    e.printStackTrace();
                    Utils.getsInstance(getApplicationContext()).showErrorMessage(getActivity(), e.getMessage());
                } catch (IOException e) {
                    e.printStackTrace();
                    Utils.getsInstance(getApplicationContext()).showErrorMessage(getActivity(), e.getMessage());
                }
                break;
            case R.id.btnPickGallery:
                openGallery();
                break;
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof GetImageFragCallBack)
            this.getImageFragCallBack = (GetImageFragCallBack)context;
    }

    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(Intent.createChooser(galleryIntent, "Chọn hình ảnh từ thư viện"), REQUEST_IMAGE_GALLERY);
    }

    public void reallyOpenCamera() throws IOException, AppException {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            File photoFile;
            photoFile = createImageFile();
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(getActivity(),
                        getApplicationContext().getPackageName() + ".fileprovider",
                        photoFile);
                //grant permission for photoURI, help camera can access to uri
                //get camera package
                String camera_package = cameraIntent.resolveActivity(getActivity().getPackageManager()).getPackageName();
                getContext().grantUriPermission(camera_package, photoURI, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(cameraIntent, REQUEST_IMAGE_CAMERA);
            }
            else
                throw  new AppException(getResources().getString(R.string.cannot_open_cam));
        }
        else {
            throw new AppException(getResources().getString(R.string.cannot_open_cam));
        }
    }

    private void openCamera() throws IOException, AppException {
           reallyOpenCamera();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case RQ_PER_CAMERA: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    try {
                        reallyOpenCamera();
                    } catch (AppException e) {
                        e.printStackTrace();
                        Utils.getsInstance(getApplicationContext()).showErrorMessage(getActivity(), e.getMessage());
                    } catch (IOException e) {
                        e.printStackTrace();
                        Utils.getsInstance(getApplicationContext()).showErrorMessage(getActivity(), e.getMessage());
                    }
                }
                break;
            }
        }
    }

    private File createImageFile() throws IOException, AppException {
        String imageFileName = "HocPhan";
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if (storageDir != null) {
            File image = File.createTempFile(
                    imageFileName,  /* prefix */
                    ".jpg",         /* suffix */
                    storageDir      /* directory */
            );

            mCurrentPhotoPath = image.getAbsolutePath();
            return image;
        }
        else {
            throw new AppException("Can't create image file!");
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, final Intent data) {
        if(resultCode != Activity.RESULT_OK) {
            return;
        }
        switch(requestCode){
            case REQUEST_IMAGE_CAMERA:
                imageUri = Uri.fromFile(new File(mCurrentPhotoPath));
                cropTipDialog.show();
                break;

            case REQUEST_IMAGE_GALLERY:
                if(data !=null) {
                    imageUri = data.getData();
                    cropTipDialog.show();
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
                    getImageFragCallBack.onBitmapAvailable(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;

            default:
                break;
        }
    }

    private void cropImage(final Uri uri) {
        CropImage.activity(uri).start(getContext(), this);
    }

    public interface GetImageFragCallBack {
        void onBitmapAvailable(Bitmap bitmap);
    }
}
