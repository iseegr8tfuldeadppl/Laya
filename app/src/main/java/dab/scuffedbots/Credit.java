package dab.scuffedbots;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;
import static dab.scuffedbots.MainActivity.m;

public class Credit extends Activity {

    private LinearLayout scrolling;
    private Animation animationer;
    private Typeface custom_font;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credit);

        custom_font = Typeface.createFromAsset(getAssets(),  "fonts/quick.ttf");

        TextView title1 = (TextView)findViewById(R.id.title1);
        TextView title2 = (TextView)findViewById(R.id.title2);
        TextView title3 = (TextView)findViewById(R.id.title3);
        TextView title4 = (TextView)findViewById(R.id.title4);
        TextView title5 = (TextView)findViewById(R.id.title5);
        TextView sub1 = (TextView)findViewById(R.id.sub1);
        TextView sub2 = (TextView)findViewById(R.id.sub2);
        TextView sub3 = (TextView)findViewById(R.id.sub3);
        TextView sub4 = (TextView)findViewById(R.id.sub4);
        TextView sub5 = (TextView)findViewById(R.id.sub5);
        Button back = (Button)findViewById(R.id.back);

        sub5.setTypeface(custom_font);
        title5.setTypeface(custom_font);
        back.setTypeface(custom_font);
        title1.setTypeface(custom_font);
        title2.setTypeface(custom_font);
        title3.setTypeface(custom_font);
        title4.setTypeface(custom_font);
        sub1.setTypeface(custom_font);
        sub2.setTypeface(custom_font);
        sub3.setTypeface(custom_font);
        sub4.setTypeface(custom_font);

        animationer = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.animation);
        Animation fadein = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in_2);
        Animation fadeout = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_out_2);
        Animation fadein2 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in_2);
        Animation fadeout2 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_out_2);
        Animation fadein3 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in_2);

        Timer t = new Timer(false);
        t.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    public void run() {
        playBeep("moozika");
        scrolling = (LinearLayout)findViewById(R.id.scrolling);

        ImageView tf = (ImageView)findViewById(R.id.tf);
        ImageView laya = (ImageView)findViewById(R.id.laya);
        laya.startAnimation(fadein);
        fadein.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                laya.setVisibility(VISIBLE);
                Timer t = new Timer(false);
                t.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            public void run() {
                                laya.startAnimation(fadeout);
                                fadeout.setAnimationListener(new Animation.AnimationListener() {
                                    @Override
                                    public void onAnimationStart(Animation animation) {}

                                    @Override
                                    public void onAnimationEnd(Animation animation) {
                                        laya.setVisibility(INVISIBLE);

                                        Timer t = new Timer(false);
                                        t.schedule(new TimerTask() {
                                            @Override
                                            public void run() {
                                                runOnUiThread(new Runnable() {
                                                    public void run() {
                                                        tf.startAnimation(fadein2);
                                                        fadein2.setAnimationListener(new Animation.AnimationListener() {
                                                            @Override
                                                            public void onAnimationStart(Animation animation) {}

                                                            @Override
                                                            public void onAnimationEnd(Animation animation) {
                                                                tf.setVisibility(VISIBLE);

                                                                Timer t = new Timer(false);
                                                                t.schedule(new TimerTask() {
                                                                    @Override
                                                                    public void run() {
                                                                        runOnUiThread(new Runnable() {
                                                                            public void run() {
                                                                                tf.startAnimation(fadeout2);
                                                                                fadeout2.setAnimationListener(new Animation.AnimationListener() {
                                                                                    @Override
                                                                                    public void onAnimationStart(Animation animation) {}

                                                                                    @Override
                                                                                    public void onAnimationEnd(Animation animation) {
                                                                                        tf.setVisibility(INVISIBLE);

                                                                                        Timer t = new Timer(false);
                                                                                        t.schedule(new TimerTask() {
                                                                                            @Override
                                                                                            public void run() {
                                                                                                runOnUiThread(new Runnable() {
                                                                                                    public void run() {
                                                                                                        scrolling.startAnimation(animationer);
                                                                                                    }
                                                                                                });
                                                                                            }
                                                                                        }, 500);

                                                                                    }

                                                                                    @Override
                                                                                    public void onAnimationRepeat(Animation animation) {}
                                                                                });
                                                                            }
                                                                        });
                                                                    }
                                                                }, 3400);

                                                            }

                                                            @Override
                                                            public void onAnimationRepeat(Animation animation) {}
                                                        });
                                                    }
                                                });
                                            }
                                        }, 500);

                                    }

                                    @Override
                                    public void onAnimationRepeat(Animation animation) {}
                                });
                            }
                        });
                    }
                }, 3400);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

                        Timer t = new Timer(false);
                        t.schedule(new TimerTask() {
                            @Override
                            public void run() {
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        //m.stop();
                                        Button back = (Button)findViewById(R.id.back);
                                        back.startAnimation(fadein3);
                                        fadein3.setAnimationListener(new Animation.AnimationListener() {
                                            @Override
                                            public void onAnimationStart(Animation animation) {}

                                            @Override
                                            public void onAnimationEnd(Animation animation) {
                                                back.setVisibility(VISIBLE);
                                            }

                                            @Override
                                            public void onAnimationRepeat(Animation animation) {}
                                        });
                                    }
                                });
                            }
                        }, 81000);


                    }
                });
            }
        }, 1100);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(m.isPlaying()){
            m.stop();
        }
        Intent start = new Intent(getApplicationContext(), Parameters.class);
        startActivity(start);
        ActivityCompat.finishAffinity(this);
    }


    public void playBeep(String filename) {
        m = new MediaPlayer();
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


    public void backClicked(View view) {
        if(m.isPlaying())
            m.stop();
        Intent start = new Intent(getApplicationContext(), Parameters.class);
        startActivity(start);
        ActivityCompat.finishAffinity(this);
    }
}
