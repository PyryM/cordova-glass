package com.rossgerbasi.cordova.plugins.glass;

import android.app.Activity;
import android.speech.RecognizerIntent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.content.Intent;
import org.apache.cordova.*;
import org.json.JSONArray;
import org.json.JSONException;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;


public class Glass extends CordovaPlugin implements View.OnGenericMotionListener {
    private static final String LOG_TAG = "CordovaGlass";

    private ArrayList<GlassMotionEventManager> managers;
    private static final int SPEECH_REQUEST = 2014;
    public CallbackContext speechCallbackContext;

    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
//        Log.d("Glass", "Init Glass Core Plugin");
        super.initialize(cordova, webView);

        // List of all Motion Event Managers
        this.managers = new ArrayList<GlassMotionEventManager>();

        //Test for Keep Awake Preference
        boolean keepAwake = webView.getProperty("rossgerbasi.glass.keepAwake", "false").equals("true");
        if(keepAwake) {
            cordova.getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }

        // Test for Touch Disabling
        boolean touchDisabled = webView.getProperty("rossgerbasi.glass.touchDisabled", "false").equals("true");
        if (!touchDisabled) {
            managers.add(new GlassTouchManager(this.webView));
        }

        // Test for Gesture Disabling
        boolean gesturesDisabled = webView.getProperty("rossgerbasi.glass.gesturesDisabled", "false").equals("true");
        if (!gesturesDisabled) {
            managers.add(new GlassGestureManager(this.webView));
        }

        this.webView.setOnGenericMotionListener(this);
    }

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (action.equals("get_launch_params")) {
            ArrayList<String> voiceResults = this.cordova.getActivity().getIntent().getExtras().getStringArrayList(RecognizerIntent.EXTRA_RESULTS);
            JSONArray jsArray;
            if (voiceResults != null) {
                jsArray = new JSONArray(voiceResults);
            } else {
                jsArray = new JSONArray();
            }
            PluginResult result = new PluginResult(PluginResult.Status.OK, jsArray);
            callbackContext.sendPluginResult(result);
        } else if(action.equals("do_speech_recognition")) {
            this.speechCallbackContext = callbackContext;
            
            PluginResult r = new PluginResult(PluginResult.Status.NO_RESULT);
            r.setKeepCallback(true);
            callbackContext.sendPluginResult(r);

            this.startSpeechRecognition();
            return true;
        }

        return super.execute(action, args, callbackContext);
    }

    @Override
    public boolean onGenericMotion(View v, MotionEvent event) {
        for(GlassMotionEventManager manager : managers){
            manager.process(event);
        }
        return true;
    }

    /**
     * Get text from the Glass's speech recognition.
     * When the speech recognition activity completes, the result is returned
     * in CordovaActivity.onActivityResult, which forwards the result to this.onActivityResult.
     *
     */
    public void startSpeechRecognition() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        this.cordova.startActivityForResult((CordovaPlugin) this, intent, SPEECH_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SPEECH_REQUEST && resultCode == Activity.RESULT_OK) {
            List<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            String spokenText = results.get(0);
            Log.d(LOG_TAG, "Got text: ");
            Log.d(LOG_TAG, spokenText);
            this.speechCallbackContext.success(spokenText);
        } else {
            Log.d(LOG_TAG, "REQUEST CODE WAS BAD??!");
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
