package com.extentia.imagetotext.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.extentia.imagetotext.ImageToTextApp;
import com.extentia.imagetotext.R;
import com.extentia.imagetotext.api.ApiInterface;
import com.extentia.imagetotext.api.NetworkState;
import com.extentia.imagetotext.model.ImageConfig;
import com.extentia.imagetotext.viewmodel.ImageViewModel;
import com.otaliastudios.cameraview.CameraListener;
import com.otaliastudios.cameraview.CameraUtils;
import com.otaliastudios.cameraview.CameraView;
import com.otaliastudios.cameraview.PictureResult;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class CameraActivity extends AppCompatActivity {
    LinearLayout progressBar;
    private CameraView cameraView;
    private ImageViewModel viewModel;
    private ImageConfig imageConfig;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        cameraView = findViewById(R.id.cameraView);
        progressBar = findViewById(R.id.progress_bar_main);
        imageConfig=(ImageConfig) getIntent().getSerializableExtra("IMAGE_CONFIG");
        findViewById(R.id.tv_picture).setOnClickListener(v -> {
            cameraView.takePicture();
        });
        cameraView.addCameraListener(new CameraListener() {
            @SuppressLint("WrongThread")
            @Override
            public void onPictureTaken(@NonNull PictureResult result) {
                super.onPictureTaken(result);
                String fileName = System.currentTimeMillis()+"_meter.jpg";
                ContextWrapper cw = new ContextWrapper(CameraActivity.this);
                File directory = cw.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);


                    File photo = new File(directory, fileName);
                FileOutputStream fileOutputStream = null;
                try {
                    fileOutputStream = new FileOutputStream(photo);
                    CameraUtils.decodeBitmap(result.getData()).compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
                    fileOutputStream.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

              //  ((ImageView)findViewById(R.id.image)).setImageURI(Uri.fromFile(photo));
              //  ((ImageView)findViewById(R.id.image)).setVisibility(View.VISIBLE);
                callLocalPython(photo);


            }
        });
        viewModel = ViewModelProviders.of(this).get(ImageViewModel.class);
    }

    private void uploadFileToServer(File file) {
        ApiInterface apiInterface =((ImageToTextApp) getApplication()).getApiInterface();
        showProgress();
        if(imageConfig!=null)
            viewModel.uploadImage(apiInterface, imageConfig.getMinAngle(), imageConfig.getMaxAngle(), imageConfig.getMinValue(), imageConfig.getMaxValue(), imageConfig.getUnit(),file, true);
        viewModel.getNetworkState().observe(CameraActivity.this, networkState -> {
            hideProgress();
            if ((networkState.getStatus() == NetworkState.Status.SUCCESS)) {
                showDialog(networkState.getMsg());
            } else if (networkState.getStatus() == NetworkState.Status.FAILED) {
                Toast.makeText(CameraActivity.this, "Fail to scan image, Please try again.!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void callLocalPython(File file) {

       Python python = Python.getInstance();
       PyObject pyObject = python.getModule("analog_reader");
       pyObject.callAttr("getAnalogMeterReading",file.getName(),file.getAbsolutePath());
    }

    @Override
    protected void onResume() {
        super.onResume();
        cameraView.open();
    }

    @Override
    protected void onPause() {
        super.onPause();
        cameraView.close();
    }

    private void showDialog(String reading){
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.app_name))
                .setMessage("Meter reading is " + reading)
                .setPositiveButton(android.R.string.yes, (dialog, which) -> dialog.cancel())
                .show();
    }

    public void showProgress() {
        progressBar.setVisibility(View.VISIBLE);
    }

    public void hideProgress() {
        progressBar.setVisibility(View.GONE);
    }
}
