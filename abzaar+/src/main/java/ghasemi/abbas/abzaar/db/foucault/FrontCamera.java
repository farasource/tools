package ghasemi.abbas.abzaar.db.foucault;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;

import android.os.Build;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;

import ghasemi.abbas.abzaar.Main;


class FrontCamera {
    private Camera camera;
    private Camera.PictureCallback call;

    FrontCamera(final Activity context, SurfaceView surfaceView) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            final int cameraId = getFrontCameraId();
            if (cameraId != -1) {
                SurfaceHolder holder = surfaceView.getHolder();
                holder.addCallback(new SurfaceHolder.Callback() {
                    @Override
                    public void surfaceCreated(SurfaceHolder surfaceHolder) {
                        try {
                            Camera camera = Camera.open(cameraId);
                            camera.setPreviewDisplay(surfaceHolder);
                            FrontCamera.this.camera = camera;
                        } catch (Exception e) {
//                            Log.e(FrontCamera.class.getName(),e.toString());
                        }
                    }

                    @Override
                    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i2, int i3) {
                        if (camera == null) {
                            return;
                        }
                        Camera.Parameters cameraParameters = camera.getParameters();
                        if (cameraParameters == null) {
                            return;
                        }
                        camera.setParameters(cameraParameters);
                        call = new Camera.PictureCallback() {
                            @Override
                            public void onPictureTaken(byte[] data, Camera camera) {
                                Bitmap bm = BitmapFactory.decodeByteArray(data, 0, data.length);
                                Matrix matrix = new Matrix();
                                matrix.postRotate(270.0f);
                                bm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);
                                try {
                                    if(Main.activity == null){
                                        Main.activity = context;
                                    }
                                    TSQL.getInstanse().addImgfoucault(bm);
                                } catch (Exception e) {
                                    //
                                }
                                context.finishAffinity();
                            }
                        };
                        camera.startPreview();
                        camera.takePicture(null, null, call);
                    }

                    @Override
                    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
                        stopCamera();
                    }
                });
                holder.setType(3);
            }
        }
    }

    void stopCamera() {
        if (camera == null) {
            return;
        }
        camera.stopPreview();
        camera.release();
        camera = null;
    }

    private int getFrontCameraId() {
        int camId = -1;
        int numberOfCameras = Camera.getNumberOfCameras();
        CameraInfo ci = new CameraInfo();

        for (int i = 0; i < numberOfCameras; i++) {
            Camera.getCameraInfo(i, ci);
            if (ci.facing == CameraInfo.CAMERA_FACING_FRONT) {
                camId = i;
            }
        }

        return camId;
    }

}