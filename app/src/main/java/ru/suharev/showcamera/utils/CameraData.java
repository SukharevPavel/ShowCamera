package ru.suharev.showcamera.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.os.Build;
import android.util.Size;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import ru.suharev.showcamera.R;

/**
 * Фрагмент, в котором отображается лист доступных камер, а также сама картинка с камеры
 */
public class CameraData {

    private List<String> mCameraIds;
    private List<String> mCameraSugarIds;
    private Context mContext;

    /*
    * Camera2 API variables
     */
    private CameraManager mCameraManager;
    private CameraDevice mCameraDevice;
    private CameraCaptureSession mCaptureSession;
    private Surface mSurface;
    CaptureRequest.Builder mPreviewRequestBuilder;


    /*
    * Camera api
     */
    private Camera mCamera;

    public CameraData(Context ctx) {
        mContext = ctx;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            initCameraLollipop();
        else
            initCameraOld();
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

    private void initCameraOld() {
        mCameraIds = new ArrayList<>();
        int numbers = Camera.getNumberOfCameras();
        for (int i = 0; i < numbers; i++) {
            mCameraIds.add(String.valueOf(i));
        }
    }
    
    private void fixCameraIds(){
        mCameraSugarIds = new ArrayList<>();
        for (String id : mCameraIds) {
            String newId = mContext.getResources().
                    getString(R.string.camera_sugar).concat(id);
            mCameraSugarIds.add(newId);
        }
    }


    public List<String> getCameraIds() {
        return mCameraSugarIds;
    }

    public void showVideo(int cameraId, SurfaceView view) {
        SurfaceHolder holder = view.getHolder();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            showVideoLollipop(cameraId, holder);
        else
            showVideoOld(cameraId, holder);


    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void showVideoLollipop(int cameraId, final SurfaceHolder holder) {
        try {
            mCameraManager.openCamera(mCameraIds.get(cameraId), new CameraDevice.StateCallback() {
                @Override
                public void onOpened(CameraDevice camera) {
                    mCameraDevice = camera;
                    try {
                        mSurface = holder.getSurface();
                        mPreviewRequestBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
                        List<Surface> surface = Collections.singletonList(mSurface);
                        mPreviewRequestBuilder.addTarget(mSurface);
                        mCameraDevice.createCaptureSession(surface, new CameraCaptureSession.StateCallback() {
                            @Override
                            public void onConfigured(CameraCaptureSession session) {
                                mCaptureSession = session;
                                CaptureRequest mPreviewRequest = mPreviewRequestBuilder.build();
                                try {
                                    mCaptureSession.setRepeatingRequest(mPreviewRequest,
                                            null, null);
                                } catch (CameraAccessException e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onConfigureFailed(CameraCaptureSession session) {

                            }
                        }, null);
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onDisconnected(CameraDevice camera) {

                }

                @Override
                public void onError(CameraDevice camera, int error) {
                    Toast.makeText(mContext,
                            mContext.getResources().getString(R.string.toast_error_camera),
                            Toast.LENGTH_LONG).show();
                }
            }, null);

        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
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
            hideVideoLollipop();
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


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void hideVideoLollipop() {
        if (mCaptureSession != null) {
            mCaptureSession.close();
            mCaptureSession = null;
        }
        if (mCameraDevice != null) {
            mCameraDevice.close();
            mCameraDevice = null;
        }
        if (mSurface != null) {
            mSurface.release();
            mSurface = null;
        }
    }

}
