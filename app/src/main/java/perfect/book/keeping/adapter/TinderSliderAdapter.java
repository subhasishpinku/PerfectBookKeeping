package perfect.book.keeping.adapter;
import static android.view.MotionEvent.ACTION_DOWN;
import static android.view.MotionEvent.ACTION_UP;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.text.Editable;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Space;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.text.HtmlCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.jsibbold.zoomage.ZoomageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import perfect.book.keeping.R;
import perfect.book.keeping.activity.Companies;
import perfect.book.keeping.activity.LoginScreen;
import perfect.book.keeping.activity.company.BusinessDashboards;
import perfect.book.keeping.activity.company.TinderSliderActivity;
import perfect.book.keeping.activity.company.gallery.PendingGallery;
import perfect.book.keeping.activity.company.gallery.ReceiptGallery;
import perfect.book.keeping.activity.company.gallery.RejectGallery;
import perfect.book.keeping.activity.company.subUser.SubUsersList;
import perfect.book.keeping.api.ApiClient;
import perfect.book.keeping.global.ClipboardUtil;
import perfect.book.keeping.global.CustomClickableSpan;
import perfect.book.keeping.global.FileDownloadUtil;
import perfect.book.keeping.global.Global;
import perfect.book.keeping.global.SharedPrefManager;
import perfect.book.keeping.manager.Manager;
import perfect.book.keeping.model.TinderSliderModel;
import perfect.book.keeping.request.FileUpdateRequest;
import perfect.book.keeping.response.DeleteFile;
import perfect.book.keeping.response.FileUpdateResponse;
import perfect.book.keeping.response.ReSubmitResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TinderSliderAdapter extends RecyclerView.Adapter<TinderSliderAdapter.ViewHolder> {
    private Context mCtx;
    private List<TinderSliderModel> tinderSliderModels;
    int listview;
    int getPos;
    int showStatus,companyId,permission;
    int clickPos = 0;
    String auth;
    SharedPreferences shared;
    AlertDialog alertDialog, editDialog;
    BottomSheetDialog editSheetDialog;
    TextInputEditText reason;
    LinearLayout reject_receipt, resubmit_receipt;
    ImageView close_reason, close_edit;
    Manager galDb;
    EditText title, enter_amount, enter_file_date;
    TextView currency_mode, reimbursement;
    CheckBox check_slfPaid;
    BottomSheetBehavior<View> bottomSheetBehavior;
    int p_flag = 0;
    private ScaleGestureDetector scaleGestureDetector;
    private float mScaleFactor = 1.0f;
    int MAX_DECIMAL_DIGITS = 2;
    String new_file_date;
    Global global;
    String zipFileName = "file.zip", zipData;
    FileDownloadUtil fileDownloadUtil;
    private static final int MAX_LINES_COLLAPSED = 1;
    public TinderSliderAdapter(Context mCtx, List<TinderSliderModel> tinderSliderModels, int showStatus,int companyId,int permission) {
        this.mCtx = mCtx;
        this.tinderSliderModels = tinderSliderModels;
        this.showStatus = showStatus;
        this.companyId=companyId;
        this.permission=permission;
        this.fileDownloadUtil = new FileDownloadUtil(mCtx);
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tinder_silider_adapter_activity, parent, false);
        return new ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(final ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        TinderSliderModel tinderSliderModel = tinderSliderModels.get(position);
        listview = position;
        Log.e("ORG",""+tinderSliderModel.getOriginal());
        String Dates;
        try {
            DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date date = inputFormat.parse(tinderSliderModel.getCreated_at());
            String modifiedString = tinderSliderModels.get(position).getCompany_dateFormat().replace("D", "d");
            DateFormat outputFormat = new SimpleDateFormat(modifiedString, Locale.getDefault());
            Dates = outputFormat.format(date);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        if (tinderSliderModel.getImageUrl().equals("")) {
            Glide.with(mCtx)
                    .load(tinderSliderModel.getOriginalLink())
                    .apply(new RequestOptions()
                            .placeholder(Drawable.createFromPath(String.valueOf(new File(tinderSliderModel.getThumbnail()))))
                            .error(Drawable.createFromPath(String.valueOf(new File(tinderSliderModel.getThumbnail())))))
                    .into(holder.myZoomImageView);

            Glide.with(mCtx)
                    .load(tinderSliderModel.getOriginalLink())
                    .apply(new RequestOptions()
                            .placeholder(Drawable.createFromPath(String.valueOf(new File(tinderSliderModel.getThumbnail()))))
                            .error(Drawable.createFromPath(String.valueOf(new File(tinderSliderModel.getThumbnail())))))
                    .into(holder.myZoomImageView_alt);
        } else{
            Glide.with(mCtx)
                    .load(tinderSliderModel.getImageUrl())
   //                 .apply(new RequestOptions()
//                            .placeholder(Drawable.createFromPath(String.valueOf(new File(tinderSliderModel.getThumbnail()))))
//                            .error(Drawable.createFromPath(String.valueOf(new File(tinderSliderModel.getThumbnail())))))
                    .into(holder.myZoomImageView);

            Glide.with(mCtx)
                    .load(tinderSliderModel.getImageUrl())
                    //                 .apply(new RequestOptions()
//                            .placeholder(Drawable.createFromPath(String.valueOf(new File(tinderSliderModel.getThumbnail()))))
//                            .error(Drawable.createFromPath(String.valueOf(new File(tinderSliderModel.getThumbnail())))))
                    .into(holder.myZoomImageView_alt);
        }


        String memo = tinderSliderModel.getTitle();
        String phase2 = " | " + tinderSliderModel.getAmount() + " "+ tinderSliderModel.getCompany_currency() + " | " + tinderSliderModel.getUploadAdd();
        String finalMemo = memo;

        if(memo.length() > 15) {
            showTruncatedText(holder.title, memo, MAX_LINES_COLLAPSED, phase2);
            holder.read_more.setVisibility(View.VISIBLE);
            holder.read_more.setOnClickListener(new View.OnClickListener() {
                boolean isExpanded = false;
                @Override
                public void onClick(View v) {
                    isExpanded = !isExpanded;
                    if (isExpanded) {
                        showFullText(holder.title, finalMemo, phase2);
                        holder.read_more.setText("Read Less");
                    } else {
                        showTruncatedText(holder.title, finalMemo, MAX_LINES_COLLAPSED, phase2);
                        holder.read_more.setText("Read More");
                    }
                }
            });
        } else {
            holder.read_more.setVisibility(View.GONE);
            memo = tinderSliderModel.getTitle();
            holder.title.setText(
                    memo + " | " + tinderSliderModel.getAmount() + " "+ tinderSliderModel.getCompany_currency() + " | " + tinderSliderModel.getUploadAdd());
        }

//        holder.amount_alt.setText();
//        holder.created_at.setText();

        String linkText = "<a href=\"" + tinderSliderModel.getLink() + "\">"+mCtx.getResources().getString(R.string.copy_url)+"</a>";
        Spannable spannable = (Spannable) HtmlCompat.fromHtml(linkText, HtmlCompat.FROM_HTML_MODE_COMPACT);
        ClickableSpan[] clickableSpans = spannable.getSpans(0, spannable.length(), ClickableSpan.class);
        for (ClickableSpan clickableSpan : clickableSpans) {
            int start = spannable.getSpanStart(clickableSpan);
            int end = spannable.getSpanEnd(clickableSpan);
            spannable.removeSpan(clickableSpan);
            CustomClickableSpan customClickableSpan = new CustomClickableSpan(mCtx, tinderSliderModel.getLink());
            spannable.setSpan(customClickableSpan, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        holder.imageUrl.setText(spannable);
        holder.imageUrl.setMovementMethod(LinkMovementMethod.getInstance());
        holder.upload_by.setText(tinderSliderModel.getCreated_user_name() + " |");
        holder.upload_add.setText("Upload at "+Dates); //
        holder.paymentFlag.setText(mCtx.getResources().getString(R.string.need_reimbursement));

        Log.e("Payment Flag",""+tinderSliderModel.getPayment_flag());

        if(showStatus == 0) {
            holder.alternate.setVisibility(View.GONE);
            holder.main.setVisibility(View.VISIBLE);
            holder.copyImage.setVisibility(View.VISIBLE);
            holder.title.setVisibility(View.VISIBLE);
            holder.upload_by.setVisibility(View.VISIBLE);
            holder.upload_add.setVisibility(View.VISIBLE);
            if(tinderSliderModel.getApproval_status() == 3) {
                holder.reject_reason_layout.setVisibility(View.VISIBLE);
                if(tinderSliderModel.getReject_reason() != null) {
                    holder.reject_reasons.setText(tinderSliderModel.getReject_reason());
                } else {
                    Log.e("IMAGE REJECT REASON",""+tinderSliderModel.getReject_reason());
                    holder.reject_reasons.setText("");
                }
            } else {
                holder.reject_reason_layout.setVisibility(View.GONE);
            }
            if (Integer.valueOf(tinderSliderModel.getPayment_flag()) == 1) {
                holder.paymentFlag.setVisibility(View.VISIBLE);
            } else {
                holder.paymentFlag.setVisibility(View.INVISIBLE);
            }
            holder.tap_icon.setImageResource(R.drawable.hide);
        } else {
            holder.alternate.setVisibility(View.VISIBLE);
            holder.main.setVisibility(View.GONE);
            holder.copyImage.setVisibility(View.INVISIBLE);
            holder.title.setVisibility(View.INVISIBLE);
            holder.upload_by.setVisibility(View.INVISIBLE);
            holder.upload_add.setVisibility(View.INVISIBLE);
            holder.reject_reason_layout.setVisibility(View.GONE);
            if (Integer.valueOf(tinderSliderModel.getPayment_flag()) == 1) {
                holder.paymentFlag.setVisibility(View.VISIBLE);
            } else {
                holder.paymentFlag.setVisibility(View.INVISIBLE);
            }
            holder.tap_icon.setImageResource(R.drawable.show);
        }

        holder.linkImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardUtil.copyToClipboard(mCtx, tinderSliderModel.getLink());
            }
        });

        holder.downloadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shared = mCtx.getSharedPreferences("book_keeping", Context.MODE_PRIVATE);
                auth = shared.getString("access_token", "");
                if(global.isNetworkAvailable(mCtx)) {
                    //loadGallery_zip("Bearer " + auth, String.valueOf(tinderSliderModel.getId()), true, tinderSliderModel.getFile_name());
                    fileDownloadUtil.downloadFile(tinderSliderModel.getOriginalLink(), tinderSliderModel.getFile_name()+".jpg");
                    Log.e("FILE",""+tinderSliderModel.getFile_name());
                } else {
                    Toast.makeText(mCtx, mCtx.getResources().getString(R.string.please_Check_Your_Internet_Connection), Toast.LENGTH_SHORT).show();
                }
            }
        });
        //Reject Icon
        if(permission == 3 || permission == 4 || permission == 7) {
            if(tinderSliderModel.getApproval_status() == 1 || tinderSliderModel.getApproval_status() == 2 || tinderSliderModel.getApproval_status() == 0) {
                holder.reject.setVisibility(View.VISIBLE);
                holder.approve.setVisibility(View.GONE);
                holder.superApprove.setVisibility(View.GONE);
                holder.space_after_reject.setVisibility(View.GONE);
                holder.space_after_super_approve.setVisibility(View.GONE);
            } else {
                holder.reject.setVisibility(View.GONE);
                holder.approve.setVisibility(View.GONE);
                holder.superApprove.setVisibility(View.GONE);
                holder.space_after_reject.setVisibility(View.GONE);
                holder.space_after_super_approve.setVisibility(View.GONE);
            }
        }
        //Approve and Super Approve Icons
//        if(permission == 3 || permission == 4 || permission == 7) {
//            if(tinderSliderModel.getApproval_status() == 0) {
//                holder.approve.setVisibility(View.VISIBLE);
//                holder.superApprove.setVisibility(View.VISIBLE);
//                holder.space_after_reject.setVisibility(View.VISIBLE);
//                holder.space_after_super_approve.setVisibility(View.VISIBLE);
//            } else if(tinderSliderModel.getApproval_status() == 3) {
//                holder.approve.setVisibility(View.GONE);
//                holder.superApprove.setVisibility(View.GONE);
//                holder.space_after_reject.setVisibility(View.GONE);
//                holder.space_after_super_approve.setVisibility(View.GONE);
//            }
//        }

        if(tinderSliderModel.getApproval_status() == 0) {
            if(permission == 3 || permission == 4 || permission == 7) {
                holder.approve.setVisibility(View.VISIBLE);
                holder.superApprove.setVisibility(View.VISIBLE);
                holder.space_after_reject.setVisibility(View.VISIBLE);
                holder.space_after_super_approve.setVisibility(View.VISIBLE);
            }
            else {
                holder.approve.setVisibility(View.GONE);
                holder.superApprove.setVisibility(View.GONE);
                holder.space_after_reject.setVisibility(View.GONE);
                holder.space_after_super_approve.setVisibility(View.GONE);
            }

        } else if(tinderSliderModel.getApproval_status() == 3) {
            if(permission == 3 || permission == 4) {
                holder.approve.setVisibility(View.VISIBLE);
                holder.superApprove.setVisibility(View.VISIBLE);
                holder.space_after_reject.setVisibility(View.VISIBLE);
                holder.space_after_super_approve.setVisibility(View.VISIBLE);
            }
            else {
                holder.approve.setVisibility(View.GONE);
                holder.superApprove.setVisibility(View.GONE);
                holder.space_after_reject.setVisibility(View.GONE);
                holder.space_after_super_approve.setVisibility(View.GONE);
            }
        }

        if(tinderSliderModel.getLogin_id() == tinderSliderModel.getUpload_by()) {
            if(tinderSliderModel.getApproval_status() == 3) {
                holder.edit.setVisibility(View.VISIBLE);
                holder.space_after_approve.setVisibility(View.VISIBLE);
                holder.space_after_edit.setVisibility(View.VISIBLE);
            } else {
                holder.edit.setVisibility(View.GONE);
                holder.space_after_approve.setVisibility(View.GONE);
                holder.space_after_edit.setVisibility(View.GONE);
            }
        }
        if(permission == 3 || permission == 4 || tinderSliderModel.getLogin_id() == tinderSliderModel.getUpload_by()) {
            if(tinderSliderModel.getApproval_status() == 0 || tinderSliderModel.getApproval_status() == 3) {
                holder.delete.setVisibility(View.VISIBLE);
                holder.space_after_approve.setVisibility(View.VISIBLE);
                //holder.space_after_edit.setVisibility(View.VISIBLE);
            } else {
                holder.delete.setVisibility(View.GONE);
                holder.space_after_approve.setVisibility(View.GONE);
                holder.space_after_edit.setVisibility(View.GONE);
            }
        }

        if(tinderSliderModel.getApproval_status() == 0) {
            holder.pendingId.setText("Pending");
            holder.status_mode.setImageDrawable(mCtx.getResources().getDrawable(R.drawable.upload_pening));
            //holder.element_layout.setBackgroundColor(ContextCompat.getColor(mCtx, R.color.pending));
        } else if(tinderSliderModel.getApproval_status() == 1) {
            holder.pendingId.setText("Approved");
            //holder.element_layout.setBackgroundColor(ContextCompat.getColor(mCtx, R.color.approve));
            holder.status_mode.setImageDrawable(mCtx.getResources().getDrawable(R.drawable.single_tick));
        } else if(tinderSliderModel.getApproval_status() == 2) {
            holder.pendingId.setText("Super Approved");
            holder.status_mode.setImageDrawable(mCtx.getResources().getDrawable(R.drawable.ic_star_turquoise_24dp));
        } else {
            holder.pendingId.setText("Rejected");
            holder.status_mode.setImageDrawable(mCtx.getResources().getDrawable(R.drawable.skip));
        }

        holder.tap_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(showStatus == 0) {
                    showStatus = 1;
                } else {
                    showStatus = 0;
                }
                notifyDataSetChanged();
            }
        });

        holder.tap_icon_alt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showStatus = 0;
                notifyDataSetChanged();
            }
        });

        Manager compDb = new Manager(mCtx);
        Cursor cursor = compDb.fetchCompanyImage(companyId);
        while (cursor.moveToNext()) {
            if(cursor.getString(2) != null) {
                Glide.with(holder.profile_image.getContext())
                        .load(new File(cursor.getString(2)))
                        .placeholder(R.drawable.company)
                        .error(R.drawable.company)
                        .into(holder.profile_image);
            } else {
                Glide.with(holder.profile_image.getContext())
                        .load(R.drawable.company)
                        .placeholder(R.drawable.company)
                        .error(R.drawable.company)
                        .into(holder.profile_image);
            }

        }
        holder.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redirect(position);
            }
        });

        holder.superApprove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(global.isNetworkAvailable(mCtx)) {
                    change_file_approval_status(tinderSliderModel.getId(), 2, position, v);
                } else {
                    Toast.makeText(mCtx, mCtx.getResources().getString(R.string.please_Check_Your_Internet_Connection), Toast.LENGTH_SHORT).show();
                }
            }
        });
        holder.approve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(global.isNetworkAvailable(mCtx)) {
                    change_file_approval_status(tinderSliderModel.getId(), 1, position, v);
                } else {
                    Toast.makeText(mCtx, mCtx.getResources().getString(R.string.please_Check_Your_Internet_Connection), Toast.LENGTH_SHORT).show();
                }
            }
        });
        holder.reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(global.isNetworkAvailable(mCtx)) {
                    change_file_approval_status(tinderSliderModel.getId(), 3, position, v);
                } else {
                    Toast.makeText(mCtx, mCtx.getResources().getString(R.string.please_Check_Your_Internet_Connection), Toast.LENGTH_SHORT).show();
                }
            }
        });
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(mCtx);
                builder.setMessage("Are you sure you want to delete");
                builder.setPositiveButton(mCtx.getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        // Do nothing, but close the dialog
                    }
                });
                builder.setNegativeButton(mCtx.getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(global.isNetworkAvailable(mCtx)) {
                            shared = mCtx.getSharedPreferences("book_keeping", Context.MODE_PRIVATE);
                            auth = shared.getString("access_token", "");
                            removeImage("Bearer " + auth, String.valueOf(tinderSliderModel.getId()), position);
                        } else {
                            Toast.makeText(mCtx, mCtx.getResources().getString(R.string.please_Check_Your_Internet_Connection), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                android.app.AlertDialog alert = builder.create();
                alert.show();

            }
        });
        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(global.isNetworkAvailable(mCtx)) {
                    openEdit(tinderSliderModel.getId(), 0, position, v, tinderSliderModel.getTitle(), tinderSliderModel.getAmount(), tinderSliderModel.getUploadAdd(), tinderSliderModel.getPayment_flag(), tinderSliderModel.getCompany_currency(), tinderSliderModel.getApproval_status());
                } else {
                    Toast.makeText(mCtx, "Please Check Your Internet Connection", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void loadGallery_zip(String auth, String ids, boolean zip, String file_name) {
        //progressDialog.show();
        Log.e("IDS FOR DOWNLOAD", ids);
        Call<JsonObject> gallery = ApiClient.getInstance().getBookKeepingApi().getZip(auth, ids, zip);
        gallery.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                if (response.code() == 200) {
                  //  progressDialog.dismiss();
                    Toast.makeText(mCtx, "Receipts downloading started...", Toast.LENGTH_SHORT).show();
                    zipData = String.valueOf(response.body().get("zip_url")).replace("\"", "");
                    zipFileName = file_name;
                    if (!zipData.equals("")) {
                        downloadFile(zipData, zipFileName);
                    }
                }
            }
            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
            }
        });

    }
    private void downloadFile(String url, String fileName) {
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setTitle("Downloading file(s)");
        request.setDescription("Please wait...");
        request.allowScanningByMediaScanner();
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

        // Set the destination path for the downloaded file
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);

        DownloadManager downloadManager = (DownloadManager)mCtx.getSystemService(Context.DOWNLOAD_SERVICE);
        downloadManager.enqueue(request);
    }

    private void showTruncatedText(TextView textView, String text, int maxLines, String phase2) {
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(text + phase2);
        textView.setMaxLines(maxLines);
        textView.setText(spannableStringBuilder, TextView.BufferType.SPANNABLE);
        textView.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private void showFullText(TextView textView, String text, String phase2) {
        textView.setMaxLines(Integer.MAX_VALUE);
        textView.setText(text + phase2);
        textView.setMovementMethod(LinkMovementMethod.getInstance());
    }

    public void removeImage(String auth, String ids, int current_position) {
        galDb = new Manager(mCtx);
        Call<DeleteFile> deleteFile = ApiClient.getInstance().getBookKeepingApi().removeFile(auth, ids);
        deleteFile.enqueue(new Callback<DeleteFile>() {
            @Override
            public void onResponse(Call<DeleteFile> call, Response<DeleteFile> response) {
                Log.e("Remove File",""+response.code());
                if(response.code() == 200) {
                    galDb.removeGalleryPhotos(companyId, "("+ids+")");
                    Cursor counter = galDb.checkGalleryImage(companyId, 3);
                    if(counter.getCount() > 0) {
                        tinderSliderModels.remove(current_position);
                        notifyDataSetChanged();
                    } else {
                        redirect(current_position);
                    }

                } else {
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        Toast.makeText(mCtx, ""+jObjError.getString("message"), Toast.LENGTH_SHORT).show();
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            @Override
            public void onFailure(Call<DeleteFile> call, Throwable t) {

            }
        });
    }

    public void redirect(int current_position) {
        SharedPrefManager.getInstance(mCtx).clearData();
        if(tinderSliderModels.get(current_position).getApproval_status() == 0) {
            Intent intent = new Intent(mCtx, PendingGallery.class);
            intent.putExtra("companyId", companyId);
            intent.putExtra("permission", permission);
            intent.putExtra("clickId", 0);
            intent.putExtra("showStatus", 0);
            mCtx.startActivity(intent);
        } else if (tinderSliderModels.get(current_position).getApproval_status() == 3) {
            Intent intent = new Intent(mCtx, RejectGallery.class);
            intent.putExtra("companyId", companyId);
            intent.putExtra("permission", permission);
            intent.putExtra("clickId", 0);
            intent.putExtra("showStatus", 0);
            mCtx.startActivity(intent);
        } else {
            Intent intent = new Intent(mCtx, ReceiptGallery.class);
            intent.putExtra("companyId", companyId);
            intent.putExtra("permission", permission);
            intent.putExtra("clickId", 0);
            intent.putExtra("showStatus", 0);
            mCtx.startActivity(intent);
        }
    }

    @SuppressLint("MissingInflatedId")
    private void openEdit(int id, int set_approval_status, int current_position, View v, String memo, double amount, String file_date, int payment_flag, String company_currency, int approval_status) {
        AlertDialog.Builder builder = new AlertDialog.Builder(v.getRootView().getContext());
        View editReceipt = LayoutInflater.from(v.getRootView().getContext()).inflate(R.layout.resubmit_file, null);
        builder.setView(editReceipt);
        builder.setCancelable(true);
        editDialog = builder.create();
        editDialog.getWindow().setBackgroundDrawable(new ColorDrawable((Color.TRANSPARENT)));
        editDialog.show();

        resubmit_receipt = editReceipt.findViewById(R.id.resubmit_receipt);
        title = editReceipt.findViewById(R.id.title);
        enter_amount = editReceipt.findViewById(R.id.amount);
        enter_file_date = editReceipt.findViewById(R.id.file_date);
        currency_mode = editReceipt.findViewById(R.id.currency_mode);
        check_slfPaid = editReceipt.findViewById(R.id.check_slfPaid);
        reimbursement = editReceipt.findViewById(R.id.reimbursement);
        close_edit = editReceipt.findViewById(R.id.close_edit);
        enter_file_date.setFocusable(false);
        enter_file_date.setClickable(true);
        String modifiedString = tinderSliderModels.get(current_position).getCompany_dateFormat().replace("D", "d").replace("Y", "y");
        String convertedDate = convertDateString(file_date, modifiedString);
        title.setText(memo);
        enter_amount.setText(String.valueOf(amount));
        enter_file_date.setText(convertedDate);
        currency_mode.setText(company_currency);
        p_flag = payment_flag;

//        if(payment_flag == 0) {
//            check_slfPaid.setChecked(false);
//        } else {
//            check_slfPaid.setChecked(true);
//        }

        close_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editDialog.dismiss();
            }
        });


//        check_slfPaid.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if(isChecked) {
//                    p_flag = 1;
//                } else {
//                    p_flag = 0;
//                }
//            }
//        });

//        reimbursement.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(check_slfPaid.isChecked()) {
//                    check_slfPaid.setChecked(false);
//                    p_flag = 0;
//                } else {
//                    check_slfPaid.setChecked(true);
//                    p_flag = 1;
//                }
//            }
//        });

        enter_amount.addTextChangedListener(new TextWatcher() {
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
                    enter_amount.getText().delete(input.length() - 1, input.length());
                }
            }
        });
        enter_file_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                String monthResult = "", dayResult = "";
                int monthIndex = 0;
                String[] fDate = convertedDate.split("-");
                Log.e("FILE YEAR",""+fDate[0]);
                Log.e("FILE MONTH",""+fDate[1]);
                Log.e("FILE Date",""+fDate[2]);
                if(Integer.valueOf(fDate[1]) < 10) {
                    monthResult = fDate[1].replaceFirst("^0+(?!$)", "");
                } else {
                    monthResult = fDate[1];
                }
                monthIndex = (Integer.valueOf(monthResult) - 1);
                if(Integer.valueOf(fDate[2]) < 10) {
                    dayResult = fDate[2].replaceFirst("^0+(?!$)", "");
                } else {
                    dayResult = fDate[2];
                }
                int year = Integer.valueOf(fDate[0]);
                Log.e("CURRENT DATE",""+year);
                int month = monthIndex;
                Log.e("CURRENT MONTH",""+month);
                int day = Integer.valueOf(dayResult);
                Log.e("CURRENT DATE",""+day);
                Calendar maxDate = Calendar.getInstance();
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        // on below line we are passing context.
                        mCtx,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                // on below line we are setting date to our edit text.
                                if ((monthOfYear + 1) > 9) {
                                    if (dayOfMonth > 9) {
                                        enter_file_date.setText(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
                                        new_file_date = enter_file_date.getText().toString();
                                    } else {
                                        enter_file_date.setText(year + "-" + (monthOfYear + 1) + "-" + "0" + dayOfMonth);
                                        new_file_date = enter_file_date.getText().toString();
                                    }
                                } else {
                                    if (dayOfMonth > 9) {
                                        enter_file_date.setText(year + "-0" + (monthOfYear + 1) + "-" + dayOfMonth);
                                        new_file_date = enter_file_date.getText().toString();

                                    } else {
                                        enter_file_date.setText(year + "-0" + (monthOfYear + 1) + "-" + "0" + dayOfMonth);
                                        new_file_date = enter_file_date.getText().toString();

                                    }
                                }

                            }
                        },
                        // on below line we are passing year,
                        // month and day for selected date in our date picker.
                        year, month, day);
                datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
                // display our date picker dialog.
                datePickerDialog.show();
            }

        });

        shared = mCtx.getSharedPreferences("book_keeping", Context.MODE_PRIVATE);
        auth = shared.getString("access_token", "");

        resubmit_receipt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(title.getText().toString().equals("")) {
                    title.setError("Please enter memo");
                } else if (enter_amount.getText().toString().equals("")) {
                    enter_amount.setError("Please enter amount");
                } else if (enter_file_date.getText().toString().equals("")) {
                    enter_file_date.setError("Please select file date");
                } else {
                    reSubmit_files(title.getText().toString(), Double.parseDouble(enter_amount.getText().toString()), enter_file_date.getText().toString(), p_flag, id, "Bearer "+auth, current_position, set_approval_status, approval_status);
                }
            }
        });
    }

    public static String convertDateString(String inputDate, String modifiedString) {
        try {
            // Define the input and output date formats
            SimpleDateFormat inputFormat = new SimpleDateFormat(modifiedString, Locale.getDefault());
            // Parse the input date string
            Date date = inputFormat.parse(inputDate);
            SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            // Format the date into the desired output format
            String outputDate = outputFormat.format(date);

            return outputDate;
        } catch (ParseException e) {
            // Handle the exception if the input date format is invalid
            e.printStackTrace();
            return null;
        }
    }

    private void reSubmit_files(String memo, double enter_amount, String enter_file_date, int p_flag, int image_id, String token, int current_position, int set_approval_status, int approval_status) {
        JSONObject fileObject = new JSONObject();
        try {
            fileObject.put("filedate", enter_file_date);
            fileObject.put("title", memo);
            fileObject.put("amount", enter_amount);
            fileObject.put("payment_flag", p_flag);
            JSONArray filesArray = new JSONArray();
            filesArray.put(fileObject);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("files", filesArray);
            Log.e("FILE TAG", "" + jsonObject);
            uploadSnaps(jsonObject, token, enter_file_date, memo, enter_amount, p_flag, image_id, current_position, set_approval_status, approval_status);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    private void uploadSnaps(JSONObject jsonObject, String token, String file_date, String memo, double amount, int p_flag, int image_id, int current_position, int set_approval_status, int approval_status) {
        galDb = new Manager(mCtx);
        JsonParser jsonParser = new JsonParser();
        Call<ReSubmitResponse> reSubmitResponseCall = ApiClient.getInstance().getBookKeepingApi().reSubmitFile(token, image_id, jsonParser.parse(jsonObject.toString()));
        reSubmitResponseCall.enqueue(new Callback<ReSubmitResponse>() {
            @Override
            public void onResponse(Call<ReSubmitResponse> call, Response<ReSubmitResponse> response) {
                if(response.code() == 200) {
                    Toast.makeText(mCtx, ""+response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    galDb.reSubmitGallery(image_id, 0, amount, memo, p_flag, file_date, "");
                    editDialog.dismiss();
                    Cursor counter = galDb.checkGalleryImage(companyId, 3);
                    if(counter.getCount() > 0) {
                        tinderSliderModels.get(current_position).setApproval_status(0);
                        if(approval_status == set_approval_status) {
                            tinderSliderModels.get(current_position).setTitle(memo);
                            tinderSliderModels.get(current_position).setPayment_flag(p_flag);
                            tinderSliderModels.get(current_position).setAmount(amount);
                            SimpleDateFormat inputDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                            SimpleDateFormat outputDateFormat = new SimpleDateFormat("MMM d, yyyy", Locale.getDefault());
                            try {
                                Date inputDate = inputDateFormat.parse(file_date);
                                String outputDateStr = outputDateFormat.format(inputDate);
                                tinderSliderModels.get(current_position).setUploadAdd(outputDateStr);
                                System.out.println("Converted date: " + outputDateStr);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            notifyItemChanged(current_position);
                        } else {
                            tinderSliderModels.remove(current_position);
                            notifyDataSetChanged();
                        }
                    } else {
                        redirect(current_position);
                    }
                } else if (response.code() == 401) {
                    String token = global.reCallList(shared.getString("refresh_token", ""));
                    if(token.equals("Refresh Token Expires")) {
                        ((TinderSliderActivity)mCtx).clearShare();
                        mCtx.startActivity(new Intent(mCtx, LoginScreen.class));
                    } else {
                        String vToken = global.validToken(token);
                        if(vToken.equals("Authentication token missing") || vToken.equals("Wrong authentication token")) {
                            ((TinderSliderActivity)mCtx).clearShare();
                            mCtx.startActivity(new Intent(mCtx, LoginScreen.class));
                        } else {
                            auth = vToken;
                            ((TinderSliderActivity)mCtx).updateShare(vToken);
                            reSubmit_files(title.getText().toString(), Double.parseDouble(enter_amount.getText().toString()), enter_file_date.getText().toString(), p_flag, tinderSliderModels.get(current_position).getId(), "Bearer "+auth, current_position, set_approval_status, approval_status);
                        }
                    }

                } else {
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        Toast.makeText(mCtx, ""+jObjError.getString("message"), Toast.LENGTH_SHORT).show();
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            @Override
            public void onFailure(Call<ReSubmitResponse> call, Throwable t) {

            }
        });
    }

    private void change_file_approval_status(int image_id, int approval_status, int current_position, View view) {
        if(approval_status == 3) {
            ask_reason(current_position, view, image_id, approval_status);
        } else {
            update_approval_status(image_id, approval_status, "", current_position);
        }
    }
    private void update_approval_status(int image_id, int approval_status, String reason, int current_position) {
        shared = mCtx.getSharedPreferences("book_keeping", Context.MODE_PRIVATE);
        auth = shared.getString("access_token", "");
        galDb = new Manager(mCtx);
        FileUpdateRequest fileUpdateRequest = new FileUpdateRequest(approval_status, image_id, reason);
        Call<FileUpdateResponse> change_status = ApiClient.getInstance().getBookKeepingApi().updateFile("Bearer "+auth, fileUpdateRequest);
        change_status.enqueue(new Callback<FileUpdateResponse>() {
            @Override
            public void onResponse(Call<FileUpdateResponse> call, Response<FileUpdateResponse> response) {
                Log.e("Response Code", ""+response.code());
                if(response.code() == 200) {
                    if(approval_status == 3) {
                        alertDialog.dismiss();
                    }
                    Cursor gallery = null;
                    galDb.updateGalleryStatus(image_id, approval_status, reason);
                    if(tinderSliderModels.size() > 1) {
                        if(approval_status == 1 || approval_status == 2) {
                            gallery = galDb.fetchGallery(companyId, "DESC", "(1,2)");
                        } else if (approval_status == 3) {
                            gallery = galDb.checkGalleryImage(companyId, 3);
                        } else {
                            gallery = galDb.getGalleryPending(companyId);
                        }
                    } else {
                        gallery = galDb.getGalleryPending(companyId);
                    }

                   // Toast.makeText(mCtx, ""+gallery.getCount(), Toast.LENGTH_SHORT).show();

                    if(gallery.getCount() > 0) {
                        tinderSliderModels.get(current_position).setApproval_status(approval_status);
                        tinderSliderModels.remove(current_position);
                        notifyDataSetChanged();
                    } else {
                        redirect(current_position);
                    }
                }
                else if (response.code() == 401) {
                    String token = global.reCallList(shared.getString("refresh_token", ""));
                    if(token.equals("Refresh Token Expires")) {
                        ((TinderSliderActivity)mCtx).clearShare();
                        mCtx.startActivity(new Intent(mCtx, LoginScreen.class));
                    } else {
                        String vToken = global.validToken(token);
                        if(vToken.equals("Authentication token missing") || vToken.equals("Wrong authentication token")) {
                            ((TinderSliderActivity)mCtx).clearShare();
                            mCtx.startActivity(new Intent(mCtx, LoginScreen.class));
                        } else {
                            auth = vToken;
                            ((TinderSliderActivity)mCtx).updateShare(vToken);
                            update_approval_status(image_id, approval_status, reason, current_position);
                        }
                    }
                } else {
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        Toast.makeText(mCtx, ""+jObjError.getString("message"), Toast.LENGTH_SHORT).show();
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            @Override
            public void onFailure(Call<FileUpdateResponse> call, Throwable t) {

            }
        });
    }
    @SuppressLint("MissingInflatedId")
    private void ask_reason(int current_position, View v, int image_id, int approval_status) {
        AlertDialog.Builder builder = new AlertDialog.Builder(v.getRootView().getContext());
        View dialogView = LayoutInflater.from(v.getRootView().getContext()).inflate(R.layout.reject_reason, null);
        builder.setView(dialogView);
        builder.setCancelable(true);
        alertDialog = builder.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable((Color.TRANSPARENT)));
        alertDialog.show();

        reason = dialogView.findViewById(R.id.reason);
        reject_receipt = dialogView.findViewById(R.id.reject_receipt);
        close_reason = dialogView.findViewById(R.id.close_reason);
        reject_receipt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(reason.getText().toString().equals("")) {
                    reason.setError("Please enter reason before continue");
                } else {
                    update_approval_status(image_id, approval_status, reason.getText().toString(), current_position);
                }
            }
        });

        close_reason.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
    }
    @Override
    public int getItemCount() {
        return tinderSliderModels.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageId,linkImage;
        TextView title,imageUrl,upload_by,upload_add,paymentFlag, pendingId, reject_reasons, read_more;
        ZoomageView myZoomImageView, myZoomImageView_alt;
        RelativeLayout alternate, main, element_layout;
        LinearLayout copyImage, infoLayout, tapLayout, reject_reason_layout;
        ImageView tap_icon, back, reject, superApprove, approve, edit, delete, tap_icon_alt, downloadImage, status_mode;
        RelativeLayout copy_url;
        CircleImageView profile_image;
        Space space_after_super_approve, space_after_reject, space_after_edit, space_after_approve;
        public ViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title);
            imageUrl = (TextView) view.findViewById(R.id.imageUrl);
            linkImage = (ImageView) view.findViewById(R.id.linkImage);
            upload_by = (TextView) view.findViewById(R.id.upload_by);
            upload_add = (TextView) view.findViewById(R.id.upload_add);
            paymentFlag = (TextView) view.findViewById(R.id.paymentFlag);
            reject_reasons = (TextView) view.findViewById(R.id.reject_reasons);
            reject_reason_layout = (LinearLayout) view.findViewById(R.id.reject_reason_layout);
            myZoomImageView = (ZoomageView) view.findViewById(R.id.myZoomImageView);
            myZoomImageView_alt=(ZoomageView) view.findViewById(R.id.myZoomImageView_alt);
            read_more = (TextView) view.findViewById(R.id.read_more);
            downloadImage = (ImageView)view.findViewById(R.id.downloadImage);
            element_layout = (RelativeLayout) view.findViewById(R.id.element_layout);
            copyImage = (LinearLayout) view.findViewById(R.id.copyImage);
            infoLayout = (LinearLayout) view.findViewById(R.id.infoLayout);
            status_mode = (ImageView) view.findViewById(R.id.status_mode);
            tapLayout = (LinearLayout) view.findViewById(R.id.tapLayout);
            tap_icon = (ImageView) view.findViewById(R.id.tap_icon);
            tap_icon_alt = (ImageView) view.findViewById(R.id.tap_icon_alt);
            copy_url = (RelativeLayout) view.findViewById(R.id.copy_url);
            pendingId = (TextView) view.findViewById(R.id.pendingId);
            back = (ImageView) view.findViewById(R.id.back);
            profile_image = (CircleImageView) view.findViewById(R.id.profile_image);
            reject = (ImageView) view.findViewById(R.id.reject);
            superApprove = (ImageView) view.findViewById(R.id.superApprove);
            approve = (ImageView) view.findViewById(R.id.approve);
            edit = (ImageView) view.findViewById(R.id.edit);
            delete = (ImageView) view.findViewById(R.id.delete);
            space_after_super_approve = (Space) view.findViewById(R.id.space_after_super_approve);
            space_after_approve = (Space) view.findViewById(R.id.space_after_approve);
            space_after_reject = (Space) view.findViewById(R.id.space_after_reject);
            space_after_edit = (Space) view.findViewById(R.id.space_after_edit);
            alternate = (RelativeLayout) view.findViewById(R.id.alternate);
            main = (RelativeLayout) view.findViewById(R.id.main);
            global = new Global();

            for(int i = 0 ; i < tinderSliderModels.size(); i++) {
                System.out.println("ClickID"+" Adapter "+tinderSliderModels.get(i).getClickId()+"  "+ Integer.valueOf(tinderSliderModels.get(i).getId()));
                if(tinderSliderModels.get(i).getClickId() == Integer.valueOf(tinderSliderModels.get(i).getId())) {
                    getPos = i;
                    System.out.println("getPos"+getPos);
                    if(clickPos == 0) {
                        ((TinderSliderActivity) mCtx).scroll_to(getPos);
                    }
                    clickPos = clickPos + 1;
                 }
            }

        }



    }




}
