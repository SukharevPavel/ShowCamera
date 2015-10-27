package ru.suharev.showcamera.ui;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.app.ListFragment;
import android.support.v4.app.ActivityCompat;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
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

    private static final int REQUEST_CAMERA_PERMISSION = 1;
    private static final String FRAGMENT_DIALOG = "dialog";

    private CameraData mCameraData;
    private SurfaceView mSurfaceView;
    private ArrayAdapter<String> mAdapter;
    private int mCurrentCamera = CAMERA_DISABLED;

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


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null &&
                savedInstanceState.containsKey(EXTRA_CAMERA_NUMBER)) {
            mCurrentCamera = savedInstanceState.getInt(EXTRA_CAMERA_NUMBER);
            if (mCurrentCamera != CAMERA_DISABLED) {
                mSurfaceView.getHolder().addCallback(mSurfaceHolderCallback);
            }
        }

        if (getActivity().checkSelfPermission(Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            requestCameraPermission();
            return;
        }

        setAdapter();
    }

    private void setAdapter() {
        mAdapter = new ArrayAdapter<>(getContext(),
                R.layout.simple_list_item_cut_width,
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
    public void onPause() {
        if (mCurrentCamera != CAMERA_DISABLED) mCameraData.hideVideo();
        super.onPause();
    }

    private void requestCameraPermission() {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA},
                    REQUEST_CAMERA_PERMISSION);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length != 1 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                ErrorDialog.newInstance(getString(R.string.request_permission))
                        .show(getFragmentManager(), FRAGMENT_DIALOG);
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    public static class ErrorDialog extends DialogFragment {

        private static final String ARG_MESSAGE = "message";

        public static ErrorDialog newInstance(String message) {
            ErrorDialog dialog = new ErrorDialog();
            Bundle args = new Bundle();
            args.putString(ARG_MESSAGE, message);
            dialog.setArguments(args);
            return dialog;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Activity activity = getActivity();
            return new AlertDialog.Builder(activity)
                    .setMessage(getArguments().getString(ARG_MESSAGE))
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            activity.finish();
                        }
                    })
                    .create();
        }

    }

    final SurfaceHolder.Callback mSurfaceHolderCallback = new SurfaceHolder.Callback() {
        @Override
        public void surfaceCreated(SurfaceHolder holder) {

        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            mCameraData.showVideo(0, mSurfaceView);
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {

        }
    };

}
