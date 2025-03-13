package perfect.book.keeping.global;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.Toast;

import perfect.book.keeping.R;
import perfect.book.keeping.activity.LoginScreen;

public class ClipboardUtil {
    public static void copyToClipboard(Context context, String text) {
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        if (clipboard != null) {
            String s = context.getResources().getString(R.string.copied_text);
            String w = context.getResources().getString(R.string.clipboard);
            ClipData clipData = ClipData.newPlainText(s, text);
            clipboard.setPrimaryClip(clipData);
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
            builder.setMessage(w);
            builder.setPositiveButton(context.getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    // Do nothing, but close the dialog
                }
            });
            android.app.AlertDialog alert = builder.create();
            alert.show();
        } else {
            String url_clipboard_unavailable = context.getResources().getString(R.string.url_clipboard_unavailable);
            Toast.makeText(context, ""+url_clipboard_unavailable, Toast.LENGTH_SHORT).show();
        }
//        ClipData clip = ClipData.newPlainText("Copied Text", text);
//        clipboard.setPrimaryClip(clip);
    }
}
