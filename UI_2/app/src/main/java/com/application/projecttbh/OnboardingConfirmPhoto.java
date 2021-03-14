package com.application.projecttbh;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.amazonaws.mobile.client.AWSMobileClient;

import java.io.File;


public class OnboardingConfirmPhoto extends Activity {
    private Button onRetakeFacialCap;
    private Button onConfirmFacialCapture;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.onboarding_confirm_photo);
        // Move to on confirm all data
        Context context = getApplicationContext();
        // String file_name =  OnboardData.getInstance().getFile();
        // String file_dir = OnboardData.getInstance().getDirectory();
        // Loading starts Here but should end in the S3 CB
        // String type = "facial";
        // S3Client.uploadFile(file_name, file_dir, context, type);

        // Set's preview Image
        // Context context = getApplicationContext();
        String pathName = context.getFilesDir() + "/Images/" + OnboardData.getInstance().getFile();

        setImagePreview(pathName);

        onRetakeFacialCap = (Button) findViewById(R.id.retake_facial_capture);
        onConfirmFacialCapture = (Button) findViewById(R.id.confirm_and_upload);


        onRetakeFacialCap.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
               // On Retake, delete photo and swap back to camera view
                String app_folder_path = "";


                app_folder_path = context.getFilesDir() + "/" + OnboardData.getInstance().getDirectory();
                File dir = new File(app_folder_path);
                System.out.println(app_folder_path);
                if (!dir.exists() && !dir.mkdirs()) {

                }
                File file = new File(dir, OnboardData.getInstance().getFile());
                boolean deleted = file.delete();
                OnboardData.getInstance().setFile("");
                OnboardData.getInstance().setDirectory("");
                finish();
            }
        });

        onConfirmFacialCapture.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if (!AppProperties.getInstance().getEnableFP()) {
                    AppProperties.getInstance().setSeqNum(4);
                }

                Intent intent = new Intent(OnboardingConfirmPhoto.this, InitialScan.class); // Call a secondary view
                startActivity(intent);

            }
        });
    }

    public void setImagePreview(String pathName) {
        File imgFile = new File(pathName);

        if(imgFile.exists()){

            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());

            ImageView facialImage = (ImageView) findViewById(R.id.imageviewFacialCheck);

            facialImage.setImageBitmap(myBitmap);
            facialImage.setRotation(90);

        }
    }
}
