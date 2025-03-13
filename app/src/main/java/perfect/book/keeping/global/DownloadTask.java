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

import perfect.book.keeping.manager.Manager;

public class DownloadTask extends AsyncTask<String, Void, String> {

    private Context context;
    String imgString;
    Manager db;

    public DownloadTask(Context context) {
        this.context = context;
    }

    @Override
    protected String doInBackground(String... imageUrl) {
        String imgUrl = imageUrl[0];
        String timestamp = imageUrl[1];
        String companyId = imageUrl[2];
        try {
            URL url = new URL(imgUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream inputStream = connection.getInputStream();
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

            String fileName = timestamp+".png";
            File directory = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES).toString(), "Company");
            if (!directory.exists()) {
                if (!directory.mkdirs()) {
                    return null;
                }
            }
            File imageFile = new File(directory, fileName);
            if (imageFile.exists()) {
                imageFile.delete();
            }

            FileOutputStream outputStream = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            outputStream.flush();
            outputStream.close();

            imgString = imageFile.getAbsolutePath() + "_" + timestamp + "_" + companyId;
        }  catch (IOException e) {
            throw new RuntimeException(e);
        }
        return imgString;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(String result) {
        db = new Manager(context);
        String[] response = result.split("_");
        db.updateImageForCompany(Integer.valueOf(response[2]), response[0], response[1]+".png");
    }
}
