package perfect.book.keeping.activity.receipt;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.aseem.versatileprogressbar.ProgBar;
import com.github.barteksc.pdfviewer.PDFView;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.net.ssl.HttpsURLConnection;

import perfect.book.keeping.R;
import perfect.book.keeping.global.FileDownloadUtil;

public class PaymentReceiptView extends AppCompatActivity {
    String companyName,url,file_name;
    int companyId,permission;
    PDFView pdfView;
    ImageView backId;
    TextView header_title;
    Dialog progressDialog;
    ImageView dowenlodId;
    FileDownloadUtil fileDownloadUtil;

    ProgBar myProgressBar;
    private ExecutorService executorService;

    InputStream result;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_receipt_view);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        android.graphics.drawable.Drawable background = PaymentReceiptView.this.getDrawable(R.drawable.gradiant_btn_alt);
        getWindow().setBackgroundDrawable(background);

        pdfView = findViewById(R.id.idPDFView);
        backId = (ImageView)findViewById(R.id.backId);
        header_title = (TextView)findViewById(R.id.header_title);
        myProgressBar = (ProgBar) findViewById(R.id.myProgressBar);
        dowenlodId = (ImageView)findViewById(R.id.dowenlodId);
        Bundle extras = getIntent().getExtras();
        myProgressBar.setVisibility(View.GONE);
        executorService = Executors.newSingleThreadExecutor();
        if (extras != null) {
            companyId = extras.getInt("companyId");
            permission = extras.getInt("permission");
            companyName = extras.getString("companyName");
            url = extras.getString("url");
            file_name = extras.getString("file_name");
            System.out.println("DataLog  "+companyId+" "+permission+" "+companyName+" "+url+" "+file_name);}
            header_title.setText(file_name);

        fileDownloadUtil = new FileDownloadUtil(PaymentReceiptView.this);
        progressDialog = new Dialog(PaymentReceiptView.this);
        progressDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        progressDialog.setCancelable(false);
        progressDialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        progressDialog.setContentView(R.layout.progress);
        if (progressDialog.getWindow() != null) {
            progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
            progressDialog.show();
            loadFile();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                progressDialog.show();
            }
        }, 1500);
            backId.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    redirect();
                }
            });
            dowenlodId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fileDownloadUtil.downloadFile(url, file_name);
            }
        });

    }

    private void loadFile() {

        progressDialog.show();
        new RetrivePDFfromUrl().execute(url);
       // InputStream file = performAsyncTask(url);
    }

    class RetrivePDFfromUrl extends AsyncTask<String, Void, InputStream> {
        @SuppressLint("WrongThread")
        @Override
        protected InputStream doInBackground(String... strings) {

            // we are using inputstream
            // for getting out PDF.

            InputStream inputStream = null;
            Log.e("Loading", "Initialize");

            try {
                URL url = new URL(strings[0]);
                HttpURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
                Log.e("Loading", "Initialize Connection"+urlConnection);
                if (urlConnection.getResponseCode() == 200) {

                    Log.e("Loading", "Loading File");
                    inputStream = new BufferedInputStream(urlConnection.getInputStream());
                    System.out.println("LoadPf "+"0");
                } else {
                    Log.e("Loading", "Loading File");
                }

            } catch (IOException e) {
                Log.e("Loading", "Loading Error"+e.getLocalizedMessage());
                progressDialog.show();
                loadFile();

                e.printStackTrace();
                return null;
            }
            return inputStream;
        }

        @Override
        protected void onPostExecute(InputStream inputStream) {
            System.out.println("LoadPf "+inputStream);
            progressDialog.dismiss();
            myProgressBar.setVisibility(View.GONE);
            pdfView.fromStream(inputStream).load();


        }
    }


    public InputStream performAsyncTask(String url) {
        // Submit a task to the executor service
        Future<InputStream> future = executorService.submit(new MyTask(url));

        // Perform other operations or wait for the result
        // ...

        try {
            // Wait for the task to complete and get the result
            result = future.get();

            // Do something with the result

        } catch (Exception e) {
            // Handle any exceptions that occurred during task execution
            e.printStackTrace();
        }


        return result;
    }

        private class MyTask implements Callable<InputStream> {
            String token;
            public MyTask(String token) {
                this.token = token;
            }

            @Override
            public InputStream call() throws Exception {
                InputStream inputStream = null;
                Log.e("Loading", "Initialize");

                try {
                    URL url = new URL(token);
                    HttpURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
                    Log.e("Loading", "Initialize Connection"+urlConnection);
                    if (urlConnection.getResponseCode() == 200) {

                        Log.e("Loading", "Loading File");
                        inputStream = new BufferedInputStream(urlConnection.getInputStream());
                        System.out.println("LoadPf "+"0");
                    } else {
                        Log.e("Loading", "Loading File");
                    }

                } catch (IOException e) {


                    Log.e("Loading", "Loading Error"+e.getLocalizedMessage());
                    e.printStackTrace();

                    loadFile();
                    return null;
                }
                return inputStream;
            }
        }

    @Override
    public void onBackPressed() {
        redirect();
    }
    public void redirect() {
        Intent pr = new Intent(PaymentReceiptView.this, PaymentReceipt.class);
        pr.putExtra("companyId",companyId);
        pr.putExtra("companyName", companyName);
        pr.putExtra("permission",permission);
        startActivity(pr);
    }

}
