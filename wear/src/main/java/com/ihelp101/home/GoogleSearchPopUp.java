package com.ihelp101.home;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PowerManager;
import android.speech.RecognizerIntent;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import java.util.List;

public class GoogleSearchPopUp extends Activity implements
        MessageApi.MessageListener,
        GoogleApiClient.ConnectionCallbacks {

    private GoogleApiClient client;
    private static final int SPEECH_REQUEST_CODE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        displaySpeechRecognizer();

        client = new GoogleApiClient.Builder(GoogleSearchPopUp.this)
                .addConnectionCallbacks(GoogleSearchPopUp.this)
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(ConnectionResult result) {
                        Log.i(MyActivity.class.getSimpleName(), "Connection failed");
                    }
                })
                .addApi(Wearable.API)
                .build();

        client.connect();
    }

    public void displaySpeechRecognizer() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        startActivityForResult(intent, SPEECH_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        if (requestCode == SPEECH_REQUEST_CODE && resultCode == RESULT_OK) {
            List<String> results = data.getStringArrayListExtra(
                    RecognizerIntent.EXTRA_RESULTS);
            String spokenText = results.get(0);

            System.out.println(spokenText);

            sendMessage(spokenText, null);

            finish();
        } else {
            finish();
        }

        finish();
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void sendMessage(final String message, final byte[] payload) {
        Log.i(MyActivity.class.getSimpleName(), "WEAR Sending message " + message);
        Wearable.NodeApi.getConnectedNodes(client).setResultCallback(new ResultCallback<NodeApi.GetConnectedNodesResult>() {
            @Override
            public void onResult(NodeApi.GetConnectedNodesResult getConnectedNodesResult) {
                List<Node> nodes = getConnectedNodesResult.getNodes();
                for (Node node : nodes) {
                    Log.i(MyActivity.class.getSimpleName(), "WEAR sending " + message + " to " + node);
                    Wearable.MessageApi.sendMessage(client, node.getId(), message, payload).setResultCallback(new ResultCallback<MessageApi.SendMessageResult>() {
                        @Override
                        public void onResult(MessageApi.SendMessageResult sendMessageResult) {
                    Log.i(MyActivity.class.getSimpleName(), "WEAR Result " + sendMessageResult.getStatus());
                    finish();
                        }
                    });
                }

            }
        });
    }


    @Override
    public void onConnected(Bundle bundle) {
        Wearable.MessageApi.addListener(client, this);
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(MyActivity.class.getSimpleName(), "Connection failed");
    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        Log.i(MyActivity.class.getSimpleName(), ""+messageEvent.getPath());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        System.out.println("Destroyed");

        Wearable.MessageApi.removeListener(client, this);
        client.disconnect();
        finish();
    }
}
