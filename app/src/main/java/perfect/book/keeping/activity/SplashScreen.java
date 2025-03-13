package perfect.book.keeping.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import com.aseem.versatileprogressbar.ProgBar;

import java.util.Locale;

import perfect.book.keeping.R;
import perfect.book.keeping.activity.company.TinderSliderActivity;
import perfect.book.keeping.api.ApiClient;
import perfect.book.keeping.global.GalleryDataTransferService;
import perfect.book.keeping.global.Global;
import perfect.book.keeping.global.RefreshToken;
import perfect.book.keeping.global.SharedPrefManager;
import perfect.book.keeping.manager.Manager;
import perfect.book.keeping.model.GlobalSharedPreferences;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SplashScreen extends AppCompatActivity {

    Global global = new Global();

    RefreshToken refreshToken;

    SharedPreferences shared;

    Manager db;

    ProgBar myProgressBar;

    private static final int PERMISSION_REQUEST_CODE = 1203;
    int companyId = 0,permission = 0,fileId=0,clickId=0,showStatus=0,fileStatus=0;
    String imageUrl;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String languageToLoad  = "en"; // your language
        Locale locale = new Locale(languageToLoad);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
        setContentView(R.layout.activity_splash);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        android.graphics.drawable.Drawable background = SplashScreen.this.getDrawable(R.drawable.gradiant_btn_alt);
        getWindow().setBackgroundDrawable(background);

        Intent intent = getIntent();
        companyId = intent.getIntExtra("companyId",companyId);
        permission = intent.getIntExtra("permission", permission);
        fileId = intent.getIntExtra("clickId", fileId);
        fileStatus = intent.getIntExtra("file_status",fileStatus);
        imageUrl = intent.getStringExtra("imageUrl");
        System.out.println("ClickID"+" SplashScreen "+fileId+" "+fileStatus+" "+imageUrl);

        refreshToken = new RefreshToken();
        GlobalSharedPreferences globalSharedPreferences = SharedPrefManager.getInstance(getApplicationContext()).getGlobalSharedPreferences();
        if (globalSharedPreferences.getFlag() != null) {
            companyId = globalSharedPreferences.getCompanyId();
            permission = globalSharedPreferences.getPermission();
            fileId = globalSharedPreferences.getFileId();
            fileStatus = globalSharedPreferences.getFile_status();
            imageUrl =  globalSharedPreferences.getUrl();
            //System.out.println("ClickID"+companyId+" "+permission+" "+fileId);
            System.out.println("ClickID"+" SplashScreens "+fileId+" "+fileStatus+" "+imageUrl);
            Intent gallerySync = new Intent(this, GalleryDataTransferService.class);
            gallerySync.putExtra("companyId",companyId);
            gallerySync.putExtra("approval_status",fileStatus);
            startService(gallerySync);
        } else {

        }

        myProgressBar = findViewById(R.id.myProgressBar);
        myProgressBar.setTextMsg("");

        myProgressBar.setVisibility(View.VISIBLE);
        db = new Manager(SplashScreen.this);
        SQLiteDatabase dbs = db.getWritableDatabase();
        shared = getSharedPreferences("book_keeping", MODE_PRIVATE);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                if(shared.getString("access_token", "").equals("")) {
                    Intent login = new Intent(SplashScreen.this, LoginScreen.class);
                    startActivity(login);
                    finish();
                } else {
                    //if(global.isNetworkAvailable(SplashScreen.this) == false) {
                        //check for Redirection
                        if(fileId != 0 && companyId != 0) {
                            if (permission == 3 || permission == 4 || permission == 5) {
                                Intent viewReceipt = new Intent(SplashScreen.this, TinderSliderActivity.class);
                                viewReceipt.putExtra("companyId", companyId);
                                viewReceipt.putExtra("permission", permission);
                                viewReceipt.putExtra("clickId", fileId);
                                viewReceipt.putExtra("showStatus", showStatus);
                                viewReceipt.putExtra("file_status", "("+fileStatus+")");
                                viewReceipt.putExtra("imageUrl",imageUrl);
                                startActivity(viewReceipt);
                            }
                        } else {
                                Intent intent = new Intent(SplashScreen.this, Companies.class);
                                intent.putExtra("openGallery", "false");
                                intent.putExtra("openDashBoard", "false");
                                intent.putExtra("companyId", 0);
                                intent.putExtra("companyName", "");
                                startActivity(intent);
                        }
//                    } else {
                        String token = refreshToken.performAsyncTask(shared.getString("refresh_token", ""),"Splash");
//                        checkValidToken(shared.getString("access_token", ""));
//                    }
                }

            }
        }, 1000);
    }
}