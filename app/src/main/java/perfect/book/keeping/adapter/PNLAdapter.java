package perfect.book.keeping.adapter;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;

import perfect.book.keeping.R;
import perfect.book.keeping.api.ApiClient;
import perfect.book.keeping.global.FileDownloadUtil;
import perfect.book.keeping.model.PNL;

public class PNLAdapter extends RecyclerView.Adapter<PNLAdapter.ViewHolder> {

    List<PNL> pnl;
    Context context;
    FileDownloadUtil fileDownloadUtil;

    public PNLAdapter(List<PNL> pnl, Context context) {
        this.pnl = pnl;
        this.context = context;
        this.fileDownloadUtil = new FileDownloadUtil(context);
    }

    @NonNull
    @Override
    public PNLAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.pnl_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull PNLAdapter.ViewHolder holder, int position) {
        holder.pnl_date.setText(pnl.get(position).getYear_month());
        String url =pnl.get(position).getFile_url();
        String file_name = pnl.get(position).getFile_path().substring(pnl.get(position).getFile_path().lastIndexOf('/') + 1);
        holder.receipt_bg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fileDownloadUtil.downloadFile(url, file_name);
            }
        });
    }
    @Override
    public int getItemCount() {
        return pnl.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView pnl_date;
        RelativeLayout receipt_bg;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            pnl_date = (itemView).findViewById(R.id.pnl_date);
            receipt_bg = (itemView).findViewById(R.id.receipt_bg);
        }
    }
}
