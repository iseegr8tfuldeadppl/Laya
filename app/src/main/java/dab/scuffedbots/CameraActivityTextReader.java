package dab.scuffedbots;

import android.Manifest;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.IBinder;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import java.io.IOException;
import java.util.Locale;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

public class CameraActivityTextReader extends Activity {

    String selectedlanguage;

    SurfaceView cameraView;
    TextView textView;
    CameraSource cameraSource;
    final int RequestCameraPermissionID = 1001;
    boolean displaydetection = false;
    backgroundspeaker backgroundspeaker;
    boolean isBound=false;
    boolean readytospeak = false;

    private String voiceinstructions;
    private String disableallvoices;
    private String cover;
    private String minimizemenus;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case RequestCameraPermissionID: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    try {
                        cameraSource.start(cameraView.getHolder());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }
            break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_text_reader);

        //recreate table
        nignog.mydb = new subdatabasenignog(this);
        //restore all stored data
        nignog.resulter = nignog.mydb.getAllDate();
        //pull all info from database
        if(nignog.resulter.getCount()>0  && nignog.resulter!=null){
            nignog.resulter.moveToFirst();
            cover = nignog.resulter.getString(1);
            nignog.resulter.moveToNext();
            minimizemenus = nignog.resulter.getString(1);
            nignog.resulter.moveToNext();
            voiceinstructions = nignog.resulter.getString(1);
            nignog.resulter.moveToNext();
            nignog.resulter.moveToNext();
            disableallvoices = nignog.resulter.getString(1);
            nignog.resulter.moveToNext();
            nignog.resulter.moveToNext();
            nignog.resulter.moveToNext();
            selectedlanguage = nignog.resulter.getString(1);

            nignog.resulter.close();
        }

        if(selectedlanguage.equals("french")){
            Button cameratoggle = (Button)findViewById(R.id.cameratoggle);
            cameratoggle.setText("Cacher la cam\u00E9ra");
            Button tab = (Button)findViewById(R.id.tab);
            tab.setText("Afficher le menu");
            Button tab2 = (Button)findViewById(R.id.tab2);
            tab2.setText("Cacher le menu");
            Button objects = (Button)findViewById(R.id.objects);
            objects.setText("Objets Et Personnes");
            Button textreader = (Button)findViewById(R.id.textreader);
            textreader.setText("Lecteur De Textes");

        }

        if(minimizemenus.equals("yes")){

            Button cameratoggle = (Button) findViewById(R.id.cameratoggle);
            cameratoggle.setVisibility(INVISIBLE);

            Button tab = (Button) findViewById(R.id.tab);
            tab.setEnabled(true);
            tab.setVisibility(VISIBLE);

            LinearLayout menu = (LinearLayout) findViewById(R.id.menu);
            menu.setVisibility(INVISIBLE);
        }

        if(cover.equals("yes")){
            Button cameratoggle = (Button) findViewById(R.id.cameratoggle);
            LinearLayout cover = (LinearLayout) findViewById(R.id.cover);
            cover.setVisibility(VISIBLE);

            cameratoggle.setTextColor(Color.parseColor("#27507b"));
            cameratoggle.setText("Show Camera");
            cameratoggle.setBackground(getResources().getDrawable(R.drawable.showcamera));
        }

        LinearLayout cover = (LinearLayout)findViewById(R.id.cover);
        SurfaceView surface_view = (SurfaceView)findViewById(R.id.surface_view);
        TextView display = (TextView) findViewById(R.id.display);
        surface_view.setOnTouchListener(new OnSwipeTouchListener(this) {
            public void onSwipeTop() {}
            public void onSwipeRight() {}
            public void onSwipeLeft() {
                Intent detector = new Intent(getApplicationContext(), DetectorActivity.class);
                startActivity(detector);
                finishAffinity();
            }
            public void onSwipeBottom() {}
            public void onDoublerBaby() {
                displaydetection = true;
                display.setVisibility(VISIBLE);
            }

        });
        cover.setOnTouchListener(new OnSwipeTouchListener(this) {
            public void onSwipeTop() {}
            public void onSwipeRight() {}
            public void onSwipeLeft() {
                Intent detector = new Intent(getApplicationContext(), DetectorActivity.class);
                startActivity(detector);
                finishAffinity();
            }
            public void onSwipeBottom() {}
            public void onDoublerBaby() {
                displaydetection = true;
                display.setVisibility(VISIBLE);
            }

        });
        display.setOnTouchListener(new OnSwipeTouchListener(this) {
            public void onSwipeTop() {
                String ignoredme;
                Animation slideoutup = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_slide_out_up);
                display.startAnimation(slideoutup);
                display.setVisibility(INVISIBLE);
                savestuff.t1.stop();
                if(selectedlanguage.equals("english"))
                    ignoredme = backgroundspeaker.AhderNoWait("text discarded");
                else if(selectedlanguage.equals("french"))
                    ignoredme = backgroundspeaker.AhderNoWait("text \u00E9fa\u00E7e\u00E9");
            }
            public void onSwipeRight() {}
            public void onSwipeLeft() {}
            public void onSwipeBottom() {
                setClipboard(getApplicationContext(), display.getText().toString());
                Animation slideoutdown = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_slide_out_down);
                display.startAnimation(slideoutdown);
                display.setVisibility(INVISIBLE);
                savestuff.t1.stop();
                String ignoredme;
                if(disableallvoices.equals("no")){
                if(voiceinstructions.equals("yes")){
                    if(selectedlanguage.equals("english"))
                    ignoredme = backgroundspeaker.AhderNoWait("text copied");
                else if(selectedlanguage.equals("french"))
                    ignoredme = backgroundspeaker.AhderNoWait("text copi\u00E9");
            } } }
            public void onDoublerBaby() {
                TextView display = (TextView) findViewById(R.id.display);
                String ignoredme;
                if (readytospeak) {
                    if (disableallvoices.equals("no"))
                        ignoredme = backgroundspeaker.AhderNoWait(display.getText().toString());
                }
                savestuff.t1.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                    @Override
                    public void onDone(String utteranceId) {
                        display.setVisibility(INVISIBLE);
                        display.setText("");
                    }

                    @Override
                    public void onError(String utteranceId) {
                    }

                    @Override
                    public void onStart(String utteranceId) {
                    }
                });
            }

        });

        if(selectedlanguage.equals("english")){
            savestuff.t1 = new TextToSpeech(this, new TextToSpeech.OnInitListener() {

                @Override
                public void onInit(int arg0) {
                    if(arg0 == TextToSpeech.SUCCESS)
                    {
                        savestuff.t1.setLanguage(Locale.US);
                        readytospeak = true;
                        String ignoredme;
                        if(disableallvoices.equals("no")) {
                            if(voiceinstructions.equals("yes")) {
                                    ignoredme = backgroundspeaker.AhderNoWait("Text Reader Activated, Tap twice to capture your text. then double tap to read it. swipe down to copy it. and swipe up to clear it.");
                            }
                        }
                    }
                }
            }); }
        else if(selectedlanguage.equals("french")){
            savestuff.t1 = new TextToSpeech(this, new TextToSpeech.OnInitListener() {

                @Override
                public void onInit(int arg0) {
                    if(arg0 == TextToSpeech.SUCCESS)
                    {
                        savestuff.t1.setLanguage(Locale.FRANCE);
                        readytospeak = true;
                        String ignoredme;
                        if(disableallvoices.equals("no")){

                            if (voiceinstructions.equals("yes")) {
                            ignoredme = backgroundspeaker.Ahder("Lecteur de textes activ\u00E9, Tap\u00E9 deux fois pour capturer votre text. double tap pour le lire. gli\u00E7e\u00E9 vers le bas pour le copier. gli\u00E7e\u00E9 vers le haut pour l'\u00E9facer");
                    }}}
                }
            });
        }

        //TTS
        Intent i = new Intent(this, backgroundspeaker.class);
        bindService(i, myConnection, Context.BIND_AUTO_CREATE);

        cameraView = (SurfaceView) findViewById(R.id.surface_view);
        textView = (TextView) findViewById(R.id.display);

        TextRecognizer textRecognizer = new TextRecognizer.Builder(getApplicationContext()).build();
        if (!textRecognizer.isOperational()) {
            Log.w("MainActivity", "Detector dependencies are not yet available");
        } else {

            cameraSource = new CameraSource.Builder(getApplicationContext(), textRecognizer)
                    .setFacing(CameraSource.CAMERA_FACING_BACK)
                    .setRequestedPreviewSize(1280, 1024)
                    .setRequestedFps(2.0f)
                    .setAutoFocusEnabled(true)
                    .build();
            cameraView.getHolder().addCallback(new SurfaceHolder.Callback() {
                @Override
                public void surfaceCreated(SurfaceHolder surfaceHolder) {

                    try {
                        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

                            ActivityCompat.requestPermissions(CameraActivityTextReader.this,
                                    new String[]{Manifest.permission.CAMERA},
                                    RequestCameraPermissionID);
                            return;
                        }
                        cameraSource.start(cameraView.getHolder());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

                }

                @Override
                public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
                    cameraSource.stop();
                }
            });

            textRecognizer.setProcessor(new Detector.Processor<TextBlock>() {
                @Override
                public void release() {

                }

                @Override
                public void receiveDetections(Detector.Detections<TextBlock> detections) {

                    final SparseArray<TextBlock> items = detections.getDetectedItems();
                    if(items.size() != 0)
                    {
                        textView.post(new Runnable() {
                            @Override
                            public void run() {
                                StringBuilder stringBuilder = new StringBuilder();
                                for(int i =0;i<items.size();++i)
                                {
                                    TextBlock item = items.valueAt(i);
                                    stringBuilder.append(item.getValue());
                                    stringBuilder.append("\n");
                                }
                                items.clear();
                                if(displaydetection){
                                textView.setText(stringBuilder.toString());
                                    displaydetection = false; }
                            }
                        });
                    }
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent start = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(start);
        ActivityCompat.finishAffinity(this);
    }

    public void objectsClicked(View view) {
        Intent start = new Intent(getApplicationContext(), DetectorActivity.class);
        startActivity(start);
        ActivityCompat.finishAffinity(this);
    }

    public void tabClicked(View view) {
        Animation slidein = AnimationUtils.loadAnimation(this, R.anim.anim_slide_in_right);
        LinearLayout menu = (LinearLayout) findViewById(R.id.menu);
        menu.startAnimation(slidein);

        Animation slidein2 = AnimationUtils.loadAnimation(this, R.anim.anim_slide_in_left);
        Button cameratoggle = (Button) findViewById(R.id.cameratoggle);
        cameratoggle.startAnimation(slidein2);
        cameratoggle.setVisibility(VISIBLE);

        Button tab = (Button) findViewById(R.id.tab);
        menu.setVisibility(VISIBLE);

        slidein.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationRepeat(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                Animation fadeout = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_out);
                tab.setEnabled(false);
                tab.startAnimation(fadeout);
                tab.setVisibility(INVISIBLE); }
        });

    }

    public void tab2Clicked(View view) {
        Animation slideout2 = AnimationUtils.loadAnimation(this, R.anim.anim_slide_out_left);
        Button cameratoggle = (Button) findViewById(R.id.cameratoggle);
        cameratoggle.startAnimation(slideout2);
        cameratoggle.setVisibility(INVISIBLE);

        Animation fadein = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in);
        Button tab = (Button) findViewById(R.id.tab);
        tab.setEnabled(true);
        tab.startAnimation(fadein);
        tab.setVisibility(VISIBLE);

        Animation slideout = AnimationUtils.loadAnimation(this, R.anim.anim_slide_out_right);
        LinearLayout menu = (LinearLayout) findViewById(R.id.menu);
        menu.startAnimation(slideout);
        menu.setVisibility(INVISIBLE);
    }

    public void togglecameraClicked(View view){

        Button cameratoggle = (Button) findViewById(R.id.cameratoggle);
        LinearLayout cover = (LinearLayout) findViewById(R.id.cover);
        if (cover.getVisibility()==INVISIBLE)
        {
            cover.setVisibility(VISIBLE);

            cameratoggle.setTextColor(Color.parseColor("#27507b"));
            cameratoggle.setText("Show Camera");
            cameratoggle.setBackground(getResources().getDrawable(R.drawable.showcamera));
        }

        else {
            cover.setVisibility(INVISIBLE);

            cameratoggle.setTextColor(Color.parseColor("#f0b63f"));
            cameratoggle.setText("Hide Camera");
            cameratoggle.setBackground(getResources().getDrawable(R.drawable.hidecamera));

        }
    }


    public void displayClicked(View view){

        //TextView display = (TextView) findViewById(R.id.display);
        String ignoredme;
        if(readytospeak) {
            if (disableallvoices.equals("no")){
                if (voiceinstructions.equals("yes")) {
                    TextView display = (TextView)findViewById(R.id.display);
                ignoredme = backgroundspeaker.AhderNoWait(display.getText().toString());
        } }}
        savestuff.t1.setOnUtteranceProgressListener(new UtteranceProgressListener() {
            @Override
            public void onDone(String utteranceId) {
                TextView display = (TextView)findViewById(R.id.display);
                display.setVisibility(INVISIBLE);
                display.setText("");
            }

            @Override
            public void onError(String utteranceId) {
            }

            @Override
            public void onStart(String utteranceId) {
            }
        });
    }



    private ServiceConnection myConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            dab.scuffedbots.backgroundspeaker.MyLocalBinder binder = (dab.scuffedbots.backgroundspeaker.MyLocalBinder) iBinder;
            backgroundspeaker = binder.getServices();
            isBound=true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            isBound=false;
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        savestuff.t1.stop();
    }

    @Override
    protected void onPause() {
        super.onPause();
        savestuff.t1.stop();
    }

    private void setClipboard(Context context, String text) {
        if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB) {
            android.text.ClipboardManager clipboard = (android.text.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            clipboard.setText(text);
        } else {
            android.content.ClipboardManager clipboard = (android.content.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            android.content.ClipData clip = android.content.ClipData.newPlainText("Copied Text", text);
            clipboard.setPrimaryClip(clip);
        }
    }

    public void parametersClicked(View view) {
        Intent parameters = new Intent(getApplicationContext(), Parameters.class);
        parameters.putExtra("ME", "TextReader");
        startActivity(parameters);
    }
}