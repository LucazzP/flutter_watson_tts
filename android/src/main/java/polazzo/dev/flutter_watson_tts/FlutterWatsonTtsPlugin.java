package polazzo.dev.flutter_watson_tts;

import android.app.Activity;
import android.media.AudioTrack;
import android.os.AsyncTask;
import android.os.StrictMode;

import com.ibm.cloud.sdk.core.security.IamAuthenticator;
import com.ibm.watson.developer_cloud.android.library.audio.StreamPlayer;
import com.ibm.watson.text_to_speech.v1.TextToSpeech;
import com.ibm.watson.text_to_speech.v1.model.Marks;
import com.ibm.watson.text_to_speech.v1.model.SynthesizeOptions;
import com.ibm.watson.text_to_speech.v1.model.Timings;
import com.ibm.watson.text_to_speech.v1.websocket.SynthesizeCallback;
import com.sun.jna.Function;

import java.io.InputStream;
import java.util.HashMap;

import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry.Registrar;

/** FlutterWatsonTtsPlugin */
public class FlutterWatsonTtsPlugin implements MethodCallHandler {
    /** Plugin registration. */
    Activity context;
    MethodChannel methodChannel;
    public static void registerWith(Registrar registrar) {
        MethodChannel channel = new MethodChannel(registrar.messenger(), "flutter_watson_tts");
        channel.setMethodCallHandler(new FlutterWatsonTtsPlugin(registrar.activity(), channel));
    }

    public FlutterWatsonTtsPlugin(Activity activity, MethodChannel methodChannel) {
        this.context = activity;
        this.methodChannel = methodChannel;
        this.methodChannel.setMethodCallHandler(this);
    }

    StreamPlayerMy streamPlayer;
    TextToSpeech textToSpeech;
    String text;
    Result result;
    boolean sentResult = false;
    boolean _disposed = false;

    @Override
    public void onMethodCall(MethodCall call, Result result) {
        if(call.method.equals("init")){
            initTextToSpeechService(String.valueOf(call.argument("apiKey")));
        } else if(call.method.equals("speak")){
            this.result = result;
            sentResult = false;
            _disposed = false;
            this.text = String.valueOf(call.argument("text"));
            SpeakBackground speak = new SpeakBackground();
            speak.execute(this.text);
        } else if(call.method.equals("dispose")){
            streamPlayer.interrupt();
            sentResult = true;
            _disposed = true;
        }
    }

    private void initTextToSpeechService(String apiKey){
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);
        IamAuthenticator options = new IamAuthenticator(apiKey);

        TextToSpeech service = new TextToSpeech(options);
        service.setServiceUrl("https://stream.watsonplatform.net/text-to-speech/api");

        streamPlayer = new StreamPlayerMy(new DoOnReadyListener() {
            @Override
            public void doOnReady() {
                context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        methodChannel.invokeMethod("doOnReady", "");
                    }
                });
            }
        });
        textToSpeech = service;
    }

    private InputStream speak(String text){
        InputStream preLoad = null;
        if (text != null && text != "null") {
            SynthesizeOptions synthesizeOptions = new SynthesizeOptions.Builder()
                    .accept("audio/wav")
                    .text(text)
                    .voice(SynthesizeOptions.Voice.PT_BR_ISABELAV3VOICE)
                    .build();

            if(!_disposed){
                preLoad = textToSpeech.synthesize(synthesizeOptions).execute().getResult();
                if(_disposed) preLoad = null;
            }
        }
        return preLoad;
    }

    private class SpeakBackground extends AsyncTask<String, Void, Void>{
        InputStream preLoad = null;

        @Override
        protected Void doInBackground(String... params) {
            System.out.println(params[0]);
            preLoad = speak(params[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if(!_disposed && preLoad != null) {
                StreamPlayerBackground streamPlayerBackground = new StreamPlayerBackground();
                streamPlayerBackground.execute(preLoad);
            }
            super.onPostExecute(aVoid);
        }
    }

    private class StreamPlayerBackground extends AsyncTask<InputStream, Void, Void>{

        @Override
        protected Void doInBackground(InputStream... params) {
            if(!_disposed && params[0] != null) {
                streamPlayer.playStream(params[0]);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if(result != null && !sentResult) {
                result.success("Success");
                sentResult = true;
            }
            super.onPostExecute(aVoid);
        }
    }
}
