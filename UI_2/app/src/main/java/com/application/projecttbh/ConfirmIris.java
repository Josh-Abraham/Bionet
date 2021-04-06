package com.application.projecttbh;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class ConfirmIris extends Activity {

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.onboarding_confirm_photo);
        Context context = getApplicationContext();
        String pathName = context.getFilesDir() + "/Iris/output.jpg";
        setImagePreview(pathName);
        Button onRetakeFacialCap = findViewById(R.id.retake_facial_capture);
        Button onConfirmFacialCapture = findViewById(R.id.confirm_and_upload);


        onRetakeFacialCap.setOnClickListener(v -> {
            finish();
        });

        onConfirmFacialCapture.setOnClickListener(v -> {
            try {
                saveData();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public void setImagePreview(String pathName) {
        File imgFile = new File(pathName);

        if(imgFile.exists()){
            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            ImageView facialImage = findViewById(R.id.imageviewFacialCheck);
            facialImage.setImageBitmap(myBitmap);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void saveData() throws IOException {
        try {
            String allData = OnboardData.getInstance().getIris_image();
            Context context = getApplicationContext();
            if (AppProperties.getInstance().getType().equals("onboarding")) {

                String tag = OnboardData.getInstance().getPassportId() + "_IRIS_" + 0 + ".txt";
                int currentScan = 3;
                if (OnboardData.getInstance().get_S3_iris_data()[0].equals("")) {

                    OnboardData.getInstance().update_S3_iris_data(tag, 0);
                } else {
                    currentScan = 4;
                    tag = OnboardData.getInstance().getPassportId() + "_IRIS_" + 1 + ".txt";
                    OnboardData.getInstance().update_S3_iris_data(tag, 1);
                }


                File dir = new File(context.getFilesDir(), "IRIS");
                if(!dir.exists()){
                    dir.mkdir();
                }
                BufferedWriter writer = new BufferedWriter(new FileWriter(context.getFilesDir() + "/IRIS/" + tag));
                writer.write(AES.encrypt(allData));

                writer.close();



                AppProperties.getInstance().setSeqNum(currentScan);
                if (AppProperties.getInstance().getSeqNum() == 4) {
                    Intent intent = new Intent(ConfirmIris.this, UploadOnboardData.class); // Call a secondary view
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(ConfirmIris.this, InitialScan.class); // Call a secondary view
                    startActivity(intent);
                }
            }
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), String.format("%s\n", e), Toast.LENGTH_LONG).show();
        }

    }
}

