package com.mahmood.cameraservice;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.media.MediaScannerConnection;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

public class TakePicService extends Service {
    public static int count = 0;
    private Camera mCamera;
    private CameraPreview mPreview;
    private Camera.PictureCallback mPicture;
    private LinearLayout cameraPreview;
    private boolean cameraFront = false;
    public static Bitmap bitmap;
    private static final String IMAGE_DIRECTORY = "/CustomImage";

    public Context mcontext;

    public TakePicService() {
        mcontext = this;
        mCamera = Camera.open();
        mCamera.setDisplayOrientation(90);


    }

    private int findFrontFacingCamera() {

        int cameraId = -1;
        // Search for the front facing camera
        int numberOfCameras = Camera.getNumberOfCameras();
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                cameraId = i;
                cameraFront = true;
                break;
            }
        }
        return cameraId;

    }

    private int findBackFacingCamera() {
        int cameraId = -1;
        //Search for the back facing camera
        //get the number of cameras
        int numberOfCameras = Camera.getNumberOfCameras();
        //for every camera check
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                cameraId = i;
                cameraFront = false;
                break;

            }

        }
        return cameraId;
    }

    public void chooseCamera() {
        //if the camera preview is the front
        if (cameraFront) {
            int cameraId = findBackFacingCamera();
            if (cameraId >= 0) {
                //open the backFacingCamera
                //set a picture callback
                //refresh the preview

                mCamera = Camera.open(cameraId);
                mCamera.setDisplayOrientation(90);
                mPicture = getPictureCallback();
               // mPreview.refreshCamera(mCamera);
            }
        } else {
            int cameraId = findFrontFacingCamera();
            if (cameraId >= 0) {
                //open the backFacingCamera
                //set a picture callback
                //refresh the preview
                mCamera = Camera.open(cameraId);
                mCamera.setDisplayOrientation(90);
                mPicture = getPictureCallback();
               // mPreview.refreshCamera(mCamera);
            }
        }
    }

    private void releaseCamera() {
        // stop and release camera
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.setPreviewCallback(null);
            mCamera.release();
            mCamera = null;
        }
    }

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

    private Camera.PictureCallback getPictureCallback() {
        Camera.PictureCallback picture = new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                Log.d("mahmood", "Image Created");
                bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
//                Intent intent = new Intent(MainActivity.this,PictureActivity.class);
//                startActivity(intent);
                saveImage(bitmap);
            }
        };
        return picture;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        ChildThread t = new ChildThread();
        t.start();

        /*final Handler handler = new Handler(Looper.myLooper());
        handler.post(new Runnable() {

            @Override
            public void run() {
                int c=0;
*//*                for (int i=0;i<5;++i) {
                    c++;
                    Toast.makeText(TakePicService.this.getApplicationContext(), "My Awesome service toast..."+c, Toast.LENGTH_SHORT).show();

//                    try {
//                        Thread.sleep(5000);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
                }*//*

              // for (int i = 0; i < 3; i++)
                {
                    TakePicService.count++;
                    //Toast.makeText(TakePicService.this, "Servie Exe: "+TakePicService.count, Toast.LENGTH_SHORT).show();
                    Log.d("mahmood", count + "");
                   // mPreview = new CameraPreview(mcontext, mCamera);
                   // mCamera.startPreview();
                    releaseCamera();
                    chooseCamera();
                    mCamera.takePicture(null, null, mPicture);
//                    try {
//                        Thread.sleep(5000);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
                }

            }
        });
*/
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        releaseCamera();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }


    class ChildThread extends Thread {
        @Override
        public void run() {

            for (int i = 0; i < 10; i++)
            {
                TakePicService.count++;
                //Toast.makeText(TakePicService.this, "Servie Exe: "+TakePicService.count, Toast.LENGTH_SHORT).show();
                Log.d("mahmood", count + "");
                //mPreview = new CameraPreview(mcontext, mCamera);
                //mCamera.startPreview();
                releaseCamera();
                chooseCamera();
                mCamera.takePicture(null, null, mPicture);
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
