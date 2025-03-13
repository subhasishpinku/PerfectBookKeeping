package perfect.book.keeping.global;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import java.security.Provider;
import java.util.List;
import java.util.Map;

import perfect.book.keeping.manager.Manager;

public class GalleryService extends Service {
    private static final String TAG = "GalleryTransferService";
    String auth;
    SharedPreferences shared;
    Manager gallery;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "Service started");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        NetworkStateReceiver.register(this);
        gallery = new Manager(getApplicationContext());
        if (NetworkUtil.isConnectedToNetwork(this)) {
            shared = getSharedPreferences("book_keeping", MODE_PRIVATE);
            auth = shared.getString("access_token", "");
            //loadGallery("Bearer " + auth, "", "", "", "", "", companyId,"","","");
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "Service destroyed");
        NetworkStateReceiver.unregister(this);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
