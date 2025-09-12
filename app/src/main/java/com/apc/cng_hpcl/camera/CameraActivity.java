package com.apc.cng_hpcl.camera;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import butterknife.ButterKnife;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.apc.cng_hpcl.R;
import com.apc.cng_hpcl.home.transaction.subtabs.TransCityGateStation;

public class CameraActivity extends AppCompatActivity implements PhotoFragment.OnFragmentInteractionListener {
//    public static final int RequestPermissionCode = 1;
    int PERMISSION_ALL = 1;
    boolean flagPermissions = false;
    Button openCamera;

    String[] PERMISSIONS = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        openCamera=(Button) findViewById(R.id.make_photo_button);
        ButterKnife.bind(this);
        checkPermissions();
        openCamera();
    }

    public void openCamera() {
        // check permissions
        if (!flagPermissions) {
            checkPermissions();
            return;
        }
        //start photo fragment
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.res_photo_layout, new PhotoFragment())
                //  .addToBackStack(null)
                .commit();
    }
    /*
        @OnClick(R.id.make_photo_button)
        void onClickScanButton() {
            // check permissions
            if (!flagPermissions) {
                checkPermissions();
                return;
            }
            //start photo fragment
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.res_photo_layout, new PhotoFragment())
                    .addToBackStack(null)
                    .commit();
        }
    */
    void checkPermissions() {
        if (!hasPermissions(this, PERMISSIONS)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(PERMISSIONS,
                        PERMISSION_ALL);
            }
            flagPermissions = false;
        }
        flagPermissions = true;
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void onFragmentInteraction(Bitmap bitmap,Bitmap bitmap2) {
        if (bitmap != null) {
            ImageFragment imageFragment = new ImageFragment();
            imageFragment.imageSetupFragment(bitmap,bitmap2);

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.res_photo_layout, imageFragment)
                    .addToBackStack(null)
                    .commit();
        }
    }
}
