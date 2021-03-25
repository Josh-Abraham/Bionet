package com.application.projecttbh;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import java.io.File;

public class OnboardingConfirmPhoto extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.onboarding_confirm_photo);
        Context context = getApplicationContext();
        String pathName = context.getFilesDir() + "/Images/" + OnboardData.getInstance().getFile();
        setImagePreview(pathName);
        Button onRetakeFacialCap = findViewById(R.id.retake_facial_capture);
        Button onConfirmFacialCapture = findViewById(R.id.confirm_and_upload);


        onRetakeFacialCap.setOnClickListener(v -> {
           // On Retake, delete photo and swap back to camera view
            String app_folder_path = "";
            app_folder_path = context.getFilesDir() + "/" + OnboardData.getInstance().getDirectory();
            File dir = new File(app_folder_path);
            if (!dir.exists() && !dir.mkdirs()) {

            }
            File file = new File(dir, OnboardData.getInstance().getFile());
            boolean deleted = file.delete();
            OnboardData.getInstance().setFile("");
            OnboardData.getInstance().setDirectory("");
            finish();
        });

        onConfirmFacialCapture.setOnClickListener(v -> {
            Intent intent = new Intent(OnboardingConfirmPhoto.this, UploadOnboardData.class); // Call a secondary view
            startActivity(intent);
        });
    }

    public void setImagePreview(String pathName) {
        File imgFile = new File(pathName);

        if(imgFile.exists()){
            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            ImageView facialImage = findViewById(R.id.imageviewFacialCheck);
            facialImage.setImageBitmap(myBitmap);
            facialImage.setRotation(90);

        }
    }
}
