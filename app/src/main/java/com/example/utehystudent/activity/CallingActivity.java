package com.example.utehystudent.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.utehystudent.R;

import org.jitsi.meet.sdk.BroadcastEvent;
import org.jitsi.meet.sdk.BroadcastIntentHelper;
import org.jitsi.meet.sdk.JitsiMeet;
import org.jitsi.meet.sdk.JitsiMeetActivity;
import org.jitsi.meet.sdk.JitsiMeetActivityDelegate;
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions;
import org.jitsi.meet.sdk.JitsiMeetUserInfo;

import java.net.MalformedURLException;
import java.net.URL;

import timber.log.Timber;

public class CallingActivity extends AppCompatActivity {

    String id;
    String name;
    String avt;
    Boolean hasMic;
    Boolean hasCam;
    TextView tvQL;
    Button btnReturn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_calling);

        Bundle bundle = getIntent().getExtras();

        id = bundle.getString("code");
        name = bundle.getString("name");
        avt = bundle.getString("avt_link");
        hasMic = bundle.getBoolean("mic");
        hasCam = bundle.getBoolean("camera");

        tvQL = findViewById(R.id.Calling_tvQL);
        btnReturn = findViewById(R.id.Calling_btnReturn);

        InitCalling();

        Event();
    }

    private void Event() {
        tvQL.setOnClickListener(view -> {
            this.finish();
        });
        btnReturn.setOnClickListener(view -> {
            startMeeting();
        });
    }

    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            onBroadcastReceived(intent);
        }
    };

    private void InitCalling() {
        //
        URL serverURL;
        try {
            serverURL = new URL("https://meet.jit.si");
        } catch (MalformedURLException e) {
            e.printStackTrace();
            throw new RuntimeException("Invalid server URL!");
        }
        JitsiMeetConferenceOptions defaultOptions
                = new JitsiMeetConferenceOptions.Builder()
                .setServerURL(serverURL)
                .setAudioMuted(false)
                .setVideoMuted(true)
                .setAudioOnly(false)
                .setFeatureFlag("welcomepage.enabled", false)
                .build();
        JitsiMeet.setDefaultConferenceOptions(defaultOptions);

        registerForBroadcastMessages();


        startMeeting();
    }

    private void startMeeting() {
        JitsiMeetUserInfo info = new JitsiMeetUserInfo();
        info.setDisplayName(name);
        try {
            info.setAvatar(new URL(avt));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        JitsiMeetConferenceOptions options
                = new JitsiMeetConferenceOptions.Builder()
                .setRoom(id)
                .setAudioMuted(!hasMic)
                .setUserInfo(info)
                .setVideoMuted(!hasCam)
                .build();

        JitsiMeetActivity.launch(this, options);
    }

    private void registerForBroadcastMessages() {
        IntentFilter intentFilter = new IntentFilter();
        for (BroadcastEvent.Type type : BroadcastEvent.Type.values()) {
            intentFilter.addAction(type.getAction());
        }
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, intentFilter);
    }

    // Example for handling different JitsiMeetSDK events
    private void onBroadcastReceived(Intent intent) {
        if (intent != null) {
            BroadcastEvent event = new BroadcastEvent(intent);

            switch (event.getType()) {
                case CONFERENCE_JOINED:
                    Timber.i("Conference Joined with url%s", event.getData().get("url"));
                    break;
                case PARTICIPANT_JOINED:
                    Timber.i("Participant joined%s", event.getData().get("name"));
                    break;
            }
        }
    }

    // Example for sending actions to JitsiMeetSDK
    private void hangUp() {
        Intent hangupBroadcastIntent = BroadcastIntentHelper.buildHangUpIntent();
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(hangupBroadcastIntent);
    }

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        JitsiMeetActivityDelegate.onNewIntent(intent);
    }

    @Override
    public void onRequestPermissionsResult(
            final int requestCode,
            final String[] permissions,
            final int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        JitsiMeetActivityDelegate.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onResume() {
        super.onResume();
        JitsiMeetActivityDelegate.onHostResume(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        JitsiMeetActivityDelegate.onHostPause(this);
    }

    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
        super.onDestroy();
    }
}