package com.example.barros.klenner.getimage;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import com.example.barros.klenner.getimage.Util.*;

public class ImageActivity extends AppCompatActivity {


    private static final int PERMISSIONS_REQUEST_EXTERNAL_STORAGE = 1; // request to read and write external storage
    private static final int PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 2; // request to read external storage
    private static final int PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 3; // request to write external storage
    private static final int PICK_PHOTO_REQUEST = 1; //request to get a picture

    private ImageView imgVwPicture;
    private Button btnGetImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        imgVwPicture = (ImageView) findViewById(R.id.imgVwPicture);
        btnGetImage = (Button) findViewById(R.id.btnGetImage);

        btnGetImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (requestPermission()) {//check if is permitted read and write on storage
                    getPicture();//get picture if is permitted
                }
            }
        });
    }

    //start new activity to get picture from gallery
    private void getPicture() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, PICK_PHOTO_REQUEST);
    }

    //result of activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_PHOTO_REQUEST && resultCode == Activity.RESULT_OK) { //if activity return a image
            Uri uri = data.getData();
            getPictureFromUri(uri);
        }
    }

    //get picture from a uri
    private void getPictureFromUri(Uri uri) {
        try {
            File localFile = FilesUtil.createFile(getBaseContext(), "picture.jpg"); //create file to copy the picture
            FileInputStream remoteFile = FilesUtil.getSourceStream(getBaseContext(), uri); //create and open FileInputStream to get the picture
            if (FilesUtil.copyToFile(remoteFile, localFile)) { //try copy and if picture copied do...
                Bitmap b = ImagesUtil.decodeImageFile(localFile, 300, 300); //resize picture
                File file = ImagesUtil.savePicture(b, this); //save the picture resized
                imgVwPicture.setImageURI(Uri.parse(file.getPath()));//show picture
            }else {//if picture won't copied
                Toast.makeText(this,R.string.error,Toast.LENGTH_LONG).show();//show error message
            }
            remoteFile.close(); //close the FileInputStream
        } catch (IOException e) {
            Log.e("getPictureFromUri", e.getMessage(), e);
        } catch (Exception e) {
            Log.e("getPictureFromUri", e.getMessage(), e);
        }
    }

    //request the permissions
    private Boolean requestPermission(){
        /*
             Don't forget to put the user permissions on Android manifest
             <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"/>
             <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>
        */
        Boolean permitido = true;
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permitido = false;
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    PERMISSIONS_REQUEST_EXTERNAL_STORAGE);
        }else if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            permitido = false;
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
        } else if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permitido = false;
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
        }
        return permitido;
    }

    //result of requests permissions
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_EXTERNAL_STORAGE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults.length > 0
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    getPicture(); //get picture if the permission is granted
                }
                else {
                    requestPermission();
                }
                return;
            }case PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED ) {
                    getPicture(); //get picture if the permission is granted
                }
                else {
                    requestPermission();
                }
                return;
            }case PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getPicture(); //get picture if the permission is granted
                }else {
                    requestPermission();
                }
                return;
            }
        }
    }


}
