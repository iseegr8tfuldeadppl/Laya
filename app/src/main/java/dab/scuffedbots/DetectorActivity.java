/*
 * Copyright 2019 The TensorFlow Authors. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package dab.scuffedbots;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.media.ImageReader.OnImageAvailableListener;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.os.SystemClock;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Size;
import android.util.TypedValue;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import dab.scuffedbots.customview.OverlayView;
import dab.scuffedbots.customview.OverlayView.DrawCallback;
import dab.scuffedbots.env.BorderedText;
import dab.scuffedbots.env.ImageUtils;
import dab.scuffedbots.tflite.Classifier;
import dab.scuffedbots.tflite.TFLiteObjectDetectionAPIModel;
import dab.scuffedbots.tracking.MultiBoxTracker;

import static dab.scuffedbots.MainActivity.m;

/**
 * An activity that uses a TensorFlowMultiBoxDetector and ObjectTracker to detect and then track
 * objects.
 */
public class DetectorActivity extends CameraActivity implements OnImageAvailableListener {

  //TTS
  backgroundspeaker backgroundspeaker;
  boolean isBound=false;
  boolean readytospeak = false;
  boolean readytodraw = false;

  String duplicatespeakavoider = "topkek";
  public static Integer delayer = 0;
  boolean onetime=true;
  public static Integer dilay = 0;
    float width;
    float height;
  int arabicbasicdelay = 0;

  // Configuration values for the prepackaged SSD model.
  private static final int TF_OD_API_INPUT_SIZE = 300;
  private static final boolean TF_OD_API_IS_QUANTIZED = true;
  private static final String TF_OD_API_MODEL_FILE = "detect.tflite";
  private static String TF_OD_API_LABELS_FILE = "file:///android_asset/english.txt";
  private static final DetectorMode MODE = DetectorMode.TF_OD_API;
  // Minimum detection confidence to track a detection.
  private static final float MINIMUM_CONFIDENCE_TF_OD_API = 0.61f;
  private static final boolean MAINTAIN_ASPECT = false;
  private static final Size DESIRED_PREVIEW_SIZE = new Size(640, 480);
  private static final boolean SAVE_PREVIEW_BITMAP = false;
  private static final float TEXT_SIZE_DIP = 10;
  OverlayView trackingOverlay;
  private Integer sensorOrientation;

  private Classifier detector;

  private long lastProcessingTimeMs;
  private Bitmap rgbFrameBitmap = null;
  private Bitmap croppedBitmap = null;
  private Bitmap cropCopyBitmap = null;
  String[] ArabicList;
  String[] EnglishList;

  private boolean computingDetection = false;

  private long timestamp = 0;

  private Matrix frameToCropTransform;
  private Matrix cropToFrameTransform;

  private MultiBoxTracker tracker;

  private BorderedText borderedText;


    @Override
  public void onPreviewSizeChosen(final Size size, final int rotation) {
    final float textSizePx =
            TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, TEXT_SIZE_DIP, getResources().getDisplayMetrics());
    borderedText = new BorderedText(textSizePx);
    borderedText.setTypeface(Typeface.MONOSPACE);

    tracker = new MultiBoxTracker(this);

    int cropSize = TF_OD_API_INPUT_SIZE;

    try {
      //TTS
      Intent i = new Intent(this, backgroundspeaker.class);
      bindService(i, myConnection, Context.BIND_AUTO_CREATE);

      if(CameraActivity.selectedlanguage.equals("english")){
      savestuff.t1 = new TextToSpeech(this, new TextToSpeech.OnInitListener() {

        @Override
        public void onInit(int arg0) {
          if(arg0 == TextToSpeech.SUCCESS)
          {
            savestuff.t1.setLanguage(Locale.US);

            if(CameraActivity.disableallvoices.equals("no")){
              if(CameraActivity.voiceinstructions.equals("yes")) {
                backgroundspeaker.Ahder("Object detector activated. Swipe right for the reader and swipe left to go back to object detection");
                savestuff.t1.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                  @Override
                  public void onStart(String utteranceId) {}

                  @Override
                  public void onDone(String utteranceId) {
                    readytospeak = true;
                  }

                  @Override
                  public void onError(String utteranceId) {}
                });
              }
            }
          }
        }
      });
      TF_OD_API_LABELS_FILE = "file:///android_asset/english.txt";
      }
      else if (CameraActivity.selectedlanguage.equals("french")){
        TF_OD_API_LABELS_FILE = "file:///android_asset/french.txt";
          savestuff.t1 = new TextToSpeech(this, new TextToSpeech.OnInitListener() {

            @Override
            public void onInit(int arg0) {
              if(arg0 == TextToSpeech.SUCCESS)
              {
                savestuff.t1.setLanguage(Locale.FRANCE);

                if(CameraActivity.disableallvoices.equals("no")){
                  if(CameraActivity.voiceinstructions.equals("yes")) {
                    backgroundspeaker.Ahder("Detecteur d'objets activ\u00E9. Gli\u00E7e\u00E9 vers la droite pour la lecture de text, et vers la gauche pour revenir au detecteur d'objets");
                    savestuff.t1.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                      @Override
                      public void onStart(String utteranceId) {}

                      @Override
                      public void onDone(String utteranceId) {
                        readytospeak = true;
                      }

                      @Override
                      public void onError(String utteranceId) {}
                    });
                  }
                }
                  }
            }
          });
        }
      else if (CameraActivity.selectedlanguage.equals("arabic")){
        TF_OD_API_LABELS_FILE = "file:///android_asset/arabic.txt";

        ArabicList = new String[91];
        ArabicList = pullEntireList("file:///android_asset/arabic.txt");
        EnglishList = new String[91];
        EnglishList = pullEntireList("file:///android_asset/english.txt");
        readytospeak = true;
      }

      detector =
              TFLiteObjectDetectionAPIModel.create(
                      getAssets(),
                      TF_OD_API_MODEL_FILE,
                      TF_OD_API_LABELS_FILE,
                      TF_OD_API_INPUT_SIZE,
                      TF_OD_API_IS_QUANTIZED);
      cropSize = TF_OD_API_INPUT_SIZE;
    } catch (final IOException e) {
      e.printStackTrace();
      Toast toast =
              Toast.makeText(
                      getApplicationContext(), "Classifier could not be initialized", Toast.LENGTH_SHORT);
      toast.show();
      finish();
    }

    previewWidth = size.getWidth();
    previewHeight = size.getHeight();

    sensorOrientation = rotation - getScreenOrientation();

    rgbFrameBitmap = Bitmap.createBitmap(previewWidth, previewHeight, Config.ARGB_8888);
    croppedBitmap = Bitmap.createBitmap(cropSize, cropSize, Config.ARGB_8888);

    frameToCropTransform =
            ImageUtils.getTransformationMatrix(
                    previewWidth, previewHeight,
                    cropSize, cropSize,
                    sensorOrientation, MAINTAIN_ASPECT);

    cropToFrameTransform = new Matrix();
    frameToCropTransform.invert(cropToFrameTransform);

    trackingOverlay = (OverlayView) findViewById(R.id.tracking_overlay);
    trackingOverlay.addCallback(
            new DrawCallback() {
              @Override
              public void drawCallback(final Canvas canvas) {
                tracker.draw(canvas);
                if (isDebug()) {
                  tracker.drawDebug(canvas);
                }
              }
            });

    tracker.setFrameConfiguration(previewWidth, previewHeight, sensorOrientation);
  }

  @Override
  protected void processImage() {
    ++timestamp;
    final long currTimestamp = timestamp;
    trackingOverlay.postInvalidate();

    // No mutex needed as this method is not reentrant.
    if (computingDetection) {
      readyForNextImage();
      return;
    }
    computingDetection = true;

    rgbFrameBitmap.setPixels(getRgbBytes(), 0, previewWidth, 0, 0, previewWidth, previewHeight);

    //Text Recognition


    readyForNextImage();

    final Canvas canvas = new Canvas(croppedBitmap);
    canvas.drawBitmap(rgbFrameBitmap, frameToCropTransform, null);
    // For examining the actual TF input.
    if (SAVE_PREVIEW_BITMAP) {
      ImageUtils.saveBitmap(croppedBitmap);
    }

    runInBackground(
            new Runnable() {
              @Override
              public void run() {
                final long startTime = SystemClock.uptimeMillis();
                final List<Classifier.Recognition> results = detector.recognizeImage(croppedBitmap);
                lastProcessingTimeMs = SystemClock.uptimeMillis() - startTime;

                cropCopyBitmap = Bitmap.createBitmap(croppedBitmap);
                final Canvas canvas = new Canvas(cropCopyBitmap);
                final Paint paint = new Paint();
                paint.setColor(Color.RED);
                paint.setStyle(Style.STROKE);
                paint.setStrokeWidth(2.0f);

                float minimumConfidence = MINIMUM_CONFIDENCE_TF_OD_API;
                switch (MODE) {
                  case TF_OD_API:
                    minimumConfidence = MINIMUM_CONFIDENCE_TF_OD_API;
                    break;
                }

                final List<Classifier.Recognition> mappedRecognitions =
                        new LinkedList<Classifier.Recognition>();

                //my work
  //dups counter
  Integer[] dupemap = new Integer[5];
  for (int i = 0; i < dupemap.length; i++) dupemap[i] = 1;
  for (final Classifier.Recognition i : results) {
    final RectF locationi = i.getLocation();
    if (locationi != null && i.getConfidence() >= Float.valueOf(confidence)) {
        int linenumberofcategoryinlabelfile2 = 0;
        if(CameraActivity.selectedlanguage.equals("arabic")) {
            for (int b = 0; b < ArabicList.length; b++) {
                if (i.getTitle().equals(ArabicList[b])) {
                    linenumberofcategoryinlabelfile2 = b;
                }
            }
        }

      for (final Classifier.Recognition j : results) {
        final RectF locationj = j.getLocation();
        if (locationj != null && j.getConfidence() >= Float.valueOf(confidence)) {

          //duplicates counter
          if (Integer.valueOf(j.getId()) > Integer.valueOf(i.getId())) {
              int linenumberofcategoryinlabelfile1 = 0;
              if(CameraActivity.selectedlanguage.equals("arabic")){
                  for (int b = 0; b < ArabicList.length; b++) {
                      if (j.getTitle().equals(ArabicList[b])) {
                          linenumberofcategoryinlabelfile1 = b;
                      }
                  }
              }
              if(CameraActivity.selectedlanguage.equals("arabic")) {
                  if (EnglishList[linenumberofcategoryinlabelfile2].equals(EnglishList[linenumberofcategoryinlabelfile1])) {
                      dupemap[Integer.valueOf(i.getId())]++;
                  }
              }
              else {
                  if (i.getTitle().equals(j.getTitle())) {
                      dupemap[Integer.valueOf(i.getId())]++;
                  }
              }


          }
        }
      }
    }
  }

  //start of detection
  for (final Classifier.Recognition result : results) {
    final RectF location = result.getLocation();
    if (location != null && result.getConfidence() >= Float.valueOf(confidence)) {

      String category = result.getTitle();

    if(CameraActivity.disableallvoices.equals("no")){
    if(CameraActivity.speakdetectedobjects.equals("yes")) {

      if(!CameraActivity.selectedlanguage.equals("arabic")) {
      if(!readytospeak){
          if (!savestuff.t1.isSpeaking()) {
            readytospeak = true;
          }
        }
      }

      if (readytospeak) {
        if (delayer > 6) {
          //French
              if (CameraActivity.selectedlanguage.equals("arabic")) {
                      int linenumberofcategoryinlabelfile = 0;
                  for (int b = 0; b < ArabicList.length; b++) {
                      if (category.equals(ArabicList[b])) {
                          linenumberofcategoryinlabelfile = b;
                      }
                  }

                  if (dupemap[Integer.valueOf(result.getId())] == 1) {
                      if(!m.isPlaying()) {
                              playBeep(EnglishList[linenumberofcategoryinlabelfile]);
                              delayer = 0;
                      }
                  } else {
                            if(EnglishList[linenumberofcategoryinlabelfile].equals("person")){
                                if(!m.isPlaying()){
                                    delayer = 0;
                                  playBeep("multiple");
                                      m.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                          @Override
                                          public void onCompletion(MediaPlayer mp) {
                                              playBeep("people");
                                          }
                                      });
                                }
                            }else if(EnglishList[linenumberofcategoryinlabelfile].equals("car")){
                                if(!m.isPlaying()){
                                    delayer = 0;
                                    playBeep("multiple");
                                    m.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                        @Override
                                        public void onCompletion(MediaPlayer mp) {
                                            playBeep("cars");
                                        }
                                    });
                                }
                            }else{
                                if(!m.isPlaying()) {
                                        delayer = 0;
                                        playBeep(EnglishList[linenumberofcategoryinlabelfile]);
                                }
                            }

                  }

          }else if (CameraActivity.selectedlanguage.equals("french")){
                  String ignoredme;
              //this dupemap is for saying 1 car or two cars and so on
              //this check below is for saying 1 only objects in french
              if (dupemap[Integer.valueOf(result.getId())] == 1) {
                if (category.contains("voiture") || category.contains("Souris") || category.contains("cuill\u00E7re") ||
                        category.contains("girafe") || category.contains("Orange") ||
                        category.contains("Pizza") || category.contains("brosse a dents") ||
                        category.contains("toilette") || category.contains("table a manger") ||
                        category.contains("carotte") || category.contains("Pomme") ||
                        category.contains("banane") || category.contains("assi\u00E7tte") ||
                        category.contains("plante") || category.contains("fourchette") ||
                        category.contains("bouteille") || category.contains("chaise") ||
                        category.contains("personne") || category.contains("moto")) {
                        ignoredme = backgroundspeaker.Ahder("une" + " " + category);
                        delayer = 0;

                } else {
                        delayer = 0;
                        ignoredme = backgroundspeaker.Ahder("un" + " " + category);
                }
              } else {
                    //this is for more than 1 as we don't need to say un or une
                        delayer = 0;
                        ignoredme = backgroundspeaker.Ahder(dupemap[Integer.valueOf(result.getId())].toString() + " " + category);
                    }
              //English
            }else  if (CameraActivity.selectedlanguage.equals("english")){
                  String ignoredme;
              if (dupemap[Integer.valueOf(result.getId())] == 1) {
                      delayer = 0;
                      ignoredme = backgroundspeaker.Ahder(dupemap[Integer.valueOf(result.getId())].toString() + " " + category);
              } else {
                    delayer = 0;
                    ignoredme = backgroundspeaker.Ahder(dupemap[Integer.valueOf(result.getId())].toString() + " " + category + "s");
              }
            }
        }else{
            delayer++;
        }
      }
    }}
        //TODO distance detection?
        canvas.drawRect(location, paint);
        cropToFrameTransform.mapRect(location);
        result.setLocation(location);
        mappedRecognitions.add(result);
    }


  }

                tracker.trackResults(mappedRecognitions, currTimestamp);
                trackingOverlay.postInvalidate();

                computingDetection = false;

                runOnUiThread(
                        new Runnable() {
                          @Override
                          public void run() {
                            showFrameInfo(previewWidth + "x" + previewHeight);
                            showCropInfo(cropCopyBitmap.getWidth() + "x" + cropCopyBitmap.getHeight());
                            showInference(lastProcessingTimeMs + "ms");
                          }
                        });
              }
            });
  }

  @Override
  protected int getLayoutId() {
    return R.layout.camera_connection_fragment_tracking;
  }

  @Override
  protected Size getDesiredPreviewFrameSize() {
    return DESIRED_PREVIEW_SIZE;
  }

  // Which detection model to use: by default uses Tensorflow Object Detection API frozen
  // checkpoints.
  private enum DetectorMode {
    TF_OD_API;
  }

  @Override
  protected void setUseNNAPI(final boolean isChecked) {
    runInBackground(() -> detector.setUseNNAPI(isChecked));
  }

  @Override
  protected void setNumThreads(final int numThreads) {
    runInBackground(() -> detector.setNumThreads(numThreads));
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


  public String[] pullEntireList(String FILE_LOCATION_LOL) {
    String[] EntireListLol = new String[91];
    AssetManager assetManager = getAssets();
    String actualFilename = FILE_LOCATION_LOL.split("file:///android_asset/")[1];
    InputStream labelsInput = null;
    try {
      labelsInput = assetManager.open(actualFilename);
    } catch (IOException ignored) {
    }
    BufferedReader br = null;
    br = new BufferedReader(new InputStreamReader(labelsInput));
    String line;
    //this counter so we can use string array and just apply a new name to every line but we cant loop since we alrdy have a true loop
    int counter = 0;
    while (true) {
      try {
        if (((line = br.readLine()) == null)) break;
        else {
          EntireListLol[counter] = line;
          counter++;
        }
      } catch (IOException ignored) {
      }

    }
    try {
      br.close();
    } catch (IOException ignored) {
    }

    return EntireListLol;
  }


  @Override
  public synchronized void onDestroy() {
    super.onDestroy();

    if (m.isPlaying()) {
      m.stop();
    }
  }


  @Override
  public synchronized void onPause() {
    super.onPause();

    if (m.isPlaying()) {
      m.stop();
    }
  }
}
