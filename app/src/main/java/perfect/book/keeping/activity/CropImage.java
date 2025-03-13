package perfect.book.keeping.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.JsonParser;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.crypto.NoSuchPaddingException;

import de.hdodenhof.circleimageview.CircleImageView;
import perfect.book.keeping.R;
import perfect.book.keeping.activity.company.AddCompany;
import perfect.book.keeping.activity.company.BusinessDashboards;
import perfect.book.keeping.activity.company.PaymentMethod;
import perfect.book.keeping.activity.company.TakeSnapDate;
import perfect.book.keeping.activity.company.gallery.PendingGallery;
import perfect.book.keeping.activity.company.gallery.ReceiptGallery;
import perfect.book.keeping.activity.profile.Profile;
import perfect.book.keeping.adapter.SnapImageAdapter;
import perfect.book.keeping.adapter.SnapSliderAdapter;
import perfect.book.keeping.api.ApiClient;
import perfect.book.keeping.encrypt.CryptLib;
import perfect.book.keeping.global.ClearAppData;
import perfect.book.keeping.global.Global;
import perfect.book.keeping.global.LocaleHelper;
import perfect.book.keeping.global.SwipeItemTouchHelper;
import perfect.book.keeping.manager.Manager;
import perfect.book.keeping.model.SnapImages;
import perfect.book.keeping.response.ChangePassword;
import perfect.book.keeping.response.LogoutResponse;
import perfect.book.keeping.response.SnapResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CropImage extends AppCompatActivity {

    List<SnapImages> snapImages = new ArrayList<>();
    SnapImageAdapter imageAdapter;

    SnapSliderAdapter snapSliderAdapter;
    RecyclerView imageSlider, showImage;
    Bitmap decodedImage, finalBitmap, originalImage, omgBitmap;
    int position, companyId, redirectPos;
    LinearLayout greyscale, add_new, retake_photo, crop_image, apply_crop, cancel_crop, finish_scan, back, openMenu;

    ImageView rotate_image;

    RelativeLayout view_layout, crop_layout;
    String fileDate, company_name, auth;

    CropImageView imageview1;

    Dialog progressDialog;

    Bitmap bitmap;

    int clickPoint = 1, imageCounter = 0, companyPermission = 0, paymentFlag=0;

    Global global = new Global();

    BottomSheetDialog sheetDialog;

    EditText password, cnfPassword, oldPassword;

    ImageView eye_btn_fp, eye_btn_fp_old, eye_btn_cnf, addCompany_alt;

    LinearLayout updatePass;

    CryptLib cryptLib;

    String newEncrypt, oldEncrypt, fcm_token;

    TextView header_title;

    Manager imgDb;
    CircleImageView profile_image;
    String company_currency, company_dateFormat;
    SharedPreferences shared;
    Locale myLocale;
    String codeLang;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop_image);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        android.graphics.drawable.Drawable background = CropImage.this.getDrawable(R.drawable.gradiant_btn_alt);
        getWindow().setBackgroundDrawable(background);

         imgDb = new Manager(CropImage.this);

        shared = getSharedPreferences("book_keeping", MODE_PRIVATE);
        if(shared.getString("access_token", "").equals("")) {
            startActivity(new Intent(CropImage.this, LoginScreen.class));
        } else {
            auth = shared.getString("access_token", "");
        }

        fcm_token = shared.getString("fcm_token","");
        codeLang = shared.getString("code_lang", "");
        setLocale(codeLang);
        try {
            cryptLib = new CryptLib();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (NoSuchPaddingException e) {
            throw new RuntimeException(e);
        }

        imageSlider = findViewById(R.id.imageSlider);
        greyscale = findViewById(R.id.greyscale);
        add_new = findViewById(R.id.add_new);
        retake_photo = findViewById(R.id.retake_photo);
        crop_image = findViewById(R.id.crop_image);
        finish_scan = findViewById(R.id.finish_scan);
        showImage = findViewById(R.id.showImage);
        header_title = findViewById(R.id.header_title);
        profile_image = findViewById(R.id.profile_image);
        view_layout = findViewById(R.id.view_layout);
        crop_layout = findViewById(R.id.crop_layout);
        imageview1 = findViewById(R.id.cropImageView);
        apply_crop = findViewById(R.id.apply_crop);
        cancel_crop = findViewById(R.id.cancel_crop);
        rotate_image = findViewById(R.id.rotate_image);
        back = findViewById(R.id.back);
        openMenu = findViewById(R.id.openMenu);

        fileDate = getIntent().getStringExtra("file_date");
        company_name = getIntent().getStringExtra("company_name");
        companyId = getIntent().getIntExtra("company_id", 0);
        redirectPos = getIntent().getIntExtra("redirectPos", redirectPos);
        companyPermission = getIntent().getIntExtra("companyPermission",0);

        Cursor cursor = imgDb.fetchCompanyImage(companyId);
        while (cursor.moveToNext()) {
            if(cursor.getString(2) != null) {
                Glide.with(profile_image.getContext())
                        .load(new File(cursor.getString(2)))
                        .placeholder(R.drawable.company)
                        .error(R.drawable.company)
                        .into(profile_image);
            } else {
                Glide.with(profile_image.getContext())
                        .load(R.drawable.company)
                        .placeholder(R.drawable.company)
                        .error(R.drawable.company)
                        .into(profile_image);
            }
            company_currency = cursor.getString(11);
            company_dateFormat = cursor.getString(15);
        }

        header_title.setText(company_name);
        progressDialog = new Dialog(CropImage.this);
        progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        progressDialog.setContentView(R.layout.progress);
        progressDialog.setCancelable(false);
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable((Color.TRANSPARENT)));

        imageSlider.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    position = getCurrentItem();
                    Log.e("POSITION CHANGE", "" + position);
                    Log.e("POSITION VALUE", "" + snapImages.get(position).getFile_name());
                    showImage.scrollToPosition(position);
                }
            }
        });

        openMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopUp(v);
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redirect();
            }
        });


        greyscale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                decodeImage(
                        snapImages.get(position).getSnap_image(),
                        snapImages.get(position).getTemp_snap_image(),
                        snapImages.get(position).getBw_status(),
                        "bw");
            }
        });

        add_new.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openCamera = new Intent(CropImage.this, TakeSnapDate.class);
                openCamera.putExtra("fileDate", fileDate);
                openCamera.putExtra("companyId", companyId);
                openCamera.putExtra("companyName", company_name);
                openCamera.putExtra("permission",companyPermission);
                openCamera.putExtra("replacePos", 0);
                openCamera.putExtra("redirectPos",0);
                startActivity(openCamera);
            }
        });

        retake_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(CropImage.this, ""+position, Toast.LENGTH_SHORT).show();
                Intent openCamera = new Intent(CropImage.this, TakeSnapDate.class);
                openCamera.putExtra("fileDate", fileDate);
                openCamera.putExtra("companyId", companyId);
                openCamera.putExtra("companyName", company_name);
                openCamera.putExtra("permission",companyPermission);
                openCamera.putExtra("replacePos",snapImages.get(position).getId());
                openCamera.putExtra("redirectPos",position);
                startActivity(openCamera);
            }
        });

        crop_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                crop_layout.setVisibility(View.VISIBLE);
                view_layout.setVisibility(View.GONE);
                //Toast.makeText(CropImage.this, ""+snapImages.get(position).getId(), Toast.LENGTH_SHORT).show();
                bitmap = decodeImages(snapImages.get(position).getSnap_image());
                originalImage = decodeImages(snapImages.get(position).getTemp_snap_image());
                imageview1.setImageBitmap(bitmap);
                apply_crop.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Bitmap cropBitmap = imageview1.getCroppedImage();
                        Matrix setMatrix = new Matrix();
                        setMatrix.postRotate(90);
                        imageview1.setImageBitmap(Bitmap.createBitmap(cropBitmap, 0, 0, cropBitmap.getWidth(), cropBitmap.getHeight(), setMatrix, true));
                       // imageview1.setImageBitmap(cropBitmap);
                        Matrix matrix = new Matrix();
                        matrix.postRotate(0);
                        bitmap = Bitmap.createBitmap(cropBitmap, 0, 0, cropBitmap.getWidth(), cropBitmap.getHeight(), matrix, true);
                        String mimeType = snapImages.get(position).getMime_type();
                        String filename = snapImages.get(position).getFile_name();
                        String path = snapImages.get(position).getFile_path();
                        int imgId = snapImages.get(position).getId();
                        int bw_status = snapImages.get(position).getBw_status();
                        int crop_status = 1;
                        int image_position = snapImages.get(position).getImage_position();
                        double amount = snapImages.get(position).getAmount();
                        int selfPaid = snapImages.get(position).getSelfPaid();
                        int amount_typed = snapImages.get(position).getAmount_typed();
                        String company_currency = snapImages.get(position).getCompany_currency();
                        String company_dateFormat = snapImages.get(position).getCompany_dateFormat();
                        getEncoded64ImageStringFromBitmap(bitmap, originalImage, bw_status, mimeType, filename, path, imgId, crop_status, image_position, amount, selfPaid, amount_typed, company_currency, company_dateFormat);
                        crop_layout.setVisibility(View.GONE);
                        view_layout.setVisibility(View.VISIBLE);
                        //imageview1.setImageBitmap("");
                    }
                });

                cancel_crop.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        view_layout.setVisibility(View.VISIBLE);
                        crop_layout.setVisibility(View.GONE);
                    }
                });

                rotate_image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Matrix matrix = new Matrix();
                        matrix.postRotate(90);
                        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                        imageview1.setImageBitmap(bitmap);
                    }
                });
            }
        });

        finish_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(snapImages.size() > 0) {
                    sendPhoto();
                }
            }
        });

        getList();
    }
    private void sendPhoto() {
        if(snapImages.size() > 0) {
            boolean enable  = true;
            Log.e("IMAGE SIZE", ""+snapImages.size());
            for (int j = 0; j < snapImages.size(); j++) {
                imageAdapter.notifyDataSetChanged();
                String amountStr = String.valueOf(snapImages.get(j).getAmount());
                Log.e("AMOUNT STR",""+snapImages.get(j).getAmount_typed());
                if (snapImages.get(j).getTitle().equals("") || snapImages.get(j).getAmount_typed() == 0) {
                        showImage.scrollToPosition(j);
                        imageSlider.scrollToPosition(j);
                        Toast.makeText(CropImage.this, getResources().getString(R.string.please_enter_Memo_and_Amount), Toast.LENGTH_SHORT).show();
                        enable = false;
                        break;
                }
                else if (snapImages.get(j).getAmount() < 0) {
                    showImage.scrollToPosition(j);
                    imageSlider.scrollToPosition(j);
                    Toast.makeText(CropImage.this, getResources().getString(R.string.negative_amount_is_not_acceptable), Toast.LENGTH_SHORT).show();
                    enable = false;
                    break;
                }
            }
            if(enable == true) {
                for(int i = 0 ; i < snapImages.size(); i++) {
                    imgDb.saveGallery(0, snapImages.get(i).getFile_name(), snapImages.get(i).getSnap_image(),
                            snapImages.get(i).getMime_type(), snapImages.get(i).getFile_path(),
                            0, 0, snapImages.get(i).getAmount(), snapImages.get(i).getTitle(),
                            snapImages.get(i).getCompany_id(), snapImages.get(i).getSelfPaid(), "", "", "", "", snapImages.get(i).getFile_date(), "", "", "", "");

                    updateSnaps(snapImages.get(i).getId(), companyId);
//                    try {
//                        JSONObject fileObject = new JSONObject();
//                        fileObject.put("file_name", snapImages.get(i).getFile_name());
//                        fileObject.put("blobdata", snapImages.get(i).getSnap_image());
//                        fileObject.put("mimetype", snapImages.get(i).getMime_type());
//                        fileObject.put("path", snapImages.get(i).getFile_path());
//                        fileObject.put("filedate",fileDate);
//                        fileObject.put("title",snapImages.get(i).getTitle());
//                        fileObject.put("amount",snapImages.get(i).getAmount());
//                        if(snapImages.get(i).getSelfPaid() == 1) {
//                            fileObject.put("payment_flag", true);
//                        } else {
//                            fileObject.put("payment_flag", false);
//                        }
//                        fileObject.put("company_id",companyId);
//                        JSONArray filesArray = new JSONArray();
//                        filesArray.put(fileObject);
//
//                        JSONObject jsonObject = new JSONObject();
//                        jsonObject.put("files", filesArray);
////                        if(global.isNetworkAvailable(CropImage.this)) {
////                            uploadSnaps(jsonObject, i, snapImages.get(i).getId());
////                        } else {
////                            updateSnaps(1, i, snapImages.get(i).getId(), companyId);
////                        }
//
//
//                    }  catch (JSONException e) {
//                        throw new RuntimeException(e);
//                    }
                }
            }
        } else {
            //Toast.makeText(this, "No Pics to Upload", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateSnaps(int updatePos, int companyId) {
        imgDb.removePhoto(updatePos);
        Intent intent = new Intent(CropImage.this, PendingGallery.class);
        intent.putExtra("companyId", companyId);
        intent.putExtra("companyName", company_name);
        intent.putExtra("permission", companyPermission);
        startActivity(intent);
    }

    private void uploadSnaps(JSONObject jsonObject, int cPos, int removePos) {
        progressDialog.show();
        JsonParser jsonParser = new JsonParser();
        Log.e("IMAGE OBJECT", ""+jsonObject);
        Call<SnapResponse> snapResponseCall = ApiClient.getInstance().getBookKeepingApi().uploadSnap("Bearer "+auth, jsonParser.parse(jsonObject.toString()));
        snapResponseCall.enqueue(new Callback<SnapResponse>() {
            @Override
            public void onResponse(Call<SnapResponse> call, Response<SnapResponse> response) {
                Log.e("Image Upload RESPONSE CODE",""+response.code());
                if(response.code() == 200) {
                    imgDb.removePhoto(removePos);
                    if((snapImages.size() - 1) == cPos)
                    {
                        Intent intent = new Intent(CropImage.this, ReceiptGallery.class);
                        intent.putExtra("companyId", companyId);
                        intent.putExtra("companyName", company_name);
                        intent.putExtra("permission", companyPermission);
                        startActivity(intent);
                        progressDialog.dismiss();

                    } else {
                        Log.e("In LOOP", "RUNNING");
                    }
                } else {
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onFailure(Call<SnapResponse> call, Throwable t) {
                Log.e("RESPONSE",""+t.getMessage());
            }
        });

    }

    private Bitmap decodeImages(String snap_image) {

        final String pureBase64Encoded = snap_image.substring(snap_image.indexOf(",")  + 1);
        final byte[] decodedBytes = Base64.decode(pureBase64Encoded, Base64.DEFAULT);
        Bitmap decodedImage = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
        Log.e("Image Byte", ""+decodedBytes);
        return decodedImage;
    }

    private int getCurrentItem() {
        return ((LinearLayoutManager) imageSlider.getLayoutManager()).findFirstVisibleItemPosition();
    }
    private void decodeImage(String snap_image, String temp_snap_image, int bw_status, String type) {
        if(snap_image != null) {
            final String pureBase64Encoded = snap_image.substring(snap_image.indexOf(",") + 1);
            final byte[] decodedBytes = Base64.decode(pureBase64Encoded, Base64.DEFAULT);
            decodedImage = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);

            final String pureBase64EncodedOrg = temp_snap_image.substring(temp_snap_image.indexOf(",") + 1);
            final byte[] decodedBytesOrg = Base64.decode(pureBase64EncodedOrg, Base64.DEFAULT);
            omgBitmap = BitmapFactory.decodeByteArray(decodedBytesOrg, 0, decodedBytesOrg.length);

            if(type.equals("bw")) {
                if(bw_status == 0) {
                    Log.e("BW","0");
                    convertToGrayScale(decodedImage, omgBitmap);
                } else {
                    Log.e("BW","1");
                    convertToOriginal(omgBitmap, decodedImage);
                }
            }
            Log.e("DECODE IMAGES", ""+decodedImage);
        }
    }
    private void convertToOriginal(Bitmap decodedImageOrg, Bitmap decodedImage) {
        int imgId = snapImages.get(position).getId();
        String mimeType = snapImages.get(position).getMime_type();
        String filename = snapImages.get(position).getFile_name();
        String path = snapImages.get(position).getFile_path();
        int image_position = snapImages.get(position).getImage_position();
        double amount = snapImages.get(position).getAmount();
        int selfPaid = snapImages.get(position).getSelfPaid();
        int amount_typed = snapImages.get(position).getAmount_typed();
        String company_currency = snapImages.get(position).getCompany_currency();
        String company_dateFormat = snapImages.get(position).getCompany_dateFormat();
        getEncoded64ImageStringFromBitmap(decodedImage, decodedImageOrg, 0, mimeType, filename, path, imgId,0, image_position, amount, selfPaid,amount_typed, company_currency, company_dateFormat);
    }
    private void convertToGrayScale(Bitmap decodedImage, Bitmap decodedImageOrg) {
        int width = decodedImage.getWidth();
        int height = decodedImage.getHeight();
        finalBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888, true);

        Paint paint = new Paint();
        ColorMatrix colorMatrix = new ColorMatrix();
        Canvas canvas = new Canvas(finalBitmap);
        colorMatrix.set(global.bwMatrixValue(-140, "bw"));
        paint.setColorFilter(new ColorMatrixColorFilter(colorMatrix));
        canvas.drawBitmap(decodedImage, 0, 0, paint);
        int imgId = snapImages.get(position).getId();
        String mimeType = snapImages.get(position).getMime_type();
        String filename = snapImages.get(position).getFile_name();
        String path = snapImages.get(position).getFile_path();
        int image_position = snapImages.get(position).getImage_position();
        double amount = snapImages.get(position).getAmount();
        int selfPaid = snapImages.get(position).getSelfPaid();
        int amount_typed = snapImages.get(position).getAmount_typed();
        String company_currency = snapImages.get(position).getCompany_currency();
        String company_dateFormat = snapImages.get(position).getCompany_dateFormat();
        getEncoded64ImageStringFromBitmap(finalBitmap, decodedImageOrg, 1, mimeType, filename, path, imgId, 0, image_position, amount, selfPaid, amount_typed, company_currency, company_dateFormat);
    }
    private void getEncoded64ImageStringFromBitmap(Bitmap finalBitmap, Bitmap finalBitmapOrg, int bw_status, String mimeType, String filename, String path, int imgId, int crop_status, int image_position, double amount, int selfPaid, int amount_typed, String company_currency, String company_dateFormat) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        finalBitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream);
        byte[] byteFormat = stream.toByteArray();
        String imgString = Base64.encodeToString(byteFormat, Base64.NO_WRAP);

        ByteArrayOutputStream streamOrg = new ByteArrayOutputStream();
        finalBitmapOrg.compress(Bitmap.CompressFormat.JPEG, 70, streamOrg);
        byte[] byteFormatOrg = streamOrg.toByteArray();
        String original = Base64.encodeToString(byteFormatOrg, Base64.NO_WRAP);
        Log.e("BW SAVE",""+bw_status);

        if(bw_status == 1) {
            SnapImages snapImg = new  SnapImages(imgId, filename, imgString, original, bw_status, mimeType, path, fileDate, "", companyId, image_position, amount, selfPaid, amount_typed, company_currency, company_dateFormat);
            snapImages.set(position, snapImg);
//            imageAdapter.notifyItemChanged(position);
//            snapSliderAdapter.notifyDataSetChanged();

            String response = imgDb.updateImages(imgId, filename, imgString, original, bw_status, mimeType, path, fileDate, "", companyId);
            if(response.equals("Update successfully")) {
                imageAdapter.notifyItemChanged(position);
                //snapSliderAdapter.notifyDataSetChanged();
            }
        } else if (crop_status == 1) {
            SnapImages snapImg = new SnapImages(imgId, filename, imgString, imgString, bw_status, mimeType, path, fileDate, "", companyId, image_position, amount, selfPaid, amount_typed, company_currency, company_dateFormat);
            snapImages.set(position, snapImg);
            String response = imgDb.updateImages(imgId, filename, original, original,bw_status, mimeType, path, fileDate, "", companyId);
            Log.e("Response Code",""+response);
            if(response.equals("Update successfully")) {
                imageAdapter.notifyItemChanged(position);
                //snapSliderAdapter.notifyDataSetChanged();
            }
        } else {
            SnapImages snapImg = new SnapImages(imgId, filename, original, original, bw_status, mimeType, path, fileDate, "", companyId, image_position, amount, selfPaid, amount_typed, company_currency, company_dateFormat);
            snapImages.set(position, snapImg);
            String response = imgDb.updateImages(imgId, filename, original, original,bw_status, mimeType, path, fileDate, "", companyId);
            if(response.equals("Update successfully")) {
                imageAdapter.notifyItemChanged(position);
                //snapSliderAdapter.notifyDataSetChanged();
            }
        }
    }
    private void getList() {
        snapImages.clear();
        Cursor cursor = imgDb.getImages(companyId);
       // Cursor cursor = medDb.getImages(22);
        while (cursor.moveToNext()) {
            SnapImages snapImage = new SnapImages(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getInt(4),
                    cursor.getString(5),
                    cursor.getString(6),
                    cursor.getString(7),
                    cursor.getString(8),
                    cursor.getInt(9),
                    cursor.getInt(13),
                    cursor.getInt(14),
                    cursor.getInt(16),
                    cursor.getInt(15),
                    company_currency,
                    company_dateFormat
            );
            snapImages.add(snapImage);
        }

        Log.e("SIZE",""+snapImages.size());

        if(snapImages.size() > 0) {
            //Main Slider
            imageAdapter = new SnapImageAdapter(CropImage.this, snapImages, companyPermission);
            imageSlider.setAdapter(imageAdapter);
            imageSlider.setLayoutManager(new LinearLayoutManager(CropImage.this,LinearLayoutManager.HORIZONTAL, false));
            imageSlider.smoothScrollToPosition(redirectPos);
            LinearSnapHelper linearSnapHelper = new SwipeItemTouchHelper();
            linearSnapHelper.attachToRecyclerView(imageSlider);
            imageAdapter.notifyDataSetChanged();
        }
    }

    public void nextScrollTo(int pos) {
        Log.e("RECEIVE POSITION", ""+pos);
        int nextPos = pos + 1;
        imageSlider.smoothScrollToPosition(nextPos);
        showImage.smoothScrollToPosition(nextPos);
    }

    public void previousScrollTo(int pos) {
        int nextPos = pos - 1;
        imageSlider.smoothScrollToPosition(nextPos);
        showImage.smoothScrollToPosition(nextPos);
    }

    public void changeImage(int pos) {
        position = pos;
        imageSlider.scrollToPosition(pos);
    }

    public void removeFromList(int position, int dbPos) {
        if((snapImages.size()-1) > 0) {
            snapImages.remove(position);
            imgDb.removePhoto(dbPos);
        } else {
            snapImages.remove(position);
            imgDb.removePhoto(dbPos);
            Intent intent = new Intent(CropImage.this, BusinessDashboards.class);
            intent.putExtra("companyId", companyId);
            intent.putExtra("companyName", company_name);
            intent.putExtra("companyPermission",companyPermission);
            startActivity(intent);
        }
        imageAdapter.notifyDataSetChanged();
    }

    @Override
    public void onBackPressed() {
        redirect();
    }

    public void redirect() {
        Intent openCamera = new Intent(CropImage.this, TakeSnapDate.class);
        openCamera.putExtra("fileDate", fileDate);
        openCamera.putExtra("companyId", companyId);
        openCamera.putExtra("companyName", company_name);
        openCamera.putExtra("permission",companyPermission);
        openCamera.putExtra("replacePos", 0);
        openCamera.putExtra("redirectPos",0);
        startActivity(openCamera);
    }

    //Menu
    private void showPopUp(View v) {
        PopupMenu popupMenu = new PopupMenu(CropImage.this, v);
        popupMenu.getMenuInflater().inflate(R.menu.navigation_item, popupMenu.getMenu());
//        MenuItem profile = popupMenu.getMenu().findItem(R.id.profileDtl);
//        MenuItem subUsers = popupMenu.getMenu().findItem(R.id.subUsers);
//        if(userType == 0) {
//            profile.setVisible(true);
//            subUsers.setVisible(true);
//        } else {
//            profile.setVisible(false);
//            subUsers.setVisible(false);
//        }
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();
                if(id == R.id.companyList) {
                    //startActivity(new Intent(CropImage.this, Companies.class));
                    Intent company = new Intent(CropImage.this, Companies.class);
                    company.putExtra("enable","0");
                    startActivity(company);
                } else if(id == R.id.profileDtl) {
                    startActivity(new Intent(CropImage.this, Profile.class));
                }  else if(id == R.id.changePassword) {
                    openSheet();
                } else {
                    ask_before_logout();
                }
                return true;
            }
        });

        popupMenu.show();
    }

    public void ask_before_logout() {
        String you_sure_that_you_want_to_logout = getResources().getString(R.string.you_sure_that_you_want_to_logout);
        String no = getResources().getString(R.string.no);
        String yes = getResources().getString(R.string.yes);
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(CropImage.this);
        //builder.setTitle("Confirmation!");
        builder.setMessage(you_sure_that_you_want_to_logout);
        builder.setPositiveButton(no, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                // Do nothing, but close the dialog
            }
        });
        builder.setNegativeButton(yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                    log_out();
                    String directoryPath = "/storage/emulated/0/Android/data/"+getApplicationContext().getPackageName()+"/files/Pictures/";
                    global.deleteAllImagesInDirectory(directoryPath);
                    String directoryPath1 = "/storage/emulated/0/Android/data/"+getApplicationContext().getPackageName()+"/files/Pictures/Company/";
                    global.deleteAllImagesInDirectory(directoryPath1);
                    String directoryPath2 = "/storage/emulated/0/Android/data/"+getApplicationContext().getPackageName()+"/files/Pictures/Thumbnail";
                    global.deleteAllImagesInDirectory(directoryPath2);
                    Manager compDb = new Manager(CropImage.this);
                    compDb.removeAllCompany();
                    SharedPreferences loginRef = getSharedPreferences("book_keeping", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = loginRef.edit();
                    editor.putString("access_token","");
                    editor.putString("fcm_token","");
                    editor.putBoolean("first_input",true);
                    editor.putInt("userId", 0);
                    editor.commit();
                    //ClearAppData.clearApplicationData(CropImage.this, getPackageName());
                    startActivity(new Intent(CropImage.this, LoginScreen.class));
            }
        });
        android.app.AlertDialog alert = builder.create();
        alert.show();
    }

    private void log_out() {
        progressDialog.show();
        String mac_address = global.getDeviceToken(CropImage.this);
        Call<LogoutResponse> logOut = ApiClient.getInstance().getBookKeepingApi().logOut("Bearer "+auth, fcm_token, mac_address);
        logOut.enqueue(new Callback<LogoutResponse>() {
            @Override
            public void onResponse(Call<LogoutResponse> call, Response<LogoutResponse> response) {
                Log.e("LOG OUT RESPONSE",""+response.code());
                if(response.code() == 200) {
                    progressDialog.dismiss();
                    imgDb.removeAllCompany();
                    SharedPreferences loginRef = getSharedPreferences("book_keeping", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = loginRef.edit();
                    editor.putString("access_token","");
                    editor.putString("fcm_token","");
                    editor.putBoolean("first_input",true);
                    editor.putInt("userId", 0);
                    editor.commit();
                    startActivity(new Intent(CropImage.this, LoginScreen.class));
                } else {
                    progressDialog.dismiss();
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                       // Toast.makeText(CropImage.this, ""+jObjError.getString("message"), Toast.LENGTH_SHORT).show();
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            @Override
            public void onFailure(Call<LogoutResponse> call, Throwable t) {

            }
        });
    }

    @SuppressLint("MissingInflatedId")
    private void openSheet() {
        sheetDialog = new BottomSheetDialog(CropImage.this, R.style.BottomSheetStyle);
        View changePass = getLayoutInflater().inflate(R.layout.change_password, null);

        password = changePass.findViewById(R.id.password);
        eye_btn_fp = changePass.findViewById(R.id.eye_btn_fp);
        cnfPassword = changePass.findViewById(R.id.cnfPassword);
        eye_btn_cnf = changePass.findViewById(R.id.eye_btn_cnf);
        oldPassword = changePass.findViewById(R.id.oldPassword);
        eye_btn_fp_old = changePass.findViewById(R.id.eye_btn_fp_old);
        updatePass = changePass.findViewById(R.id.updatePass);


        //Password Toggle
        eye_btn_fp_old.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(oldPassword.getTransformationMethod().equals(HideReturnsTransformationMethod.getInstance())){
                    oldPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    eye_btn_fp_old.setImageResource(R.drawable.hide);
                } else {
                    oldPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    eye_btn_fp_old.setImageResource(R.drawable.show);
                }
            }
        });

        oldPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                eye_btn_fp_old.setVisibility(View.VISIBLE);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        eye_btn_fp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(password.getTransformationMethod().equals(HideReturnsTransformationMethod.getInstance())){
                    password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    eye_btn_fp.setImageResource(R.drawable.hide);
                } else {
                    password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    eye_btn_fp.setImageResource(R.drawable.show);
                }
            }
        });

        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                eye_btn_fp.setVisibility(View.VISIBLE);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        eye_btn_cnf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(cnfPassword.getTransformationMethod().equals(HideReturnsTransformationMethod.getInstance())){
                    cnfPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    eye_btn_cnf.setImageResource(R.drawable.hide);
                } else {
                    cnfPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    eye_btn_cnf.setImageResource(R.drawable.show);
                }
            }
        });

        cnfPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                eye_btn_cnf.setVisibility(View.VISIBLE);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        updatePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String please_enter_your_current_password = getResources().getString(R.string.please_enter_your_current_password);
                String please_enter_new_password = getResources().getString(R.string.please_enter_new_password);
                String new_password_must_be_6_character_long = getResources().getString(R.string.new_password_must_be_6_character_long);
                String please_enter_the_new_password_again = getResources().getString(R.string.please_enter_the_new_password_again);

                if(oldPassword.getText().toString().equals("")) {
                    eye_btn_fp_old.setVisibility(View.GONE);
                    oldPassword.setError(please_enter_your_current_password);
                } else if(password.getText().toString().equals("")) {
                    eye_btn_fp.setVisibility(View.GONE);
                    password.setError(please_enter_new_password);
                } else if (password.getText().toString().length() < 6) {
                    eye_btn_fp.setVisibility(View.GONE);
                    password.setError(new_password_must_be_6_character_long);
                } else if(cnfPassword.getText().toString().equals("")) {
                    eye_btn_cnf.setVisibility(View.GONE);
                    cnfPassword.setError(please_enter_the_new_password_again);
                } else if (cnfPassword.getText().toString().length() < 6) {
                    eye_btn_cnf.setVisibility(View.GONE);
                    cnfPassword.setError(please_enter_the_new_password_again);
                } else if(password.getText().toString().equals(cnfPassword.getText().toString())) {
                    eye_btn_cnf.setVisibility(View.VISIBLE);
                    eye_btn_fp_old.setVisibility(View.VISIBLE);
                    eye_btn_fp.setVisibility(View.VISIBLE);
                    if(global.isNetworkAvailable(CropImage.this)) {
                        try {
                            newEncrypt = cryptLib.encryptPlainTextWithRandomIV(password.getText().toString(), ApiClient.getInstance().getKey());
                            oldEncrypt = cryptLib.encryptPlainTextWithRandomIV(oldPassword.getText().toString(), ApiClient.getInstance().getKey());
                            passwordUpdate(newEncrypt, oldEncrypt);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    } else {
                        String please_Check_Your_Internet_Connection = getResources().getString(R.string.please_Check_Your_Internet_Connection);

                        Toast.makeText(CropImage.this, please_Check_Your_Internet_Connection, Toast.LENGTH_SHORT).show();
                    }

                } else {
                    eye_btn_cnf.setVisibility(View.GONE);
                    cnfPassword.setError("Confirm password mismatch");
                }
            }
        });

        sheetDialog.setContentView(changePass);
        sheetDialog.show();
    }
    private void passwordUpdate(String newEncrypt, String oldEncrypt) {
        progressDialog.show();
        Call<ChangePassword> changePassword = ApiClient.getInstance().getBookKeepingApi().changePassword("Bearer "+auth, oldEncrypt, newEncrypt);
        changePassword.enqueue(new Callback<ChangePassword>() {
            @Override
            public void onResponse(Call<ChangePassword> call, Response<ChangePassword> response) {
                if(response.code() == 200) {
                    Toast.makeText(CropImage.this, getResources().getString(R.string.password_change_successfully), Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                    sheetDialog.dismiss();
                } else if (response.code()==401) {
                    String token = global.reCallList(shared.getString("refresh_token", ""));
                    if(token.equals("Refresh Token Expires")) {
                        clearShare();
                        startActivity(new Intent(CropImage.this, LoginScreen.class));
                    } else {
                        String vToken = global.validToken(token);
                        if(vToken.equals("Authentication token missing") || vToken.equals("Wrong authentication token")) {
                            clearShare();
                            startActivity(new Intent(CropImage.this, LoginScreen.class));
                        } else {
                            auth = vToken;
                            updateShare(vToken);
                            try {
                                String newEncrypt = cryptLib.encryptPlainTextWithRandomIV(password.getText().toString(), ApiClient.getInstance().getKey());
                                String oldEncrypt = cryptLib.encryptPlainTextWithRandomIV(oldPassword.getText().toString(), ApiClient.getInstance().getKey());
                                passwordUpdate(newEncrypt, oldEncrypt);
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }
                } else {
                    progressDialog.dismiss();
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        Toast.makeText(CropImage.this, ""+jObjError.getString("message"), Toast.LENGTH_SHORT).show();
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
//                    eye_btn_cnf.setVisibility(View.GONE);
//                    cnfPassword.setError();
                }
            }

            @Override
            public void onFailure(Call<ChangePassword> call, Throwable t) {

            }
        });
    }
    public void clearShare() {
        SharedPreferences loginRef = getSharedPreferences("book_keeping", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = loginRef.edit();
        editor.putString("access_token","");
        editor.putString("fcm_token","");
        editor.putBoolean("first_input",true);
        editor.putInt("userId", 0);
        editor.commit();
        Manager db = new Manager(CropImage.this);
        db.removeAllCompany();
    }
    public void updateShare(String token) {
        SharedPreferences sharedPreferences = getSharedPreferences("book_keeping", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("access_token", token);
        editor.commit();
    }

    public void setLocale(String localeName) {
        System.out.println("GET_Code"+" "+localeName);
        Context context = LocaleHelper.setLocale(this, localeName);
        //Resources resources = context.getResources();
        myLocale = new Locale(localeName);
        Resources res = context.getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);

    }
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base));
    }
}