package com.application.projecttbh;
import android.app.Activity;
import android.app.Application;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.auth.BasicAWSCredentials;

import java.io.File;

public class S3Client extends Activity{

    public void uploadFile(String location, Object file) {
        String accessKey = "";
        String secret = "";
        BasicAWSCredentials credentials = new BasicAWSCredentials(accessKey,secret);
        AmazonS3Client s3 = new AmazonS3Client(credentials);
        s3.setRegion(Region.getRegion(Regions.US_EAST_2));

        TransferUtility transferUtility = TransferUtility.builder().context(getApplicationContext()).s3Client(s3).build();
        File myObj = new File("hellloooo.txt");
        TransferObserver observer = transferUtility.upload("profiles-capstone", "video_test.jpg", myObj);
    }


}
