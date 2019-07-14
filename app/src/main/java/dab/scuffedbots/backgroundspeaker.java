package dab.scuffedbots;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.speech.tts.TextToSpeech;

public class backgroundspeaker extends Service {

    private final IBinder myBinder = new MyLocalBinder();
    public backgroundspeaker() {
    }

    public class MyLocalBinder extends Binder {
        backgroundspeaker getServices(){
            return backgroundspeaker.this;
        }
    }

    public String Ahder(String category){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            savestuff.t1.speak(category, TextToSpeech.QUEUE_ADD,null,null);
        } else {
            savestuff.t1.speak(category, TextToSpeech.QUEUE_ADD, null);
        }
        return "done";
    }

    public String AhderNoWait(String category){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            savestuff.t1.speak(category, TextToSpeech.QUEUE_FLUSH,null,null);
        } else {
            savestuff.t1.speak(category, TextToSpeech.QUEUE_FLUSH, null);
        }
        return "done";
    }


    @Override
    public IBinder onBind(Intent intent) {
        return myBinder;
    }
}
