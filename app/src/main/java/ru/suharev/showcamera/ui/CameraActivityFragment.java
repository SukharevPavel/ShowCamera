package ru.suharev.showcamera.ui;

import android.content.Context;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;

import ru.suharev.showcamera.R;
import ru.suharev.showcamera.utils.CameraData;

/**
 * A placeholder fragment containing a simple view.
 */
public class CameraActivityFragment extends Fragment {

    private CameraData mCameraData;
    private SurfaceView mSurfaceView;

    public CameraActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_camera, container, false);
        mCameraData = new CameraData(getContext());
        mSurfaceView = (SurfaceView) v.findViewById(R.id.video_view);
        return v;
    }


}
