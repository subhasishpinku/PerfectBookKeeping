package perfect.book.keeping.global;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import perfect.book.keeping.activity.SplashScreen;
import perfect.book.keeping.activity.company.TinderSliderActivity;
import perfect.book.keeping.model.GlobalSharedPreferences;

public class SharedPrefManager {
    public static final String SHARED_PREF_NAME   = "PERFECTBOOKKEEPING";
    public static final String COMPANY_ID   = "COMPANY_ID";
    public static final String PERMISSION   = "PERMISSION";
    public static final String FILE_ID   = "FILE_ID";
    public static final String FLAG   = "FLAG";
    public static final String FILESTATUS   = "file_status";
    public static final String URL   = "url";
    private static SharedPrefManager mInstance;
    private static Context mCtx;

    private SharedPrefManager(Context context) {
        mCtx = context;
    }
    int showStatus=0;
    public static synchronized SharedPrefManager getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new SharedPrefManager(context);
        }
        return mInstance;
    }
    public void globalData(GlobalSharedPreferences globalSharedPreferences) {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(COMPANY_ID, globalSharedPreferences.getCompanyId());
        editor.putInt(PERMISSION, globalSharedPreferences.getPermission());
        editor.putInt(FILE_ID, globalSharedPreferences.getFileId());
        editor.putString(FLAG,globalSharedPreferences.getFlag());
        editor.putInt(FILESTATUS,globalSharedPreferences.getFile_status());
        editor.putString(URL,globalSharedPreferences.getUrl());
        editor.apply();
    }
    public GlobalSharedPreferences getGlobalSharedPreferences() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return new GlobalSharedPreferences(
                sharedPreferences.getInt(COMPANY_ID, 0),
                sharedPreferences.getInt(PERMISSION, 0),
                sharedPreferences.getInt(FILE_ID, 0),
                sharedPreferences.getString(FLAG, null),
                sharedPreferences.getInt(FILESTATUS, 0),
                sharedPreferences.getString(URL, null)
        );
    }
    public void clearData() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.commit();
        editor.apply();
    }
    public void sendData(){
        System.out.println("SHARE"+"0");
        GlobalSharedPreferences globalSharedPreferences = SharedPrefManager.getInstance(mCtx).getGlobalSharedPreferences();
        if (globalSharedPreferences.getFlag() != null) {
            System.out.println("SHARE"+"1");
            Intent intent = new Intent(mCtx, TinderSliderActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("companyId", globalSharedPreferences.getCompanyId());
            intent.putExtra("permission", globalSharedPreferences.getPermission());
            intent.putExtra("clickId", globalSharedPreferences.getFileId());
            intent.putExtra("showStatus", showStatus);
            intent.putExtra("file_status", "("+ globalSharedPreferences.getFile_status()+")");
            intent.putExtra("imageUrl",globalSharedPreferences.getUrl());
            mCtx.startActivity(intent);
        } else {
            System.out.println("SHARE"+"2");
        }

    }
}
