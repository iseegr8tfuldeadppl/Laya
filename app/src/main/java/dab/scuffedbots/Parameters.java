package dab.scuffedbots;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class Parameters extends AppCompatActivity {

    List<ListViewItem> items;
    static String selectedlanguego;
    String confidence;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parameters);

        TextView confidencepre2 = (TextView)findViewById(R.id.confidencepre2);
        TextView scuffed = (TextView)findViewById(R.id.scuffed);
        Typeface custom_font2 = Typeface.createFromAsset(getAssets(),  "fonts/collegec.ttf");
        scuffed.setTypeface(custom_font2);
        TextView credits = (TextView)findViewById(R.id.credits);
        credits.setTypeface(custom_font2);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Parameters");
        getSupportActionBar().setSubtitle("Laya");


        //recreate table
        nignog.mydb = new subdatabasenignog(this);

        //restore all stored data
        nignog.resulter = nignog.mydb.getAllDate();
        items = new ArrayList<ListViewItem>();

        //pull all info from database
        if(nignog.resulter.getCount()>0  && nignog.resulter!=null){
            while(nignog.resulter.moveToNext()){
                items.add(new ListViewItem(){{ CHECKED = nignog.resulter.getString(1); }});
            }
            //adapter.notifyDataSetChanged();
            nignog.resulter.close();
        }

        selectedlanguego = items.get(7).CHECKED;

        if(selectedlanguego.equals("arabic")){
            getSupportActionBar().setTitle(this.getString(R.string.settingstitle));
            CharSequence[] fiilliste = getResources().getStringArray(R.array.languages);
            fiilliste[0] = this.getString(R.string.changelanguage);
            TextView confidencepre = (TextView)findViewById(R.id.confidencepre);
            confidencepre.setText(this.getString(R.string.confidencearabic));
        } else if(selectedlanguego.equals("french")){
            getSupportActionBar().setTitle(this.getString(R.string.settingstitlefrench));
            CharSequence[] fiilliste = getResources().getStringArray(R.array.languages);
            fiilliste[0] = this.getString(R.string.changelanguagefrench);
        }

        final Spinner language2 = (Spinner) findViewById(R.id.language);
        ArrayAdapter spinneradapter = ArrayAdapter.createFromResource(getApplicationContext(), R.array.languages, R.layout.spinneritem);
        spinneradapter.setDropDownViewResource(R.layout.spinner_dropdown_items);
        language2.setAdapter(spinneradapter);
        spinneradapter.notifyDataSetChanged();


        Spinner language = (Spinner)findViewById(R.id.language); /*
        if(!items.get(5).CHECKED.equals("nothing")) {
            if(language.getSelectedItem().equals(""))
        //set default language as spinner display item
        //ArrayAdapter myAdap = (ArrayAdapter) language.getAdapter(); //cast to an ArrayAdapter
        if(items.get(5).CHECKED.equals("arabic"))
            language.setSelection(3);
        else if(items.get(5).CHECKED.equals("english"))
            language.setSelection(1);
        else if(items.get(5).CHECKED.equals("french"))
            language.setSelection(2);
        } */


        //send all database info into the recycler view
        // Lookup the recyclerview in activity layout
        RecyclerView rvContacts = (RecyclerView) findViewById(R.id.rvContacts);
        // Create adapter passing in the sample user data
        items.remove(7); //we remove the language index at the end since it doesn't belong in the recycler
        ContactsAdapter adapter = new ContactsAdapter(items);
        // Attach the adapter to the recyclerview to populate items
        rvContacts.setAdapter(adapter);
        // Set layout manager to position the items
        rvContacts.setLayoutManager(new LinearLayoutManager(this));
        items.remove(7);




        language.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if(language.getSelectedItem().toString().equals("English") ||
                        language.getSelectedItem().toString().equals(getApplicationContext().getString(R.string.changinglanguagecancer)) ||
                        language.getSelectedItem().toString().equals(getApplicationContext().getString(R.string.changinglanguagecancer2))) {
                    if(language.getSelectedItemPosition() == 1){
                        nignog.mydb.updateData("8", "english"); }
                    else if(language.getSelectedItemPosition() == 2){
                        nignog.mydb.updateData("8", "french"); }
                    else if(language.getSelectedItemPosition() == 3){
                        nignog.mydb.updateData("8", "arabic"); }

                Intent restart = new Intent(getApplicationContext(), Main2Activity.class);
                startActivity(restart);
                ActivityCompat.finishAffinity(Parameters.this); }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });



        //seekbar
        //recreate table
        nignog.mydb = new subdatabasenignog(this);
        //restore all stored data
        nignog.resulter = nignog.mydb.getAllDate();
        //pull all info from database
        if(nignog.resulter.getCount()>0  && nignog.resulter!=null){
            nignog.resulter.moveToFirst();
            nignog.resulter.moveToNext();
            nignog.resulter.moveToNext();
            nignog.resulter.moveToNext();
            nignog.resulter.moveToNext();
            nignog.resulter.moveToNext();
            nignog.resulter.moveToNext();
            nignog.resulter.moveToNext();
            nignog.resulter.moveToNext();
            confidence = nignog.resulter.getString(1);
            nignog.resulter.close();
        }
        confidencepre2.setText("    -    " + String.valueOf((int) (Float.valueOf(confidence) * 100)) + "%");
        SeekBar confidenceer = (SeekBar)findViewById(R.id.confidence);
        confidenceer.setProgress(((int) (Float.valueOf(confidence) * 100)-50 )*2);
        confidenceer.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                confidencepre2.setText("    -    " + String.valueOf(progress/2 + 50) + "%");
                float lel = progress;
                lel = (lel/2 + 50)/100;
                BigDecimal result;
                result=round(lel,2);
                nignog.mydb.updateData("9", String.valueOf(result.floatValue()));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent mIntent = getIntent();
        String previousActivity= mIntent.getStringExtra("ME");
        if(previousActivity!=null) {
            if (previousActivity.equals("Detector")) {
                Intent parameters = new Intent(getApplicationContext(), DetectorActivity.class);
                startActivity(parameters);
                finish();
            } else if (previousActivity.equals("TextReader")) {
                Intent parameters = new Intent(getApplicationContext(), DetectorActivity.class);
                startActivity(parameters);
                finish();
            }
        }else{
            Intent parameters = new Intent(getApplicationContext(), DetectorActivity.class);
            startActivity(parameters);
            finish();
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent mIntent = getIntent();
                String previousActivity= mIntent.getStringExtra("ME");
                if(previousActivity!=null) {
                    if (previousActivity.equals("Detector")) {
                        Intent parameters = new Intent(getApplicationContext(), DetectorActivity.class);
                        startActivity(parameters);
                        finish();
                    } else if (previousActivity.equals("TextReader")) {
                        Intent parameters = new Intent(getApplicationContext(), DetectorActivity.class);
                        startActivity(parameters);
                        finish();
                    }
                }else{
                    Intent parameters = new Intent(getApplicationContext(), DetectorActivity.class);
                    startActivity(parameters);
                    finish();
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void creditsClicked(View view) {
        Intent credits = new Intent(this, Credit.class);
        startActivity(credits);
        ActivityCompat.finishAffinity(this);
    }

    static class ListViewItem{
        //public int ThumbnailResource;
        String CHECKED;
    }

    public void scuffedClicked(View view) {
        Intent scuffedbots = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.instagram.com/scuffedbots"));
        startActivity(scuffedbots);
        finish();
    }


    public static BigDecimal round(float d, int decimalPlace) {
        BigDecimal bd = new BigDecimal(Float.toString(d));
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
        return bd;
    }


}

//nignog
class nignog {
    static subdatabasenignog mydb;
    static Cursor resulter;
}
