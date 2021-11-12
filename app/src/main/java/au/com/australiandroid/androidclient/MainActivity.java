package au.com.australiandroid.androidclient;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import org.webrtc.DataChannel;
import org.webrtc.DefaultVideoDecoderFactory;
import org.webrtc.EglBase;
import org.webrtc.IceCandidate;
import org.webrtc.MediaConstraints;
import org.webrtc.PeerConnection;
import org.webrtc.PeerConnection.IceConnectionState;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.SessionDescription;
import org.webrtc.VideoDecoderFactory;

import java.net.URISyntaxException;
import java.util.ArrayList;

import au.com.australiandroid.androidclient.helpers.CustomPeerConnectionObserver;
import au.com.australiandroid.androidclient.helpers.SimpleDataChannelObserver;
import au.com.australiandroid.androidclient.helpers.SimpleSdpObserver;
import io.socket.client.IO;
import io.socket.client.Socket;

import static io.socket.client.Socket.EVENT_CONNECT;
import static org.webrtc.SessionDescription.Type.OFFER;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private PeerConnectionFactory peerConnectionFactory;
    private PeerConnection peerConnection;
    private DataChannel dataChannel;

    private Socket socket;
    {
        try {
            socket = IO.socket("http://192.168.0.31:4000");
        } catch (URISyntaxException e) {
            Log.e(TAG, "instance initializer: failed to bind socket", e);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onPause() {
        super.onPause();
        closeConnections();
    }

    @Override
    protected void onStop() {
        super.onStop();
        closeConnections();
    }

    @Override
    protected void onResume() {
        super.onResume();

        initialise();
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        initialise();
    }

    private void initialise() {
        bindSocketEvents();
        createPeerConnectionFactory();
        initDataChannel();
        connectSocket();
    }

    private void closeConnections() {
        socket.disconnect();
        peerConnection.close();
    }

    private void bindSocketEvents() {
        socket.on(EVENT_CONNECT, args -> {
            socket.emit("watcher");
        }).on("broadcaster", args -> {
            socket.emit("watcher");
        }).on("offer", args -> {
            setRemoteDescription(args);
            performAnswer();
        }).on("candidate", this::addIceCandidate);
    }

    private void createPeerConnectionFactory() {
        PeerConnectionFactory.InitializationOptions initializationOptions =
                PeerConnectionFactory.InitializationOptions.builder(this)
                        .createInitializationOptions();

        PeerConnectionFactory.initialize(initializationOptions);
        PeerConnectionFactory.Options options = new PeerConnectionFactory.Options();

        EglBase eglBase = EglBase.create();
        VideoDecoderFactory decoderFactory = new DefaultVideoDecoderFactory(eglBase.getEglBaseContext());

        peerConnectionFactory = PeerConnectionFactory.builder()
                .setOptions(options)
                .setVideoDecoderFactory(decoderFactory)
                .createPeerConnectionFactory();

        PeerConnection.RTCConfiguration rtcConfig = new PeerConnection.RTCConfiguration(new ArrayList<>());
        createPeerConnection(rtcConfig);
    }

    private void createPeerConnection(PeerConnection.RTCConfiguration rtcConfig) {
        peerConnection = peerConnectionFactory.createPeerConnection(rtcConfig, new CustomPeerConnectionObserver() {
            @Override
            public void onIceCandidate(IceCandidate iceCandidate) {
                super.onIceCandidate(iceCandidate);
            }

            @Override
            public void onDataChannel(DataChannel dataChannel) {
                super.onDataChannel(dataChannel);
            }

            @Override
            public void onIceConnectionChange(IceConnectionState iceConnectionState) {
                super.onIceConnectionChange(iceConnectionState);
                if(iceConnectionState == IceConnectionState.CONNECTED){
                    Log.d(TAG, "onIceConnectionChange: is connected");
                }

                if(iceConnectionState == IceConnectionState.CLOSED){
                    Log.d(TAG, "onIceConnectionChange: is closed");
                }

                if(iceConnectionState == IceConnectionState.FAILED){
                    Log.d(TAG, "onIceConnectionChange: has failed");
                }
            }
        });
    }

    private void initDataChannel() {
        DataChannel.Init dcInit = new DataChannel.Init();
        dataChannel = peerConnection.createDataChannel("1", dcInit);
        dataChannel.registerObserver(new SimpleDataChannelObserver() {
            @Override
            public void onMessage(DataChannel.Buffer buffer) {
                super.onMessage(buffer);
                Log.d(TAG, "onMessage: new message in data channel");
            }
        });
    }

    private void setRemoteDescription(Object[] arguments) {
        JSONObject message = (JSONObject) arguments[1];
        try {
            String sdp = message.getString("sdp");
            SessionDescription sessionDescription = new SessionDescription(OFFER, sdp);
            peerConnection.setRemoteDescription(new SimpleSdpObserver(), sessionDescription);
        } catch (JSONException e) {
            Log.e(TAG, "setRemoteDescription: failed to parse JSON", e);
        }
    }

    private void performAnswer() {
        peerConnection.createAnswer(new SimpleSdpObserver() {
            @Override
            public void onCreateSuccess(SessionDescription sessionDescription) {
                super.onCreateSuccess(sessionDescription);
                peerConnection.setLocalDescription(new SimpleSdpObserver(), sessionDescription);
                socket.emit("answer", sessionDescription.description);
            }

            @Override
            public void onCreateFailure(String s) {
                Log.e(TAG, "onCreateFailure: failed to create answer: " + s);
            }
        }, new MediaConstraints());
    }

    private void addIceCandidate(Object[] arguments) {
        JSONObject message = (JSONObject) arguments[1];
        try {
            String sdpMid = message.getString("sdpMid");
            int sdpMLineIndex = message.getInt("sdpMLineIndex");
            String sdp = message.getString("candidate");
            peerConnection.addIceCandidate(
                    new IceCandidate(sdpMid, sdpMLineIndex, sdp));
        } catch (JSONException e) {
            Log.e(TAG, "addIceCandidate: failed to parse JSON object", e);
        }
    }

    private void connectSocket() {
        socket.connect();
    }
}