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
        AppProperties.getInstance().setSeqNum(0); // Reset
        setContentView(R.layout.onboarding_confirm_photo);
        Context context = getApplicationContext();
        String pathName = context.getFilesDir() + "/" + MatchingProperties.getInstance().getDirectory() + "/" + MatchingProperties.getInstance().getFacialScan();
        setImagePreview(pathName);
        Button onRetakeFacialCap = findViewById(R.id.retake_facial_capture);
        Button onConfirmFacialCapture = findViewById(R.id.confirm_and_upload);


        onRetakeFacialCap.setOnClickListener(v -> {
            // On Retake, delete photo and swap back to camera view
            String app_folder_path = "";
            app_folder_path = context.getFilesDir() + "/" + MatchingProperties.getInstance().getDirectory();
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
            AppProperties.getInstance().setSeqNum(1);
            if (MatchingProperties.getInstance().getFullSeq().length > 1) {
                intent = new Intent(MatchingConfirmPhoto.this, InitialMatchingScan.class); // Call a secondary view
            } else {
                intent = new Intent(MatchingConfirmPhoto.this, MatchingStart.class); // Call a secondary view
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
