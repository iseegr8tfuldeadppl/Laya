package dab.scuffedbots;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.TextView;

public class Main2Activity extends Activity {

    private String voiceinstructions;
    private String disableallvoices;
    private String selectedlanguage;
    private String skipthispage;
    private static final int REQUEST_EXTERNAL_STORAGE_ACCESS = 3211;

    Handler handler1 = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Intent start = new Intent(getApplicationContext(), MainActivity.class);

            start.putExtra("voiceinstructions", voiceinstructions);
            start.putExtra("disableallvoices", disableallvoices);
            start.putExtra("selectedlanguage", selectedlanguage);
            start.putExtra("skipthispage", skipthispage);

            startActivity(start);
            ActivityCompat.finishAffinity(Main2Activity.this);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        TextView scuffed = (TextView)findViewById(R.id.scuffed);
        Typeface custom_font2 = Typeface.createFromAsset(getAssets(),  "fonts/collegec.ttf");
        scuffed.setTypeface(custom_font2);

        //recreate table
        nignog.mydb = new subdatabasenignog(this);

        //restore all stored data
        nignog.resulter = nignog.mydb.getAllDate();

        if(nignog.resulter.getCount()<=0){
            //camera hidden on start
            //menu hidden on start
            //disable voice instructions
            //speak outloud detected items
            //disable all voices in the app
            //default language
            nignog.mydb.insertData( "yes");
            nignog.mydb.insertData( "yes");
            nignog.mydb.insertData( "yes");
            nignog.mydb.insertData( "yes");
            nignog.mydb.insertData( "no");
            nignog.mydb.insertData("no");
            nignog.mydb.insertData("no");
            nignog.mydb.insertData("nothing");
            nignog.mydb.insertData("0.61");
        }

        nignog.resulter.close();

        //restore all stored data
        nignog.resulter = nignog.mydb.getAllDate();
        //pull all info from database
        if(nignog.resulter.getCount()>6  && nignog.resulter!=null){
            nignog.resulter.moveToFirst();
            nignog.resulter.moveToNext();
            nignog.resulter.moveToNext();
            voiceinstructions = nignog.resulter.getString(1);
            nignog.resulter.moveToNext();
            nignog.resulter.moveToNext();
            disableallvoices = nignog.resulter.getString(1);
            nignog.resulter.moveToNext();
            skipthispage = nignog.resulter.getString(1);
            nignog.resulter.moveToNext();
            nignog.resulter.moveToNext();
            selectedlanguage = nignog.resulter.getString(1);
            nignog.resulter.close();
        }

        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_EXTERNAL_STORAGE_ACCESS);
        }
        else{
            // If request is cancelled, the result arrays are empty.
            Runnable r2 = new Runnable() {
                @Override
                public void run() {
                    long futuretime = System.currentTimeMillis() + 2000;
                    while(System.currentTimeMillis() < futuretime){
                        synchronized (this){
                            try{
                                wait(futuretime - System.currentTimeMillis());
                            }
                            catch (Exception ignored){}
                        }
                    }
                    handler1.sendEmptyMessage(0);
                }
            };
            Thread myThreadty = new Thread(r2);
            myThreadty.start();
        }

    }


    public void scuffedClicked(View view) {
        Intent scuffedbots = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.instagram.com/scuffedbots"));
        startActivity(scuffedbots);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_EXTERNAL_STORAGE_ACCESS: {
                // If request is cancelled, the result arrays are empty.
                Runnable r2 = new Runnable() {
                    @Override
                    public void run() {
                        long futuretime = System.currentTimeMillis() + 2000;
                        while(System.currentTimeMillis() < futuretime){
                            synchronized (this){
                                try{
                                    wait(futuretime - System.currentTimeMillis());
                                }
                                catch (Exception ignored){}
                            }
                        }
                        handler1.sendEmptyMessage(0);
                    }
                };
                Thread myThreadty = new Thread(r2);
                myThreadty.start();
            }
        }
    }
}
