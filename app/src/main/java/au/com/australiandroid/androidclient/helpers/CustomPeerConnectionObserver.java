package au.com.australiandroid.androidclient.helpers;

import android.util.Log;

import org.webrtc.DataChannel;
import org.webrtc.IceCandidate;
import org.webrtc.MediaStream;
import org.webrtc.PeerConnection.IceConnectionState;
import org.webrtc.PeerConnection.IceGatheringState;
import org.webrtc.PeerConnection.Observer;
import org.webrtc.PeerConnection.SignalingState;
import org.webrtc.RtpReceiver;

public class CustomPeerConnectionObserver implements Observer {
    private static final String TAG = "CustomPeerConnectionObs";

    /**
     * Constructor.
     */
    public CustomPeerConnectionObserver() {}

    @Override
    public void onSignalingChange(SignalingState signalingState) {
        Log.d(TAG, "onSignalingChange: new signaling state is " + signalingState.name());
    }

    @Override
    public void onIceConnectionChange(IceConnectionState iceConnectionState) {
        Log.d(TAG, "onIceConnectionChange: new ice connection state is " + iceConnectionState.name());
    }

    @Override
    public void onIceConnectionReceivingChange(boolean b) {
        Log.d(TAG, "onIceConnectionReceivingChange: " + b);
    }

    @Override
    public void onIceGatheringChange(IceGatheringState iceGatheringState) {
        Log.d(TAG, "onIceGatheringChange: new ice gathering state is " + iceGatheringState.name());
    }

    @Override
    public void onIceCandidate(IceCandidate iceCandidate) {
        Log.d(TAG, "onIceCandidate: ice candidate ID is " + iceCandidate.sdpMid);
    }

    @Override
    public void onIceCandidatesRemoved(IceCandidate[] iceCandidates) {
        Log.d(TAG, String.format("onIceCandidatesRemoved: remove %d ice candidates", iceCandidates.length));
    }

    @Override
    public void onAddStream(MediaStream mediaStream) {
        Log.d(TAG, "onAddStream: adding media stream " + mediaStream.getId());
    }

    @Override
    public void onRemoveStream(MediaStream mediaStream) {
        Log.d(TAG, "onRemoveStream: removing media stream " + mediaStream.getId());
    }

    @Override
    public void onDataChannel(DataChannel dataChannel) {
        Log.d(TAG, "onDataChannel: something occurred on data channel " + dataChannel.id());
    }

    @Override
    public void onRenegotiationNeeded() {
        Log.d(TAG, "onRenegotiationNeeded: renegotiation is needed");
    }

    @Override
    public void onAddTrack(RtpReceiver rtpReceiver, MediaStream[] mediaStreams) {
        Log.d(TAG, String.format("onAddTrack: a track was added on rtp receiver ID '%s'; " +
                "there are now %d media streams",
                rtpReceiver.id(),
                mediaStreams.length));
    }
}
