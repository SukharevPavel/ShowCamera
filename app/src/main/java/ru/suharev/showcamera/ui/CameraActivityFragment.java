package ru.suharev.showcamera.ui;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import ru.suharev.showcamera.R;
import ru.suharev.showcamera.utils.CameraData;

/**
 * A placeholder fragment containing a simple view.
 */
public class CameraActivityFragment extends ListFragment {

    public static final int CAMERA_DISABLED = -1;
    public static final String EXTRA_CAMERA_NUMBER = "camera_number";


    private CameraData mCameraData;
    private SurfaceView mSurfaceView;
    private ArrayAdapter<String> mAdapter;
    private int mCurrentCamera = CAMERA_DISABLED;

    public CameraActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_camera, container, false);
        mCameraData = new CameraData(getContext());
        mSurfaceView = (SurfaceView) v.findViewById(R.id.video_view);
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null &&
                savedInstanceState.containsKey(EXTRA_CAMERA_NUMBER)) {
            mCurrentCamera = savedInstanceState.getInt(EXTRA_CAMERA_NUMBER);
            if (mCurrentCamera != CAMERA_DISABLED) {
                mCameraData.showVideo(mCurrentCamera, mSurfaceView);
            }
        }
        setAdapter();
    }

    private void setAdapter() {
        mAdapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_list_item_1,
                mCameraData.getCameraIds());
        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mCurrentCamera != position) {
                    mCurrentCamera = position;
                    mCameraData.showVideo(position, mSurfaceView);
                } else {
                    mCurrentCamera = CAMERA_DISABLED;
                    mCameraData.hideVideo();
                }
            }
        });
        setListAdapter(mAdapter);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(EXTRA_CAMERA_NUMBER, mCurrentCamera);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (mCurrentCamera != CAMERA_DISABLED) mCameraData.hideVideo();
    }




}
