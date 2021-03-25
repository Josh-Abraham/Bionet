 package com.application.projecttbh;

import android.content.Context;
import android.os.Environment;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.auth.BasicAWSCredentials;
import org.mitre.jet.exceptions.EbtsBuildingException;
import java.io.File;


public class S3Client {
    private static final String FACIAL_CAPTURE = "Facial-captures/";
    private static final String EBTS_CAPTURE = "EBTS-captures/";
    private static final String FP_CAPTURE = "FP-captures/";
    private static final String MATCHING_CAPTURE = "MatchingStart-captures/";
    private static final String IRIS_CAPTURE = "IRIS-captures/";
    private static String accessKey = "AKIASSDK5I27GBNEH74M";
    private static String secret = "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";

    public static void uploadFacialFile(String fileName, Context context) {

        BasicAWSCredentials credentials = new BasicAWSCredentials(accessKey, secret);
        AmazonS3Client s3 = new AmazonS3Client(credentials);
        s3.setRegion(Region.getRegion(Regions.US_EAST_2));

        TransferUtility transferUtility = TransferUtility.builder().context(context).s3Client(s3).build();
        File dir = new File(context.getFilesDir(), "Images");
        File faceFile = new File(dir, fileName);
        TransferObserver observer = null;

        observer = transferUtility.upload("profiles-capstone", FACIAL_CAPTURE + fileName, faceFile);
    }

    public static void uploadEBTS(File ebts, Context context) throws EbtsBuildingException {
        BasicAWSCredentials credentials = new BasicAWSCredentials(accessKey, secret);
        AmazonS3Client s3 = new AmazonS3Client(credentials);
        s3.setRegion(Region.getRegion(Regions.US_EAST_2));
        TransferUtility transferUtility = TransferUtility.builder().context(context).s3Client(s3).build();
        TransferObserver observer = null;

        if (AppProperties.getInstance().getDebugMode()) {
            observer = transferUtility.upload("profiles-capstone", EBTS_CAPTURE + "DEBUG_MODE_EBTS.txt", ebts);
        } else {
            observer = transferUtility.upload("profiles-capstone", EBTS_CAPTURE + OnboardData.getInstance().getPassportId() + "_EBTS.txt", ebts);
        }
    }

    public static void uploadBiometric(String fpFileName, Context context, String dirName) {
        BasicAWSCredentials credentials = new BasicAWSCredentials(accessKey, secret);
        AmazonS3Client s3 = new AmazonS3Client(credentials);
        s3.setRegion(Region.getRegion(Regions.US_EAST_2));
        TransferUtility transferUtility = TransferUtility.builder().context(context).s3Client(s3).build();
        File dir = new File(context.getFilesDir(), dirName);
        File fpFile = new File(dir, fpFileName);
        String S3Dir = FP_CAPTURE;

        if (dirName.equals("IRIS")) {
            S3Dir = IRIS_CAPTURE;
        }
        if (AppProperties.getInstance().getDebugMode()) {
            transferUtility.upload("profiles-capstone", S3Dir + "DEBUG_MODE_FP.txt", fpFile);
        } else {
            transferUtility.upload("profiles-capstone", S3Dir + fpFileName, fpFile);
        }
    }

    public static void uploadMatchingFile(String fileName, Context context) {

        BasicAWSCredentials credentials = new BasicAWSCredentials(accessKey, secret);
        AmazonS3Client s3 = new AmazonS3Client(credentials);
        s3.setRegion(Region.getRegion(Regions.US_EAST_2));

        TransferUtility transferUtility = TransferUtility.builder().context(context).s3Client(s3).build();
        File dir = new File(context.getFilesDir(), MatchingProperties.getInstance().getDirectory());
        File faceFile = new File(dir, fileName);

        transferUtility.upload("profiles-capstone", MATCHING_CAPTURE + fileName, faceFile);
    }

}
