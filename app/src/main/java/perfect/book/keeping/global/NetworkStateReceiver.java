package perfect.book.keeping.global;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

public class NetworkStateReceiver extends BroadcastReceiver {
    private static final String TAG = "NetworkStateReceiver";
    private static boolean isRegistered = false;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (NetworkUtil.isConnectedToNetwork(context)) {
            Log.d(TAG, "Network connected");
            // Start the data transfer service when network becomes available
            context.startService(new Intent(context, DataTransferService.class));
            context.startService(new Intent(context, GalleryDataTransferService.class));
        } else {
            Log.d(TAG, "Network disconnected");
        }
    }

    public static void register(Context context) {
        if (!isRegistered) {
            IntentFilter intentFilter = new IntentFilter(android.net.ConnectivityManager.CONNECTIVITY_ACTION);
          //  context.registerReceiver(new NetworkStateReceiver(), intentFilter);
            isRegistered = true;
        }
    }

    public static void unregister(Context context) {
        if (isRegistered) {
            //context.unregisterReceiver(new NetworkStateReceiver());
            isRegistered = false;
        }
    }




}
