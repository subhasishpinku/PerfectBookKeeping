package perfect.book.keeping.global;

import android.app.Activity;
import android.app.Application;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

import java.io.File;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import perfect.book.keeping.activity.LoginScreen;
import perfect.book.keeping.activity.company.gallery.PendingGallery;
import perfect.book.keeping.api.ApiClient;
import perfect.book.keeping.manager.Manager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Global extends Application {

    private String android_id, token;

    Dialog progressDialog;

    public int view;

    public static final String OPEN_COMPANY = "ENABLE";
    Locale myLocale;
    String currentLanguage = "en", currentLang;

    RefreshToken refreshToken;

    ValidToken validToken;

    public Global() {

    }



    public String getDeviceToken(Activity activity) {
        android_id = Settings.Secure.getString(activity.getContentResolver(),
                Settings.Secure.ANDROID_ID);

        return android_id;

    }

    public String getFCMToken() {
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if(task.isSuccessful()) {
                    token = task.getResult();
                }
            }
        });
        return token;
    }

    public String timeZone() {
        TimeZone tz = TimeZone.getDefault();

        return  tz.getID();
    }

    public float[] bwMatrixValue(int scale, String type) {
        float[] greyColorTransform;
        if(type.equals("bw")) {
            greyColorTransform = new float[]{
                    85, 85, 85, 0, scale * 255,
                    85, 85, 85, 0, scale * 255,
                    85, 85, 85, 0, scale * 255,
                    0, 0, 0, 1, 0};
        } else if(type.equals("original")) {
            greyColorTransform = new float[]{
                    255, 0, 0, 0, -128*255,
                    0, 255, 0, 0, -128*255,
                    0, 0, 255, 0, -128*255,
                    0, 0, 0, 1, 0};
        } else {
            greyColorTransform = new float[]{
                    0, 0, 1, 0, 0,
                    1, 0, 0, 0, 0,
                    0, 1, 0, 0, 0,
                    0, 0, 0, 1, 0};
        }
        return greyColorTransform;
    }


    public boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public boolean checkUi(Context context) {
        int nightModeFlags = context.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        boolean isDarkModeEnabled = nightModeFlags == Configuration.UI_MODE_NIGHT_YES;

        return isDarkModeEnabled;

    }


    public int getView() {
        return view;
    }

    public void setView(int view) {
        this.view = view;
    }

    public void deleteAllImagesInDirectory(String directoryPath) {
        File directory = new File(directoryPath);

        if (directory.exists() && directory.isDirectory()) {
            File[] files = directory.listFiles();

            if (files != null) {
                for (File file : files) {
                    if (file.isFile() && isImageFile(file.getName())) {
                        file.delete(); // Delete the image file
                    }
                }
            }
        }
    }

    private boolean isImageFile(String fileName) {
        String[] imageExtensions = {".jpg", ".jpeg", ".png", ".gif", ".bmp"};
        for (String extension : imageExtensions) {
            if (fileName.toLowerCase().endsWith(extension)) {
                return true;
            }
        }
        return false;
    }
    public void setLocale(String localeName, Context context) {
        if (!localeName.equals(currentLanguage)) {
            context = LocaleHelper.setLocale(context, localeName);
            //Resources resources = context.getResources();
            myLocale = new Locale(localeName);
            Resources res = context.getResources();
            DisplayMetrics dm = res.getDisplayMetrics();
            Configuration conf = res.getConfiguration();
            conf.locale = myLocale;
            res.updateConfiguration(conf, dm);
        } else {
            //  Toast.makeText(LoginScreen.this, "Language already selected!", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base));
    }

    public String reCallList(String refresh_token) {
        refreshToken = new RefreshToken();

        return refreshToken.performAsyncTask(refresh_token, "UploadHistory");

    }

    public String validToken(String token) {
        validToken = new ValidToken();

        return validToken.performAsyncTask(token, "UploadHistory");

    }


    private void checkValidToken(String result, String mode) {
        String newToken = result;
        Log.e("CALL CHECK TOKEN",""+result);

    }

    public String getSettings(String name, Context context) {
        Manager db = new Manager(context);
        Cursor cursor = db.checkSettings(name);
        Log.e("CURSOR",""+cursor);
        String value = "";
        while (cursor.moveToNext()) {
            value = cursor.getString(2);
        }
        return value;
    }
}
