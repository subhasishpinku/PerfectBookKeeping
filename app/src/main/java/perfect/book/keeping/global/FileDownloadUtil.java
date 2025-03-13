package perfect.book.keeping.global;

import android.app.Dialog;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import androidx.core.content.FileProvider;

import java.io.File;

import perfect.book.keeping.R;
import perfect.book.keeping.activity.pnl.ProfitLoss;

public class FileDownloadUtil {
    private Context context;
    private long downloadId;

    private String file_name;

    private Dialog progressDialog;

    public FileDownloadUtil(Context context) {
        this.context = context;
    }

    public void downloadFile(String url, String destinationFileName) {
        progressDialog = new Dialog(context);
        progressDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        progressDialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        progressDialog.setContentView(R.layout.progress);
        if (progressDialog.getWindow() != null) {
            progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        progressDialog.show();
        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(url);

        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, destinationFileName);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_ONLY_COMPLETION);

        downloadId = downloadManager.enqueue(request);

        // Register BroadcastReceiver to receive notification when download is complete
        context.registerReceiver(downloadCompleteReceiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        file_name = destinationFileName;
    }

    private BroadcastReceiver downloadCompleteReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            Log.e("ID IS",""+id);
            Log.e("ID IS DOWNLOAD",""+downloadId);
            if (id == downloadId) {
                // Download is complete, now open the file
                openDownloadedFile();
                progressDialog.dismiss();
            }
        }
    };

    private void openDownloadedFile() {
        String fileName = file_name; // Replace with the actual file name
        String filePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath() + "/" + fileName;
        Uri fileUri = Uri.parse("file://" + filePath);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(fileUri, getMimeType(fileName));
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

        try {
            context.startActivity(intent);
        } catch (Exception e) {
            // Handle exception, e.g., if no application can handle the file type
            Log.e("ERROR FILE OPEN",""+e.getMessage());
            e.printStackTrace();
        }
    }

    private String getMimeType(String fileName) {
        String[] nameParts = fileName.split("\\.");
        String extension = nameParts[nameParts.length - 1];
        Log.e("FILE EXTENSION", ""+extension);
        switch (extension.toLowerCase()) {
            case "pdf":
                return "application/pdf";
            case "txt":
                return "text/plain";
            case "jpg":
                return "image/jpg";
            case "jpeg":
                return "image/jpeg";
            case "png":
                return "image/png";
            // Add more cases for other file types if needed
            default:
                return "*/*"; // Use the generic mime type if the file type is not supported
        }
    }
}
