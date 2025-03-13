package perfect.book.keeping.adapter;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.EditorInfo;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import perfect.book.keeping.R;
import perfect.book.keeping.activity.CropImage;
import perfect.book.keeping.activity.OpenCamera;
import perfect.book.keeping.manager.Manager;
import perfect.book.keeping.model.SnapImages;

public class SnapImageAdapter extends RecyclerView.Adapter<SnapImageAdapter.ViewHolder> {


    List<SnapImages> snapImages;
    Context context;
    int MAX_DECIMAL_DIGITS = 2;

    String file_date = "";
    int companyPermission;

    public SnapImageAdapter(Context context, List<SnapImages> snapImages, int companyPermission) {
        this.snapImages = snapImages;
        this.context = context;
        this.companyPermission = companyPermission;
    }

    @NonNull
    @Override
    public SnapImageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View snapImage = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_slider_layout, parent, false);
        return new ViewHolder(snapImage);
    }

    @Override
    public void onBindViewHolder(@NonNull SnapImageAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        Log.e("IMAGE POSITION ADAPTER",""+snapImages.get(position).getImage_position());
        Log.e("POS oF IMAGE LIST",""+position);
        Log.e("POS oF IMAGE LIST SIZE",""+snapImages.size());

        try {
            DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date date = inputFormat.parse(snapImages.get(position).getFile_date());
            String modifiedString = snapImages.get(position).getCompany_dateFormat().replace("D", "d");
            DateFormat outputFormat = new SimpleDateFormat(modifiedString, Locale.getDefault());
            Log.e("DATE FORMAT PATTERN",""+modifiedString);
            Log.e("DATE FORMAT DATE",""+outputFormat.format(date));
            file_date = outputFormat.format(date);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        if(companyPermission == 3 || companyPermission == 4) {
            holder.checkView.setVisibility(View.VISIBLE);
        } else {
            holder.checkView.setVisibility(View.GONE);
        }
        if(snapImages.get(position).getSnap_image() != null) {
            Bitmap img = decodeImage(snapImages.get(position).getSnap_image());
            Matrix matrix = new Matrix();
            matrix.postRotate(90);
            holder.image_element.setImageBitmap(Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), matrix, true));
            holder.image_element.setImageBitmap(img);
        }
        holder.file_date.setText(" "+file_date);
        holder.imgPos.setText(String.valueOf(snapImages.get(position).getId()));
        holder.title.setText(snapImages.get(position).getTitle());
        holder.currency_mode.setText(snapImages.get(position).getCompany_currency());
        Log.e("AMOUNT",""+snapImages.get(position).getAmount());
        if(snapImages.get(position).getAmount() != 0) {
            holder.amount.setText(String.valueOf(snapImages.get(position).getAmount()));
        }

        if(snapImages.get(position).getSelfPaid() == 1) {
            holder.check_slfPaid.setChecked(true);
        }

        if(position == 0) {
            holder.previous.setVisibility(View.GONE);
        } else {
            holder.previous.setVisibility(View.VISIBLE);
        }

        if(position == (snapImages.size() - 1)) {
            holder.next.setVisibility(View.GONE);
        } else {
            holder.next.setVisibility(View.VISIBLE);
        }

        holder.next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((CropImage)context).nextScrollTo(holder.getAdapterPosition());
            }
        });

        holder.previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((CropImage)context).previousScrollTo(holder.getAdapterPosition());
            }
        });

        holder.remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((CropImage)context).removeFromList(position, snapImages.get(position).getId());
                notifyDataSetChanged();
            }
        });
    }


    @Override
    public int getItemCount() {
        return snapImages.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image_element;
        EditText title, amount;
        TextView showText,imgPos, file_date, reimbursement,currency_mode;
        View tagView;
        CheckBox check_slfPaid;
        LinearLayout previous, next, remove, checkView;
        Manager updateSnap = new Manager(context);
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image_element = itemView.findViewById(R.id.image_element);
            title = (itemView).findViewById(R.id.title);
            amount = (itemView).findViewById(R.id.amount);
            imgPos = (itemView).findViewById(R.id.imgPos);
            check_slfPaid = (itemView).findViewById(R.id.check_slfPaid);
            previous = (itemView).findViewById(R.id.previous);
            next = (itemView).findViewById(R.id.next);
            remove = (itemView).findViewById(R.id.remove);
            file_date = (itemView).findViewById(R.id.file_date);
            reimbursement = (itemView).findViewById(R.id.reimbursement);
            currency_mode = (itemView).findViewById(R.id.currency_mode);
            checkView = (itemView).findViewById(R.id.checkView);

            title.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    String update_title_response = updateSnap.updateTitle(snapImages.get(getAdapterPosition()).getImage_position(), snapImages.get(getAdapterPosition()).getCompany_id(), s.toString());
                    snapImages.get(getAdapterPosition()).setTitle(s.toString());
                    Log.e("TITLE TEXT",""+update_title_response);
                }

            });

            amount.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    String input = s.toString();
                    int pointIndex = input.indexOf(".");
                    if (pointIndex != -1 && input.length() - pointIndex > MAX_DECIMAL_DIGITS + 1) {
                        // Remove Last Element of the EditText when more than two digits are entered after the point
                        amount.getText().delete(input.length() - 1, input.length());
                    }
                    if(!s.toString().equals("")) {
                        updateSnap.updateAmount(snapImages.get(getAdapterPosition()).getImage_position(), snapImages.get(getAdapterPosition()).getCompany_id(), Double.parseDouble(input), 1);
                        snapImages.get(getAdapterPosition()).setAmount(Float.parseFloat(input));
                        snapImages.get(getAdapterPosition()).setAmount_typed(1);
                    } else {
                        updateSnap.updateAmount(snapImages.get(getAdapterPosition()).getImage_position(), snapImages.get(getAdapterPosition()).getCompany_id(), 0.00, 0);
                        snapImages.get(getAdapterPosition()).setAmount_typed(0);
                    }
                }
            });

            check_slfPaid.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked){
                        updateSnap.updateIsPaid(snapImages.get(getAdapterPosition()).getImage_position(), snapImages.get(getAdapterPosition()).getCompany_id(), 1);
                        snapImages.get(getAdapterPosition()).setSelfPaid(1);
                    } else {
                        updateSnap.updateIsPaid(snapImages.get(getAdapterPosition()).getImage_position(), snapImages.get(getAdapterPosition()).getCompany_id(), 0);

                    }
                }
            });

            reimbursement.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(check_slfPaid.isChecked()) {
                        check_slfPaid.setChecked(false);
                        updateSnap.updateIsPaid(snapImages.get(getAdapterPosition()).getImage_position(), snapImages.get(getAdapterPosition()).getCompany_id(), 0);
                    } else {
                        check_slfPaid.setChecked(true);
                        updateSnap.updateIsPaid(snapImages.get(getAdapterPosition()).getImage_position(), snapImages.get(getAdapterPosition()).getCompany_id(), 1);
                    }
                }
            });
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
