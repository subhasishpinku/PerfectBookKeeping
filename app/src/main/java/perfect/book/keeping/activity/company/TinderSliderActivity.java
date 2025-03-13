package perfect.book.keeping.activity.company;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;

import perfect.book.keeping.R;
import perfect.book.keeping.activity.Companies;
import perfect.book.keeping.activity.CropImage;
import perfect.book.keeping.activity.company.gallery.PendingGallery;
import perfect.book.keeping.activity.company.gallery.ReceiptGallery;
import perfect.book.keeping.activity.company.gallery.RejectGallery;
import perfect.book.keeping.adapter.SnapImageAdapter;
import perfect.book.keeping.adapter.TinderSliderAdapter;
import perfect.book.keeping.api.ApiClient;
import perfect.book.keeping.global.GalleryDataTransferService;
import perfect.book.keeping.global.Global;
import perfect.book.keeping.global.LocaleHelper;
import perfect.book.keeping.global.SharedPrefManager;
import perfect.book.keeping.global.SwipeItemTouchHelper;
import perfect.book.keeping.manager.Manager;
import perfect.book.keeping.model.GalleryAll;
import perfect.book.keeping.model.TinderSliderModel;
import perfect.book.keeping.response.UserResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TinderSliderActivity extends AppCompatActivity {
    SharedPreferences shared;
    String auth;
    int companyId, permission, clickId, position = -1, showStatus;
    RecyclerView sliderRV;
    List<TinderSliderModel> TinderSliderModel;
    ArrayList<TinderSliderModel> arrayListModelSlider = new ArrayList<TinderSliderModel>();
    Dialog progressDialog;
    TinderSliderAdapter adapter;

    Global global = new Global();
    Manager galDb;
    String last_date = "";
    String company_currency, company_dateFormat;
    int login_id;
    String file_status= "";
    String imageUrl = "";
    Locale myLocale;
    String codeLang;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tinder_silider_activity);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        android.graphics.drawable.Drawable background = TinderSliderActivity.this.getDrawable(R.drawable.gradiant_btn_alt);
        getWindow().setBackgroundDrawable(background);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        galDb = new Manager(TinderSliderActivity.this);

        Intent intent = getIntent();
        companyId = intent.getIntExtra("companyId",companyId);
        permission = intent.getIntExtra("permission", 0);
        clickId = intent.getIntExtra("clickId", 0);
        showStatus = intent.getIntExtra("showStatus",0);
        file_status = intent.getStringExtra("file_status");
        imageUrl = intent.getStringExtra("imageUrl");
        System.out.println("ClickID"+" TinderActivity "+companyId+"   "+permission + "   "+clickId+"  "+showStatus+"  "+file_status+"  "+imageUrl);



        Cursor cursor = galDb.fetchCompanyImage(companyId);
        while (cursor.moveToNext()) {
            company_currency = cursor.getString(11);
            company_dateFormat = cursor.getString(15);
        }


        shared = getSharedPreferences("book_keeping", Context.MODE_PRIVATE);
        auth = shared.getString("access_token", "");
        login_id = shared.getInt("login_user_id", 0);
        codeLang = shared.getString("code_lang", "");
        setLocale(codeLang);
        sliderRV = (RecyclerView)findViewById(R.id.sliderRV);
        progressDialog = new Dialog(TinderSliderActivity.this);
        progressDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        progressDialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        progressDialog.setContentView(R.layout.progress);
        TinderSliderModel =new ArrayList<>();
        arrayListModelSlider =new ArrayList<>();
        if (!imageUrl.equals("") && file_status.equals("(3)")){
            Log.e("REJECT GALLERY","YES");
            Intent viewReceipt = new Intent(TinderSliderActivity.this, RejectGallery.class);
            viewReceipt.putExtra("companyId", companyId);
            viewReceipt.putExtra("permission",permission);
            startActivity(viewReceipt);
        } else {
            syncData("DESC");
        }
    }

    private void syncData(String orderBy) {
        Cursor gallery = galDb.fetchGallery(companyId, orderBy, file_status);
        JSONObject jsonObject = new JSONObject();
        JSONObject finalObject = new JSONObject();
        while (gallery.moveToNext()) {
            Log.e("IMAGES",""+gallery.getString(16));
            String file_date = gallery.getString(16);
            int iuFlag = (!file_date.equals(last_date)) ? 0 : 1;
            finalObject = bindData(gallery, jsonObject,iuFlag);
            last_date = file_date;
        }
        Log.e("JSON OBJECT IS",""+finalObject);

        JSONArray dayObject = finalObject.names();
        if(dayObject != null) {
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
                String modifiedString = company_dateFormat.replace("D", "d");
                DateFormat outputFormat = new SimpleDateFormat(modifiedString, Locale.getDefault());
                Dates = outputFormat.format(date);
                //headerViewHolder.headerTextView.setText(Date);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
            try {
                JSONArray ImageObject = finalObject.getJSONArray(String.valueOf(dayObject.get(i)));
                Log.e("Data Date Image",""+ImageObject.getJSONObject(0).getInt("id"));
                for(int l = 0; l < ImageObject.length(); l++) {

                    if(!ImageObject.getJSONObject(l).getString("created_at").equals("") || !ImageObject.getJSONObject(l).getString("thumbnail").equals("")) {
                        TinderSliderModel.add(
                                new TinderSliderModel(
                                        ImageObject.getJSONObject(l).getInt("id"),
                                        ImageObject.getJSONObject(l).getString("file_name"),
                                        ImageObject.getJSONObject(l).getString("thumbnail"),
                                        ImageObject.getJSONObject(l).getString("original"),
                                        ImageObject.getJSONObject(l).getString("link"),
                                        ImageObject.getJSONObject(l).getDouble("amount"),
                                        ImageObject.getJSONObject(l).getString("title"),
                                        ImageObject.getJSONObject(l).getString("created_user_name"),
                                        ImageObject.getJSONObject(l).getInt("payment_flag"), Dates,
                                        clickId, true,
                                        ImageObject.getJSONObject(l).getString("thumbnailLink"),
                                        ImageObject.getJSONObject(l).getString("originalLink"),
                                        ImageObject.getJSONObject(l).getString("created_at"),
                                        ImageObject.getJSONObject(l).getInt("approval_status"),
                                        company_currency,
                                        ImageObject.getJSONObject(l).getInt("created_user_id"),
                                        login_id,imageUrl,
                                        ImageObject.getJSONObject(l).getString("reject_reason"),
                                        company_dateFormat
                                ));
                    }
                    arrayListModelSlider.addAll(TinderSliderModel);
                    adapter = new TinderSliderAdapter(TinderSliderActivity.this, TinderSliderModel, showStatus,companyId,permission);
                    sliderRV.setAdapter(adapter);
                    sliderRV.setLayoutManager(new LinearLayoutManager(getApplication(),
                            LinearLayoutManager.HORIZONTAL, false));
                    sliderRV.setOnFlingListener(null);
                    adapter.notifyDataSetChanged();
                    LinearSnapHelper linearSnapHelper = new SwipeItemTouchHelper();
                    linearSnapHelper.attachToRecyclerView(sliderRV);
                }

            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }
        }else {
            Intent viewReceipt = new Intent(TinderSliderActivity.this, ReceiptGallery.class);
            viewReceipt.putExtra("companyId", companyId);
            viewReceipt.putExtra("permission",permission);
            startActivity(viewReceipt);
        }
    }

    public JSONObject bindData(Cursor gallery, JSONObject jsonObject, int iuFlag) {
        JSONArray jsonArray = new JSONArray();
        try {
            JSONObject dataObject = new JSONObject();
            dataObject.put("id", gallery.getInt(1));
            dataObject.put("file_name", gallery.getString(2));
            dataObject.put("approval_status", gallery.getInt(6));
            dataObject.put("created_user_id", gallery.getInt(7));
            dataObject.put("amount", gallery.getDouble(8));
            dataObject.put("title", gallery.getString(9));
            dataObject.put("company_id", gallery.getInt(10));
            dataObject.put("payment_flag", gallery.getInt(11));
            dataObject.put("created_user_name", gallery.getString(12));
            dataObject.put("thumbnail", gallery.getString(13));
            dataObject.put("original", gallery.getString(14));
            dataObject.put("link", gallery.getString(15));
            dataObject.put("thumbnailLink", gallery.getString(17));
            dataObject.put("originalLink", gallery.getString(18));
            dataObject.put("created_at", gallery.getString(19));
            dataObject.put("snap_image", gallery.getString(3));
            dataObject.put("reject_reason", gallery.getString(20));
            jsonArray.put(dataObject);
            last_date = gallery.getString(16);
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


    public void clearShare() {
        SharedPreferences loginRef = getSharedPreferences("book_keeping", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = loginRef.edit();
        editor.putString("access_token","");
        editor.putString("fcm_token","");
        editor.putBoolean("first_input",true);
        editor.putInt("userId", 0);
        editor.commit();
    }
    public void updateShare(String token) {
        SharedPreferences sharedPreferences = getSharedPreferences("book_keeping", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("access_token", token);
        editor.commit();
    }


    public void scroll_to(int pos){
//        sliderRV.smoothScrollToPosition(pos);
        sliderRV.scrollToPosition(pos);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        SharedPrefManager.getInstance(getApplicationContext()).clearData();
        if(file_status.equals("(3)")) {
            Intent viewReceipt = new Intent(TinderSliderActivity.this, RejectGallery.class);
            viewReceipt.putExtra("companyId", companyId);
            viewReceipt.putExtra("permission",permission);
            startActivity(viewReceipt);
        } else if (file_status.equals("(0)")) {
            Intent viewReceipt = new Intent(TinderSliderActivity.this, PendingGallery.class);
            viewReceipt.putExtra("companyId", companyId);
            viewReceipt.putExtra("permission",permission);
            startActivity(viewReceipt);
        } else {
            Intent viewReceipt = new Intent(TinderSliderActivity.this, ReceiptGallery.class);
            viewReceipt.putExtra("companyId", companyId);
            viewReceipt.putExtra("permission",permission);
            startActivity(viewReceipt);
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
