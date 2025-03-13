package perfect.book.keeping.global;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.Provider;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import perfect.book.keeping.R;
import perfect.book.keeping.activity.BookKeepers;
import perfect.book.keeping.activity.LoginScreen;
import perfect.book.keeping.api.ApiClient;
import perfect.book.keeping.manager.Manager;
import perfect.book.keeping.response.SnapResponse;
import perfect.book.keeping.response.ValidTokenResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UploadReceipt extends Service {

    String auth;
    SharedPreferences shared;
    Manager galDb;
    String imgString = "";
    private List<Integer> imageIds = new ArrayList<>();
    DownloadGalleryTask galleryTask;
    Global global;
    String compIds = "";
    public static boolean isServiceRunning;
    private Timer runTimer = null;
    long interval = 10000;
    private Handler uploadHandler = new Handler();


    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("RECEIPT LOG", "RECEIPT Service started");
        isServiceRunning = true;
        runTimer = new Timer();
        runTimer.schedule(new TimerTaskToUpload(), 1 , interval);

        Log.d("RECEIPT LOG", "RECEIPT Service started"+isServiceRunning);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        galDb = new Manager(getApplicationContext());
        global = new Global();
        shared = getSharedPreferences("book_keeping", MODE_PRIVATE);
        auth = shared.getString("access_token", "");


        isServiceRunning = true;
        final String CHANNEL_ID = "Foreground Service";
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_ID, NotificationManager.IMPORTANCE_LOW);
        getSystemService(NotificationManager.class).createNotificationChannel(channel);
        Notification.Builder notification = new Notification.Builder(this, CHANNEL_ID)
                                            .setContentText(getResources().getString(R.string.pbk_running))
                                            .setSmallIcon(R.drawable.fire_base_icon);
        startForeground(1001, notification.build());

        //return super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    private class TimerTaskToUpload extends TimerTask {

        @Override
        public void run() {
            uploadHandler.post(new Runnable() {
                @Override
                public void run() {
                    getNetworkInfo();
                }
            });
        }
    }

    private void getNetworkInfo() {
        Log.e("RECEIPT LOG SERVICE RUNNING","FOREGROUND SERVICE RUNNING FOR UPLOAD RECEIPT");
        Cursor companyIds = galDb.fetchCompanyId();
        while (companyIds.moveToNext()) {
            //Log.e("COMPANY IDS", "(" + companyIds.getString(0) + ")");
            compIds = "(" + companyIds.getString(0) + ")";
        }
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        NetworkCapabilities nc = connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork());
        Cursor galleryPending = galDb.getGalleryPendingResCom(compIds);
        if(galleryPending.getCount() > 0) {

            if (networkInfo == null) {
                Log.e("Network Mode", "Not Allowed");
            } else if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                sync(nc.getLinkDownstreamBandwidthKbps(), nc.getLinkUpstreamBandwidthKbps());
            } else if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                sync(nc.getLinkDownstreamBandwidthKbps(), nc.getLinkUpstreamBandwidthKbps());
            } else {
                Log.e("Network Mode", "Not Allowed");
            }
        }
    }

    private void sync(int downloadSpeed, int uploadSpeed) {
        Log.e("Speed Upload", "" + uploadSpeed);
        Log.e("Speed Download", "" + downloadSpeed);

        if (uploadSpeed >= 10000) {
            uploadReceipt(auth, getApplicationContext(), compIds);
        } else {
            Toast.makeText(this, getResources().getString(R.string.file_upload_msg_for_speed), Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadReceipt(String auth, Context applicationContext, String compIds) {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);
        String currentTime = hour + ":" + minute + ":" + second;
        Manager galDb = new Manager(this);
        Cursor galleryPending = galDb.getGalleryPendingResCom(compIds);
        if (galleryPending.getCount() > 0) {
            while (galleryPending.moveToNext()) {
                if(compIds.contains(galleryPending.getString(10))) {
                    try {
                        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
                        Date date1 = dateFormat.parse(currentTime);
                        Date date2 = dateFormat.parse(galleryPending.getString(21));
                        if (date1.after(date2)) {
                            Log.e("TIME COMPARE",""+date1 + " is after " + date2);
                            JSONObject fileObject = new JSONObject();
                            fileObject.put("file_name", galleryPending.getString(2));
                            fileObject.put("blobdata", galleryPending.getString(3));
                            fileObject.put("mimetype", galleryPending.getString(4));
                            fileObject.put("path", galleryPending.getString(5));
                            fileObject.put("filedate", galleryPending.getString(16));
                            fileObject.put("title", galleryPending.getString(9));
                            fileObject.put("company_id", galleryPending.getString(10));
                            fileObject.put("amount", galleryPending.getString(8));
                            fileObject.put("payment_flag", galleryPending.getString(11));
                            JSONArray filesArray = new JSONArray();
                            filesArray.put(fileObject);
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("files", filesArray);
                            Log.e("FILE TAG", "" + jsonObject);
                            uploadSnaps(jsonObject, galleryPending.getInt(0), applicationContext);

                        } else {
                            Log.e("TIME COMPARE",""+date1 + " is before " + date2);
                        }
                    } catch (JSONException e) {
                      throw new RuntimeException(e);
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    Log.e("GET FILES","NOT ALLOW TO UPLOAD");
                }

            }


        }
    }

    private void uploadSnaps(JSONObject jsonObject, int aId, Context applicationContext) {
        Calendar calendar = Calendar.getInstance();
        // Add 5 minutes to the current time
        calendar.add(Calendar.MINUTE, 5);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);
        String currentTime = hour + ":" + minute + ":" + second;
        galDb.updateTime(currentTime, aId);
        JsonParser jsonParser = new JsonParser();
        Call<SnapResponse> snapResponseCall = ApiClient.getInstance().getBookKeepingApi().uploadSnap("Bearer "+auth, jsonParser.parse(jsonObject.toString()));
        snapResponseCall.enqueue(new Callback<SnapResponse>() {
            @Override
            public void onResponse(Call<SnapResponse> call, Response<SnapResponse> response) {
                Log.e("Response Code",""+response.code());
                if(response.code() == 200) {
                    Cursor check = galDb.checkSettings("receipt_refresh");
                    if(check.getCount() > 0) {
                        galDb.updateSettings("receipt_refresh", "1");
                    } else {
                        galDb.addSettings("receipt_refresh", "1");
                    }

                    String updateResponse = galDb.updateGallery(response.body().getData().getId(),
                            response.body().getData().getCreated_by(),
                            response.body().getData().getCreated_by_name(),
                            response.body().getData().getCreated_at(),
                            imgString,
                            "",
                            response.body().getData().getLink(),
                            aId,
                            response.body().getData().getThumbnail(),
                            response.body().getData().getOriginal(),
                            response.body().getData().getAmount());
                    DownloadGalleryTask galleryTask = (DownloadGalleryTask) new DownloadGalleryTask(applicationContext).execute(
                            response.body().getData().getThumbnail(),
                            response.body().getData().getFile_name(),
                            String.valueOf(response.body().getData().getId()));

                    Log.e("DATA UPDATE RESPONSE",""+updateResponse);
                } else if(response.code() == 401) {
                    String token = global.reCallList(shared.getString("refresh_token", ""));
                    if(token.equals("Refresh Token Expires")) {
                        clearShare();
                        Intent redirect = new Intent(getApplicationContext(), LoginScreen.class);
                        redirect.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(redirect);
                    } else {
                        String vToken = global.validToken(token);
                        if(vToken.equals("Authentication token missing") || vToken.equals("Wrong authentication token")) {
                            clearShare();
                            Intent redirect = new Intent(getApplicationContext(), LoginScreen.class);
                            redirect.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(redirect);
                        } else {
                            auth = vToken;
                            updateShare(vToken);
                            uploadReceipt(auth, applicationContext, compIds);
                        }
                    }
                } else {
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        Toast.makeText(applicationContext, ""+jObjError.getString("message"), Toast.LENGTH_SHORT).show();
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            @Override
            public void onFailure(Call<SnapResponse> call, Throwable t) {
                Log.e("RESPONSE",""+t.getMessage());
            }
        });
    }

    public void clearShare() {
        SharedPreferences loginRef = getSharedPreferences("book_keeping", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = loginRef.edit();
        editor.putString("access_token","");
        editor.putString("fcm_token","");
        editor.putBoolean("first_input",true);
        editor.putInt("userId", 0);
        editor.commit();
        Manager db = new Manager(getApplicationContext());
        db.removeAllCompany();
    }

    public void updateShare(String token) {
        SharedPreferences sharedPreferences = getSharedPreferences("book_keeping", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("access_token", token);
        editor.commit();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        Log.e("RECEIPT LOG SERVICE RUNNING DESTROY", "YES");
        isServiceRunning = false;
        stopForeground(true);
        Intent broadCastIntent = new Intent(this, UploadReceiver.class);
        sendBroadcast(broadCastIntent);
        super.onDestroy();

    }

}
