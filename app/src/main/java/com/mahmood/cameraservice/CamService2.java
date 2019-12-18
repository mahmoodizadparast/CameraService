package com.mahmood.cameraservice;

import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.media.MediaScannerConnection;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.view.SurfaceView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

public class CamService2 extends Service {
    private static final String IMAGE_DIRECTORY = "/CustomImage";

    Camera cam;
    Camera.Parameters param;
    Camera.PictureCallback rawCallback = new Camera.PictureCallback() {
        public void onPictureTaken(byte[] data, Camera camera) {
            Log.d("CAMERA", "onPictureTaken - raw");

            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
//                Intent intent = new Intent(MainActivity.this,PictureActivity.class);
//                startActivity(intent);
            saveImage(bitmap);

            camera.stopPreview();
            camera.release();
        }
    };

    public String saveImage(Bitmap myBitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        myBitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
        File wallpaperDirectory = new File(
                Environment.getExternalStorageDirectory() + IMAGE_DIRECTORY);
        // have the object build the directory structure, if needed.

        if (!wallpaperDirectory.exists()) {
            Log.d("dirrrrrr", "" + wallpaperDirectory.mkdirs());
            wallpaperDirectory.mkdirs();
        }

        try {
            File f = new File(wallpaperDirectory, Calendar.getInstance()
                    .getTimeInMillis() + ".jpg");
            f.createNewFile();   //give read write permission
            FileOutputStream fo = new FileOutputStream(f);
            fo.write(bytes.toByteArray());
            MediaScannerConnection.scanFile(this,
                    new String[]{f.getPath()},
                    new String[]{"image/jpeg"}, null);
            fo.close();
            Log.d("TAG", "File Saved::--->" + f.getAbsolutePath());

            return f.getAbsolutePath();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return "";

    }

    Camera.PictureCallback jpgCallback = new Camera.PictureCallback() {
        public void onPictureTaken(byte[] data, Camera camera) {
            Log.d("CAMERA", "onPictureTaken - jpg");
            camera.stopPreview();
            camera.release();
        }
    };

    Camera.ShutterCallback shutterCallback = new Camera.ShutterCallback() {
        public void onShutter() {
            Log.i("CAMERA", "onShutter'd");
        }
    };


    public CamService2() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            cam = Camera.open();
            Log.i("CAMERA", "Success");
        } catch (RuntimeException e) {
            Log.e("CAMERA", "Camera currently unavailable");
            e.printStackTrace();
        }
        try {
            param = cam.getParameters();
            cam.setParameters(param);
            Log.i("CAMERA", "Success");
        } catch (Exception e1) {
            Log.e("CAMERA", "Parameter problem");
            e1.printStackTrace();
        }
        try {
            SurfaceView view = new SurfaceView(this);
            cam.setPreviewDisplay(view.getHolder());
            cam.startPreview();
            Log.i("CAMERA", "Success");
        } catch (Exception e) {
            Log.e("CAMERA", "Surface Problem");
            e.printStackTrace();
        }
        try {
            cam.takePicture(shutterCallback, rawCallback, null);
            Log.i("CAMERA", "Success");
        } catch (Exception e) {
            Log.e("CAMERA", "Click Failure");
            e.printStackTrace();
        }
        // Commented out following line and moved it into your callbacks
        //cam.release();
        return super.onStartCommand(intent, flags, startId);
    }
}
