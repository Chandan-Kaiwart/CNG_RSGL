package com.apc.cng_hpcl.camera;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import android.content.Context;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.apc.cng_hpcl.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class CameraFragment extends Fragment implements SurfaceHolder.Callback
{
    Camera camera;
    SurfaceView surfaceView;
    SurfaceHolder surfaceHolder;
    boolean previewing = false;
    Context context;
    float mDist = 0;

    @BindView(R.id.preview_layout)
    LinearLayout previewLayout;
    //@BindView(R.id.border_camera)
    @BindView(R.id.border_camera_reading)
    View borderCamera;
    @BindView(R.id.res_border_size)
    TextView resBorderSizeTV;

    @BindView(R.id.res_message)
    Button res_message;


    private OnFragmentInteractionListener mListener;
    Camera.Size previewSizeOptimal;

    Bitmap bitmap_date,bitmap_time,bitmap_reading,bitmap_kw,bitmap_meter_num;
    private static int click_num=1;

    public interface OnFragmentInteractionListener {
        //void onFragmentInteraction(Bitmap bitmap, Bitmap bitmap2, Bitmap bitmap3, Bitmap bitmap4);
        void onFragmentInteraction(Bitmap bitmap, Bitmap bitmap2);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_camera, container, false);
        ButterKnife.bind(this, view);
        context = getContext();

        surfaceView = (SurfaceView) view.findViewById(R.id.camera_preview_surface);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        view.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event)
            {
                /*
                if(event.getAction() == MotionEvent.ACTION_MOVE)
                {
                    //do something
                }
                return true;
                */

                // Get the pointer ID
                Camera.Parameters params = camera.getParameters();

/***************Auto focus code start*******************/
                params.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
/*************Auto focus code end*******************/

                int action = event.getAction();
                if (event.getPointerCount() > 1) {
                    // handle multi-touch events
                    if (action == MotionEvent.ACTION_POINTER_DOWN) {
                        mDist = getFingerSpacing(event);
                    } else if (action == MotionEvent.ACTION_MOVE && params.isZoomSupported()) {
                        camera.cancelAutoFocus();
                        handleZoom(event, params);
                    }
                } else {
                    // handle single touch events
                    if (action == MotionEvent.ACTION_UP) {
                        handleFocus(event, params);
                    }
                }
                return true;
            }
        });

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        camera = Camera.open();

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if (previewing) {
            camera.stopPreview();
            previewing = false;
        }

        if (camera != null) {
            try {
                Camera.Parameters parameters = camera.getParameters();
                //get preview sizes
                List<Camera.Size> previewSizes = parameters.getSupportedPreviewSizes();

                //find optimal - it very important
                previewSizeOptimal = getOptimalPreviewSize(previewSizes, parameters.getPictureSize().width,
                        parameters.getPictureSize().height);

                //set parameters
                if (previewSizeOptimal != null) {
                    parameters.setPreviewSize(previewSizeOptimal.width, previewSizeOptimal.height);
                }

                if (camera.getParameters().getFocusMode().contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
                    parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
                }
                if (camera.getParameters().getFlashMode().contains(Camera.Parameters.FLASH_MODE_AUTO)) {
                    parameters.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO);
                }

                camera.setParameters(parameters);

                //rotate screen, because camera sensor usually in landscape mode
                Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
                if (display.getRotation() == Surface.ROTATION_0) {
                    camera.setDisplayOrientation(90);
                } else if (display.getRotation() == Surface.ROTATION_270) {
                    camera.setDisplayOrientation(180);
                }

                //write some info
                int x1 = previewLayout.getWidth();
                int y1 = previewLayout.getHeight();

                int x2 = borderCamera.getWidth();
                int y2 = borderCamera.getHeight();

                String info = "Preview width:" + String.valueOf(x1) + "\n" + "Preview height:" + String.valueOf(y1) + "\n" +
                        "Border width:" + String.valueOf(x2) + "\n" + "Border height:" + String.valueOf(y2);
                resBorderSizeTV.setText(info);

                camera.setPreviewDisplay(surfaceHolder);
                camera.startPreview();
                previewing = true;
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    public Camera.Size getOptimalPreviewSize(List<Camera.Size> sizes, int w, int h) {
        final double ASPECT_TOLERANCE = 0.1;
        double targetRatio = (double) w / h;
        if (sizes == null) return null;

        Camera.Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        int targetHeight = h;

        // Try to find an size match aspect ratio and size
        for (Camera.Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }

        // Cannot find the one match the aspect ratio, ignore the requirement
        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Camera.Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }
        return optimalSize;
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        camera.stopPreview();
        camera.release();
        camera = null;
        previewing = false;
    }

    @OnClick(R.id.make_photo_button)
    void makePhoto() {
        /*
        if(click_num==0)
        {
            click_num=1;
        }
        else if(click_num==1)
        {
            click_num=2;
        }
        else if(click_num==2)
        {
            click_num=3;
        }
*/
        if (camera != null) {
            camera.takePicture(myShutterCallback, myPictureCallback_RAW, myPictureCallback_JPG);
        }
    }

    Camera.ShutterCallback myShutterCallback = new Camera.ShutterCallback() {
        @Override
        public void onShutter() {

        }
    };
    Camera.PictureCallback myPictureCallback_RAW = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {

        }
    };

    Camera.PictureCallback myPictureCallback_JPG = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            Bitmap bitmapPicture = BitmapFactory.decodeByteArray(data, 0, data.length);
            Bitmap croppedBitmap = null;

            Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
            if (display.getRotation() == Surface.ROTATION_0)
            {
                //rotate bitmap, because camera sensor usually in landscape mode
                Matrix matrix = new Matrix();
                matrix.postRotate(90);
                Bitmap rotatedBitmap = Bitmap.createBitmap(bitmapPicture, 0, 0, bitmapPicture.getWidth(), bitmapPicture.getHeight(), matrix, true);
                //save file
                createImageFile(rotatedBitmap);

                //calculate aspect ratio
                float koefX = (float) rotatedBitmap.getWidth() / (float) previewLayout.getWidth();
                float koefY = (float) rotatedBitmap.getHeight() / (float) previewLayout.getHeight();

                //get viewfinder border size and position on the screen
                int x1 = borderCamera.getLeft();
                int y1 = borderCamera.getTop();

                int x2 = borderCamera.getWidth();
                int y2 = borderCamera.getHeight();

                //calculate position and size for cropping
                int cropStartX = Math.round(x1 * koefX);
                int cropStartY = Math.round(y1 * koefY);

                int cropWidthX = Math.round(x2 * koefX);
                int cropHeightY = Math.round(y2 * koefY);

                //check limits and make crop
                if (cropStartX + cropWidthX <= rotatedBitmap.getWidth() && cropStartY + cropHeightY <= rotatedBitmap.getHeight()) {
                    croppedBitmap = Bitmap.createBitmap(rotatedBitmap, cropStartX, cropStartY, cropWidthX, cropHeightY);
                } else {
                    croppedBitmap = null;
                }
                //save result
                if (croppedBitmap != null)
                {
                    createImageFile(croppedBitmap);
                    Log.e("TAG","saved-"+click_num);
                    if(click_num==1)//consumption
                    {
                        Toast.makeText(context, "Current consumption captured", Toast.LENGTH_SHORT).show();
                        bitmap_reading=croppedBitmap;
                        Log.e("TAG","current consumption captured");
                        click_num++;
                        res_message.setText("Capture Meter number");
                    }
                    else if(click_num==2)//meter number
                    {
                        Toast.makeText(context, "Meter number captured", Toast.LENGTH_SHORT).show();
                        bitmap_meter_num=croppedBitmap;
                        Log.e("TAG","Meter number captured");
                        if (mListener != null) {
                            if (croppedBitmap != null)
                                Log.e("TAG","fragment moved");
                            //mListener.onFragmentInteraction(croppedBitmap,croppedBitmap,croppedBitmap);
                            mListener.onFragmentInteraction(bitmap_reading,bitmap_meter_num);
                            click_num=1;
                        }

                    }
                    /*
                    if(click_num==1)
                    {
                        Toast.makeText(context, "Current consumption captured", Toast.LENGTH_SHORT).show();
                        bitmap_reading=croppedBitmap;
                        Log.e("TAG","current consumption captured");
                        click_num++;
                        res_message.setText("Capture Date( dd:mm:yy)");
                    }
                    else if(click_num==2)
                    {
                        Toast.makeText(context, "Date captured", Toast.LENGTH_SHORT).show();
                        bitmap_date=croppedBitmap;
                        Log.e("TAG","Date captured");
                        click_num++;
                        res_message.setText("Capture Time( hrs:min:sec)");
                    }
                    else if(click_num==3)
                    {
                        Toast.makeText(context, "Time captured", Toast.LENGTH_SHORT).show();
                        bitmap_time=croppedBitmap;
                        Log.e("TAG","Time captured");
                        click_num++;
                        res_message.setText("Capture MD value (KW)");
                    }
                    else if(click_num==4)
                    {
                        Toast.makeText(context, "MD value captured", Toast.LENGTH_SHORT).show();
                        bitmap_kw=croppedBitmap;
                        Log.e("TAG","MD value captured");

                        if (mListener != null) {
                            if (croppedBitmap != null)
                                Log.e("TAG","fragment moved");
                            //mListener.onFragmentInteraction(croppedBitmap,croppedBitmap,croppedBitmap);
                            mListener.onFragmentInteraction(bitmap_date,bitmap_time,bitmap_reading,bitmap_kw);
                            click_num=1;
                        }
                    }
                    */

                }

            } else if (display.getRotation() == Surface.ROTATION_270) {
                // for Landscape mode
            }

/*
            //pass to another fragment
            if(click_num>=3)
            {
                if (mListener != null) {
                    if (croppedBitmap != null)
                        Log.e("TAG","fragment moved");
                        //mListener.onFragmentInteraction(croppedBitmap,croppedBitmap,croppedBitmap);
                        mListener.onFragmentInteraction(bitmap_date,bitmap_time,bitmap_reading);
                }
            }
*/
            if (camera != null) {
                camera.startPreview();
            }
        }
    };

    public void createImageFile(final Bitmap bitmap)
    {
        //  click_num++;
        //  Log.e("TAG","saved-"+click_num);
        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        String timeStamp = new SimpleDateFormat("MMdd_HHmmssSSS").format(new Date());
        String imageFileName = "region_" + timeStamp + ".jpg";
        final File file = new File(path, imageFileName);

        try {
            // Make sure the Pictures directory exists.
            if (path.mkdirs()) {
                Toast.makeText(context, "Not exist :" + path.getName(), Toast.LENGTH_SHORT).show();
            }

            OutputStream os = new FileOutputStream(file);

            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);

            os.flush();
            os.close();
            Log.e("ExternalStorage", "Writed " + path + file.getName());
            // Tell the media scanner about the new file so that it is
            // immediately available to the user.
            MediaScannerConnection.scanFile(context,
                    new String[]{file.toString()}, null,
                    new MediaScannerConnection.OnScanCompletedListener() {
                        public void onScanCompleted(String path, Uri uri) {
                            Log.e("ExternalStorage", "Scanned " + path + ":");
                            Log.e("ExternalStorage", "-> uri=" + uri);
                        }
                    });
            // Toast.makeText(context, file.getName(), Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            // Unable to create file, likely because external storage is
            // not currently mounted.
            Log.e("ExternalStorage", "Error writing " + file, e);
        }
    }

    private void handleZoom(MotionEvent event, Camera.Parameters params) {
        int maxZoom = params.getMaxZoom();
        int zoom = params.getZoom();
        float newDist = getFingerSpacing(event);
        if (newDist > mDist) {
            //zoom in
            if (zoom < maxZoom)
                zoom++;
        } else if (newDist < mDist) {
            //zoom out
            if (zoom > 0)
                zoom--;
        }
        mDist = newDist;
        params.setZoom(zoom);
        camera.setParameters(params);
    }

    public void handleFocus(MotionEvent event, Camera.Parameters params) {
        int pointerId = event.getPointerId(0);
        int pointerIndex = event.findPointerIndex(pointerId);
        // Get the pointer's current position
        float x = event.getX(pointerIndex);
        float y = event.getY(pointerIndex);

        List<String> supportedFocusModes = params.getSupportedFocusModes();
        if (supportedFocusModes != null && supportedFocusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
            camera.autoFocus(new Camera.AutoFocusCallback() {
                @Override
                public void onAutoFocus(boolean b, Camera camera) {
                    // currently set to auto-focus on single touch
                }
            });
        }
    }

    /** Determine the space between the first two fingers */
    private float getFingerSpacing(MotionEvent event) {
        // ...
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float)Math.sqrt(x * x + y * y);
    }
}


