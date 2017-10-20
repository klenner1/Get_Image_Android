package com.example.barros.klenner.getimage.Util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


public class ImagesUtil {

    //create a bitmap from a file
    public static Bitmap decodeImageFile(File f, int width, int height) {
        try {
            Matrix matrix = getMatrix(f); // Create matrix
            BitmapFactory.Options o = new BitmapFactory.Options(); // Create BitmapFactory options
            o.inJustDecodeBounds = true; // allow the caller to query the bitmap without having to allocate the memory for its pixels.
            Bitmap bitmap1 = BitmapFactory.decodeStream(new FileInputStream(f), null, o);//Bitmap for find the final height and width
            int scale = 1; //Scale
            while (o.outWidth / scale / 2 >= width && o.outHeight / scale / 2 >= height) {//while the new size is bigger than required
                scale *= 2; //Finding the scale to resize
            }
            BitmapFactory.Options o2 = new BitmapFactory.Options(); // Create BitmapFactory options for resize new bitmap
            o2.inSampleSize = scale; //  requests the decoder to sub amplify the original image
            Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(f), null, o2); //create a sub amplify bitmap
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, false);//rotate bitmap if necessary
            return bitmap; // Return the Bitmap
        } catch (FileNotFoundException e) {
            Log.e("buscar-imagem", e.getMessage(), e);
            return null;
        } catch (IOException e) {
            Log.e("buscar-imagem", e.getMessage(), e);
            return null;
        } catch (Exception e) {
            Log.e("buscar-imagem", e.getMessage(), e);
            return null;
        }
    }

    //Get Image Matrix
    private static Matrix getMatrix(File f) throws IOException {
        int rotation;
        int degrees = 0;
        try {
            ExifInterface exif = new ExifInterface(f.getPath());
            rotation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL); // Get rotation tag
            degrees = exifToDegrees(rotation); // Get rotate in degrees
        } catch (Exception e){}
        finally {
            Matrix matrix = new Matrix();
            if (degrees != 0) {// If rotation = 0 , don't need to rotate
                matrix.preRotate(degrees); // Pre rotate
            }
            return matrix; // Return Matrix
        }

    }

    //save Picture
    public static File savePicture(Bitmap bitmap, Context context) {
        File file = null;
        file = new File(context.getExternalCacheDir(), "picture.jpg"); // Create a new file to save picture on external cache
        try {
            ByteArrayOutputStream stream = new ByteArrayOutputStream(); // Create a ByteArrayOutputStream
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream); // Write a compressed version of the bitmap to the stream
            byte[] bytes = stream.toByteArray(); // ByteArrayOutputStream to byte array
            FileOutputStream fos = new FileOutputStream(file); // Create a FileOutputStream
            fos.write(bytes); // Write the bytes in the file
            fos.close(); // Close the FileOutputStream
            Log.i("imagem_salva", file.getAbsolutePath());
            return file; // Return the picture file
        } catch (IOException e) {
            Log.e("bitmap", e.getMessage(), e);
            return null;
        } catch (Exception e) {
            Log.e("bitmap", e.getMessage(), e);
            return null;
        }
    }

    public static int exifToDegrees(int exifOrientation) {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
            return 90;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
            return 180;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
            return 270;
        } else
        return 0;
    }
}
