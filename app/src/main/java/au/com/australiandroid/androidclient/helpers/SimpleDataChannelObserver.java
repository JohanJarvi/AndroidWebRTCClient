package au.com.australiandroid.androidclient.helpers;

import android.util.Log;

import org.webrtc.DataChannel.Buffer;
import org.webrtc.DataChannel.Observer;

public class SimpleDataChannelObserver implements Observer {
    private static final String TAG = "SimpleDataChannelObs";

    /**
     * Constructor.
     */
    public SimpleDataChannelObserver() {}

    @Override
    public void onBufferedAmountChange(long l) {
        Log.d(TAG, "onBufferedAmountChange: the buffered amount changed to " + l);
    }

    @Override
    public void onStateChange() {
        Log.d(TAG, "onStateChange: the state has changed");
    }

    @Override
    public void onMessage(Buffer buffer) {
        Log.d(TAG, "onMessage: there was a new message: " + buffer.toString());
    }
}
