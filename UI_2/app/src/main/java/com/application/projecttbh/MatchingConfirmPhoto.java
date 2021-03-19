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

public class MatchingConfirmPhoto extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.onboarding_confirm_photo);
        Context context = getApplicationContext();
        String pathName = context.getFilesDir() + "/" + MatchingProperties.getInstance().getMatchingDirectoy() + "/" + MatchingProperties.getInstance().getFacialScan();
        setImagePreview(pathName);
        Button onRetakeFacialCap = findViewById(R.id.retake_facial_capture);
        Button onConfirmFacialCapture = findViewById(R.id.confirm_and_upload);


        onRetakeFacialCap.setOnClickListener(v -> {
            // On Retake, delete photo and swap back to camera view
            String app_folder_path = "";
            app_folder_path = context.getFilesDir() + "/" + MatchingProperties.getInstance().getMatchingDirectoy();
            File dir = new File(app_folder_path);
            if (!dir.exists() && !dir.mkdirs()) {

            }
            String id = MatchingProperties.getInstance().getPassportId();
            String fileName = id + "_face" + ".jpg";
            File file = new File(dir, fileName);
            boolean deleted = file.delete();
            MatchingProperties.getInstance().setFacialScan("");
            finish();
        });

        onConfirmFacialCapture.setOnClickListener(v -> {
            Intent intent;
            boolean fp[] = MatchingProperties.getInstance().getFpOptions();
            boolean iris[] = MatchingProperties.getInstance().getIrisOptions();
            if ((MatchingProperties.getInstance().isEnableFP() && (fp[0] || fp[1] || fp[2] || fp[3])) || (MatchingProperties.getInstance().isEnableFP() && (iris[0] || iris[1]))) {
                intent = new Intent(MatchingConfirmPhoto.this, InitialScan.class); // Call a secondary view
            } else {
                //TODO: EDIT THIS
                intent = new Intent(MatchingConfirmPhoto.this, AgentHome.class); // Call a secondary view
            }

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
