package perfect.book.keeping.global;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import perfect.book.keeping.activity.company.gallery.ReceiptGallery;
import perfect.book.keeping.manager.Manager;

public class DownloadGalleryTask extends AsyncTask<String, String, String> {
    private Context context;
    String imgString;
    Manager db;

    public DownloadGalleryTask(Context context) {
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... gallery) {
        String imgUrl = gallery[0];
        String fileName = gallery[1];
        String imageId = gallery[2];
        try {
            URL url = new URL(imgUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();

            InputStream inputStream = connection.getInputStream();
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

            String file_name = fileName+".png";

            File directory = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES).toString(), "Thumbnail");
            if (!directory.exists()) {
                if (!directory.mkdirs()) {
                    return null;
                }
            }

            File imageFile = new File(directory, file_name);

            // Delete any previous file with the same name
            if (imageFile.exists()) {
                imageFile.delete();
            }

            FileOutputStream outputStream = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            outputStream.flush();
            outputStream.close();

            imgString = imageFile.getAbsolutePath() + ":" + imageId;
            Log.e("IMAGE SAVE TAG", "Image Path" + imgString);
        } catch (IOException e) {
            imgString = "";
            Log.e("IMAGE SAVE TAG", "Failed to download the image: " + e.getMessage());
        }
        return  imgString;
    }

    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);
        Log.e("VALUES FOR PROGRESS UPDATE",""+values);
    }

    @Override
    protected void onPostExecute(String result) {
        db = new Manager(context);
        String[] response = result.split(":");
        Log.e("IMAGE SAVE TAG", "Image Path" + response[1]);
        Log.e("IMAGE SAVE TAG", "Image Path" + response[0]);
        db.updateGalleryImage(Integer.parseInt(response[1]),response[0]);
    }
}
