package au.com.australiandroid.androidclient.helpers;

import android.util.Log;

import org.webrtc.SdpObserver;
import org.webrtc.SessionDescription;

public class SimpleSdpObserver implements SdpObserver {
    private static final String TAG = "SimpleSdpObserver";
    @Override
    public void onCreateSuccess(SessionDescription sessionDescription) {
        Log.d(TAG, "onCreateSuccess: created the remote description successfully");
    }

    @Override
    public void onSetSuccess() {
        Log.d(TAG, "onSetSuccess: set the remote description successfully");
    }

    @Override
    public void onCreateFailure(String s) {
        Log.e(TAG, "onCreateFailure: failed to create the remote description: " + s);
    }

    @Override
    public void onSetFailure(String s) {
        Log.e(TAG, "onSetFailure: failed to set the remote description: " + s);
    }
}
