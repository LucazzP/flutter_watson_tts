package polazzo.dev.flutter_watson_tts;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.util.Log;

import com.sun.jna.Function;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;


interface DoOnReadyListener {
    void doOnReady();
}

/**
 * Exposes the ability to play raw audio data from an InputStream.
 */
public final class StreamPlayerMy {

    public StreamPlayerMy(DoOnReadyListener doOnReady){
        this.doOnReady = doOnReady;
    }

    private final String TAG = "StreamPlayer";
    // default sample rate for .wav from Watson TTS
    // see https://console.bluemix.net/docs/services/text-to-speech/http.html#format
    private final int DEFAULT_SAMPLE_RATE = 22050;

    private AudioTrack audioTrack;

    public DoOnReadyListener doOnReady;

    private static byte[] convertStreamToByteArray(InputStream is) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buff = new byte[10240];
        int i;
        while ((i = is.read(buff, 0, buff.length)) > 0) {
            baos.write(buff, 0, i);
        }

        return baos.toByteArray();
    }

    /**
     * Play the given InputStream. The stream must be a PCM audio format with a sample rate of 22050.
     *
     * @param stream the stream derived from a PCM audio source
     */
    public void playStream(InputStream stream) {
        try {
            byte[] data = convertStreamToByteArray(stream);
            int headSize = 44, metaDataSize = 48;
            int destPos = headSize + metaDataSize;
            int rawLength = data.length - destPos;
            byte[] d = new byte[rawLength];
            System.arraycopy(data, destPos, d, 0, rawLength);

            this.doOnReady.doOnReady();

            initPlayer(DEFAULT_SAMPLE_RATE);
            audioTrack.write(d, 0, d.length);
            stream.close();
            if (audioTrack != null && audioTrack.getState() != AudioTrack.STATE_UNINITIALIZED) {
                audioTrack.release();
            }
        } catch (IOException e2) {
            Log.e(TAG, e2.getMessage());
        }
    }

    /**
     * Play the given InputStream. The stream must be a PCM audio format.
     *
     * @param stream the stream derived from a PCM audio source
     * @param sampleRate the sample rate for the provided stream
     */
    public void playStream(InputStream stream, int sampleRate) {
        try {
            byte[] data = convertStreamToByteArray(stream);
            int headSize = 44, metaDataSize = 48;
            int destPos = headSize + metaDataSize;
            int rawLength = data.length - destPos;
            byte[] d = new byte[rawLength];
            System.arraycopy(data, destPos, d, 0, rawLength);
            initPlayer(sampleRate);
            audioTrack.write(d, 0, d.length);
            stream.close();
            if (audioTrack != null && audioTrack.getState() != AudioTrack.STATE_UNINITIALIZED) {
                audioTrack.release();
            }
        } catch (IOException e2) {
            Log.e(TAG, e2.getMessage());
        }
    }

    /**
     * Interrupt the audioStream.
     */
    public void interrupt() {
        if (audioTrack != null) {
            if (audioTrack.getState() == AudioTrack.STATE_INITIALIZED
                    || audioTrack.getState() == AudioTrack.PLAYSTATE_PLAYING) {
                audioTrack.pause();
            }
            audioTrack.flush();
            audioTrack.release();
        }
    }

    /**
     * Initialize AudioTrack by getting buffersize
     *
     * @param sampleRate the sample rate for the audio to be played
     */
    private void initPlayer(int sampleRate) {
        synchronized (this) {
            int bufferSize = AudioTrack.getMinBufferSize(
                    sampleRate,
                    AudioFormat.CHANNEL_OUT_MONO,
                    AudioFormat.ENCODING_PCM_16BIT);
            if (bufferSize == AudioTrack.ERROR_BAD_VALUE) {
                throw new RuntimeException("Could not determine buffer size for audio");
            }

            audioTrack = new AudioTrack(
                    AudioManager.STREAM_MUSIC,
                    sampleRate,
                    AudioFormat.CHANNEL_OUT_MONO,
                    AudioFormat.ENCODING_PCM_16BIT,
                    bufferSize,
                    AudioTrack.PERFORMANCE_MODE_LOW_LATENCY
            );

            audioTrack.play();
        }
    }
}
