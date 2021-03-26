package com.application.projecttbh;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;


public class MatchingStart extends Activity {
    Button uploadBtn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.matching_load);
        uploadBtn = findViewById(R.id.start_authentication);

        uploadBtn.setOnClickListener(v -> {
            uploadS3Data();
            Intent intent = new Intent(MatchingStart.this, MatchingRun.class); // Call a secondary view
            startActivity(intent);
        });
    }

    private void uploadS3Data() {
        Context context = getApplicationContext();
        MatchingProperties instance = MatchingProperties.getInstance();
        if (instance.isEnableFace() && !instance.getFacialScan().equals("")) {
            S3Client.uploadMatchingFile(instance.getFacialScan(), context);
        }

        if (instance.isEnableFP() && !instance.getFpS3()[0].equals("")) {
            S3Client.uploadMatchingFile(instance.getFpS3()[0], context);
        }

        if (instance.isEnableFP() && !instance.getFpS3()[1].equals("")) {
            S3Client.uploadMatchingFile(instance.getFpS3()[1], context);
        }

        if (instance.isEnableIris() && !instance.getIrisS3()[0].equals("")) {
            S3Client.uploadMatchingFile(instance.getIrisS3()[0], context);
        }

        if (instance.isEnableIris() && !instance.getIrisS3()[1].equals("")) {
            S3Client.uploadMatchingFile(instance.getIrisS3()[1], context);
        }
    }
}
