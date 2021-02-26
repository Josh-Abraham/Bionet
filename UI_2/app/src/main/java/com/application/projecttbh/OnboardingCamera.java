package com.application.projecttbh;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraControl;
import androidx.camera.core.CameraInfo;
import androidx.camera.core.CameraSelector;

import androidx.camera.core.FocusMeteringAction;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.MeteringPoint;
import androidx.camera.core.MeteringPointFactory;
import androidx.camera.core.Preview;

import androidx.camera.extensions.HdrImageCaptureExtender;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;
import androidx.security.crypto.EncryptedFile;
import androidx.security.crypto.MasterKey;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.common.util.concurrent.ListenableFuture;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.GeneralSecurityException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class OnboardingCamera extends AppCompatActivity {

    private Executor executor = Executors.newSingleThreadExecutor();
    private int REQUEST_CODE_PERMISSIONS = 101;
    private final String[] REQUIRED_PERMISSIONS = new String[]{"android.permission.CAMERA", "android.permission.WRITE_EXTERNAL_STORAGE"};

    PreviewView mPreviewView;
    ImageView captureImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.onboarding_camera);

        mPreviewView = findViewById(R.id.previewView);
        captureImage = findViewById(R.id.captureImg);

        if (allPermissionsGranted()) {
            startCamera(); //start camera if permission has been granted by user
        } else {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
        }
    }

    private void startCamera() {

        final ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);

        cameraProviderFuture.addListener(new Runnable() {
            @Override
            public void run() {
                try {

                    ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                    bindPreview(cameraProvider);

                } catch (ExecutionException | InterruptedException e) {
                    // No errors need to be handled for this Future.
                    // This should never be reached.
                }
            }
        }, ContextCompat.getMainExecutor(this));
    }

    void bindPreview(@NonNull ProcessCameraProvider cameraProvider) {

        Preview preview = new Preview.Builder()
                .build();

        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();

        ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                .build();

        ImageCapture.Builder builder = new ImageCapture.Builder();

        //Vendor-Extensions (The CameraX extensions dependency in build.gradle)
        HdrImageCaptureExtender hdrImageCaptureExtender = HdrImageCaptureExtender.create(builder);

        // Query if extension is available (optional).
        if (hdrImageCaptureExtender.isExtensionAvailable(cameraSelector)) {
            // Enable the extension if available.
            hdrImageCaptureExtender.enableExtension(cameraSelector);
        }

        final ImageCapture imageCapture = builder
                .setTargetRotation(this.getWindowManager().getDefaultDisplay().getRotation())
                .build();
        preview.setSurfaceProvider(mPreviewView.getSurfaceProvider());
        Camera camera = cameraProvider.bindToLifecycle((LifecycleOwner) this, cameraSelector, preview, imageAnalysis, imageCapture);

        CameraControl cameraControl = camera.getCameraControl();
        Context context = getApplicationContext();

        captureImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SimpleDateFormat mDateFormat = new SimpleDateFormat("yyyyMMddHHmmss", Locale.US);
                String fileName = mDateFormat.format(new Date()) + ".jpg";
                File file = new File(getBatchDirectoryName(), fileName);

                ImageCapture.OutputFileOptions outputFileOptions = new ImageCapture.OutputFileOptions.Builder(file).build();
                imageCapture.takePicture(outputFileOptions, executor, new ImageCapture.OnImageSavedCallback() {
                    @Override
                    public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @RequiresApi(api = Build.VERSION_CODES.O)
                            @Override
                            public void run() {
                                OnboardData.getInstance().setDirectory(getBatchDirectoryName());
                                OnboardData.getInstance().setFile(fileName);
                                // Toast.makeText(OnboardingCamera.this, "Image Saved successfully", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(OnboardingCamera.this, FinishOnboarding.class); // Call a secondary view
                                startActivity(intent);
                            }
                        });
                    }

                    @Override
                    public void onError(@NonNull ImageCaptureException error) {
                        error.printStackTrace();
                    }
                });

                // Need to figure out onboard encryption - not getting the right file right now :(
//                ContextWrapper cw = new ContextWrapper(getApplicationContext());
//                File directory = cw.getDir("TSA", Context.MODE_PRIVATE);
//                file = new File(getBatchDirectoryName(), mDateFormat.format(new Date()) + ".jpg");
//                MasterKey mainKey = null;
//                try {
//                    mainKey = new MasterKey.Builder(context).setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build();
//                    EncryptedFile encryptedFile = new EncryptedFile.Builder(context,
//                            new File(directory, mDateFormat.format(new Date())  + ".txt"),
//                            mainKey,
//                            EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
//                    ).build();
//
//
//
//                    byte bytes[] = new byte[(int) file.length()];
//                    BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
//                    DataInputStream dis = new DataInputStream(bis);
//                    dis.readFully(bytes);
//
//                    System.out.println(bytes[0] + bytes[1] + bytes[2] + bytes[3] + bytes[4] + bytes[5]);
//
//                    OutputStream outputStream = encryptedFile.openFileOutput();
//                    outputStream.write(bytes);
//                    outputStream.flush();
//                    outputStream.close();
//
//                    // Decrypt
//
//                    EncryptedFile encryptedFileRead = new EncryptedFile.Builder(context,
//                            new File(directory, mDateFormat.format(new Date())  + ".txt"),
//                            mainKey,
//                            EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
//                    ).build();
//
//                    InputStream inputStream = encryptedFileRead.openFileInput();
//                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//                    int nextByte = inputStream.read();
//                    while (nextByte != -1) {
//                        byteArrayOutputStream.write(nextByte);
//                        nextByte = inputStream.read();
//                    }
//
//                    byte[] plaintext = byteArrayOutputStream.toByteArray();
//                    System.out.println("BYTES BACK");
//                    System.out.println(plaintext[0] + plaintext[1] + plaintext[2] + plaintext[3] + plaintext[4] + plaintext[5]);
//
//                } catch (GeneralSecurityException e) {
//                    e.printStackTrace();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
            }
        });

        ScaleGestureDetector.SimpleOnScaleGestureListener listener = new ScaleGestureDetector.SimpleOnScaleGestureListener() {
            @Override
            public boolean onScale(ScaleGestureDetector detector) {
                CameraInfo camInfo = camera.getCameraInfo();
                float currentZoomRatio = camInfo.getZoomState().getValue().getZoomRatio();
                float delta = detector.getScaleFactor();
                cameraControl.setZoomRatio(currentZoomRatio * delta);
                return true;
            }

        };
        ScaleGestureDetector scaleGestureDetector = new ScaleGestureDetector(context, listener);

        mPreviewView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == 1) {
                    MeteringPointFactory factory = mPreviewView.getMeteringPointFactory();
                    MeteringPoint point = factory.createPoint(event.getX(), event.getY());
                    FocusMeteringAction action = new FocusMeteringAction.Builder(point).build();
                    cameraControl.startFocusAndMetering(action);
                } else {
                    scaleGestureDetector.onTouchEvent(event);
                }
                return true;
            }
        });
    }


    public String getBatchDirectoryName() {

        String app_folder_path = "";
        app_folder_path = Environment.getExternalStorageDirectory().toString() + "/TSA";
        File dir = new File(app_folder_path);
        if (!dir.exists() && !dir.mkdirs()) {

        }

        return app_folder_path;
    }

    private boolean allPermissionsGranted() {

        for (String permission : REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera();
            } else {
                Toast.makeText(this, "Permissions not granted by the user.", Toast.LENGTH_SHORT).show();
                this.finish();
            }
        }
    }
}
