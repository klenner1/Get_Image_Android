package com.example.barros.klenner.getimage.Util;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.ParcelFileDescriptor;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FilesUtil {

    public static File createFile(Context context, String imageFileName){
        try {
            if (TextUtils.isEmpty(imageFileName))
                imageFileName = "file"; //create the file name if was null

            final File root;
            root = context.getExternalCacheDir(); //create a file on external cache dir

            if (root != null && !root.exists())
                root.mkdirs(); //Create the directory
            return new File(root, imageFileName); //return the file
        }catch (Exception e){
            Log.e("saveFile",e.getMessage(),e);
            return null;
        }
    }
    public static FileInputStream getSourceStream(Context context, Uri u){
        FileInputStream out = null;
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) { //case KitKat or more recent
                ParcelFileDescriptor parcelFileDescriptor;

                parcelFileDescriptor = context.getContentResolver().openFileDescriptor(u, "r");

                FileDescriptor fileDescriptor;
                if (parcelFileDescriptor != null) {
                    fileDescriptor = parcelFileDescriptor.getFileDescriptor();
                    out = new FileInputStream(fileDescriptor);
                }
            } else { //case older than KitKat
                out = (FileInputStream) context.getContentResolver().openInputStream(u);
            }
        } catch (FileNotFoundException e) {
            Log.e("getSourceStream",e.getMessage(),e);
        }finally {
            return out;
        }
    }

    //Copy file
    public static boolean copyToFile(InputStream inputStream, File destFile) {
        if (inputStream == null || destFile == null) return false;
        try {
            OutputStream out = new FileOutputStream(destFile);
            try {
                byte[] buffer = new byte[5120];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) >= 0) {
                    out.write(buffer, 0, bytesRead);
                }
            } finally {
                out.close();
            }
            return true;
        } catch (IOException e) {
            Log.e("copyToFile",e.getMessage(),e);
            return false;
        }
    }
}
