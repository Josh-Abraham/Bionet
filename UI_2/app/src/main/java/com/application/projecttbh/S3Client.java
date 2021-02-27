package com.application.projecttbh;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Environment;

import androidx.security.crypto.EncryptedFile;
import androidx.security.crypto.MasterKey;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.auth.BasicAWSCredentials;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.GeneralSecurityException;

public class S3Client {
    private static final String FACIAL = "facial";
    private static final String FACIAL_CAPTURE = "facial-captures/";

    public static void uploadFile(String fileName, String fileDir, Context context, String type) {
        String accessKey = "";
        String secret = "";
        BasicAWSCredentials credentials = new BasicAWSCredentials(accessKey, secret);
        AmazonS3Client s3 = new AmazonS3Client(credentials);
        s3.setRegion(Region.getRegion(Regions.US_EAST_2));

        TransferUtility transferUtility = TransferUtility.builder().context(context).s3Client(s3).build();
        String location = fileDir + "/" + fileName;
        File myObj = new File(location);
        TransferObserver observer = null;
        if (type.equals(FACIAL)) {
            observer = transferUtility.upload("profiles-capstone", FACIAL_CAPTURE + fileName, myObj);
        }


        observer.setTransferListener(new TransferListener() {

            @Override
            public void onStateChanged(int id, TransferState state) {
                if (state.toString().equals("COMPLETED")) {
                    // callback logic for loading vs complete
                    if (type.equals(FACIAL)) {
                        OnboardData.getInstance().setS3_facial_key(FACIAL_CAPTURE + fileName);
                    }
                }
            }

            @Override
            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {

            }

            @Override
            public void onError(int id, Exception ex) {

            }
        });
    }

    public static String getBatchDirectoryName() {

        String app_folder_path = "";
        app_folder_path = Environment.getExternalStorageDirectory().toString() + "/TSA";
        File dir = new File(app_folder_path);
        if (!dir.exists() && !dir.mkdirs()) {

        }

        return app_folder_path;
    }

}
