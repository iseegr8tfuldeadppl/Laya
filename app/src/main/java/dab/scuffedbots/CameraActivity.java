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

import android.Manifest;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.graphics.Color;
import android.graphics.RectF;
import android.hardware.Camera;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.Image.Plane;
import android.media.ImageReader;
import android.media.ImageReader.OnImageAvailableListener;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Trace;
import android.support.annotation.NonNull;
import android.support.v4.view.GestureDetectorCompat;
import android.util.Size;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.nio.ByteBuffer;

import dab.scuffedbots.env.ImageUtils;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;
import static dab.scuffedbots.MainActivity.m;

public abstract class CameraActivity extends Activity
        implements OnImageAvailableListener,
        Camera.PreviewCallback {

  private static final int PERMISSIONS_REQUEST = 1;

  private static final String PERMISSION_CAMERA = Manifest.permission.CAMERA;
  protected int previewWidth = 0;
  protected int previewHeight = 0;
  private boolean debug = false;
  private Handler handler;
  private HandlerThread handlerThread;
  private boolean useCamera2API;
  private boolean isProcessingFrame = false;
  private byte[][] yuvBytes = new byte[3][];
  private int[] rgbBytes = null;
  private int yRowStride;
  private Runnable postInferenceCallback;
  private Runnable imageConverter;



  private GestureDetectorCompat gestureDetectorCompat = null;

  RectF rekt;
  private String cover;
  private String minimizemenus;
  public static String voiceinstructions;
  private String skipthispage;
  public static String speakdetectedobjects;
  public static String disableallvoices;
  public static String selectedlanguage;
  public static String showconfidence;
  public String confidence;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(null);
    getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

    setContentView(R.layout.activity_camera);


    m = new MediaPlayer();


    LinearLayout coverer = (LinearLayout) findViewById(R.id.cover);
    coverer.setOnTouchListener(new OnSwipeTouchListener(this) {
      public void onSwipeTop() {}
      public void onSwipeRight() {
        if(!selectedlanguage.equals("arabic")) {
          if (savestuff.t1.isSpeaking()) {
            savestuff.t1.stop();
          }

          Intent reader = new Intent(getApplicationContext(), CameraActivityTextReader.class);
          startActivity(reader);
          finish();
        } else{
          playBeep("cantspeakarabiclol");
        }

      }
      public void onSwipeLeft() {}
      public void onSwipeBottom() {}
      public void onDoublerBaby(){}
    });

    RelativeLayout displayboy = (RelativeLayout) findViewById(R.id.displayboy);
    displayboy.setOnTouchListener(new OnSwipeTouchListener(this) {
      public void onSwipeTop() {}
      public void onSwipeRight() {
        if(savestuff.t1.isSpeaking()){
          savestuff.t1.stop();
        }

        Intent reader = new Intent(getApplicationContext(), CameraActivityTextReader.class);
        startActivity(reader);
        finish();
      }
      public void onSwipeLeft() {}
      public void onSwipeBottom() {}
      public void onDoublerBaby(){}
    });




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
      speakdetectedobjects = nignog.resulter.getString(1);
      nignog.resulter.moveToNext();
      disableallvoices = nignog.resulter.getString(1);
      nignog.resulter.moveToNext();
      skipthispage = nignog.resulter.getString(1);
      nignog.resulter.moveToNext();
      showconfidence = nignog.resulter.getString(1);
      nignog.resulter.moveToNext();
      selectedlanguage = nignog.resulter.getString(1);
      nignog.resulter.moveToNext();
      confidence = nignog.resulter.getString(1);
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

    } else if(selectedlanguage.equals("arabic")) {
      Button cameratoggle = (Button)findViewById(R.id.cameratoggle);
      cameratoggle.setText(this.getString(R.string.hidecamera));
      Button tab = (Button)findViewById(R.id.tab);
      tab.setText(this.getString(R.string.showmenu));
      Button tab2 = (Button)findViewById(R.id.tab2);
      tab2.setText(this.getString(R.string.showmenu));
      Button objects = (Button)findViewById(R.id.objects);
      objects.setText(this.getString(R.string.objectsandpeople));
      Button textreader = (Button)findViewById(R.id.textreader);
      textreader.setText(this.getString(R.string.textreader));
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
      if(selectedlanguage.equals("french")){
        cameratoggle.setText("Afficher La Camera");
      } else if(selectedlanguage.equals("english")){
        cameratoggle.setText("Show Camera");
      } else if(selectedlanguage.equals("arabic")){
        cameratoggle.setText(this.getString(R.string.showcamera));
        //TODO:
      }
      cameratoggle.setBackground(getResources().getDrawable(R.drawable.showcamera));
    }


    if (hasPermission()) {
      setFragment();
    } else {
      requestPermission();
    }



  }



  @Override
  public void onBackPressed() {
    //TextView toggle = (TextView)findViewById(R.id.cameratoggle);
    //toggle.setText(rekt.left +" " + rekt.right + '\n' + rekt.top +" " + rekt.bottom);
    Intent start = new Intent(getApplicationContext(), Main2Activity.class);
    startActivity(start);
    finish();
  }


  protected int[] getRgbBytes() {
    imageConverter.run();
    return rgbBytes;
  }

  protected int getLuminanceStride() {
    return yRowStride;
  }

  protected byte[] getLuminance() {
    return yuvBytes[0];
  }

  /** Callback for android.hardware.Camera API */
  @Override
  public void onPreviewFrame(final byte[] bytes, final Camera camera) {
    if (isProcessingFrame) {
      return;
    }

    try {
      // Initialize the storage bitmaps once when the resolution is known.
      if (rgbBytes == null) {
        Camera.Size previewSize = camera.getParameters().getPreviewSize();
        previewHeight = previewSize.height;
        previewWidth = previewSize.width;
        rgbBytes = new int[previewWidth * previewHeight];
        onPreviewSizeChosen(new Size(previewSize.width, previewSize.height), 90);
      }
    } catch (final Exception e) {
      return;
    }

    isProcessingFrame = true;
    yuvBytes[0] = bytes;
    yRowStride = previewWidth;

    imageConverter =
            new Runnable() {
              @Override
              public void run() {
                ImageUtils.convertYUV420SPToARGB8888(bytes, previewWidth, previewHeight, rgbBytes);
              }
            };

    postInferenceCallback =
            new Runnable() {
              @Override
              public void run() {
                camera.addCallbackBuffer(bytes);
                isProcessingFrame = false;
              }
            };
    processImage();
  }

  /** Callback for Camera2 API */
  @Override
  public void onImageAvailable(final ImageReader reader) {
    // We need wait until we have some size from onPreviewSizeChosen
    if (previewWidth == 0 || previewHeight == 0) {
      return;
    }
    if (rgbBytes == null) {
      rgbBytes = new int[previewWidth * previewHeight];
    }
    try {
      final Image image = reader.acquireLatestImage();

      if (image == null) {
        return;
      }

      if (isProcessingFrame) {
        image.close();
        return;
      }
      isProcessingFrame = true;
      Trace.beginSection("imageAvailable");
      final Plane[] planes = image.getPlanes();
      fillBytes(planes, yuvBytes);
      yRowStride = planes[0].getRowStride();
      final int uvRowStride = planes[1].getRowStride();
      final int uvPixelStride = planes[1].getPixelStride();

      imageConverter =
              new Runnable() {
                @Override
                public void run() {
                  ImageUtils.convertYUV420ToARGB8888(
                          yuvBytes[0],
                          yuvBytes[1],
                          yuvBytes[2],
                          previewWidth,
                          previewHeight,
                          yRowStride,
                          uvRowStride,
                          uvPixelStride,
                          rgbBytes);
                }
              };

      postInferenceCallback =
              new Runnable() {
                @Override
                public void run() {
                  image.close();
                  isProcessingFrame = false;
                }
              };

      processImage();
    } catch (final Exception e) {
      Trace.endSection();
      return;
    }
    Trace.endSection();
  }

  @Override
  public synchronized void onStart() {

    super.onStart();
  }

  @Override
  public synchronized void onResume() {

    super.onResume();

    handlerThread = new HandlerThread("inference");
    handlerThread.start();
    handler = new Handler(handlerThread.getLooper());
  }

  @Override
  public synchronized void onPause() {

    handlerThread.quitSafely();
    try {
      handlerThread.join();
      handlerThread = null;
      handler = null;
    } catch (final InterruptedException e) {
    }

    super.onPause();
  }

  @Override
  public synchronized void onStop() {
    super.onStop();
  }

  @Override
  public synchronized void onDestroy() {
    super.onDestroy();
  }

  protected synchronized void runInBackground(final Runnable r) {
    if (handler != null) {
      handler.post(r);
    }
  }

  @Override
  public void onRequestPermissionsResult(
          final int requestCode, @NonNull final String[] permissions, @NonNull final int[] grantResults) {
    if (requestCode == PERMISSIONS_REQUEST) {
      if (grantResults.length > 0
              && grantResults[0] == PackageManager.PERMISSION_GRANTED
              && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
        setFragment();
      } else {
        requestPermission();
      }
    }
  }

  private boolean hasPermission() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      return checkSelfPermission(PERMISSION_CAMERA) == PackageManager.PERMISSION_GRANTED;
    } else {
      return true;
    }
  }

  private void requestPermission() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      if (shouldShowRequestPermissionRationale(PERMISSION_CAMERA)) {
        Toast.makeText(
                CameraActivity.this,
                "Camera permission is required for this demo",
                Toast.LENGTH_LONG)
                .show();
      }
      requestPermissions(new String[] {PERMISSION_CAMERA}, PERMISSIONS_REQUEST);
    }
  }

  // Returns true if the device supports the required hardware level, or better.
  private boolean isHardwareLevelSupported(
          CameraCharacteristics characteristics, int requiredLevel) {
    int deviceLevel = characteristics.get(CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL);
    if (deviceLevel == CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_LEGACY) {
      return requiredLevel == deviceLevel;
    }
    // deviceLevel is not LEGACY, can use numerical sort
    return requiredLevel <= deviceLevel;
  }

  private String chooseCamera() {
    final CameraManager manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
    try {
      for (final String cameraId : manager.getCameraIdList()) {
        final CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraId);

        // We don't use a front facing camera in this sample.
        final Integer facing = characteristics.get(CameraCharacteristics.LENS_FACING);
        if (facing != null && facing == CameraCharacteristics.LENS_FACING_FRONT) {
          continue;
        }

        final StreamConfigurationMap map =
                characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);

        if (map == null) {
          continue;
        }

        // Fallback to camera1 API for internal cameras that don't have full support.
        // This should help with legacy situations where using the camera2 API causes
        // distorted or otherwise broken previews.
        useCamera2API =
                (facing == CameraCharacteristics.LENS_FACING_EXTERNAL)
                        || isHardwareLevelSupported(
                        characteristics, CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_FULL);
        return cameraId;
      }
    } catch (CameraAccessException ignored) {
    }

    return null;
  }

  protected void setFragment() {
    String cameraId = chooseCamera();

    Fragment fragment;
    if (useCamera2API) {
      CameraConnectionFragment camera2Fragment =
              CameraConnectionFragment.newInstance(
                      new CameraConnectionFragment.ConnectionCallback() {
                        @Override
                        public void onPreviewSizeChosen(final Size size, final int rotation) {
                          previewHeight = size.getHeight();
                          previewWidth = size.getWidth();
                          CameraActivity.this.onPreviewSizeChosen(size, rotation);
                        }
                      },
                      this,
                      getLayoutId(),
                      getDesiredPreviewFrameSize());

      camera2Fragment.setCamera(cameraId);
      fragment = camera2Fragment;
    } else {
      fragment =
              new LegacyCameraConnectionFragment(this, getLayoutId(), getDesiredPreviewFrameSize());
    }

    getFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();
  }

  protected void fillBytes(final Plane[] planes, final byte[][] yuvBytes) {
    // Because of the variable row stride it's not possible to know in
    // advance the actual necessary dimensions of the yuv planes.
    for (int i = 0; i < planes.length; ++i) {
      final ByteBuffer buffer = planes[i].getBuffer();
      if (yuvBytes[i] == null) {
        yuvBytes[i] = new byte[buffer.capacity()];
      }
      buffer.get(yuvBytes[i]);
    }
  }

  public boolean isDebug() {
    return debug;
  }

  protected void readyForNextImage() {
    if (postInferenceCallback != null) {
      postInferenceCallback.run();
    }
  }

  protected int getScreenOrientation() {
    switch (getWindowManager().getDefaultDisplay().getRotation()) {
      case Surface.ROTATION_270:
        return 270;
      case Surface.ROTATION_180:
        return 180;
      case Surface.ROTATION_90:
        return 90;
      default:
        return 0;
    }
  }

  protected void showFrameInfo(String frameInfo) {
    //frameValueTextView.setText(frameInfo);
  }

  protected void showCropInfo(String cropInfo) {
    //cropValueTextView.setText(cropInfo);
  }

  protected void showInference(String inferenceTime) {
    //inferenceTimeTextView.setText(inferenceTime);
  }

  protected abstract void processImage();

  protected abstract void onPreviewSizeChosen(final Size size, final int rotation);

  protected abstract int getLayoutId();

  protected abstract Size getDesiredPreviewFrameSize();

  protected abstract void setNumThreads(int numThreads);

  protected abstract void setUseNNAPI(boolean isChecked);

  public void togglecameraClicked(View view){

    Button cameratoggle = (Button) findViewById(R.id.cameratoggle);
    LinearLayout cover = (LinearLayout) findViewById(R.id.cover);

    if (cover.getVisibility()==INVISIBLE)
    {
      cover.setVisibility(VISIBLE);

      cameratoggle.setTextColor(Color.parseColor("#27507b"));
      if(selectedlanguage.equals("french")){
        cameratoggle.setText("Afficher La Camera");
      } else if(selectedlanguage.equals("english")){
        cameratoggle.setText("Show Camera");
      } else if(selectedlanguage.equals("arabic")){
        cameratoggle.setText(this.getString(R.string.showcamera));
      }
      cameratoggle.setBackground(getResources().getDrawable(R.drawable.showcamera));
    }

    else {
      cover.setVisibility(INVISIBLE);

      cameratoggle.setTextColor(Color.parseColor("#f0b63f"));
      if(selectedlanguage.equals("french")){
        cameratoggle.setText("Cacher La Camera");
      } else if(selectedlanguage.equals("english")){
        cameratoggle.setText("Hide Camera");
      } else if(selectedlanguage.equals("arabic")){
        cameratoggle.setText(this.getString(R.string.hidecamera));
      }
      cameratoggle.setBackground(getResources().getDrawable(R.drawable.hidecamera));

    }
  }

  public void textClicked(View view) {
    if(!selectedlanguage.equals("arabic")) {
      if (savestuff.t1.isSpeaking()) {
        savestuff.t1.stop();
      }
    Intent reader = new Intent(getApplicationContext(), CameraActivityTextReader.class);
    startActivity(reader);
    m.stop();
    finish();
    } else{
      playBeep("cantspeakarabiclol");
    }
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

    public void parametersClicked(View view) {
      if(!selectedlanguage.equals("arabic")) {
        if (savestuff.t1.isSpeaking()) {
          savestuff.t1.stop();
        }
      }
      Intent parameters = new Intent(getApplicationContext(), Parameters.class);
      parameters.putExtra("ME", "Detector");
      startActivity(parameters);
      assert m!=null;
      if(m!=null) {
        if (m.isPlaying())
          m.stop();
      }
      finish();
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





}
