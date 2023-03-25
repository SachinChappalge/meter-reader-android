package com.extentia.imagetotext.ui;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.extentia.imagetotext.R;
import com.extentia.imagetotext.model.ImageConfig;
import com.google.android.material.textfield.TextInputEditText;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pub.devrel.easypermissions.EasyPermissions;

public class ConfigActivity extends Activity implements EasyPermissions.PermissionCallbacks{


    @BindView(R.id.maxValueET)
    TextInputEditText maxValueET;

    @BindView(R.id.minValueET)
    TextInputEditText minValueET;

    @BindView(R.id.maxAngleET)
    TextInputEditText maxAngleET;

    @BindView(R.id.minAngleET)
    TextInputEditText minAngleET;

    @BindView(R.id.unitsET)
    TextInputEditText unitET;

    @BindView(R.id.captureImageTV)
    TextView captureImageTV;

    private static final int RC_CAMERA_STORAGE_PERM = 125;
    private static final int CAMERA_REQUEST = 1888;
    private static final String[] CAMERA_AND_STORAGE = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_config);
        ButterKnife.bind(this);
        if(hasPermission())
        {

        }
        else
        {
            EasyPermissions.requestPermissions(
                    this,
                    getString(R.string.request_camera_permission),
                    RC_CAMERA_STORAGE_PERM,
                    CAMERA_AND_STORAGE);
        }

        maxAngleET.setText(getValues("max-angle"));
        minAngleET.setText(getValues("min-angle"));
        maxValueET.setText(getValues("max-value"));
        minValueET.setText(getValues("min-value"));
        unitET.setText(getValues("units"));


    }

    @OnClick(R.id.captureImageTV)
    void handleCaptureClick()
    {
        if(isValidConfig())
        {
            ImageConfig imageConfig = getImageConfig();
            saveValues("max-angle",imageConfig.getMaxAngle());
            saveValues("min-angle",imageConfig.getMinValue());
            saveValues("max-value",imageConfig.getMaxValue());
            saveValues("min-value",imageConfig.getMinValue());
            saveValues("units",imageConfig.getUnit());

            Intent intent=new Intent(this, CameraActivity.class);
            intent.putExtra("IMAGE_CONFIG", getImageConfig());
            startActivity(intent);
        }
        else
        {
            Toast.makeText(this, "Please enter valid config", Toast.LENGTH_SHORT).show();
        }
    }

    private ImageConfig getImageConfig()
    {
        ImageConfig imageConfig = new ImageConfig();
        imageConfig.setMaxAngle(maxAngleET.getText().toString());
        imageConfig.setMinAngle(minAngleET.getText().toString());
        imageConfig.setMaxValue(maxValueET.getText().toString());
        imageConfig.setMinValue(minValueET.getText().toString());
        imageConfig.setUnit(unitET.getText().toString());
        return imageConfig;
    }

    private boolean isValidConfig()
    {
        if(maxAngleET.getText().toString().trim().isEmpty())
        {
            return false;
        }

        else if(minAngleET.getText().toString().trim().isEmpty())
        {
            return false;
        }
        else if(maxValueET.getText().toString().trim().isEmpty())
        {
            return false;
        }
        else if(minValueET.getText().toString().trim().isEmpty())
        {
            return false;
        }
        else if(unitET.getText().toString().trim().isEmpty())
        {
            return false;
        }
        return true;
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private boolean hasPermission() {
        return EasyPermissions.hasPermissions(this, CAMERA_AND_STORAGE);
    }

    private void saveValues(String key, String value){
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(key, value);
        editor.apply();
    }
    private String getValues(String key){
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
       return sharedPref.getString(key, "");
    }
}
