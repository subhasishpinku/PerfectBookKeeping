package perfect.book.keeping.global;

import android.content.Context;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import perfect.book.keeping.R;

public class CustomClickableSpan extends ClickableSpan {

    private Context context;
    private String link;

    public CustomClickableSpan(Context context, String link) {
        this.context = context;
        this.link = link;
    }

    @Override
    public void onClick(@NonNull View widget) {
        ClipboardUtil.copyToClipboard(context, link);
    }

    @Override
    public void updateDrawState(TextPaint ds) {
        super.updateDrawState(ds);
        ds.setUnderlineText(false); // Remove underline
        ds.setColor(ContextCompat.getColor(context, R.color.white)); // Change link color
    }
}
