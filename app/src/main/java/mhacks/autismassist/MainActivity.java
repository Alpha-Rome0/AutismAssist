package mhacks.autismassist;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.firebase.client.Firebase;
import com.affectiva.android.affdex.sdk.Frame;
import com.affectiva.android.affdex.sdk.Frame.ROTATE;
import com.affectiva.android.affdex.sdk.detector.CameraDetector;
import com.affectiva.android.affdex.sdk.detector.Detector;
import com.affectiva.android.affdex.sdk.detector.Face;

import com.affectiva.android.affdex.sdk.detector.Face.GENDER;
import com.affectiva.android.affdex.sdk.detector.Face.GLASSES;

import java.util.List;

    public class MainActivity extends Activity implements CameraDetector.CameraEventListener, Detector.FaceListener,Detector.ImageListener {
        private SurfaceView cameraView; //SurfaceView used to display camera images
        CameraDetector detector;
        private RelativeLayout mainLayout; //layout, to be resized, containing all UI elements
        int cameraPreviewWidth = 0;
        int cameraPreviewHeight = 0;
        private Frame mostRecentFrame;
    private TextView view;
    private TextView secView;
    private FirebaseHelper helper;

    /*
     * (non-Javadoc)
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Firebase.setAndroidContext(this);
        String url = "https://autismassist.firebaseIO.com";

        helper = new FirebaseHelper(url);
        this.setContentView(R.layout.activity_main);

        View decorView = getWindow().getDecorView();
        // Hide the status bar.
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
        // Remember that you should never show the action bar if the
        // status bar is hidden, so hide that too if necessary.
//        ActionBar actionBar = getActionBar();
//        actionBar.hide();
        view = (TextView) findViewById(R.id.attention_text);
        secView = (TextView) findViewById(R.id.smile_text);
        mainLayout = (RelativeLayout) findViewById(R.id.main_layout);
        cameraView = (SurfaceView) findViewById(R.id.camera_preview);
        PackageManager manager = getPackageManager();
        Log.d("Tag", Boolean.toString(manager.hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT)) + "front");
        Log.d("Tag", Boolean.toString(manager.hasSystemFeature(PackageManager.FEATURE_CAMERA)) + "back");
        initializeCameraDetector();
    }

    /*
     * (non-Javadoc)
     * @see android.app.Activity#onResume()
     */
    @Override
    protected void onResume() {
        super.onResume();


    }

    /*
     * (non-Javadoc)
     * @see android.app.Activity#onPause()
     */
    @Override
    protected void onPause() {
        super.onPause();
    }

    void initializeCameraDetector() {
        /* Put the SDK in camera mode by using this constructor. The SDK will be in control of
         * the camera. If a SurfaceView is passed in as the last argument to the constructor,
         * that view will be painted with what the camera sees.
         */
        detector = new CameraDetector(this, CameraDetector.CameraType.CAMERA_BACK, cameraView, 1, Detector.FaceDetectorMode.LARGE_FACES);

        // update the license path here if you name your file something else
        detector.setLicensePath("license.txt");
        detector.setImageListener(this);
        detector.setFaceListener(this);
        detector.setOnCameraEventListener(this);
        detector.setDetectAttention(true);
        detector.setDetectSmile(true);
        detector.setDetectContempt(true);
        detector.setDetectAllEmotions(true);
        detector.setDetectValence(true);
        detector.setDetectGender(true);
        detector.start();
        Log.d("TAG", Boolean.toString(detector.isRunning()));
    }

    @Override
    public void onCameraSizeSelected(int cameraWidth, int cameraHeight, ROTATE rotation) {
        if (rotation == ROTATE.BY_90_CCW || rotation == ROTATE.BY_90_CW) {
            cameraPreviewWidth = cameraHeight;
            cameraPreviewHeight = cameraWidth;
        } else {
            cameraPreviewWidth = cameraWidth;
            cameraPreviewHeight = cameraHeight;
        }
        //drawingView.setThickness((int) (cameraPreviewWidth / 100f));

        mainLayout.post(new Runnable() {
            @Override
            public void run() {
                //Get the screen width and height, and calculate the new app width/height based on the surfaceview aspect ratio.
                DisplayMetrics displaymetrics = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
                int layoutWidth = displaymetrics.widthPixels;
                int layoutHeight = displaymetrics.heightPixels;

                if (cameraPreviewWidth == 0 || cameraPreviewHeight == 0 || layoutWidth == 0 || layoutHeight == 0)
                    return;

                float layoutAspectRatio = (float) layoutWidth / layoutHeight;
                float cameraPreviewAspectRatio = (float) cameraPreviewWidth / cameraPreviewHeight;

                int newWidth;
                int newHeight;

                if (cameraPreviewAspectRatio > layoutAspectRatio) {
                    newWidth = layoutWidth;
                    newHeight = (int) (layoutWidth / cameraPreviewAspectRatio);
                } else {
                    newWidth = (int) (layoutHeight * cameraPreviewAspectRatio);
                    newHeight = layoutHeight;
                }

                //drawingView.updateViewDimensions(newWidth, newHeight, cameraPreviewWidth, cameraPreviewHeight);

                ViewGroup.LayoutParams params = mainLayout.getLayoutParams();
                params.height = newHeight;
                params.width = newWidth;
                mainLayout.setLayoutParams(params);

                //Now that our main layout has been resized, we can remove the progress bar that was obscuring the screen (its purpose was to obscure the resizing of the SurfaceView)
                //progressBarLayout.setVisibility(View.GONE);
            }
        });

    }


    /**
     * Process picture - from example GDK
     *
     * @param picturePath
     */


    /**
     * Added but irrelevant
     */
    /*
     * (non-Javadoc)
	 * @see android.app.Activity#onKeyDown(int, android.view.KeyEvent)
	 */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_CAMERA) {
            // Stop the preview and release the camera.
            // Execute your logic as quickly as possible
            // so the capture happens quickly.
            return false;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    @Override
    public void onFaceDetectionStarted() {
        //
    }
    @Override
    public void onFaceDetectionStopped() {
    }
    @Override
    public void onImageResults(List<Face> faces, Frame image,float timestamp) {

        if (faces == null) {
            //Log.d("TAG", "frame not processed");
            return; //frame was not processed
        }

        if (faces.size() == 0) {
            //Log.d("TAG", "no face found");
            return; //no face found
        }

        //For each face found
        for (int i = 0 ; i < faces.size() ; i++) {
            Face face = faces.get(i);
            int[] measurements = new int[13];
            int faceId = face.getId();

            //Appearance
            Face.GENDER genderValue = face.appearance.getGender();
            Face.GLASSES glassesValue = face.appearance.getGlasses();
            switch(genderValue) {
                case MALE:
                    measurements[0] = 0;
                    break;
                case FEMALE:
                    measurements[0] = 1;
                    break;
                case UNKNOWN:
                    measurements[0] = 2;
                    break;
            }
            switch(glassesValue) {
                case NO:
                    measurements[1] = 0;
                    break;
                case YES:
                     measurements[1] = 1;
                     break;
            }

            //Some Emoji
//            float smiley = face.emojis.getSmiley();
//            float laughing = face.emojis.getLaughing();
//            float wink = face.emojis.getWink();


            int anger = Math.round(face.emotions.getAnger());
            int contempt = Math.round(face.emotions.getContempt());
            int disgust = Math.round(face.emotions.getDisgust());

            //Some Expressions
            float engagement = face.emotions.getEngagement();
            float attention = face.expressions.getAttention();
            int fear = Math.round(face.emotions.getFear());
            int joy = Math.round(face.emotions.getJoy());
            int sad = Math.round(face.emotions.getSadness());
            int surprise = Math.round(face.emotions.getSurprise());
            int valence = Math.round(face.emotions.getValence());
            int s=Math.round(engagement);
            int a=Math.round(attention);
            measurements[2] = anger;
            measurements[3] = contempt;
            measurements[4] = disgust;
            measurements[5] = s;
            measurements[6] = a;
            measurements[7] = fear;
            measurements[8] = joy;
            measurements[9] = sad;
            measurements[10] = surprise;
            measurements[11] = valence;
            measurements[12] = Math.round(face.expressions.getSmile());
            //Measurements
//            float interocular_distance = face.measurements.getInterocularDistance();
//            float yaw = face.measurements.orientation.getYaw();
//            float roll = face.measurements.orientation.getRoll();
//            float pitch = face.measurements.orientation.getPitch();
            view.setText("Engagement is " + Integer.toString(s));
            secView.setText("Attention is " + Integer.toString(a));
            helper.saveArray(measurements);
            view.setText(Integer.toString(s));
            secView.setText(Integer.toString(a));


            //Face feature points coordinates
            //PointF[] points = face.getFacePoints();

        }
    }
}
