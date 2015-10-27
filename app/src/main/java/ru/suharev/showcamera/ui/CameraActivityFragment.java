package ru.suharev.showcamera.ui;

import android.app.ListFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.List;

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
    private final SurfaceHolder.Callback mSurfaceHolderCallback = new SurfaceHolder.Callback() {
        @Override
        public void surfaceCreated(SurfaceHolder holder) {

        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            if (mCurrentCamera != CAMERA_DISABLED) {
                mCameraData.showVideo(mCurrentCamera, mSurfaceView);
            }
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {

        }
    };


    public CameraActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_camera, container, false);
        mCameraData = new CameraData(getActivity());
        mSurfaceView = (SurfaceView) v.findViewById(R.id.video_view);
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null &&
                savedInstanceState.containsKey(EXTRA_CAMERA_NUMBER)) {
            mCurrentCamera = savedInstanceState.getInt(EXTRA_CAMERA_NUMBER);


        }

        setAdapter();
    }

    public void setAdapter() {
        mAdapter = new ArrayAdapter<>(getActivity(),
                R.layout.simple_list_item_cut_width,
                addSugar(mCameraData.getCameraIds()));
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
        mAdapter.notifyDataSetChanged();
    }

    private List<String> addSugar(List<String> input) {
        List<String> result = new ArrayList<>();
        for (String element : input) {
            result.add(getResources().getString(R.string.camera_sugar).concat(element));
        }
        return result;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(EXTRA_CAMERA_NUMBER, mCurrentCamera);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onResume() {
        super.onResume();
        mSurfaceView.getHolder().addCallback(mSurfaceHolderCallback);
        if (mSurfaceView.getHolder().getSurface().isValid()) {
            if (mCurrentCamera != CAMERA_DISABLED) {
                mCameraData.showVideo(mCurrentCamera, mSurfaceView);
            }
        }
    }

    @Override
    public void onPause() {
        if (mCurrentCamera != CAMERA_DISABLED) mCameraData.hideVideo();
        mSurfaceView.getHolder().removeCallback(mSurfaceHolderCallback);
        super.onPause();
    }


}
