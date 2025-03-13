package perfect.book.keeping.global;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class UploadWorker extends Worker {
    private final Context context;

    public UploadWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.context = context;
    }

    @NonNull
    @Override
    public Result doWork() {
        Log.e("SERVICE STATUS",""+UploadReceipt.isServiceRunning);
        if(!UploadReceipt.isServiceRunning) {
            Intent serviceIntent = new Intent(this.context, UploadReceipt.class);
            ContextCompat.startForegroundService(context, serviceIntent);
        }
        return Result.success();
    }

    @Override
    public void onStopped() {
        super.onStopped();
    }
}
