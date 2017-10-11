package pe.bravos.taxis.servicio;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import java.util.Locale;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by hbs on 22/07/17.
 */
public class TTSService extends Service implements TextToSpeech.OnInitListener{

    private String str;
    private TextToSpeech mTts;
    private static final String TAG="TTSService";
    Timer timer = new Timer();

    @Override

    public IBinder onBind(Intent arg0) {
        return null;
    }


    @Override
    public void onCreate() {
        mTts = new TextToSpeech(this,
                this  // OnInitListener
        );
        mTts.setSpeechRate(0.75f);
        Log.v(TAG, "oncreate_service");
        str ="";
        super.onCreate();
    }


    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        if (mTts != null) {
            mTts.stop();
            mTts.shutdown();
        }
        if(timer!=null) {
            timer.cancel();
        }
        super.onDestroy();
    }

    @Override
    public void onStart(Intent intent, int startId) {

        if(intent!=null)
        {
            Bundle extras = intent.getExtras();
            boolean banderaBucle=false;
            if(extras == null) {
                Log.d("Service","null");
            } else {
                Log.d("Service","not null");
                str= (String) extras.get("talk");
                if(Objects.equals(str, "Su taxi lo está esperando"))
                {
                    banderaBucle=true;
                }
            }
            final Handler handler = new Handler();

            if(banderaBucle)
            {
                TimerTask task = new TimerTask() {
                    @Override
                    public void run() {
                        handler.post(new Runnable() {
                            public void run() {
                                try {
                                    sayHello(str);
                                } catch (Exception e) {
                                    Log.e("error", e.getMessage());
                                }
                            }
                        });
                    }
                };
                timer.schedule(task, 0, 10000);  //ejecutar en intervalo de 30 segundos.
            }
            else {
                sayHello(str);
            }



            Log.v(TAG, "onstart_service");
        }

        super.onStart(intent, startId);
    }

    @Override
    public void onInit(int status) {
        Log.v(TAG, "oninit");
        if (status == TextToSpeech.SUCCESS) {
            int result = mTts.setLanguage(new Locale(Locale.getDefault().getLanguage()));
            if (result == TextToSpeech.LANG_MISSING_DATA ||
                    result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.v(TAG, "Language is not available.");
            } else {
                sayHello(str);
            }
        } else {
            Log.v(TAG, "Could not initialize TextToSpeech.");
        }
    }

    private void sayHello(String str) {

        AudioManager am = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        int amStreamMusicMaxVol = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        am.setStreamVolume(AudioManager.STREAM_MUSIC, amStreamMusicMaxVol, 0);
        mTts.speak(str,
                TextToSpeech.QUEUE_FLUSH,
                null);
    }

}
