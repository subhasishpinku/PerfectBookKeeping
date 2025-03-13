package perfect.book.keeping.global;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

public class ClearAppData {
    public static void clearApplicationData(Context context, String packageName) {
        try {
            PackageManager packageManager = context.getPackageManager();
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(packageName, 0);

            // Clear app data
            Runtime runtime = Runtime.getRuntime();
            runtime.exec("pm clear " + applicationInfo.packageName);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
