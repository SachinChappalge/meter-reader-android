package com.extentia.imagetotext.ui;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;
import com.extentia.imagetotext.R;
import com.extentia.imagetotext.model.ImageConfig;
import com.extentia.imagetotext.utils.ImageFilePath;
import com.extentia.imagetotext.utils.Utils;
import com.google.android.material.textfield.TextInputEditText;

import java.io.File;
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

    @BindView(R.id.openGallery)
    TextView openGallery;
    int SELECT_PICTURE = 200;
    private static final int RC_CAMERA_STORAGE_PERM = 125;
    private static final int CAMERA_REQUEST = 1888;
    private static final String[] CAMERA_AND_STORAGE = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    ProgressDialog pd  = null;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_config);
        ButterKnife.bind(this);
        if (! Python.isStarted()) {
            Python.start(new AndroidPlatform(this));
        }
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
        pd = new ProgressDialog(this);


    }

    @OnClick(R.id.openGallery)
    void openGallery(){
        saveConfig();
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select Picture"),SELECT_PICTURE);

    }

    @OnClick(R.id.captureImageTV)
    void handleCaptureClick()
    {
        if(isValidConfig())
        {
           saveConfig();
            Intent intent=new Intent(this, CameraActivity.class);
            intent.putExtra("IMAGE_CONFIG", getImageConfig());
            startActivity(intent);
        }
        else
        {
            Toast.makeText(this, "Please enter valid config", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveConfig(){
        ImageConfig imageConfig = getImageConfig();
        saveValues("max-angle",imageConfig.getMaxAngle());
        saveValues("min-angle",imageConfig.getMinAngle());
        saveValues("max-value",imageConfig.getMaxValue());
        saveValues("min-value",imageConfig.getMinValue());
        saveValues("units",imageConfig.getUnit());
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

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                Uri selectedImageUri = data.getData();
                if (null != selectedImageUri) {
                    final String path = ImageFilePath.getPath(this,selectedImageUri);
                    if (path != null) {
                        File f = new File(path);
                        showProgress();
                        AsyncTask.execute(() -> callLocalPython(f.getAbsolutePath()));
                    }


                }
            }
        }
    }

    private void callLocalPython(String path) {
        ImageConfig imageConfig = getImageConfig();
        Python python = Python.getInstance();
        PyObject pyObject = python.getModule("analog_reader");
        PyObject resultObject = pyObject.callAttr("getAnalogMeterReading",path,imageConfig.getMinAngle(), imageConfig.getMaxAngle(), imageConfig.getMinValue(), imageConfig.getMaxValue(), imageConfig.getUnit());

        runOnUiThread(() -> {
            hideProgress();
            showDialog(resultObject.toString(),imageConfig.getUnit());
        });
    }

    private void showDialog(String reading,String unit){
        Double readingInDouble = Double.parseDouble(reading);
        String readingInString = Utils.convertToFormat(readingInDouble);
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.app_name))
                .setMessage("Meter reading is " + readingInString + " "+ unit )
                .setPositiveButton(android.R.string.yes, (dialog, which) -> dialog.cancel())
                .show();
    }

    private void showProgress(){
        pd.setMessage("Please wait, reading...");
        pd.show();
    }
    private void hideProgress(){
        pd.dismiss();
    }
}
