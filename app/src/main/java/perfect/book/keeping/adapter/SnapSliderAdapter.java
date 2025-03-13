package perfect.book.keeping.adapter;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import perfect.book.keeping.R;
import perfect.book.keeping.activity.CropImage;
import perfect.book.keeping.model.SnapImages;

public class SnapSliderAdapter extends RecyclerView.Adapter<SnapSliderAdapter.ViewHolder> {

    List<SnapImages> snapImages;
    Context context;
    private Runnable showImageRunnable;

    private Handler handler;

    private int selectedItem  = RecyclerView.NO_POSITION;

    CropImage cropImage;

    public SnapSliderAdapter(Context context, List<SnapImages> snapImages, CropImage cropImage) {
        this.snapImages = snapImages;
        this.context = context;
        this.cropImage = cropImage;

    }

    @NonNull
    @Override
    public SnapSliderAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_grid, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull SnapSliderAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        if(snapImages.get(position).getSnap_image() != null) {
            Bitmap img = decodeImage(snapImages.get(position).getSnap_image());
            holder.image.setImageBitmap(img);
            if (position == selectedItem) {
                holder.remove.setVisibility(View.VISIBLE);

            } else {
                holder.remove.setVisibility(View.GONE);
               // holder.itemView.setBackgroundColor(Color.TRANSPARENT);
            }

            if(holder.remove.getVisibility() == View.VISIBLE) {
                holder.remove.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        cropImage.removeFromList(position, snapImages.get(position).getId());
                        notifyDataSetChanged();
                    }
                });
            }

            holder.image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (position == selectedItem) {
                        holder.itemView.setBackground(ContextCompat.getDrawable(context, R.drawable.small_image_border));
                    }
                    handler = new Handler();
                    showImageRunnable = new Runnable() {
                        @Override
                        public void run() {
                            int previousSelectedItem = selectedItem;
                            selectedItem = holder.getAdapterPosition();
                            if (previousSelectedItem != RecyclerView.NO_POSITION) {
                                notifyItemChanged(previousSelectedItem);
                            }
                            notifyItemChanged(selectedItem);
                        }
                    };
                    handler.postDelayed(showImageRunnable, 500);

                    cropImage.changeImage(position);
                    if (position == selectedItem) {
                        holder.remove.setVisibility(View.VISIBLE);
                       // holder.itemView.setBackground(ContextCompat.getDrawable(context, R.drawable.small_image_border));
                    } else {
                        holder.remove.setVisibility(View.GONE);
                      //  holder.itemView.setBackgroundColor(Color.TRANSPARENT);
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return snapImages.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image, remove;
        RelativeLayout imageSelect;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            remove = itemView.findViewById(R.id.remove);
            image = itemView.findViewById(R.id.image);
            imageSelect = itemView.findViewById(R.id.imageSelect);
        }
    }

    private Bitmap decodeImage(String snap_image) {
        final String pureBase64Encoded = snap_image.substring(snap_image.indexOf(",")  + 1);
        final byte[] decodedBytes = Base64.decode(pureBase64Encoded, Base64.DEFAULT);
        Bitmap decodedImage = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
        Log.e("Image Byte", ""+decodedBytes);
        return decodedImage;
    }

}
