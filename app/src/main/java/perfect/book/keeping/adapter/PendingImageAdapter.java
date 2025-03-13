package perfect.book.keeping.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import perfect.book.keeping.R;
import perfect.book.keeping.model.PendingImageModel;

public class PendingImageAdapter extends RecyclerView.Adapter<PendingImageAdapter.ViewHolder> {

    List<PendingImageModel> pendingImageModelList;
    Context context;

    public PendingImageAdapter(List<PendingImageModel> pendingImageModelList, Context context) {
        this.pendingImageModelList = pendingImageModelList;
        this.context = context;
    }

    @NonNull
    @Override
    public PendingImageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.pending_image_grid, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull PendingImageAdapter.ViewHolder holder, int position) {
        Log.e("PENDING IMAGE",""+pendingImageModelList.get(position).getSnap_image());
        if(pendingImageModelList.get(position).getSnap_image() != null) {
            Bitmap img = decodeImage(pendingImageModelList.get(position).getSnap_image());
            holder.image.setImageBitmap(img);
        }
    }

    @Override
    public int getItemCount() {
        return pendingImageModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
        }
    }

    private Bitmap decodeImage(String snap_image) {
        final String pureBase64Encoded = snap_image.substring(snap_image.indexOf(",")  + 1);
        final byte[] decodedBytes = Base64.decode(pureBase64Encoded, Base64.DEFAULT);
        Bitmap decodedImage = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
        Log.e("Pending Image Byte", ""+decodedBytes);
        return decodedImage;
    }
}
