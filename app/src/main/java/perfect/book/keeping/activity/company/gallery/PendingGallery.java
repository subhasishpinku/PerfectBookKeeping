package perfect.book.keeping.activity.company.gallery;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.aseem.versatileprogressbar.ProgBar;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.jsibbold.zoomage.ZoomageView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import javax.crypto.NoSuchPaddingException;

import de.hdodenhof.circleimageview.CircleImageView;
import perfect.book.keeping.R;
import perfect.book.keeping.activity.Companies;
import perfect.book.keeping.activity.LoginScreen;
import perfect.book.keeping.activity.company.AddCompany;
import perfect.book.keeping.activity.company.BusinessDashboards;
import perfect.book.keeping.activity.company.TinderSliderActivity;
import perfect.book.keeping.activity.profile.Profile;
import perfect.book.keeping.api.ApiClient;
import perfect.book.keeping.encrypt.CryptLib;
import perfect.book.keeping.global.ClipboardUtil;
import perfect.book.keeping.global.GalleryDataTransferService;
import perfect.book.keeping.global.Global;
import perfect.book.keeping.global.LocaleHelper;
import perfect.book.keeping.global.RefreshToken;
import perfect.book.keeping.global.SharedPrefManager;
import perfect.book.keeping.global.UploadReceipt;
import perfect.book.keeping.global.UploadWorker;
import perfect.book.keeping.manager.Manager;
import perfect.book.keeping.model.FilterUser;
import perfect.book.keeping.model.Gallery;
import perfect.book.keeping.model.GalleryAll;
import perfect.book.keeping.request.FileUpdateRequest;
import perfect.book.keeping.response.ChangePassword;
import perfect.book.keeping.response.CompanyResponse;
import perfect.book.keeping.response.DeleteFile;
import perfect.book.keeping.response.FileUpdateResponse;
import perfect.book.keeping.response.LogoutResponse;
import perfect.book.keeping.response.SnapResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
public class PendingGallery extends AppCompatActivity {
    SharedPreferences shared;
    String auth, fcm_token,imgString, company_currency;
    int companyId, permission, filterApply = 0;
    Manager galDb;
    String last_date= "", fromDates="", toDates="", search_by = "", filterNamesIdSpaceRemove = "",approvalStatusNewSpaceRemove ="(0)";
    EditText response;
    RefreshToken refreshToken;
    Dialog progressDialog;
    List<GalleryAll> galleryAllItems = new ArrayList<>();
    ShimmerFrameLayout shimmer_container;
    ImageView companyLogo;
    LinearLayout openMenu, filterByDate, sortBy, filter, zip_edt, removeFile, counter_layout;
    BottomSheetDialog sheetDialog;
    EditText password, cnfPassword, oldPassword;
    ImageView eye_btn_fp, eye_btn_fp_old, eye_btn_cnf;
    LinearLayout updatePass, copyImage, date_range, approval_status, upload_by, back, reset_filter;
    CryptLib cryptLib;
    String newEncrypt, oldEncrypt, copyImageUrl;
    int pressCounter = 0, counter = 0;
    RelativeLayout date_filter_field, status_filter_field, user_filter_field;
    TextView date_range_text, status_text, upload_by_text, amount, count;
    RecyclerView user_list;
    List<FilterUser> filterUsers = new ArrayList<>();
    //FilterUserAdapter filterUserAdapter;
    ArrayList<String> filterNames = new ArrayList<>();
    ArrayList<String> filterNamesIds = new ArrayList<>();
    ArrayList<String> statusNames = new ArrayList<>();
    ArrayList<String> statusNamesValues = new ArrayList<>();
    CheckBox check_pending,check_approve,check_super_approve, check_reject;
    ImageView reloadId;
    EditText searchByTitle;
    RelativeLayout noImage;
    SwipeRefreshLayout swipeRefresh;
    TextView fromDate, toDate, companyName;
    public RecyclerView galleryImages;
    private List<Integer> sectionSelections = new ArrayList<>();
    List<Integer> images = new ArrayList<>();
    HashSet<Integer> hashSet = new HashSet<>();
    String orderBy = "DESC", str_ids = "", company_name, value;
    List<LinkedHashMap<String,String>> ImageLoads = new ArrayList<>();

    Global global;
    private List<Integer> imageIds = new ArrayList<>();
    String zipFileName = "file.zip", zipData;
    BottomSheetDialog bottomsheetdialog;

    BottomSheetBehavior<View> bottomSheetBehavior;
    RadioButton radioButton, radioButtonDesc;
    GalleryAdapter galleryAdapter;
    FilterUserAdapter filterUserAdapter;
    CircleImageView profile_image;
    double total_amount = 0.00;
    final Handler handler = new Handler();
    final int delay = 10000; // 10000 milliseconds == 10 seconds
    UploadReceipt uploadReceipt;
    String codeLang;
    Locale myLocale;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pending_gallery);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        android.graphics.drawable.Drawable background = PendingGallery.this.getDrawable(R.drawable.gradiant_btn_alt);
        getWindow().setBackgroundDrawable(background);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        initialize();
        startServiceWorker();
        shared = getSharedPreferences("book_keeping", Context.MODE_PRIVATE);
        codeLang = shared.getString("code_lang", "");
        setLocale(codeLang);
    }

    private void startServiceWorker() {
        String UNIQUE_WORK_NAME="UploadPendingReceipt";
        WorkManager workManager = WorkManager.getInstance(this);
        PeriodicWorkRequest request = new PeriodicWorkRequest.Builder(
                UploadWorker.class,
                16,
                TimeUnit.MINUTES).build();
        workManager.enqueueUniquePeriodicWork(UNIQUE_WORK_NAME, ExistingPeriodicWorkPolicy.KEEP, request);
    }

    private void initialize() {
        global = new Global();
        uploadReceipt = new UploadReceipt();
        galDb = new Manager(PendingGallery.this);
        shared = getSharedPreferences("book_keeping", Context.MODE_PRIVATE);
        /*GET SHARD PREF DATA*/
        auth = shared.getString("access_token", "");
        fcm_token = shared.getString("fcm_token","");
        /*GET INTENT DATA*/
        companyId = getIntent().getIntExtra("companyId", 0);
        permission = getIntent().getIntExtra("permission", 0);
        /*INITIALIZE*/
        galleryImages = findViewById(R.id.galleryImages);
        response = findViewById(R.id.response);
        companyLogo = findViewById(R.id.companyLogo);
        sortBy = findViewById(R.id.sortBy);
        filter = findViewById(R.id.filter);
        shimmer_container = findViewById(R.id.shimmer_container);
        noImage = findViewById(R.id.noImage);
        zip_edt = findViewById(R.id.zip);
        removeFile = findViewById(R.id.remove);
        openMenu = findViewById(R.id.openMenu);
        swipeRefresh = findViewById(R.id.swipeRefresh);
        companyName = findViewById(R.id.company_name);
        amount = findViewById(R.id.amount);
        count = findViewById(R.id.counter);
        counter_layout = findViewById(R.id.counter_layout);
        profile_image = findViewById(R.id.profile_image);
        back = findViewById(R.id.back);
        try {
            cryptLib = new CryptLib();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (NoSuchPaddingException e) {
            throw new RuntimeException(e);
        }
        Cursor cursor = galDb.fetchCompanyImage(companyId);
        while (cursor.moveToNext()) {
            if(cursor.getString(1).length() > 12) {
                companyName.setText(cursor.getString(1).substring(0, Math.min(cursor.getString(1).length(), 12)) + "...");
            } else {
                companyName.setText(cursor.getString(1));
            }
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
        }
        //getCompany("Bearer "+auth, companyId);
        if(permission == 6 || permission == 7) {
            removeFile.setVisibility(View.GONE);
        } else {
            removeFile.setVisibility(View.VISIBLE);
        }
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

        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Intent intent = getIntent();
                finish(); // Close the current activity
                startActivity(intent); // Start the activity again
                counter = counter+1;
                swipeRefresh.setRefreshing(false);
            }
        });

        sortBy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSort();
            }
        });
        filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFilter();
                //callUser("Bearer " + auth);
                SubUser(companyId);
            }
        });
        removeFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(str_ids.equals("") || str_ids == null) {
                    Toast.makeText(PendingGallery.this, getResources().getString(R.string.choose_at_least_one_image_to_remove), Toast.LENGTH_SHORT).show();
                } else {
                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(PendingGallery.this);
                    builder.setMessage(getResources().getString(R.string.are_you_sure_you_want_to_remove_the_file));
                    builder.setPositiveButton("NO", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            // Do nothing, but close the dialog
                        }
                    });
                    builder.setNegativeButton("YES", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if(global.isNetworkAvailable(PendingGallery.this)) {
                                removeImage("Bearer "+auth, str_ids);
                            } else {
                                Toast.makeText(PendingGallery.this, getResources().getString(R.string.please_Check_Your_Internet_Connection), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    android.app.AlertDialog alert = builder.create();
                    alert.show();
                }
            }
        });
        zip_edt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("SELECTED IDS", "" + str_ids);
                if (str_ids.equals("") || str_ids == null) {
                    Toast.makeText(PendingGallery.this, getResources().getString(R.string.choose_at_least_one_image_to_download), Toast.LENGTH_SHORT).show();
                } else {
                    if(global.isNetworkAvailable(PendingGallery.this)) {
                        loadGallery_zip("Bearer " + auth, str_ids, true);
                    } else {
                        Toast.makeText(PendingGallery.this, getResources().getString(R.string.please_Check_Your_Internet_Connection), Toast.LENGTH_SHORT).show();

                    }
                }
            }
        });
        refreshToken = new RefreshToken();
        progressDialog = new Dialog(PendingGallery.this);
        progressDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        progressDialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        progressDialog.setContentView(R.layout.progress);
        if (progressDialog.getWindow() != null) {
            progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
       // if(global.isNetworkAvailable(PendingGallery.this)) {
            //Upload Receipt
            startUploadService();
            //Download Receipt
            Intent gallerySync = new Intent(this, GalleryDataTransferService.class);
            gallerySync.putExtra("companyId",companyId);
            gallerySync.putExtra("approval_status","0");
            startService(gallerySync);
      //  }
        galleryAllItems.clear();
        fetchData();



//        if(value != null) {
//            if (value.equals("1")) {
                handler.postDelayed(new Runnable() {
                    public void run() {
                        refresh();
                        handler.postDelayed(this, delay);
                    }
                }, delay);

//        }
    }

    public void startUploadService() {
        UploadReceipt uploadReceipt = new UploadReceipt();
        Log.e("RECEIPT LOG RUNNING STATUS",""+uploadReceipt.isServiceRunning);
        if(UploadReceipt.isServiceRunning == false) {
            Intent galleryUploadSync = new Intent(this, UploadReceipt.class);
            startForegroundService(galleryUploadSync);
        }
    }

    private void refresh() {
        Cursor checkSettings = galDb.checkSettings("receipt_refresh");
        while (checkSettings.moveToNext()) {
            value = checkSettings.getString(2);
        }
        if(value != null) {
            if (value.equals("1")) {
                Intent intent = getIntent();
                finish(); // Close the current activity
                startActivity(intent); // Start the activity again
                galDb.updateSettings("receipt_refresh","0");
            }
        }

    }

    private boolean getConnectionInfo() {
        Cursor galleryPending = galDb.getGalleryPending(companyId);
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        NetworkCapabilities nc = connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork());
        if(galleryPending.getCount() > 0) {
            if (networkInfo == null) {
                Log.e("Network Mode", "Not Allowed");
                return false;
            } else if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                sync(nc.getLinkDownstreamBandwidthKbps(), nc.getLinkUpstreamBandwidthKbps());
            } else if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                sync(nc.getLinkDownstreamBandwidthKbps(), nc.getLinkUpstreamBandwidthKbps());
            } else {
                Log.e("Network Mode", "Not Allowed");
            }
        }
        return networkInfo.isConnected();
    }

    private void sync(int downloadSpeed, int uploadSpeed) {
        Log.e("Speed Upload", ""+uploadSpeed);
        Log.e("Speed Download", ""+downloadSpeed);
        //if(downloadSpeed >= 50000) {
//            Intent gallerySync = new Intent(this, GalleryDataTransferService.class);
//            gallerySync.putExtra("companyId",companyId);
//            gallerySync.putExtra("approval_status","0");
//            startService(gallerySync);
//        } else {
//            Toast.makeText(this, getResources().getString(R.string.file_download_msg_for_speed), Toast.LENGTH_SHORT).show();
//        }

        if(uploadSpeed >= 10000) {
            Intent gallerySync = new Intent(this, UploadReceipt.class);
            gallerySync.putExtra("companyId",companyId);
            gallerySync.putExtra("approval_status","0");
        } else {
            Toast.makeText(this, getResources().getString(R.string.file_upload_msg_for_speed), Toast.LENGTH_SHORT).show();
        }

    }

    private void getPendingData() {
        Cursor galleryPending = galDb.getGalleryPending(companyId);
        if(galleryPending.getCount() > 0) {
            while (galleryPending.moveToNext()) {

    //                    try {
    //                        JSONObject fileObject = new JSONObject();
    //                        fileObject.put("file_name", galleryPending.getString(2));
    //                        fileObject.put("blobdata", galleryPending.getString(3));
    //                        fileObject.put("mimetype", galleryPending.getString(4));
    //                        fileObject.put("path", galleryPending.getString(5));
    //                        fileObject.put("filedate", galleryPending.getString(16));
    //                        fileObject.put("title", galleryPending.getString(9));
    //                        fileObject.put("company_id", galleryPending.getString(10));
    //                        fileObject.put("amount", galleryPending.getString(8));
    //                        fileObject.put("payment_flag", galleryPending.getString(11));
    //                        JSONArray filesArray = new JSONArray();
    //                        filesArray.put(fileObject);
    //                        JSONObject jsonObject = new JSONObject();
    //                        jsonObject.put("files", filesArray);
    //                        Log.e("FILE TAG", "" + jsonObject);
    //                        uploadSnaps(jsonObject, galleryPending.getInt(0));
    //                    } catch (JSONException e) {
    //                        throw new RuntimeException(e);
    //                    }
            }
        }
    }
    private void uploadSnaps(JSONObject jsonObject, int aId) {
        JsonParser jsonParser = new JsonParser();
        Call<SnapResponse> snapResponseCall = ApiClient.getInstance().getBookKeepingApi().uploadSnap("Bearer "+auth, jsonParser.parse(jsonObject.toString()));
        snapResponseCall.enqueue(new Callback<SnapResponse>() {
            @Override
            public void onResponse(Call<SnapResponse> call, Response<SnapResponse> response) {
                Log.e("Response Code",""+response.code());
                if(response.code() == 200) {
                    downloadImage(response.body().getData().getThumbnail(), response.body().getData().getFile_name());
                    String updateResponse = galDb.updateGallery(response.body().getData().getId(),
                            response.body().getData().getCreated_by(),
                            response.body().getData().getCreated_by_name(),
                            response.body().getData().getCreated_at(),
                            imgString,
                            "",
                            response.body().getData().getLink(),
                            aId,
                            response.body().getData().getThumbnail(),
                            response.body().getData().getOriginal(),
                            response.body().getData().getAmount());
                    Cursor galleryPending = galDb.getGalleryPending(companyId);
                    if(galleryPending.getCount() > 0) {
                        //getPendingData();
                    } else {
                        if(searchByTitle != null) {
                            search_by = searchByTitle.getText().toString();
                        } else {
                            search_by = "";
                        }
                        Intent intent = getIntent();
                        finish(); // Close the current activity
                        startActivity(intent); // Start the activity again
                    }
                    Log.e("DATA UPDATE RESPONSE",""+updateResponse);
                } else {
                    String token = global.reCallList(shared.getString("refresh_token", ""));
                    if(token.equals("Refresh Token Expires")) {
                        clearShare();
                        startActivity(new Intent(PendingGallery.this, LoginScreen.class));
                    } else {
                        String vToken = global.validToken(token);
                        if(vToken.equals("Authentication token missing") || vToken.equals("Wrong authentication token")) {
                            clearShare();
                            startActivity(new Intent(PendingGallery.this, LoginScreen.class));
                        } else {
                            auth = vToken;
                            updateShare(vToken);
                            getPendingData();
                        }
                    }
                }
            }
            @Override
            public void onFailure(Call<SnapResponse> call, Throwable t) {
                Log.e("RESPONSE",""+t.getMessage());
            }
        });

    }

    public void updateShare(String token) {
        SharedPreferences sharedPreferences = getSharedPreferences("book_keeping", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("access_token", token);
        editor.commit();
    }

    public void clearShare() {
        SharedPreferences loginRef = getSharedPreferences("book_keeping", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = loginRef.edit();
        editor.putString("access_token","");
        editor.putString("fcm_token","");
        editor.putBoolean("first_input",true);
        editor.putInt("userId", 0);
        editor.commit();
        Manager db = new Manager(PendingGallery.this);
        db.removeAllCompany();
    }

    private void fetchData() {
        Cursor gallery = galDb.getGalleryAllPending(companyId, "DESC");
        Log.e("COUNT OF FILES",""+gallery.getCount());
        if(gallery.getCount() > 0) {
            //progressDialog.show();
            progressDialog.show();
            galleryAllItems.clear();
            if(searchByTitle != null) {
                search_by = searchByTitle.getText().toString();
            } else {
                search_by = "";
            }
            syncFilterData(fromDates,toDates,approvalStatusNewSpaceRemove,filterNamesIdSpaceRemove, search_by, "DESC", filterApply);
        }  else {
            if(global.isNetworkAvailable(PendingGallery.this)) {
                loadGallery("Bearer " + auth, "", "", "", "", "", companyId, "0", "", "");
            } else {
                progressDialog.dismiss();
                shimmer_container.stopShimmer();
                shimmer_container.setVisibility(View.GONE);
                noImage.setVisibility(View.VISIBLE);
                galleryImages.setVisibility(View.GONE);
            }
        }
    }
    public JSONObject bindData(Cursor gallery, JSONObject jsonObject, int iuFlag) {
        total_amount = gallery.getDouble(21);
        amount.setText(String.valueOf(total_amount) + " " +company_currency);
        JSONArray jsonArray = new JSONArray();
        try {
            JSONObject dataObject = new JSONObject();
            dataObject.put("id", gallery.getInt(1));
            dataObject.put("file_name", gallery.getString(2));
            dataObject.put("approval_status", gallery.getInt(6));
            dataObject.put("created_user_id", gallery.getInt(7));
            dataObject.put("amount", gallery.getInt(8));
            dataObject.put("title", gallery.getString(9));
            dataObject.put("company_id", gallery.getInt(10));
            dataObject.put("payment_flag", gallery.getInt(11));
            dataObject.put("created_user_name", gallery.getString(12));
            dataObject.put("thumbnail", gallery.getString(13));
            dataObject.put("original", gallery.getString(14));
            dataObject.put("link", gallery.getString(15));
            dataObject.put("thumbnailLink", gallery.getString(17));
            dataObject.put("originalLink", gallery.getString(18));
            dataObject.put("snap_image", gallery.getString(3));
            jsonArray.put(dataObject);
            last_date = gallery.getString(16);
            Log.e("FLAG",""+jsonArray.length());
            if(iuFlag == 1) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    jsonObject.append(last_date, dataObject);
                } else {
                    jsonObject.accumulate(last_date, dataObject);
                }
            } else {
                jsonObject.put(last_date, jsonArray);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }
    public void loadGallery(String verify, String ids, String orderType, String orderField, String month, String year, int company_id,String approvalStatusNewSpaceRemove,String filterNamesIdSpaceRemove,String title) {
        progressDialog.show();
        galleryAllItems.clear();
        Call<JsonObject> gallery = ApiClient.getInstance().getBookKeepingApi().getGallery(verify, ids, orderType, orderField, month, year, company_id,approvalStatusNewSpaceRemove,filterNamesIdSpaceRemove,title);
        gallery.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                System.out.println("response "+response.code());
                if (response.code() == 200) {
                    if (response.body().get("data").isJsonObject() == false) {
                        progressDialog.dismiss();
                        shimmer_container.stopShimmer();
                        shimmer_container.setVisibility(View.GONE);
                        noImage.setVisibility(View.VISIBLE);
                        galleryImages.setVisibility(View.GONE);
                    } else {
                        try {
                            JSONObject dataObject = new JSONObject(String.valueOf(response.body().get("data")));
                            JSONArray dataObj = dataObject.names();
                            for (int i = 0; i < dataObj.length(); i++) { //Year Loop
                                JSONObject yearObject = dataObject.getJSONObject(String.valueOf(dataObj.get(i)));
                                JSONArray monthObj = yearObject.names();
                                for (int j = 0; j < monthObj.length(); j++) { //MONTH Loop
                                    JSONObject monthObject = yearObject.getJSONObject(String.valueOf(monthObj.get(j)));
                                    JSONArray dayObject = monthObject.names();
                                    for (int k = 0; k < dayObject.length(); k++) { //Day Loop
                                        JSONArray ImageObject = monthObject.getJSONArray(String.valueOf(dayObject.get(k)));
                                        String Date;
                                        try {
                                            DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                                            java.util.Date date = inputFormat.parse(dayObject.get(k).toString());
                                            DateFormat outputFormat = new SimpleDateFormat("MMM d, yyyy", Locale.getDefault());
                                            Date = outputFormat.format(date);
                                            Log.e("DATE FORMAT",""+Date);
                                            //headerViewHolder.headerTextView.setText(Date);
                                        } catch (ParseException e) {
                                            throw new RuntimeException(e);
                                        }
                                        GalleryAll galleryAll = new GalleryAll(0, "", "",
                                                false, Date, true, false, 0, 0, "", "","", "", 0, "");
                                        galleryAllItems.add(galleryAll);
                                        for(int l = 0; l < ImageObject.length(); l++) {
                                            galleryAllItems.add(new GalleryAll(ImageObject.getJSONObject(l).getInt("id"),
                                                    ImageObject.getJSONObject(l).getString("thumbnail"),
                                                    ImageObject.getJSONObject(l).getString("original"),
                                                    false,
                                                    dayObject.get(k).toString(),
                                                    false,
                                                    false, ImageObject.getJSONObject(l).getInt("approval_status"),
                                                    ImageObject.getJSONObject(l).getInt("amount"),
                                                    ImageObject.getJSONObject(l).getString("title"),
                                                    ImageObject.getJSONObject(l).getJSONObject("created_user").getString("name"),
                                                    ImageObject.getJSONObject(l).getString("link"), Date, ImageObject.getJSONObject(l).getInt("payment_flag"), ""));
                                            sectionSelections.add(0);

                                        }
                                    }
                                }
                            }
                            if (galleryAllItems.size() > 0) {
                                progressDialog.dismiss();
                                galleryAdapter = new GalleryAdapter(galleryAllItems, PendingGallery.this,  sectionSelections, "live");
                                galleryImages.setAdapter(galleryAdapter);
                                GridLayoutManager gridLayoutManager = new GridLayoutManager(PendingGallery.this, 4);
                                gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                                    @Override
                                    public int getSpanSize(int position) {
                                        int viewType = galleryAdapter.getItemViewType(position);
                                        Log.e("VIEW TYPE", ""+viewType);
                                        if(viewType == 0) {
                                            return 4;
                                        } else {
                                            return 1;
                                        }
                                        //return 1;
                                    }
                                });
                                galleryImages.setLayoutManager(gridLayoutManager);
                                galleryAdapter.notifyDataSetChanged();
                                shimmer_container.stopShimmer();
                                shimmer_container.setVisibility(View.GONE);
                                noImage.setVisibility(View.GONE);
                                galleryImages.setVisibility(View.VISIBLE);
                            } else{
                                shimmer_container.stopShimmer();
                                shimmer_container.setVisibility(View.GONE);
                                noImage.setVisibility(View.VISIBLE);
                                galleryImages.setVisibility(View.GONE);
                            }
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }
                } else {
                    String token = global.reCallList(shared.getString("refresh_token", ""));
                    if(token.equals("Refresh Token Expires")) {
                        clearShare();
                        startActivity(new Intent(PendingGallery.this, LoginScreen.class));
                    } else {
                        String vToken = global.validToken(token);
                        if(vToken.equals("Authentication token missing") || vToken.equals("Wrong authentication token")) {
                            clearShare();
                            startActivity(new Intent(PendingGallery.this, LoginScreen.class));
                        } else {
                            auth = vToken;
                            updateShare(vToken);
                            loadGallery("Bearer " + auth, "", "", "", "", "", companyId, "0", "", "");
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

            }
        });
    }



    public String downloadImage(String imageUrl, String file_name) {
        try {
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();

            InputStream inputStream = connection.getInputStream();
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

            String fileName = file_name+".png";

            File directory = new File(PendingGallery.this.getExternalFilesDir(Environment.DIRECTORY_PICTURES).toString(), "Thumbnail");
            if (!directory.exists()) {
                if (!directory.mkdirs()) {
                    return null;
                }
            }

            File imageFile = new File(directory, fileName);

            // Delete any previous file with the same name
            if (imageFile.exists()) {
                imageFile.delete();
            }

            FileOutputStream outputStream = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            outputStream.flush();
            outputStream.close();

            imgString = imageFile.getAbsolutePath();
        } catch (IOException e) {
            imgString = "";
            Log.e("IMAGE SAVE TAG", "Failed to download the image: " + e.getMessage());
        }
        return  imgString;
    }
    public void storeId(int pos) {
        images.add(pos);
        hashSet.addAll(images);
        images.clear();
        images.addAll(hashSet);
        str_ids = images.toString().substring(1, images.toString().length() - 1);
        str_ids=str_ids.replaceAll("\s+", "");
        Log.e("str_ids",""+str_ids);
    }
    public void removeItem(int position) {
        hashSet.clear();
        int pos = -1;
        for (int i = 0; i < images.size(); i++) {
            if (images.get(i).equals(position)) {
                pos = i;
                break;
            }
        }
        if (pos != -1) {
            images.remove(pos);
            str_ids = images.toString().substring(1, images.toString().length() - 1);
            str_ids=str_ids.replaceAll("\s+", "");
            Log.e("str_ids",""+str_ids);
            //notifyItemRemoved(pos);
            if (images.size() == 0) {
                //  toggleCheckboxVisibility();
            }
        }
    }
    private void getCompany(String auth, int companyId) {
        Call<CompanyResponse> company = ApiClient.getInstance().getBookKeepingApi().companies(auth, String.valueOf(companyId));
        company.enqueue(new Callback<CompanyResponse>() {
            @Override
            public void onResponse(Call<CompanyResponse> call, Response<CompanyResponse> response) {
                Log.e("RESPONSE",""+response.code());
                if(response.code() == 200) {
                    if(response.body().getData().get(0).getName().length() > 12) {
                        companyName.setText(response.body().getData().get(0).getName().substring(0, Math.min(response.body().getData().get(0).getName().length(), 12)) + "...");
                    } else {
                        companyName.setText(response.body().getData().get(0).getName());
                    }
                    company_name = response.body().getData().get(0).getName();
                }
            }

            @Override
            public void onFailure(Call<CompanyResponse> call, Throwable t) {

            }
        });
    }
    private void removeImage(String auth, String ids) {
        progressDialog.show();
        Call<DeleteFile> deleteFile = ApiClient.getInstance().getBookKeepingApi().removeFile(auth, ids);
        deleteFile.enqueue(new Callback<DeleteFile>() {
            @Override
            public void onResponse(Call<DeleteFile> call, Response<DeleteFile> response) {
                Log.e("Remove File",""+response.code());
                if(response.code() == 200) {
                    galDb.removeGalleryPhotos(companyId, "("+ids+")");
                    progressDialog.dismiss();
                    Intent intent = getIntent();
                    finish();
                    startActivity(intent);
                } else {
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        Toast.makeText(PendingGallery.this, ""+jObjError.getString("message"), Toast.LENGTH_SHORT).show();
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    progressDialog.dismiss();
                }
            }
            @Override
            public void onFailure(Call<DeleteFile> call, Throwable t) {
                progressDialog.dismiss();
            }
        });
    }
    private void loadGallery_zip(String auth, String ids, boolean zip) {
        progressDialog.show();
        Log.e("IDS FOR DOWNLOAD", ids);
        Call<JsonObject> gallery = ApiClient.getInstance().getBookKeepingApi().getZip(auth, ids, zip);
        gallery.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                if (response.code() == 200) {
                    progressDialog.dismiss();
                    Toast.makeText(PendingGallery.this, getResources().getString(R.string.receipts_downloading_started), Toast.LENGTH_SHORT).show();
                    zipData = String.valueOf(response.body().get("zip_url")).replace("\"", "");
                    zipFileName = "receipts.zip";
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

        DownloadManager downloadManager = (DownloadManager)PendingGallery.this.getSystemService(Context.DOWNLOAD_SERVICE);
        downloadManager.enqueue(request);
    }
    @SuppressLint("MissingInflatedId")
    private void openSort() {
        bottomsheetdialog = new BottomSheetDialog(PendingGallery.this, R.style.BottomSheetStyle);
        View sort = LayoutInflater.from(PendingGallery.this).inflate(R.layout.sort_bottom_sheet, null);
        radioButton = sort.findViewById(R.id.radioButton);
        radioButtonDesc = sort.findViewById(R.id.radioButtonDesc);
        radioButtonDesc.setChecked(true);
        if (orderBy.equals("ASC")) {
            radioButton.setChecked(true);
            radioButtonDesc.setChecked(false);
        } else {
            radioButtonDesc.setChecked(true);
            radioButton.setChecked(false);
        }

        radioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                radioButtonDesc.setChecked(false);
                if (radioButton.isChecked() == true) {
                    orderBy = "ASC";
                    if(searchByTitle != null) {
                        search_by = searchByTitle.getText().toString();
                    } else {
                        search_by = "";
                    }
                    syncFilterData(fromDates,toDates,approvalStatusNewSpaceRemove,filterNamesIdSpaceRemove, search_by, orderBy, filterApply);
                    bottomsheetdialog.dismiss();
                }

            }
        });

        radioButtonDesc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                radioButton.setChecked(false);
                if (radioButtonDesc.isChecked() == true) {
                    orderBy = "DESC";
                    if(searchByTitle != null) {
                        search_by = searchByTitle.getText().toString();
                    } else {
                        search_by = "";
                    }
                    syncFilterData(fromDates,toDates,approvalStatusNewSpaceRemove,filterNamesIdSpaceRemove, search_by, orderBy,filterApply);
                    bottomsheetdialog.dismiss();
                }

            }
        });
        bottomsheetdialog.setContentView(sort);
        bottomsheetdialog.show();
    }
    @SuppressLint("MissingInflatedId")
    private void openFilter() {
        bottomsheetdialog = new BottomSheetDialog(PendingGallery.this, R.style.BottomSheetStyle);
        View filter = LayoutInflater.from(PendingGallery.this).inflate(R.layout.filter_bottomsheet, null);
        fromDate = filter.findViewById(R.id.fromDate);
        toDate = filter.findViewById(R.id.toDate);
        filterByDate = filter.findViewById(R.id.filterByDate);
        date_range = filter.findViewById(R.id.date_range);
        approval_status = filter.findViewById(R.id.approval_status);
        upload_by = filter.findViewById(R.id.upload_by);
        date_filter_field = filter.findViewById(R.id.date_filter_field);
        status_filter_field = filter.findViewById(R.id.status_filter_field);
        user_filter_field = filter.findViewById(R.id.user_filter_field);
        date_range_text = filter.findViewById(R.id.date_range_text);
        status_text = filter.findViewById(R.id.status_text);
        upload_by_text = filter.findViewById(R.id.upload_by_text);
        user_list = filter.findViewById(R.id.user_list);
        check_pending = filter.findViewById(R.id.check_pending);
        check_approve = filter.findViewById(R.id.check_approve);
        check_super_approve = filter.findViewById(R.id.check_super_approve);
        check_reject = filter.findViewById(R.id.check_reject);
        reloadId = filter.findViewById(R.id.reloadId);
        reset_filter = filter.findViewById(R.id.reset_filter);
        searchByTitle = filter.findViewById(R.id.searchBy);

        if(permission == 5) {
            upload_by.setVisibility(View.GONE);
        } else {
            upload_by.setVisibility(View.VISIBLE);
        }

        fromDate.setText(fromDates);
        toDate.setText(toDates);

        reset_filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                statusNamesValues.clear();
                statusNames.clear();
                filterNamesIds.clear();
                filterNames.clear();
                // bottomsheetdialog.dismiss();
                check_pending.setChecked(false);
                check_approve.setChecked(false);
                check_super_approve.setChecked(false);
                //
                filterUserAdapter = new FilterUserAdapter(filterUsers, PendingGallery.this);
                user_list.setAdapter(filterUserAdapter);
                user_list.setLayoutManager(new LinearLayoutManager(PendingGallery.this));
                filterUserAdapter.notifyDataSetChanged();
                //
                fromDate.setText("");
                toDate.setText("");
                fromDates = "";
                toDates = "";
                orderBy = "DESC";
                filterApply = 0;
                approvalStatusNewSpaceRemove = "(0)";
                filterNamesIdSpaceRemove = "()";
                if(searchByTitle != null) {
                    search_by = searchByTitle.getText().toString();
                } else {
                    search_by = "";
                }
                syncFilterData(fromDates,toDates,approvalStatusNewSpaceRemove,filterNamesIdSpaceRemove, search_by, orderBy, filterApply);
                bottomsheetdialog.dismiss();
            }
        });
        check_pending.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Handle the checkbox state change here
                if (isChecked) {
                    // The checkbox is checked
                    statusNames.add("Pending");
                    statusNamesValues.add("0");
                } else {
                    // The checkbox is unchecked
                    removeStatus("Pending");
                    removeStatusCode("0");
                }
            }
        });
        check_approve.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Handle the checkbox state change here
                if (isChecked) {
                    // The checkbox is checked
                    statusNames.add("Approve");
                    statusNamesValues.add("1");
                } else {
                    // The checkbox is unchecked
                    removeStatus("Approve");
                    removeStatusCode("1");
                }
            }
        });
        check_super_approve.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Handle the checkbox state change here
                if (isChecked) {
                    // The checkbox is checked
                    statusNames.add("Super Approve");
                    statusNamesValues.add("2");
                } else {
                    // The checkbox is unchecked
                    removeStatus("Super Approve");
                    removeStatusCode("2");
                }
            }
        });
        check_reject.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Handle the checkbox state change here
                if (isChecked) {
                    // The checkbox is checked
                    statusNames.add("Reject");
                    statusNamesValues.add("3");
                } else {
                    // The checkbox is unchecked
                    removeStatus("Reject");
                    removeStatusCode("3");
                }
            }
        });

        for (int i = 0; i < statusNames.size(); i++) {
            String statusView = statusNames.get(i);
            if ("Pending".equals(statusView)){
                check_pending.setChecked(true);
            }
            if ("Approve".equals(statusView)){
                check_approve.setChecked(true);
            }
            if ("Super Approve".equals(statusView)){
                check_super_approve.setChecked(true);
            }
            if ("Reject".equals(statusView)){
                check_reject.setChecked(true);
            }
            HashSet<String> uniqueSet = new HashSet<>();
            for (String element : statusNamesValues) {
                uniqueSet.add(element);
            }
            statusNamesValues.clear();
            statusNamesValues.addAll(uniqueSet);
            System.out.println("statusNamesValues "+statusNamesValues);
        }

        fromDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // showPopup(getActivity());
                final Calendar c = Calendar.getInstance();
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);

                Calendar maxDate = Calendar.getInstance();



                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        // on below line we are passing context.
                        PendingGallery.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                // on below line we are setting date to our edit text.
                                if ((monthOfYear + 1) > 9) {
                                    if (dayOfMonth > 9) {
                                        fromDate.setText(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
                                        fromDates = fromDate.getText().toString();
                                        System.out.println("from_date "+"0 "+fromDate.getText().toString());

                                    } else {
                                        fromDate.setText(year + "-" + (monthOfYear + 1) + "-" + "0" + dayOfMonth);
                                        fromDates = fromDate.getText().toString();
                                        System.out.println("from_date "+"1 "+fromDate.getText().toString());
                                    }
                                } else {
                                    if (dayOfMonth > 9) {
                                        fromDate.setText(year + "-0" + (monthOfYear + 1) + "-" + dayOfMonth);
                                        fromDates = fromDate.getText().toString();
                                        System.out.println("from_date "+"2 "+fromDate.getText().toString());

                                    } else {
                                        fromDate.setText(year + "-0" + (monthOfYear + 1) + "-" + "0" + dayOfMonth);
                                        fromDates = fromDate.getText().toString();
                                        System.out.println("from_date "+"3 "+fromDate.getText().toString());

                                    }
                                }

                            }
                        },
                        // on below line we are passing year,
                        // month and day for selected date in our date picker.
                        year, month, day);
                // at last we are calling show to
                datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
                // display our date picker dialog.
                datePickerDialog.show();
            }
        });

        toDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        // on below line we are passing context.
                        PendingGallery.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                // on below line we are setting date to our edit text.
                                if ((monthOfYear + 1) > 9) {
                                    if (dayOfMonth > 9) {
                                        toDate.setText(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
                                        toDates = toDate.getText().toString();
                                    } else {
                                        toDate.setText(year + "-" + (monthOfYear + 1) + "-" + "0" + dayOfMonth);
                                        toDates = toDate.getText().toString();
                                    }

                                } else {
                                    if (dayOfMonth > 9) {
                                        toDate.setText(year + "-0" + (monthOfYear + 1) + "-" + dayOfMonth);
                                        toDates = toDate.getText().toString();
                                    } else {
                                        toDate.setText(year + "-0" + (monthOfYear + 1) + "-" + "0" + dayOfMonth);
                                        toDates = toDate.getText().toString();
                                    }
                                }
                            }
                        },
                        // on below line we are passing year,
                        // month and day for selected date in our date picker.
                        year, month, day);
                // at last we are calling show to
                c.add(Calendar.DAY_OF_MONTH, 1);
                datePickerDialog.getDatePicker().setMaxDate(c.getTimeInMillis());
                // display our date picker dialog.
                datePickerDialog.show();
            }
        });


        filterByDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String filterNamesId = String.valueOf(filterNamesIds);
                String filterNamesIdOld = filterNamesId.replace("[", "(");
                String filterNamesIdNew = filterNamesIdOld.replace("]", ")");
                filterNamesIdSpaceRemove = filterNamesIdNew.replace(" ", "");
                System.out.println("response "+approvalStatusNewSpaceRemove+"  "+filterNamesIdSpaceRemove);
                bottomsheetdialog.dismiss();
                filterApply = 1;
                //Toast.makeText(ReceiptGallery.this, "WIP", Toast.LENGTH_SHORT).show();
                syncFilterData(fromDate.getText().toString(),toDate.getText().toString(),approvalStatusNewSpaceRemove,filterNamesIdSpaceRemove, searchByTitle.getText().toString(), "DESC", filterApply);
            }
        });


        date_range.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSide("dateFilter");
            }
        });

        approval_status.setVisibility(View.GONE);
        approval_status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSide("status");
            }
        });

        upload_by.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSide("upload");
//              callUser("Bearer "+auth);
                SubUser(companyId);
            }
        });


        bottomsheetdialog.setContentView(filter);
//        bottomSheetBehavior = BottomSheetBehavior.from((View)filter.getParent());
//        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_SETTLING);

        bottomsheetdialog.show();
    }

    private void syncFilterData(String fromDates,String toDates,String approvalStatus,String uploadById, String searchBy,String orderBy, int filterApply) {
        String apStatus = "";
        String uploadBy = "";
        if(approvalStatus.equals("()")) {
            apStatus = "";
        } else {
            apStatus = approvalStatus;
        }

        if(uploadById.equals("()")) {
            uploadBy = "";
        } else {
            uploadBy = uploadById;
        }
        Cursor filterGallery = galDb.getFilterGallery(fromDates,toDates,apStatus,uploadBy, searchBy,orderBy, companyId);
        count.setText(String.valueOf(filterGallery.getCount()));
        if(filterApply == 1) {
            counter_layout.setVisibility(View.VISIBLE);
        } else {
            counter_layout.setVisibility(View.GONE);
        }
        if (filterGallery.getCount() > 0){
            galleryAllItems.clear();
            JSONObject jsonObject = new JSONObject();
            JSONObject finalObject = new JSONObject();
            while (filterGallery.moveToNext()) {
                Log.e("IMAGES",""+filterGallery.getString(16));
                String file_date = filterGallery.getString(16);
                int iuFlag = (!file_date.equals(last_date)) ? 0 : 1;
                finalObject = bindData(filterGallery, jsonObject,iuFlag);
                last_date = file_date;
            }
            Log.e("FINAL OBJECT"," "+finalObject.toString());
            JSONArray dayObject = finalObject.names();
            Log.e("Data Date",""+dayObject);
            for (int i = 0; i < dayObject.length(); i++) {
                String Dates;
                try {
                    DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    Date date = null;
                    try {
                        date = inputFormat.parse(dayObject.get(i).toString());
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                    DateFormat outputFormat = new SimpleDateFormat("MMM d, yyyy", Locale.getDefault());
                    Dates = outputFormat.format(date);
                    //headerViewHolder.headerTextView.setText(Date);
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
                GalleryAll galleryAll = new GalleryAll(0, "", "",
                        false, Dates, true, false, 0, 0, "", "","", "", 0,"");
                galleryAllItems.add(galleryAll);
                try {
                    JSONArray ImageObject = finalObject.getJSONArray(String.valueOf(dayObject.get(i)));
                    Log.e("Data Date Image",""+ImageObject.getJSONObject(0).getInt("id"));
                    for(int l = 0; l < ImageObject.length(); l++) {

                        galleryAllItems.add(new GalleryAll(ImageObject.getJSONObject(l).getInt("id"),
                                ImageObject.getJSONObject(l).getString("thumbnail"),
                                ImageObject.getJSONObject(l).getString("original"),
                                false,
                                dayObject.get(i).toString(),
                                false,
                                false, ImageObject.getJSONObject(l).getInt("approval_status"),
                                ImageObject.getJSONObject(l).getInt("amount"),
                                ImageObject.getJSONObject(l).getString("title"),
                                ImageObject.getJSONObject(l).getString("created_user_name"),
                                ImageObject.getJSONObject(l).getString("link"),
                                Dates,
                                ImageObject.getJSONObject(l).getInt("payment_flag"),
                                ImageObject.getJSONObject(l).getString("snap_image")));
                        sectionSelections.add(0);
                    }

                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
            if (galleryAllItems.size() > 0) {
                progressDialog.dismiss();
                galleryAdapter = new GalleryAdapter(galleryAllItems, PendingGallery.this,  sectionSelections, "loacl");
                galleryImages.setAdapter(galleryAdapter);
                GridLayoutManager gridLayoutManager = new GridLayoutManager(PendingGallery.this, 4);
                gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                    @Override
                    public int getSpanSize(int position) {
                        int viewType = galleryAdapter.getItemViewType(position);
                        Log.e("VIEW TYPE", ""+viewType);
                        if(viewType == 0) {
                            return 4;
                        } else {
                            return 1;
                        }
                        //return 1;
                    }
                });
                galleryImages.setLayoutManager(gridLayoutManager);
                galleryAdapter.notifyDataSetChanged();
                shimmer_container.stopShimmer();
                noImage.setVisibility(View.GONE);
                shimmer_container.setVisibility(View.GONE);
                galleryImages.setVisibility(View.VISIBLE);
            }
        } else {
            progressDialog.dismiss();
            shimmer_container.setVisibility(View.GONE);
            noImage.setVisibility(View.VISIBLE);
            galleryImages.setVisibility(View.GONE);
        }
    }

    public void removeStatus(String nameStatus){
        List<String> dl = new ArrayList<String>();
        for (int i = 0; i < statusNames.size(); i++) {
            String a = statusNames.get(i);
            System.out.println(a);
            if(a.equals(nameStatus)){
                dl.add(a);
            }}
        statusNames.removeAll(dl);
        System.out.println("RemoveValue"+statusNames);
    }
    public void removeStatusCode(String statusCode){
        List<String> dl1 = new ArrayList<String>();
        for (int j = 0; j < statusNamesValues.size(); j++) {
            String a1 = statusNamesValues.get(j);
            System.out.println(a1);
            if(a1.equals(statusCode)){
                dl1.add(a1);
            }}
        statusNamesValues.removeAll(dl1);
        System.out.println("RemoveValue"+statusNamesValues);
    }
    private void openSide(String mode) {
        if(mode.equals("dateFilter")) {
            date_filter_field.setVisibility(View.VISIBLE);
            status_filter_field.setVisibility(View.GONE);
            user_filter_field.setVisibility(View.GONE);
            //Change Layout Color
            date_range.setBackgroundColor(Color.parseColor("#E1E1E1"));
            approval_status.setBackgroundColor(Color.parseColor("#FFFFFF"));
            upload_by.setBackgroundColor(Color.parseColor("#FFFFFF"));
            //Change Text Color
            date_range_text.setTextColor(Color.parseColor("#FF000000"));
            status_text.setTextColor(Color.parseColor("#098CFF"));
            upload_by_text.setTextColor(Color.parseColor("#098CFF"));
        } else if(mode.equals("status")) {
            date_filter_field.setVisibility(View.GONE);
            status_filter_field.setVisibility(View.VISIBLE);
            user_filter_field.setVisibility(View.GONE);
            //Change Layout Color
            approval_status.setBackgroundColor(Color.parseColor("#FFFFFF"));
            date_range.setBackgroundColor(Color.parseColor("#E1E1E1"));
            upload_by.setBackgroundColor(Color.parseColor("#E1E1E1"));
            //Change Text Color
            status_text.setTextColor(Color.parseColor("#098CFF"));
            date_range_text.setTextColor(Color.parseColor("#FF000000"));
            upload_by_text.setTextColor(Color.parseColor("#FF000000"));
        } else if (mode.equals("upload")) {
            date_filter_field.setVisibility(View.GONE);
            status_filter_field.setVisibility(View.GONE);
            user_filter_field.setVisibility(View.VISIBLE);
            //Change Layout Color
            upload_by.setBackgroundColor(Color.parseColor("#E1E1E1"));
            approval_status.setBackgroundColor(Color.parseColor("#FFFFFF"));
            date_range.setBackgroundColor(Color.parseColor("#FFFFFF"));
            //Change Text Color
            upload_by_text.setTextColor(Color.parseColor("#FF000000"));
            status_text.setTextColor(Color.parseColor("#098CFF"));
            date_range_text.setTextColor(Color.parseColor("#098CFF"));
        } else {
            date_filter_field.setVisibility(View.VISIBLE);
            status_filter_field.setVisibility(View.GONE);
            user_filter_field.setVisibility(View.GONE);
            date_range.setBackgroundColor(Color.parseColor("#FFFFFF"));
            approval_status.setBackgroundColor(Color.parseColor("#E1E1E1"));
            upload_by.setBackgroundColor(Color.parseColor("#E1E1E1"));
            //Change Text Color
            date_range_text.setTextColor(Color.parseColor("#098CFF"));
            status_text.setTextColor(Color.parseColor("#FF000000"));
            upload_by_text.setTextColor(Color.parseColor("#FF000000"));
        }
    }
    public void redirect(){
        if(galleryAllItems.size() > 0) {
            if(galleryAdapter.checkBoxVisible == true) {
                galleryAdapter.updateCheckboxVisibility(false);
            } else {
                Intent businessDashboard = new Intent(PendingGallery.this, BusinessDashboards.class);
                businessDashboard.putExtra("companyId",companyId);
                businessDashboard.putExtra("companyPermission", permission);
                startActivity(businessDashboard);
            }
        } else {
            Intent businessDashboard = new Intent(PendingGallery.this, BusinessDashboards.class);
            businessDashboard.putExtra("companyId",companyId);
            businessDashboard.putExtra("companyPermission", permission);
            startActivity(businessDashboard);

        }
    }
    @Override
    public void onBackPressed() {
        SharedPrefManager.getInstance(getApplicationContext()).clearData();
        redirect();
    }
    private void showPopUp(View v) {
        PopupMenu popupMenu = new PopupMenu(PendingGallery.this, v);
        popupMenu.getMenuInflater().inflate(R.menu.navigation_item, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();
                if(id == R.id.companyList) {
                    // startActivity(new Intent(ReceiptGallery.this, Companies.class));
                    Intent company = new Intent(PendingGallery.this, Companies.class);
                    company.putExtra("enable","0");
                    startActivity(company);
                } else if(id == R.id.profileDtl) {
                    startActivity(new Intent(PendingGallery.this, Profile.class));
                } else if(id == R.id.changePassword) {
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
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(PendingGallery.this);
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
                galDb.removeAllCompany();
                SharedPreferences loginRef = getSharedPreferences("book_keeping", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = loginRef.edit();
                editor.putString("access_token","");
                editor.putString("fcm_token","");
                editor.putBoolean("first_input",true);
                editor.putInt("userId", 0);
                editor.commit();
                //ClearAppData.clearApplicationData(PendingGallery.this, getPackageName());
                startActivity(new Intent(PendingGallery.this, LoginScreen.class));

            }
        });
        android.app.AlertDialog alert = builder.create();
        alert.show();
    }
    private void log_out() {
        progressDialog.show();
        String mac_address = global.getDeviceToken(PendingGallery.this);
        Call<LogoutResponse> logOut = ApiClient.getInstance().getBookKeepingApi().logOut("Bearer "+auth, fcm_token, mac_address);
        logOut.enqueue(new Callback<LogoutResponse>() {
            @Override
            public void onResponse(Call<LogoutResponse> call, Response<LogoutResponse> response) {
                Log.e("LOG OUT RESPONSE",""+response.code());
                if(response.code() == 200) {
                    progressDialog.dismiss();
                } else {
                    progressDialog.dismiss();
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        Toast.makeText(PendingGallery.this, ""+jObjError.getString("message"), Toast.LENGTH_SHORT).show();
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
        sheetDialog = new BottomSheetDialog(PendingGallery.this, R.style.BottomSheetStyle);
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
                    if(global.isNetworkAvailable(PendingGallery.this)) {
                        try {
                            newEncrypt = cryptLib.encryptPlainTextWithRandomIV(password.getText().toString(), ApiClient.getInstance().getKey());
                            oldEncrypt = cryptLib.encryptPlainTextWithRandomIV(oldPassword.getText().toString(), ApiClient.getInstance().getKey());
                            passwordUpdate(newEncrypt, oldEncrypt);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    } else {
                        String please_Check_Your_Internet_Connection = getResources().getString(R.string.please_Check_Your_Internet_Connection);

                        Toast.makeText(PendingGallery.this, please_Check_Your_Internet_Connection, Toast.LENGTH_SHORT).show();
                    }

                } else {
                    eye_btn_cnf.setVisibility(View.GONE);
                    cnfPassword.setError(getResources().getString(R.string.confirm_password_mismatch));
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
                    progressDialog.dismiss();
                    Toast.makeText(PendingGallery.this, getResources().getString(R.string.password_change_successfully), Toast.LENGTH_SHORT).show();
                    sheetDialog.dismiss();
                } else {
                    progressDialog.dismiss();
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        Toast.makeText(PendingGallery.this, ""+jObjError.getString("message"), Toast.LENGTH_SHORT).show();
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            @Override
            public void onFailure(Call<ChangePassword> call, Throwable t) {

            }
        });
    }
    public void SubUser(int companyId) {
        filterUsers.clear();
        Cursor getSubUser = galDb.fetchSubUser(companyId);
        if(getSubUser.getCount() > 0) {
            while (getSubUser.moveToNext()) {
                filterUsers.add(new FilterUser(getSubUser.getInt(1),
                        getSubUser.getString(2), false,
                        getSubUser.getString(3)
                ));
                if(filterUsers.size() > 0) {
                    filterUserAdapter = new FilterUserAdapter(filterUsers, PendingGallery.this);
                    user_list.setAdapter(filterUserAdapter);
                    user_list.setLayoutManager(new LinearLayoutManager(PendingGallery.this));
                    filterUserAdapter.notifyDataSetChanged();
                }
                Log.e("USERS LIST VIEW",""+getSubUser.getInt(1));
            }

        }
    }
    /*Image Binding in Section Header Recycler View*/
    public class GalleryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        List<GalleryAll> gallery;
        Context context;

        private List<Integer> selectedItems;

        private List<Integer> sectionSelections;
        private boolean areCheckboxesVisible = false ;

        private SparseBooleanArray checkedItems;

        AlertDialog alertDialog;

        Gallery item;

        int id = 0;

        private static final int VIEW_TYPE_HEADER = 0;
        private static final int VIEW_TYPE_ITEM = 1;
        int selectionCount = 0;

        private RecyclerView mRecyclerView;

        Handler handler = new Handler();
        int delayMillis = 100;

        Dialog dialog;

        public boolean checkBoxVisible = false;

        int windowWidth;
        int screenCenter;

        LinearLayout loader_view, action_layout;

        ProgBar myProgressBar;

        ImageView dislikeBtn, superBtn, likeBtn;
        String mode;

        List<GalleryAll> filterGallery;

        public GalleryAdapter(List<GalleryAll> gallery, Context context, List<Integer> sectionSelections, String mode){
            this.gallery = gallery;
            this.context = context;
            selectedItems = new ArrayList<>();
            this.sectionSelections = sectionSelections;
            this.checkedItems = new SparseBooleanArray();
            this.mode = mode;
            this.filterGallery = gallery;

        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            if (viewType == VIEW_TYPE_HEADER) {
                View headerView = inflater.inflate(R.layout.gallery_item, parent, false);
                return new GalleryAdapter.HeaderViewHolder(headerView);
            } else {
                View itemView = inflater.inflate(R.layout.gallery_image_item, parent, false);
                return new GalleryAdapter.ItemViewHolder(itemView);
            }
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
            if (holder instanceof GalleryAdapter.HeaderViewHolder) {
                GalleryAdapter.HeaderViewHolder headerViewHolder = (GalleryAdapter.HeaderViewHolder) holder;
                GalleryAll headerItem = gallery.get(position);


                ((GalleryAdapter.HeaderViewHolder) holder).bind(headerItem);

                headerViewHolder.headerTextView.setText(headerItem.getDate());
                headerViewHolder.checkMonthAlt.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    clickItem(position, isChecked);
                    // notifyDataSetChanged();

                });
            } else if (holder instanceof GalleryAdapter.ItemViewHolder) {

                GalleryAdapter.ItemViewHolder itemViewHolder = (GalleryAdapter.ItemViewHolder) holder;
                GalleryAll childItem = gallery.get(position);
                Log.e("TABLE ID",""+childItem.getId());
                if(mode.equals("live")) {
                    Glide.with(context)
                            .load(childItem.getImageData())
                            .apply(new RequestOptions()
                                    .placeholder(R.drawable.img_loader)
                                    .error(R.drawable.img_loader))
                            .into(itemViewHolder.galleryItem);
                } else {
                    if(childItem.getId() != 0) {
                        Log.e("Image Byte img1", ""+childItem.getImageData());
                        Glide.with(context)
                                .load(new File(childItem.getImageData()))
                                .into(itemViewHolder.galleryItem);
                    } else {
                        Bitmap img = decodeImage(childItem.getSnap_image());
                        itemViewHolder.galleryItem.setImageBitmap(img);
                    }

                }
                ((GalleryAdapter.ItemViewHolder) holder).bind(childItem);
                itemViewHolder.check.setChecked(childItem.isSelected());
                if(childItem.getApproval_status() == 0) {
                    if(childItem.getId() != 0) {
                        itemViewHolder.status_indicator.setImageResource(R.drawable.upload_pening);
                    } else {
                        itemViewHolder.status_indicator.setImageResource(R.drawable.access_time);
                    }
                } else if(childItem.getApproval_status() == 1) {
                    itemViewHolder.status_indicator.setImageResource(R.drawable.approve_tick);
                } else if(childItem.getApproval_status() == 2) {
                    itemViewHolder.status_indicator.setImageResource(R.drawable.ic_star_turquoise_24dp);
                } else {
                    itemViewHolder.status_indicator.setImageResource(R.drawable.skip);
                }
                itemViewHolder.check.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    clickItem(position, isChecked);
                    checkBoxVisible = true;
                });

                itemViewHolder.galleryItem.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        clickItem(position, true);
                        checkBoxVisible = true;
                        return true;
                    }
                });

                itemViewHolder.galleryItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.e("IMAGE ID",""+childItem.getId());
                        if(childItem.getId() == 0) {
                            Toast.makeText(context, getResources().getString(R.string.file_not_Uploaded_yet), Toast.LENGTH_SHORT).show();
                        } else {
                            if (pressCounter == 0) {
                                Intent intent = new Intent(getApplicationContext(), TinderSliderActivity.class);
                                intent.putExtra("companyId", companyId);
                                intent.putExtra("permission", permission);
                                intent.putExtra("clickId", childItem.getId());
                                intent.putExtra("file_status", "(0)");
                                intent.putExtra("showStatus", 0);
                                intent.putExtra("imageUrl", "");
                                startActivity(intent);
                            }
                        }
                    }
                });
            } else {
                Toast.makeText(context, "WRONG", Toast.LENGTH_SHORT).show();
            }
        }

        private Bitmap decodeImage(String snap_image) {
            final String pureBase64Encoded = snap_image.substring(snap_image.indexOf(",")  + 1);
            final byte[] decodedBytes = Base64.decode(pureBase64Encoded, Base64.DEFAULT);
            Bitmap decodedImage = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
            Log.e("Image Byte", ""+decodedBytes);
            return decodedImage;
        }

        @SuppressLint("MissingInflatedId")
        private void viewImage(String imageData, String original, View v, int imageId, int approval_status) {

            windowWidth = getWindowManager().getDefaultDisplay().getWidth();
            screenCenter = windowWidth / 2;
            ViewGroup.LayoutParams layoutTvParams = new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            View dialogView = LayoutInflater.from(v.getRootView().getContext()).inflate(R.layout.view_gallery_image, null);
            dialog = new Dialog(context, android.R.style.Theme_DeviceDefault_Light_NoActionBar_Fullscreen);
            dialog.setContentView(dialogView);
            ZoomageView myZoomImageView = dialogView.findViewById(R.id.myZoomImageView);
            TextView imageUrl = dialogView.findViewById(R.id.imageUrl);
            loader_view = dialogView.findViewById(R.id.loader_view);
            myProgressBar = dialogView.findViewById(R.id.myProgressBar);
            myProgressBar.setTextMsg("");
            copyImage = dialogView.findViewById(R.id.copyImage);
            action_layout = dialogView.findViewById(R.id.action_layout);
            dislikeBtn = dialogView.findViewById(R.id.dislikeBtn);
            superBtn = dialogView.findViewById(R.id.superBtn);
            likeBtn = dialogView.findViewById(R.id.likeBtn);



            dislikeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    approval(3, imageId);
                }
            });

            superBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    approval(2, imageId);
                }
            });

            likeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    approval(1, imageId);
                }
            });

            Bitmap decodedByte = getBitmapFromURL(imageData);
            myZoomImageView.setImageBitmap(decodedByte);
            copyImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ClipboardUtil.copyToClipboard(context, copyImageUrl);
                }
            });

            Call<JsonObject> galleryResponseCall = ApiClient.getInstance().getBookKeepingApi().singleImage("Bearer "+auth, imageId);
            galleryResponseCall.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    try {
                        JSONObject dataObject = new JSONObject(String.valueOf(response.body().get("data")));
                        JSONArray dataObj = dataObject.names();
                        Log.e("DATA OBJ",""+dataObj);
                        for (int i = 0; i < dataObj.length(); i++) {
                            JSONObject yearObject = dataObject.getJSONObject(String.valueOf(dataObj.get(i)));
                            JSONArray monthObj = yearObject.names();
                            for (int j = 0; j < monthObj.length(); j++) {
                                JSONObject monthObject = yearObject.getJSONObject(String.valueOf(monthObj.get(j)));
                                JSONArray dayObject = monthObject.names();

                                for (int k = 0; k < dayObject.length(); k++) {
                                    JSONArray ImageObject = monthObject.getJSONArray(String.valueOf(dayObject.get(k)));
                                    String Date;
                                    try {
                                        DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                                        java.util.Date date = inputFormat.parse(dayObject.get(k).toString());
                                        DateFormat outputFormat = new SimpleDateFormat("MMM d, yyyy", Locale.getDefault());
                                        Date = outputFormat.format(date);
                                        //headerViewHolder.headerTextView.setText(Date);
                                    } catch (ParseException e) {
                                        throw new RuntimeException(e);
                                    }

                                    for(int l = 0; l < ImageObject.length(); l++) {
                                        Bitmap decodedByte = getBitmapFromURL(ImageObject.getJSONObject(l).getString("original"));
                                        if(ImageObject.getJSONObject(l).getString("original").length() > 35) {
                                            imageUrl.setText(ImageObject.getJSONObject(l).getString("link").substring(0, Math.min(ImageObject.getJSONObject(l).getString("original").length(), 35)) + "...");
                                        } else {
                                            imageUrl.setText(ImageObject.getJSONObject(l).getString("link"));
                                        }
                                        copyImageUrl = ImageObject.getJSONObject(l).getString("link");
                                        Log.e("IMAGE URL",""+copyImageUrl);
                                        myZoomImageView.setImageBitmap(decodedByte);
                                        pressCounter = 0;
                                        Log.e("PRESS COUNTER",""+ImageObject.getJSONObject(l).getString("original"));
                                        loader_view.setVisibility(View.GONE);
                                        if(permission == 7) {
                                            if(approval_status == 0) {
                                                action_layout.setVisibility(View.VISIBLE);
                                            } else {
                                                action_layout.setVisibility(View.GONE);
                                            }
                                        } else {
                                            action_layout.setVisibility(View.GONE);
                                        }
                                    }
                                }
                            }
                        }
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {

                }
            });
            //imageView.setImageBitmap(img);
            dialog.show();
        }

        private void approval(int approval_status, int file_id) {
            loader_view.setVisibility(View.VISIBLE);
            FileUpdateRequest fileUpdateRequest = new FileUpdateRequest(approval_status, file_id,"");
            Call<FileUpdateResponse> fileUpdate = ApiClient.getInstance().getBookKeepingApi().updateFile("Bearer "+auth, fileUpdateRequest);
            fileUpdate.enqueue(new Callback<FileUpdateResponse>() {
                @Override
                public void onResponse(Call<FileUpdateResponse> call, Response<FileUpdateResponse> response) {
                    Log.e("File UPDATE Response",""+response.code());
                    if(response.code() == 200) {
                        loader_view.setVisibility(View.GONE);
                        dialog.dismiss();
                        loadGallery("Bearer " + auth, "", "", "", "", "", companyId,"0","","");
                    } else {
                        loader_view.setVisibility(View.GONE);
                        Toast.makeText(context, "Try again later", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<FileUpdateResponse> call, Throwable t) {

                }
            });
        }

        public static Bitmap getBitmapFromURL(String src) {
            try {
                URL url = new URL(src);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(input);
                return myBitmap;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        } // Author: silentnuke

        public void clickItem(int position, boolean isChecked) {
            int headerPosition = findHeaderPosition(position);
            Log.e("Click POS",""+position);
            Log.e("Click Header POS",""+headerPosition);
            if(gallery.get(position).isHeader()) {
                List<perfect.book.keeping.model.Gallery> gal = getItemsInSection(position);
                Log.e("Click CHECK STATS XYZ",""+isChecked);
                if(gallery.get(position).isHeaderSelected()) {
                    for (perfect.book.keeping.model.Gallery imgGallery : gal) {
                        gallery.get(imgGallery.getPosition()).setSelected(isChecked);
                        gallery.get(position).setHeaderSelected(isChecked);
                        removeItem(imgGallery.getId());
                        GalleryAdapter.HeaderViewHolder headerViewHolder = (GalleryAdapter.HeaderViewHolder) galleryImages.findViewHolderForAdapterPosition(position);
                        if(headerViewHolder != null) {
                            headerViewHolder.checkMonthAlt.setVisibility(View.GONE);
                        }
                        GalleryAdapter.ItemViewHolder itemViewHolder = (GalleryAdapter.ItemViewHolder) galleryImages.findViewHolderForAdapterPosition(imgGallery.getPosition());
                        if (itemViewHolder != null) {
                            itemViewHolder.check.setVisibility(View.GONE);
                            itemViewHolder.check.setChecked(gallery.get(imgGallery.getPosition()).isSelected());
                            checkBoxVisible = false;
                        }
                    }

                    Log.e("IMAGE POS ALT +VE", "" + gallery.get(position).isHeaderSelected());
                }
                else {
                    for (perfect.book.keeping.model.Gallery imgGallery : gal) {
                        Log.e("IMAGE POS", "" + imgGallery.getPosition());
                        gallery.get(imgGallery.getPosition()).setSelected(isChecked);
                        storeId(imgGallery.getId());

                        Log.e("IMAGE POS", "" + gallery.get(position).isHeaderSelected());
                        GalleryAdapter.ItemViewHolder itemViewHolder = (GalleryAdapter.ItemViewHolder) galleryImages.findViewHolderForAdapterPosition(imgGallery.getPosition());
                        if (itemViewHolder != null) {
                            itemViewHolder.check.setChecked(gallery.get(imgGallery.getPosition()).isSelected());
                            //itemViewHolder.check.setVisibility(View.VISIBLE);
                        }
                    }
                }
                Log.e("IMAGE POS ALT", "" + gallery.get(position).isHeaderSelected());
            }
            else {
                if(isChecked == true) {
                    storeId(gallery.get(position).getId());
                } else {
                    removeItem(gallery.get(position).getId());
                }
                Log.e("ReceiptGallery Image ID",""+ position);
                Log.e("ReceiptGallery Image STATE",""+ gallery.get(position).isSelected());
                gallery.get(position).setSelected(isChecked);
                List<Gallery> gal = getItemsInSection(headerPosition);
                int checkCounter = 0;
                for (Gallery imgGallery : gal) {
                    if(gallery.get(imgGallery.getPosition()).isSelected()) {
                        checkCounter = checkCounter + 1;
                        Log.e("COUNTER",""+checkCounter);
                    }
                }
                for (Gallery imgGallery : gal) {
                    GalleryAdapter.ItemViewHolder itemViewHolder = (GalleryAdapter.ItemViewHolder) galleryImages.findViewHolderForAdapterPosition(imgGallery.getPosition());
                    Log.e("COUNTER",""+checkCounter);
                    GalleryAdapter.HeaderViewHolder headerViewHolder = (GalleryAdapter.HeaderViewHolder) galleryImages.findViewHolderForAdapterPosition(headerPosition);
                    if(isChecked) {
                        if (itemViewHolder != null && itemViewHolder.check != null) {
                            itemViewHolder.check.setVisibility(View.VISIBLE);
                            headerViewHolder.checkMonthAlt.setVisibility(View.VISIBLE);
                        }
                    } else {
                        if(checkCounter >= 1) {
                            if (itemViewHolder != null && itemViewHolder.check != null) {
                                itemViewHolder.check.setVisibility(View.VISIBLE);
                                headerViewHolder.checkMonthAlt.setVisibility(View.VISIBLE);
                            }
                        } else {
                            if (itemViewHolder != null && itemViewHolder.check != null) {
                                itemViewHolder.check.setVisibility(View.GONE);
                                itemViewHolder.check.setChecked(false);
                                headerViewHolder.checkMonthAlt.setVisibility(View.GONE);
                                checkBoxVisible = false;
                            }

                        }
                    }
                }
                GalleryAdapter.ItemViewHolder itemViewHolder = (GalleryAdapter.ItemViewHolder) galleryImages.findViewHolderForAdapterPosition(position);
                if (itemViewHolder != null && itemViewHolder.check != null) {
                    if(isChecked == true) {
                        itemViewHolder.check.setChecked(true);
                        checkBoxVisible = true;
                    } else {
                        itemViewHolder.check.setChecked(false);
                        checkBoxVisible = false;
                    }
                }
                //Log.e("Check Counter",""+checkCounter);
                //Log.e("CHECK STATUS MATCHED COUNTER",""+checkCounter);
                // Log.e("CHECK STATUS MATCHED SIZE",""+gal.size());
                GalleryAdapter.HeaderViewHolder headerViewHolder = (GalleryAdapter.HeaderViewHolder) galleryImages.findViewHolderForAdapterPosition(headerPosition);
                if(checkCounter == gal.size()) {
                    gallery.get(headerPosition).setHeaderSelected(true);
                    Log.e("CHECK STATUS MATCHED",""+gallery.get(headerPosition).isHeaderSelected());

                    if (headerViewHolder != null) {
                        headerViewHolder.checkMonthAlt.setOnCheckedChangeListener(null);
                        headerViewHolder.checkMonthAlt.setChecked(true);
                        headerViewHolder.checkMonthAlt.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                Log.e("INSIDE CLICK MATCH",""+isChecked);
                                gallery.get(position).setHeaderSelected(false);
                                clickItem(headerPosition, isChecked);
                            }
                        });
                    }
                    // notifyDataSetChanged();
                } else {
                    gallery.get(headerPosition).setHeaderSelected(false);
                    Log.e("CHECK STATUS",""+gallery.get(headerPosition).isHeaderSelected());
                    if (headerViewHolder != null) {
                        headerViewHolder.checkMonthAlt.setOnCheckedChangeListener(null);
                        headerViewHolder.checkMonthAlt.setChecked(false);
                        headerViewHolder.checkMonthAlt.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                Log.e("INSIDE CLICK UN-MATCH",""+isChecked);
                                clickItem(headerPosition, isChecked);
                            }
                        });
                    }
                }

            }
            //notifyDataSetChanged();
            Log.e("Click Header",""+gallery.get(position).isHeader());
        }

        private int findHeaderPosition(int position) {
            for (int i = position; i >= 0; i--) {
                if (getItemViewType(i) == VIEW_TYPE_HEADER) {
                    Log.e("HEADER POSITION",""+i);
                    return i;
                }
            }
            return -1; // Header position not found
        }

        @Override
        public int getItemViewType(int position) {
            GalleryAll galleryAll = gallery.get(position);
            if(galleryAll.isHeader()) {
                return VIEW_TYPE_HEADER;
            } else {
                return VIEW_TYPE_ITEM;
            }
        }

        private boolean isHeaderSelected(int position) {
            Log.e("POS", ""+position);
            return selectedItems.contains(position);
        }

        private void toggleSelection(int position) {
            // Log.e("ITEM COUNT SELECTED VAL",""+selectedItems.size());
            if (selectedItems.contains(position)) {
                selectedItems.remove(Integer.valueOf(position));
                Log.e("SELECTED VALUES", "" + selectedItems);
            } else {
                selectedItems.add(position);
                Log.e("SELECTED VALUES", "" + selectedItems);
            }
        }

        public static class HeaderViewHolder extends RecyclerView.ViewHolder {
            TextView headerTextView;
            CheckBox checkMonth, checkMonthAlt;

            HeaderViewHolder(View itemView) {
                super(itemView);
                headerTextView = itemView.findViewById(R.id.month_name);
                checkMonth = itemView.findViewById(R.id.checkMonth);
                checkMonthAlt = itemView.findViewById(R.id.checkMonthAlt);
            }

            void bind(GalleryAll section) {
                headerTextView.setText(section.getDate());
                checkMonthAlt.setChecked(section.isHeaderSelected());
                checkMonthAlt.setOnCheckedChangeListener(null);
            }
        }

        private class ItemViewHolder extends RecyclerView.ViewHolder {
            ImageView galleryItem, status_indicator;
            RelativeLayout check_layout;

            CheckBox check;

            ItemViewHolder(View itemView) {
                super(itemView);
                galleryItem = itemView.findViewById(R.id.galleryItem);
                status_indicator = itemView.findViewById(R.id.status_indicator);
                check_layout = itemView.findViewById(R.id.check_layout);
                check = itemView.findViewById(R.id.check);
            }

            void bind(GalleryAll item) {
                //Log.e("SELECTED",""+item.isSelected());
                check.setChecked(item.isSelected());
                check.setOnCheckedChangeListener(null);
            }
        }

        private void removeSection(int adapterPosition) {
            int sectionIndex = getSectionIndex(adapterPosition);
            selectionCount = sectionSelections.get(sectionIndex);
            selectionCount += -1;

            Log.e("ADD COUNT",""+selectionCount);
            Log.e("ADD IS SELECTED COUNT",""+item.isSelected());

            sectionSelections.set(sectionIndex, selectionCount);
            toggleCheckbox();
        }

        private void storeSection(int adapterPosition) {
            int sectionIndex = getSectionIndex(adapterPosition);
            selectionCount = sectionSelections.get(sectionIndex);
            selectionCount += 1;
            sectionSelections.set(sectionIndex, selectionCount);
        }

        @Override
        public int getItemCount() {

            return gallery.size();
        }
        private List<Gallery> getItemsInSection(int adapterPosition) {
            // Calculate the total item count in the section
            int itemCount = 0;
            List<Gallery>  items = new ArrayList<>();
            int sectionIndex = getSectionIndex(adapterPosition);
            for (int i = adapterPosition + 1; i < getItemCount(); i++) {
                if (getSectionIndex(i) == sectionIndex) {
                    items.add(new Gallery(gallery.get(i).getId(), gallery.get(i).getImageData(), gallery.get(i).getOriginal(), i));

                } else {
                    break;
                }
            }
            Log.e("ELEMENT COUNT",""+itemCount);
            return items;
        }

        private int getItemCountInSection(int adapterPosition) {
            // Calculate the total item count in the section
            int itemCount = 0;
            int sectionIndex = getSectionIndex(adapterPosition);
            for (int i = adapterPosition + 1; i < getItemCount(); i++) {
                if (getSectionIndex(i) == sectionIndex) {
                    itemCount++;
                } else {
                    break;
                }
            }
            Log.e("ELEMENT COUNT",""+itemCount);
            return itemCount;
        }
        public void toggleCheckbox() {
            areCheckboxesVisible = !areCheckboxesVisible;
            notifyDataSetChanged();
        }
        private int getSectionIndex(int position) {
            int sectionIndex = 0;
            for (int i = position; i >= 0; i--) {
                if (getItemViewType(i) == VIEW_TYPE_HEADER) {
                    sectionIndex++;
                }
            }
            Log.e("HEADER POS",""+(sectionIndex - 1));
            return sectionIndex - 1;
        }

        public void updateCheckboxVisibility(boolean isVisible) {
            loadGallery("Bearer " + auth, "", "", "", "", "", companyId,"0","","");
            //galleryAdapter.notifyDataSetChanged();
            checkBoxVisible = false;
        }
    }
    public class FilterUserAdapter extends RecyclerView.Adapter<FilterUserAdapter.ViewHolder> {

        List<FilterUser> filterUserList;
        Context context;
        String filtersName,filtersName1;
        public FilterUserAdapter(List<FilterUser> filterUserList, Context context) {
            this.filterUserList = filterUserList;
            this.context = context;
        }

        @NonNull
        @Override
        public FilterUserAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.upload_by_row, parent, false);
            return new FilterUserAdapter.ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull FilterUserAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
            if(filterUserList.get(position).getSubUser_name() == null) {
                holder.user_name.setText(filterUserList.get(position).getName());
            } else {
                holder.user_name.setText(filterUserList.get(position).getSubUser_name());
            }

            holder.check_user.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        filterNames.add(filterUserList.get(position).getName());
                        filterNamesIds.add(String.valueOf(filterUserList.get(position).getId()));
                    } else {
                        String addName = filterUserList.get(position).getName();
                        String ids = String.valueOf(filterUserList.get(position).getId());
                        List<String> dl = new ArrayList<String>();
                        for (int i = 0; i < filterNames.size(); i++) {
                            String a = filterNames.get(i);
                            System.out.println(a);
                            if(a.equals(addName)){
                                dl.add(a);
                            }}
                        filterNames.removeAll(dl);
                        System.out.println("RemoveValue"+filterNames);
                        //
                        List<String> dl1 = new ArrayList<String>();
                        for (int i = 0; i < filterNamesIds.size(); i++) {
                            String a = filterNamesIds.get(i);
                            System.out.println(a);
                            if(a.equals(ids)){
                                dl1.add(a);
                            }}
                        filterNamesIds.removeAll(dl1);
                        System.out.println("RemoveValueIds"+filterNamesIds);

                    }}
            });
            for (int i = 0; i < filterNames.size(); i++) {
                filtersName = filterNames.get(i);
                if (filterUserList.get(position).getName().equals(filtersName)){
                    holder.check_user.setChecked(true);
                }
            }
            HashSet<String> uniqueSet = new HashSet<>();
            for (String element : filterNamesIds) {
                uniqueSet.add(element);
            }
            filterNamesIds.clear();
            filterNamesIds.addAll(uniqueSet);
            System.out.println(filterNamesIds);
        }
        @Override
        public int getItemCount() {
            return filterUserList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            LinearLayout user_layout;
            CheckBox check_user;
            TextView user_name;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                user_layout = (itemView).findViewById(R.id.user_layout);
                check_user = (itemView).findViewById(R.id.check_user);
                user_name = (itemView).findViewById(R.id.user_name);
                check_user = (itemView).findViewById(R.id.check_user);
            }
        }
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
