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

public class ConfirmIrisMatch extends Activity {

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
            String allData = MatchingProperties.getInstance().getIris_image();
            Context context = getApplicationContext();
            String tag = MatchingProperties.getInstance().getPassportId() + "_IRIS_" + 0 + ".txt";

            if (MatchingProperties.getInstance().getFullSeq()[AppProperties.getInstance().getSeqNum()] == 3) {
                MatchingProperties.getInstance().updateIrisS3(0, tag);
            } else {
                tag = MatchingProperties.getInstance().getPassportId() + "_IRIS_" + 1 + ".txt";
                MatchingProperties.getInstance().updateIrisS3(1, tag);
            }

            File dir = new File(context.getFilesDir(), MatchingProperties.getInstance().getDirectory());
            if (!dir.exists()) {
                dir.mkdir();
            }
            BufferedWriter writer = new BufferedWriter(new FileWriter(context.getFilesDir() + "/" + MatchingProperties.getInstance().getDirectory() + "/" + tag));
            writer.write(AES.encrypt(allData));
            writer.close();

            int key = AppProperties.getInstance().getSeqNum();
            key += 1;
            Intent intent;
            if (key < MatchingProperties.getInstance().getFullSeq().length) {
                AppProperties.getInstance().setSeqNum(key);
                intent = new Intent(ConfirmIrisMatch.this, InitialMatchingScan.class); // Call a secondary view
            } else {
               intent = new Intent(ConfirmIrisMatch.this, MatchingStart.class); // Call a secondary view
            }
            startActivity(intent);

        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), String.format("%s\n", e), Toast.LENGTH_LONG).show();
        }
    }
}

