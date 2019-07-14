package dab.scuffedbots;

import android.Manifest;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

public class MainActivity extends Activity {

    Button english, french;
    backgroundspeaker backgroundspeaker;
    boolean isBound=false;
    static MediaPlayer m;
    boolean nowfrench = false;

    private String cover;
    String arabicenglishfrench = "";
    private String minimizemenus;
    private String voiceinstructions;
    private String speakdetectedobjects;
    private String disableallvoices;
    private String selectedlanguage;
    private String skipthispage;
    private static final int REQUEST_EXTERNAL_STORAGE_ACCESS = 3211;
    String ARABIC_FILE_LOCATION = "file:///android_asset/arabic.txt";
    String EXTRAS_ARABIC_FILE_LOCATION = "file:///android_asset/arabic_extras.txt";
    LinearLayout downloadcover;
    static DownloadManager downloadManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        nignog.mydb = new subdatabasenignog(this);

        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            voiceinstructions = extras.getString("voiceinstructions");
            disableallvoices = extras.getString("disableallvoices");
            selectedlanguage = extras.getString("selectedlanguage");
            skipthispage = extras.getString("skipthispage");
        }

        assert selectedlanguage != null;
        if(skipthispage.equals("yes")){
            if(selectedlanguage.equals("english") || selectedlanguage.equals("french") || selectedlanguage.equals("arabic")){
                savestuff.isitenglish = true;
                Intent start = new Intent(getApplicationContext(), DetectorActivity.class);
                startActivity(start);
                ActivityCompat.finishAffinity(this);
            }
        }

        if(skipthispage.equals("no")) {
            if (disableallvoices.equals("no")) {
                if (voiceinstructions.equals("yes"))
                    playBeep("start");
            }
        }


        french = (Button)findViewById(R.id.french);
        english = (Button)findViewById(R.id.english);
        Typeface custom_font = Typeface.createFromAsset(getAssets(),  "fonts/quick.ttf");
        french.setTypeface(custom_font);
        english.setTypeface(custom_font);

    }


    public void playBeep(String filename) {
        MediaPlayer m = new MediaPlayer();
        try {
            if (m.isPlaying()) {
                m.stop();
                m.release();
                m = new MediaPlayer();
            }

            AssetFileDescriptor descriptor = getAssets().openFd(filename + ".mp3");
            m.setDataSource(descriptor.getFileDescriptor(), descriptor.getStartOffset(), descriptor.getLength());
            descriptor.close();

            m.prepare();
            m.setVolume(1f, 1f);
            m.setLooping(false);
            m.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    private long backpressedTime;
    @Override
    public void onBackPressed() {
        if(backpressedTime + 2000 > System.currentTimeMillis()){
            super.onBackPressed();
            return;
        } else{
            Toast.makeText(getBaseContext(), "Press back again to exit", Toast.LENGTH_SHORT).show();
        }
        backpressedTime = System.currentTimeMillis();
    }

    public void englishClicked(View view) {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            arabicenglishfrench = "english";
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA}, REQUEST_EXTERNAL_STORAGE_ACCESS);
        } else {
            nignog.mydb.updateData("8", "english");
            nignog.mydb.close();

            savestuff.isitenglish = true;

            Intent start = new Intent(getApplicationContext(), DetectorActivity.class);
            startActivity(start);
            ActivityCompat.finishAffinity(this);
        }
    }

    public void frenchClicked(View view) {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            arabicenglishfrench = "french";
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA}, REQUEST_EXTERNAL_STORAGE_ACCESS);
        } else {
            nignog.mydb.updateData("8", "french");
            nignog.mydb.close();

            savestuff.isitenglish = false;

            Intent start = new Intent(getApplicationContext(), DetectorActivity.class);
            startActivity(start);
            ActivityCompat.finishAffinity(this);
        }
    }


    public void arabicClicked(View view) {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            arabicenglishfrench = "arabic";
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA}, REQUEST_EXTERNAL_STORAGE_ACCESS);
        } else {
            nignog.mydb.updateData("8", "arabic");
            nignog.mydb.close();

            Intent start = new Intent(getApplicationContext(), DetectorActivity.class);
            startActivity(start);
            ActivityCompat.finishAffinity(this);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_EXTERNAL_STORAGE_ACCESS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if(arabicenglishfrench.equals("english")){
                        nignog.mydb.updateData("8", "english");
                        nignog.mydb.close();

                        savestuff.isitenglish = true;

                        Intent start = new Intent(getApplicationContext(), DetectorActivity.class);
                        startActivity(start);
                        ActivityCompat.finishAffinity(this);
                    } else if(arabicenglishfrench.equals("french")){
                        nignog.mydb.updateData("8", "french");
                        nignog.mydb.close();

                        savestuff.isitenglish = false;

                        Intent start = new Intent(getApplicationContext(), DetectorActivity.class);
                        startActivity(start);
                        ActivityCompat.finishAffinity(this);
                    } else if(arabicenglishfrench.equals("arabic")){
                        nignog.mydb.updateData("8", "arabic");
                        nignog.mydb.close();

                        Intent start = new Intent(getApplicationContext(), DetectorActivity.class);
                        startActivity(start);
                        ActivityCompat.finishAffinity(this);
                    }
                }
            }
        }
    }

}
