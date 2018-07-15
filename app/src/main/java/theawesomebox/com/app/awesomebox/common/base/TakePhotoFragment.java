package theawesomebox.com.app.awesomebox.common.base;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;

import theawesomebox.com.app.awesomebox.R;
import theawesomebox.com.app.awesomebox.common.utils.DateUtils;
import theawesomebox.com.app.awesomebox.common.utils.Logger;
import theawesomebox.com.app.awesomebox.common.utils.PermissionUtils;

/**
 * Created by bilal on 11/01/2018.
 */

public class TakePhotoFragment extends BaseFragment {
    /**
     * Flag to identify camera access permission is requested
     */
    public static final int TAKE_PHOTO_PERMISSION = 101;
    /**
     * Flag to identify gallery access permission is requested
     */
    public static final int CHOOSE_PHOTO_PERMISSION = 102;
    /**
     * Flag to identify camera intent is called
     */
    public static final int TAKE_PHOTO_RESULT_CODE = 110;
    /**
     * Flag to identify gallery intent is called
     */
    public static final int CHOOSE_PHOTO_RESULT_CODE = 120;
    /**
     * Flag to identify gallery intent is called
     */
    public static final int CHOOSE_MULTIPLE_RESULT_CODE = 130;
    public Uri selectedPhotoUri;

    @Override
    public String getTitle() {
        return null;
    }

    /**
     * show user dialog to select either open camera Or gallery
     */
    protected void showChoosePhotoDialog() {
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.BOTTOM;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(wlp);
        dialog.setContentView(R.layout.choose_photo_dialog);
        TextView takePhotoTv = (TextView) dialog.findViewById(R.id.tv_take_photo);
        TextView choosePhotoTv = (TextView) dialog
                .findViewById(R.id.tv_photo_lib);
        takePhotoTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (PermissionUtils.checkAndRequestPermissions(getActivity()
                        , new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE}, TAKE_PHOTO_PERMISSION))
                    dispatchTakePictureIntent();
            }
        });

        choosePhotoTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (PermissionUtils.checkAndRequestPermissions(getActivity()
                        , new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE}, CHOOSE_PHOTO_PERMISSION))
//                    checkAndDispatchGalleryIntent();
                    dispatchChoosePictureIntent();
            }
        });
        dialog.findViewById(R.id.cancel_dialog_tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == TAKE_PHOTO_PERMISSION && PermissionUtils.verifyPermission(grantResults)) {
            dispatchTakePictureIntent();
        } else if (requestCode == CHOOSE_PHOTO_PERMISSION && PermissionUtils.verifyPermission(grantResults)) {
            checkAndDispatchGalleryIntent();
        }
    }

    /**
     * Check build os version and then dispatch single or multiple choose picture intent
     */
    private void checkAndDispatchGalleryIntent() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2)
            dispatchMultipleChoosePictureIntent();
        else
            dispatchChoosePictureIntent();
        ;
    }

    /**
     * create and call open camera intent
     */
    public void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File photo = getPhotoFile();
        if (photo != null && takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, selectedPhotoUri = Uri.fromFile(photo));
            startActivityForResult(Intent.createChooser(takePictureIntent, ""), TAKE_PHOTO_RESULT_CODE);
        }
    }

    /**
     * create and open choose from gallery/photos intent
     */
    public void dispatchChoosePictureIntent() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Choose Picture"), CHOOSE_PHOTO_RESULT_CODE);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void dispatchMultipleChoosePictureIntent() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(Intent.createChooser(intent, "Choose Picture"), CHOOSE_MULTIPLE_RESULT_CODE);
    }

    /**
     * create and return a file to store the taken image in the device storage
     *
     * @return created file in device storage
     */
    final public File getPhotoFile() {
        String timeStamp = DateUtils.oneFormatToAnother(DateUtils.getCalendar().getTime().toString(),
                DateUtils.CALENDAR_DEFAULT_FORMAT, DateUtils.CAMERA_FORMAT);
        String imageFileName = "IMG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File photoFile = null;
        try {
            photoFile = File.createTempFile(imageFileName, ".jpg", storageDir);
        } catch (IOException e) {
            Logger.caughtException(e);
        }
        return photoFile;
    }
}
