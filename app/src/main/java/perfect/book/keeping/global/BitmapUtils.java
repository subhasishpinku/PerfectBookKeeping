package perfect.book.keeping.global;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.os.Environment;
import android.provider.MediaStore;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class BitmapUtils {

    public static String getImageUri(Context context, Bitmap bitmap, String displayName) throws IOException {
        // Get the external storage directory
        File imagesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        // Create a new file in the directory
        File imageFile = new File(imagesDir, displayName + ".png");

        // Save the bitmap to the file
        FileOutputStream fos = new FileOutputStream(imageFile);
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
        fos.flush();
        fos.close();

        // Notify the media scanner to add the saved image to the gallery
        MediaScannerConnection.scanFile(context, new String[]{imageFile.getAbsolutePath()}, null, null);

        // Get the content resolver
        ContentResolver contentResolver = context.getContentResolver();

        // Create the content values
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DISPLAY_NAME, displayName);
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
        values.put(MediaStore.Images.Media.DATA, imageFile.getAbsolutePath());

        // Insert the image to the media store
        return contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values).toString();
    }
}
