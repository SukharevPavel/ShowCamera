package ru.suharev.showcamera.utils;

import android.content.Context;
import android.hardware.Camera;
import android.os.Build;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ru.suharev.showcamera.R;

/**
 * Класс для работы с Camera API и Camera2 API
 */
public class CameraData {

    private List<String> mCameraSugarIds;
    private Context mContext;

    private Camera2Data mCamera2Data;
    /*
    * Camera2 API variables
     */

    /*
    * Camera api
     */
    private Camera mCamera;

    public CameraData(Context ctx) {
        mContext = ctx;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            mCamera2Data = new Camera2Data(mContext);
    }

    public List<String> getCameraIds() {
        mCameraSugarIds = new ArrayList<>();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            return mCamera2Data.getCameraIdsLollipop();
        else return getCameraIdsOld();
    }


    public List<String> getCameraIdsOld() {
        List<String> cameraIds = new ArrayList<>();
        int numbers = Camera.getNumberOfCameras();
        for (int i = 0; i < numbers; i++) {
            cameraIds.add(String.valueOf(i));
        }
        return cameraIds;
    }

    public void showVideo(int cameraId, SurfaceView view) {
        SurfaceHolder holder = view.getHolder();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            mCamera2Data.showVideoLollipop(cameraId, holder);
        else
            showVideoOld(cameraId, holder);


    }


    private void showVideoOld(int cameraId, SurfaceHolder holder) {
        hideVideo();
        try {
            mCamera = Camera.open(cameraId);
        } catch (Exception e) {
            Toast.makeText(mContext, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            return;
        }
        try {
            mCamera.setPreviewDisplay(holder);
            mCamera.startPreview();
        } catch (IOException e) {
            Toast.makeText(mContext,
                    mContext.getResources().getString(R.string.toast_error_camera),
                    Toast.LENGTH_LONG).show();
        }
    }

    public void hideVideo() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            mCamera2Data.hideVideoLollipop();
        else
            hideVideoOld();
    }

    private void hideVideoOld() {
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }


}
