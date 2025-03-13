package perfect.book.keeping.activity.company;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.jsibbold.zoomage.ZoomageView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import perfect.book.keeping.R;
import perfect.book.keeping.global.ClipboardUtil;

public class ViewReceiptZoomImage extends AppCompatActivity {
    String keyUrl,keyUrlLink;
    ZoomageView myZoomImageView;
    LinearLayout loader_view;
    TextView imageUrl;
    ImageView CopyImage;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_receipt_zoom_image);
        Intent intent = getIntent();
        keyUrl = intent.getStringExtra("keyUrl");
        keyUrlLink = intent.getStringExtra("keyUrlLink");
        System.out.println("Url"+" "+keyUrl);
        myZoomImageView = (ZoomageView)findViewById(R.id.myZoomImageView);
        loader_view = (LinearLayout)findViewById(R.id.loader_view);
        imageUrl = (TextView)findViewById(R.id.imageUrl);
        CopyImage = (ImageView)findViewById(R.id.CopyImage);
        Glide.with(myZoomImageView)
                .load(keyUrl)
                .into(myZoomImageView);
        loader_view.setVisibility(View.GONE);
        imageUrl.setText( keyUrl.substring(0, Math.min(keyUrl.length(), 35)) + "...");
        CopyImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardUtil.copyToClipboard(getApplicationContext(), keyUrlLink);
            }
        });
    }
}
