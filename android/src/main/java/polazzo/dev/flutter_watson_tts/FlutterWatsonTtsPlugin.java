package polazzo.dev.flutter_watson_tts;

import android.media.AudioTrack;
import android.os.AsyncTask;
import android.os.StrictMode;

import com.ibm.cloud.sdk.core.security.IamAuthenticator;
import com.ibm.watson.developer_cloud.android.library.audio.StreamPlayer;
import com.ibm.watson.text_to_speech.v1.TextToSpeech;
import com.ibm.watson.text_to_speech.v1.model.SynthesizeOptions;

import java.io.InputStream;

import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry.Registrar;

/** FlutterWatsonTtsPlugin */
public class FlutterWatsonTtsPlugin implements MethodCallHandler {
    /** Plugin registration. */
    public static void registerWith(Registrar registrar) {
        final MethodChannel channel = new MethodChannel(registrar.messenger(), "flutter_watson_tts");
        channel.setMethodCallHandler(new FlutterWatsonTtsPlugin());
    }

    @Override
    public void onMethodCall(MethodCall call, Result result) {
        switch (call.method) {
            case "init":
                initTextToSpeechService(String.valueOf(call.argument("apiKey")));
            case "speak":
                text = String.valueOf(call.argument("text"));
                SpeakBackground speak = new SpeakBackground();
                speak.execute();
                result.success("Success");
            default:
                break;
        }
    }

    StreamPlayer streamPlayer;
    TextToSpeech textToSpeech;
    String text;
    String textOnPreLoad;

    private void initTextToSpeechService(String apiKey){
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);
        IamAuthenticator options = new IamAuthenticator(apiKey);

        TextToSpeech service = new TextToSpeech(options);
        service.setServiceUrl("https://stream.watsonplatform.net/text-to-speech/api");

        streamPlayer = new StreamPlayer();
        textToSpeech = service;
    }

    private void speak(String text){
        if (text != null && text != "null") {
            SynthesizeOptions synthesizeOptions = new SynthesizeOptions.Builder()
                    .text(text)
                    .accept("audio/wav")
                    .voice(SynthesizeOptions.Voice.PT_BR_ISABELAV3VOICE)
                    .build();

            InputStream preLoad = textToSpeech.synthesize(synthesizeOptions).execute().getResult();

            streamPlayer.playStream(preLoad);
        }
    }

    private class SpeakBackground extends AsyncTask<String, Void, Void>{
        @Override
        protected Void doInBackground(String... params) {
            speak(text);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            streamPlayer.interrupt();
            super.onPostExecute(aVoid);
        }
    }
}
