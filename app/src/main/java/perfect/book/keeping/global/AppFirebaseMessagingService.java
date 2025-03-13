package perfect.book.keeping.global;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.graphics.drawable.IconCompat;

import com.bumptech.glide.Glide;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import perfect.book.keeping.R;
import perfect.book.keeping.activity.CropImage;
import perfect.book.keeping.activity.SplashScreen;
import perfect.book.keeping.activity.company.TinderSliderActivity;
import perfect.book.keeping.activity.company.gallery.ReceiptGallery;
import perfect.book.keeping.manager.Manager;
import perfect.book.keeping.model.GlobalSharedPreferences;

public class AppFirebaseMessagingService extends FirebaseMessagingService {
    int fileId,companyId,approval_status;
    int companyPermission = 0;
    String comIdsWishImageLink,url,sound;
    private MediaPlayer mediaPlayer;



    public void onCreate() {
        Log.d("TAG"+"DDDDDDDDD", "onCreate() Firebase Service");
        super.onCreate();
        mediaPlayer = MediaPlayer.create(this, R.raw.notification);
        mediaPlayer.start();
    }

    @Override
    public void handleIntent(Intent intent) {
        super.handleIntent(intent);

        String action =  intent.getAction();
        Log.d("TAG"+"DDDDDDDDD1", action);
        Bundle data = intent.getExtras();
        RemoteMessage remoteMessage = new RemoteMessage(data);
        if (remoteMessage.getData().isEmpty()) {
        }else {
            try {
                JSONObject jsonObject = new JSONObject(remoteMessage.getData());
                System.out.println("Notification_Data"+" "+"jsonObject"+jsonObject);
                fileId = jsonObject.getInt("file_id");
                companyId = jsonObject.getInt("company_id");
                url = jsonObject.getString("url");
                approval_status = jsonObject.getInt("approval_status");
               // sound = jsonObject.getString("sound");
                System.out.println("Notification_Data"+" "+"0"+companyId+" "+url+" "+approval_status+" "+sound);
                Manager compDb = new Manager(getApplicationContext());
                Cursor cursor = compDb.fetchCompanyImage(companyId);
                while (cursor.moveToNext()) {
                    comIdsWishImageLink = cursor.getString(2);
                    companyPermission = cursor.getInt(3);
                    System.out.println("Notification_Data"+" "+"2"+comIdsWishImageLink+"  "+companyId);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
            System.out.println("ClickID"+" Notification "+fileId);
            mediaPlayer.stop();
            SharedPrefManager.getInstance(getApplicationContext()).clearData();
            GlobalSharedPreferences globalSharedPreferences = new GlobalSharedPreferences(
                    companyId,companyPermission,fileId,"1",approval_status,"");
            SharedPrefManager.getInstance(getApplicationContext()).globalData(globalSharedPreferences);


        }

    }
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d("TAG", "From: " + remoteMessage.getFrom());

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d("strLogName", "Message Notification Body: " + remoteMessage.getNotification().getBody());
            Log.d("strLogName", "Message Notification Body Data: " + remoteMessage.getData());
            try {
                JSONObject jsonObject = new JSONObject(remoteMessage.getData());
                System.out.println("Notification_Data"+" "+"jsonObject"+jsonObject);
                fileId = jsonObject.getInt("file_id");
                companyId = jsonObject.getInt("company_id");
                url = jsonObject.getString("url");
                approval_status = jsonObject.getInt("approval_status");
               // sound = jsonObject.getString("sound");
                System.out.println("Notification_Data_For_openApp"+" "+"approval_status"+"  "+fileId+"  "+companyId+" "+url+" "+approval_status+" "+sound);
                // Now you can use fileId and companyId as needed
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        sendNotification(remoteMessage.getFrom(), remoteMessage.getNotification().getBody(), remoteMessage.getNotification().getTitle(), companyId,url,approval_status,fileId);
    }

    private void sendNotification(String from, String messageBody, String title,int companyId,String url,int approval_status,int fileId) {
        System.out.println("Notification_Data"+" "+"1"+companyId);
        Manager compDb = new Manager(getApplicationContext());
        Cursor cursor = compDb.fetchCompanyImage(companyId);
        while (cursor.moveToNext()) {
            comIdsWishImageLink = cursor.getString(2);
            companyPermission = cursor.getInt(3);
            System.out.println("Notification_Data"+" "+"2"+comIdsWishImageLink+"  "+companyId);
        }
        Intent gallerySync = new Intent(this, GalleryDataTransferService.class);
        gallerySync.putExtra("companyId",companyId);
        gallerySync.putExtra("approval_status",approval_status);
        startService(gallerySync);
        Intent intent = new Intent(this, TinderSliderActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("companyId", companyId);
        intent.putExtra("permission", companyPermission);
        intent.putExtra("clickId", fileId);
        intent.putExtra("file_status","("+approval_status+")" );
        intent.putExtra("showStatus", 0);
        intent.putExtra("imageUrl", "");
        Log.e("COMPANY ID",""+companyId);
        Log.e("COMPANY permission",""+companyPermission);
        Log.e("COMPANY fileId",""+fileId);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
        String channelId = "My channel ID";
        Uri sound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + this.getPackageName() + "/" + R.raw.notification);  //Here is FILE_NAME is the name of file that you want to play
        System.out.println("Sound"+sound);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        AudioAttributes attributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .build();
        NotificationChannel mChannels = new NotificationChannel(channelId,
                this.getString(R.string.app_name),
                NotificationManager.IMPORTANCE_HIGH);
        String filePath = comIdsWishImageLink;
        Bitmap iconBitmap = BitmapFactory.decodeFile(filePath);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.fire_base_icon)
                        .setContentTitle(title)
                        .setContentText(messageBody)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setSound(sound)
                        .setContentIntent(pendingIntent)
                        .setFullScreenIntent(pendingIntent, true)
                        .setAutoCancel(true);
                        mChannels.enableLights(true);
                        mChannels.enableVibration(true);
                        mChannels.setSound(sound, attributes);
        if (iconBitmap != null) {
            notificationBuilder.setLargeIcon(iconBitmap); // Set the large icon to the loaded image
        }
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_LOW;
            NotificationChannel mChannel = new NotificationChannel(channelId, "Perfect Book Keeping", importance);
            mChannel.setDescription(messageBody);
            mChannel.enableLights(true);
            mChannel.setLightColor(Color.RED);
            mChannel.enableVibration(true);
            mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});

            notificationManager.createNotificationChannel(mChannel);
//            NotificationChannel channel = new NotificationChannel(channelId,
//                    "Channel human readable title",
//                    NotificationManager.IMPORTANCE_DEFAULT);
//            notificationManager.createNotificationChannel(channel);
        }
        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }
}
