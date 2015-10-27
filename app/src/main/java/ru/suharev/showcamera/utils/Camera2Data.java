package ru.suharev.showcamera.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.os.Build;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import ru.suharev.showcamera.R;

/**
 * Создан отдельный класс для обхода VerifyError на устройствах с API < 21
 */
public class Camera2Data {

    CaptureRequest.Builder mPreviewRequestBuilder;

    private CameraManager mCameraManager;
    private CameraDevice mCameraDevice;
    private CameraCaptureSession mCaptureSession;
    private Surface mSurface;

    private Context mContext;

    public Camera2Data(Context ctx) {
        mContext = ctx;
        mCameraManager = (CameraManager) mContext.getSystemService(Context.CAMERA_SERVICE);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public List<String> getCameraIdsLollipop() {
        List<String> cameraIds = new ArrayList<>();
        try {
            cameraIds = Arrays.asList(mCameraManager.getCameraIdList());
        } catch (CameraAccessException e) {
            Toast.makeText(mContext,
                    mContext.getResources().getString(R.string.toast_error_camera),
                    Toast.LENGTH_LONG).show();
        }
        return cameraIds;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void showVideoLollipop(int cameraId, final SurfaceHolder holder) {
        hideVideoLollipop();
        try {
            mCameraManager.openCamera(String.valueOf(cameraId), new CameraDevice.StateCallback() {
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
                    camera.close();
                    mCaptureSession = null;
                    mCameraDevice = null;
                }

                @Override
                public void onError(CameraDevice camera, int error) {
                    Toast.makeText(mContext,
                            mContext.getResources().getString(R.string.toast_error_camera),
                            Toast.LENGTH_LONG).show();
                    camera.close();
                    mCameraDevice = null;
                }
            }, null);

        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void hideVideoLollipop() {
        if (mCaptureSession != null) {
            mCaptureSession.close();
            mCaptureSession = null;
        }
        if (mCameraDevice != null) {
            mCameraDevice.close();
            mCameraDevice = null;
        }
    }

}
