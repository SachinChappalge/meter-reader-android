package com.extentia.imagetotext.ui;

import android.Manifest;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.Rect;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.extentia.imagetotext.ImageToTextApp;
import com.extentia.imagetotext.R;
import com.extentia.imagetotext.api.ApiInterface;
import com.extentia.imagetotext.api.NetworkState;
import com.extentia.imagetotext.model.ImageConfig;
import com.extentia.imagetotext.viewmodel.ImageViewModel;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity implements SurfaceHolder.Callback {

    @BindView(R.id.transparentView)
    SurfaceView transparentView;

    @BindView(R.id.progress_bar_main)
    ProgressBar progressBar;
    private SurfaceHolder holder, holderTransparent;
    @BindView(R.id.cameraView)
    SurfaceView cameraView;
    private Camera camera;
    private ImageViewModel viewModel;
    private ImageConfig imageConfig;


    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageConfig=(ImageConfig) getIntent().getSerializableExtra("IMAGE_CONFIG");
        viewModel = ViewModelProviders.of(this).get(ImageViewModel.class);
        ButterKnife.bind(this);
        init();
    }

    private void init()
    {
        holder = cameraView.getHolder();
        holder.addCallback((SurfaceHolder.Callback) this);
        cameraView.setSecure(true);
        holderTransparent = transparentView.getHolder();
        holderTransparent.addCallback((SurfaceHolder.Callback) this);
        holderTransparent.setFormat(PixelFormat.TRANSLUCENT);
        transparentView.setZOrderMediaOverlay(true);
    }

    private void Draw() {
        Canvas canvas = holderTransparent.lockCanvas();
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.GREEN);
        paint.setStrokeWidth(3);
        int canvasW = canvas.getWidth();
        int canvasH = canvas.getHeight();
        Point centerOfCanvas = new Point(canvasW / 2, canvasH / 2);
        int rectW = 1000;
        int rectH = 1000;
        int left = centerOfCanvas.x - (rectW / 2);
        int top = centerOfCanvas.y - (rectH / 2);
        int right = centerOfCanvas.x + (rectW / 2);
        int bottom = centerOfCanvas.y + (rectH / 2);
        Rect rect = new Rect(left, top, right, bottom);
        canvas.drawRect(rect, paint);
        holderTransparent.unlockCanvasAndPost(canvas);
    }


    @Override

    public void surfaceCreated(SurfaceHolder holder) {
        try {
            synchronized (holder) {
                Draw();
            }   //call a draw method
            camera = Camera.open();
        } catch (Exception e) {
            Log.i("Exception", e.toString());
            return;
        }
        Camera.Parameters param;
        param = camera.getParameters();
        param.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
        Display display = ((WindowManager) getSystemService(WINDOW_SERVICE)).getDefaultDisplay();
        if (display.getRotation() == Surface.ROTATION_0) {
            camera.setDisplayOrientation(90);
        }
        camera.setParameters(param);
        try {
            camera.setPreviewDisplay(holder);
            camera.startPreview();
        } catch (Exception e) {
            return;
        }
    }
    Camera.ShutterCallback myShutterCallback = new Camera.ShutterCallback(){

        public void onShutter() {
            // TODO Auto-generated method stub
        }};

    Camera.PictureCallback myPictureCallback_RAW = new Camera.PictureCallback(){

        public void onPictureTaken(byte[] arg0, Camera arg1) {
            // TODO Auto-generated method stub
        }};

    Camera.PictureCallback myPictureCallback_JPG = new Camera.PictureCallback(){

        public void onPictureTaken(byte[] arg0, Camera arg1) {
            // TODO Auto-generated method stub
            Bitmap bitmapPicture = BitmapFactory.decodeByteArray(arg0, 0, arg0.length);
            Bitmap correctBmp = Bitmap.createBitmap(bitmapPicture, 0, 0, bitmapPicture.getWidth(), bitmapPicture.getHeight(), null, true);
            try {
                ContextWrapper cw = new ContextWrapper(MainActivity.this);
                String fullPath =cw.getExternalFilesDir(Environment.DIRECTORY_MUSIC).toString();
                File directory = cw.getExternalFilesDir(Environment.DIRECTORY_MUSIC);
                File file = new File(directory, "/" + "Meter.jpg");
                OutputStream os = new BufferedOutputStream(new FileOutputStream(file));
                correctBmp.compress(Bitmap.CompressFormat.JPEG, 100, os);
                os.close();
                ApiInterface apiInterface =((ImageToTextApp) getApplication()).getApiInterface();
                showProgress();
                if(imageConfig!=null)
                viewModel.uploadImage(apiInterface, imageConfig.getMinAngle(), imageConfig.getMaxAngle(), imageConfig.getMinValue(), imageConfig.getMaxValue(), imageConfig.getUnit(),file, true);
                viewModel.getNetworkState().observe(MainActivity.this, new Observer<NetworkState>() {
                    @Override
                    public void onChanged(@Nullable NetworkState networkState) {
                        hideProgress();
                        if ((networkState.getStatus() == NetworkState.Status.SUCCESS)) {
                            Toast.makeText(MainActivity.this, "Image uploaded successfully..!!", Toast.LENGTH_SHORT).show();
                        } else if (networkState.getStatus() == NetworkState.Status.FAILED) {
                            Toast.makeText(MainActivity.this, "Fail to scan image, Please try again.!", Toast.LENGTH_SHORT).show();
                        }
                        try {
                            camera.setPreviewDisplay(holder);
                            camera.startPreview();
                        } catch (Exception e) {
                        }
                    }
                });
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            Toast.makeText(MainActivity.this, "Image Captured..!!", Toast.LENGTH_SHORT).show();
        }};

    @OnClick(R.id.captureTV)
    void handleCaptureTV()
    {
        if(camera != null)
        {
            camera.takePicture(myShutterCallback, myPictureCallback_RAW, myPictureCallback_JPG);

        }
    }

    @Override

    protected void onDestroy() {
        super.onDestroy();
    }

    @Override

    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        refreshCamera();
    }

    public void refreshCamera() {

        if (holder.getSurface() == null) {
            return;
        }
        try {
            camera.stopPreview();
        } catch (Exception e) {
        }
        try {
            camera.setPreviewDisplay(holder);
            camera.startPreview();
        } catch (Exception e) {
        }

    }



    @Override

    public void surfaceDestroyed(SurfaceHolder holder) {
        camera.release();
    }


    public void showProgress() {
        progressBar.setVisibility(View.VISIBLE);
    }

    public void hideProgress() {
        progressBar.setVisibility(View.GONE);
    }


}