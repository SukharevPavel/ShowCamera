package ru.suharev.showcamera.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.hardware.Camera;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import ru.suharev.showcamera.R;

/**
 * Фрагмент, в котором отображается лист доступных камер, а также сама картинка с камеры
 */
public class CameraData {

    private List<String> mCameraIds;
    private Context mContext;
    private CameraManager mCameraManager;

    public CameraData(Context ctx) {
        mContext = ctx;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            initCameraLollipop();
        else
            initCamera();
        fixCameraIds();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void initCameraLollipop() {
        mCameraManager = (CameraManager) mContext.getSystemService(Context.CAMERA_SERVICE);
        try {
            mCameraIds = Arrays.asList(mCameraManager.getCameraIdList());
        } catch (CameraAccessException e) {
            Toast.makeText(mContext,
                    mContext.getResources().getString(R.string.toast_error_camera),
                    Toast.LENGTH_LONG).show();
        }
    }

    private void initCamera() {
        int numbers = Camera.getNumberOfCameras();
        for (int i = 0; i < numbers; i++) {
            mCameraIds.add(String.valueOf(i));
        }
    }
    
    private void fixCameraIds(){
        for (int i = 0; i< mCameraIds.size(); i++) {
            String newId = mContext.getResources().
                    getString(R.string.camera_sugar).concat(mCameraIds.get(i));
                    mCameraIds.add(i,newId);
        }
    }


    private List<String> getCameraIds(){
        return mCameraIds;
    }


}
