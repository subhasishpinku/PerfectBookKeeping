package perfect.book.keeping.activity.company;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
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
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
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
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import javax.crypto.NoSuchPaddingException;

import de.hdodenhof.circleimageview.CircleImageView;
import perfect.book.keeping.R;
import perfect.book.keeping.activity.Companies;
import perfect.book.keeping.activity.LoginScreen;
import perfect.book.keeping.activity.company.gallery.RejectGallery;
import perfect.book.keeping.activity.pnl.ProfitLoss;
import perfect.book.keeping.activity.profile.Profile;
import perfect.book.keeping.adapter.BusinessDashboardAdapter;
import perfect.book.keeping.api.ApiClient;
import perfect.book.keeping.encrypt.CryptLib;
import perfect.book.keeping.global.DownloadGalleryTask;
import perfect.book.keeping.global.Global;
import perfect.book.keeping.global.ItemOffsetDecoration;
import perfect.book.keeping.global.LocaleHelper;
import perfect.book.keeping.global.RefreshToken;
import perfect.book.keeping.manager.Manager;
import perfect.book.keeping.model.BusinessDashboardModel;
import perfect.book.keeping.response.ChangePassword;
import perfect.book.keeping.response.CompanyResponse;
import perfect.book.keeping.response.FilterSubUserResponse;
import perfect.book.keeping.response.LogoutResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BusinessDashboards extends AppCompatActivity {

    TextView header_title, company_name, company_ein;

    ImageView companyLogo;

    RecyclerView grid;

    List<BusinessDashboardModel> businessDashboardList = new ArrayList<>();

    SharedPreferences shared;

    BusinessDashboardAdapter businessDashboardAdapter;

    int companyId, permission= 0, userType = 0, package_id, ein_show;
    double company_amount;
    String companyName = "", auth, fcm_token, company_currency, ein;

    Dialog progressDialog;

    RefreshToken refreshToken;

    LinearLayout openMenu;

    BottomSheetDialog sheetDialog;

    EditText password, cnfPassword, oldPassword;

    ImageView eye_btn_fp, eye_btn_fp_old, eye_btn_cnf;

    LinearLayout updatePass, back, amount_layout;

    CryptLib cryptLib;

    String newEncrypt, oldEncrypt, compImage;
    TextView currency_mode, amount;
    Global global;
    Manager galDb;
    CircleImageView profile_image;
    private List<Integer> userIds = new ArrayList<Integer>();
    private List<Integer> imageIds = new ArrayList<>();
    DownloadGalleryTask galleryTask;
    Manager db;
    Locale myLocale;
    String codeLang;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_dashboard);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        android.graphics.drawable.Drawable background = BusinessDashboards.this.getDrawable(R.drawable.gradiant_btn_alt);
        getWindow().setBackgroundDrawable(background);

        header_title = findViewById(R.id.header_title);
        company_name = findViewById(R.id.company_name);
        company_ein = findViewById(R.id.company_ein);
        profile_image = findViewById(R.id.profile_image);
        currency_mode = findViewById(R.id.currency_mode);
        amount = findViewById(R.id.amount);
        amount_layout = findViewById(R.id.amount_layout);
        grid = findViewById(R.id.grid);
        back = findViewById(R.id.back);


        Uri data = getIntent().getData();

        if (data != null) {
            String path = data.getPath();
            Log.e("CLICK PATH","HI"+path);
        }

        db = new Manager(BusinessDashboards.this);

        shared = getSharedPreferences("book_keeping", Context.MODE_PRIVATE);
        auth = shared.getString("access_token", "");
        fcm_token = shared.getString("fcm_token", "");
        userType = shared.getInt("userType",0);

        shared = getSharedPreferences("book_keeping", Context.MODE_PRIVATE);
        codeLang = shared.getString("code_lang", "");
        setLocale(codeLang);

        galDb = new Manager(BusinessDashboards.this);
        global = new Global();

        try {
            cryptLib = new CryptLib();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (NoSuchPaddingException e) {
            throw new RuntimeException(e);
        }



        companyLogo = findViewById(R.id.companyLogo);

        openMenu = findViewById(R.id.openMenu);

        refreshToken = new RefreshToken();

        permission = getIntent().getIntExtra("companyPermission", 0);
        companyId = getIntent().getIntExtra("companyId", 0);

        recallDB();
        loadGalleryDownload("Bearer " + auth, "", "", "", "", "", companyId, "", "", "", "");
//        if(global.isNetworkAvailable(BusinessDashboards.this)) {
//            Intent gallerySync = new Intent(this, GalleryDataTransferService.class);
//            gallerySync.putExtra("companyId",companyId);
//            gallerySync.putExtra("approval_status","3");
//            startService(gallerySync);
//        }
        reCallAdapter(0);
        progressDialog = new Dialog(BusinessDashboards.this);
        progressDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        progressDialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        progressDialog.setContentView(R.layout.progress);
        if (progressDialog.getWindow() != null) {
            progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
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


        callUser("Bearer " + auth);
        updateCompanyLocal("Bearer " + auth);
    }

    public void loadGalleryDownload(String verify, String ids, String orderType, String orderField, String month, String year, int company_id,String approvalStatusNewSpaceRemove,String filterNamesIdSpaceRemove,String title, String mode) {
        Log.e("APPROVAL STATUS",""+approvalStatusNewSpaceRemove);
        Call<JsonObject> gallery = ApiClient.getInstance().getBookKeepingApi().getGallery(verify, ids, orderType, orderField, month, year, company_id,"",filterNamesIdSpaceRemove,title);
        gallery.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.code() == 200) {
                    imageIds.clear();
                    if (response.body().get("data").isJsonObject() == false) {
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
                                        for(int l = 0; l < ImageObject.length(); l++) {
                                            imageIds.add(Integer.parseInt(ImageObject.getJSONObject(l).getString("id")));
                                            Log.e("IMAGE IDS",""+imageIds);
                                            Cursor checkImg = galDb.checkGallery(company_id, Integer.parseInt(ImageObject.getJSONObject(l).getString("id")));
                                            Log.e("COUNTER",""+checkImg.getCount());
                                            if(checkImg.getCount() > 0) {
                                                //Update
                                                while (checkImg.moveToNext()){
                                                    if(checkImg.getInt(6) != Integer.parseInt(ImageObject.getJSONObject(l).getString("approval_status"))) {
//                                                        galDb.updateGalleryStatus(Integer.parseInt(ImageObject.getJSONObject(l).getString("id")),
//                                                                Integer.parseInt(ImageObject.getJSONObject(l).getString("approval_status")));
                                                        String reject_reason;
                                                        if(ImageObject.getJSONObject(l).getString("reason") == null) {
                                                            reject_reason = "";
                                                        } else {
                                                            reject_reason = ImageObject.getJSONObject(l).getString("reason");
                                                        }
                                                        galDb.reSubmitGallery(
                                                                Integer.parseInt(ImageObject.getJSONObject(l).getString("id")),
                                                                Integer.parseInt(ImageObject.getJSONObject(l).getString("approval_status")),
                                                                Double.parseDouble(ImageObject.getJSONObject(l).getString("amount")),
                                                                ImageObject.getJSONObject(l).getString("title"),
                                                                Integer.parseInt(ImageObject.getJSONObject(l).getString("payment_flag")),
                                                                dayObject.get(k).toString(),
                                                                reject_reason);
                                                    }
                                                }
                                            }
                                            else {
                                                //Insert
                                                String reject_reason;
                                                if(ImageObject.getJSONObject(l).getString("reason") == null) {
                                                    reject_reason = "";
                                                } else {
                                                    reject_reason = ImageObject.getJSONObject(l).getString("reason");
                                                }
                                                Log.e("REJECT REASON", ""+reject_reason);
                                                galDb.saveGallery(
                                                        Integer.parseInt(ImageObject.getJSONObject(l).getString("id")),
                                                        ImageObject.getJSONObject(l).getString("file_name"),
                                                        "",
                                                        "",
                                                        "",
                                                        Integer.parseInt(ImageObject.getJSONObject(l).getString("approval_status")),
                                                        Integer.parseInt(ImageObject.getJSONObject(l).getJSONObject("created_user").getString("id")),
                                                        Double.parseDouble(ImageObject.getJSONObject(l).getString("amount")),
                                                        ImageObject.getJSONObject(l).getString("title"),
                                                        Integer.parseInt(ImageObject.getJSONObject(l).getString("company_id")),
                                                        Integer.parseInt(ImageObject.getJSONObject(l).getString("payment_flag")),
                                                        ImageObject.getJSONObject(l).getJSONObject("created_user").getString("name"),
                                                        "",
                                                        "",
                                                        ImageObject.getJSONObject(l).getString("link"),
                                                        dayObject.get(k).toString(),
                                                        ImageObject.getJSONObject(l).getString("thumbnail"),
                                                        ImageObject.getJSONObject(l).getString("original"), ImageObject.getJSONObject(l).getString("createdAt"),
                                                        reject_reason
                                                );
                                                galleryTask = (DownloadGalleryTask) new DownloadGalleryTask(getApplicationContext()).execute(
                                                        ImageObject.getJSONObject(l).getString("thumbnail"),
                                                        ImageObject.getJSONObject(l).getString("file_name"),
                                                        ImageObject.getJSONObject(l).getString("id")
                                                );
                                                //syncData();
                                                Log.e("IMAGES RESPONSE","Insert"+ImageObject.getJSONObject(l).getString("id"));
                                            }
                                        }
                                    }
                                }

                            }
                            String acceptImageIds = String.valueOf(imageIds);
                            String acceptImageIdsOld = acceptImageIds.replace("[", "(");
                            String acceptImageIdsNew = acceptImageIdsOld.replace("]", ")");
                            String acceptImageIdsNewSpaceRemove = acceptImageIdsNew.replace(" ", "");
                            Log.e("IMAGE IDS",""+acceptImageIdsNewSpaceRemove);
                            galDb.removeGalleryPhoto(company_id, acceptImageIdsNewSpaceRemove, approvalStatusNewSpaceRemove);
                            reCallAdapter(1);
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }

                    }
                }
                else if(response.code() == 401) {
                    String token = global.reCallList(shared.getString("refresh_token", ""));
                    if(token.equals("Refresh Token Expires")) {
                        clearShare();
                        startActivity(new Intent(BusinessDashboards.this, LoginScreen.class));
                    } else {
                        String vToken = global.validToken(token);
                        if(vToken.equals("Authentication token missing") || vToken.equals("Wrong authentication token")) {
                            clearShare();
                            startActivity(new Intent(BusinessDashboards.this, LoginScreen.class));
                        } else {
                            auth = vToken;
                            updateShare(vToken);
                            loadGalleryDownload("Bearer " + auth, "", "", "", "", "", companyId, "", "", "", "");
                        }
                    }
                }
                else {
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        Toast.makeText(BusinessDashboards.this, ""+jObjError.getString("message"), Toast.LENGTH_SHORT).show();
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

            }
        });
    }

    public void recallDB() {
        Log.e("CALLING","BACKGROUND");
        Cursor cursor = db.fetchCompanyImage(companyId);
        while (cursor.moveToNext()) {
            company_name.setText(cursor.getString(1));
//            if(cursor.getString(1).length() > 12) {
//                company_name.setText(cursor.getString(1).substring(0, Math.min(cursor.getString(1).length(), 12)) + "...");
//            } else {
//                company_name.setText(cursor.getString(1));
//            }
            Log.e("IMAGE in DASHBOARD",""+cursor.getString(2));
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
            companyName = cursor.getString(1);
            package_id = cursor.getInt(7);
            company_amount = cursor.getDouble(12);
            company_currency = cursor.getString(11);
            ein_show = cursor.getInt(14);
            ein = cursor.getString(13);
        }
        if(permission == 3 || permission == 4) {
            company_ein.setVisibility(View.VISIBLE);
            company_ein.setText(ein);
        } else {
            if(ein_show == 1) {
                company_ein.setVisibility(View.VISIBLE);
                company_ein.setText(ein);
            } else {
                company_ein.setVisibility(View.GONE);
                company_ein.setText("");
            }
        }

        amount.setText(" "+company_amount + " " + company_currency);
        if(company_amount >= 0) {
            amount.setTextColor(getResources().getColor(R.color.green_color));
        } else {
            amount.setTextColor(getResources().getColor(R.color.red));
        }
        if(permission == 5) {
            amount_layout.setVisibility(View.VISIBLE);
        } else {
            amount_layout.setVisibility(View.GONE);
        }
    }

    public void reCallAdapter(int type) {
        String upload_receipts = getResources().getString(R.string.upload_receipts);
        String approved_receipts = getResources().getString(R.string.approved_receipts);
        String pending_receipts = getResources().getString(R.string.pending_receipts);
        String rejected_receipts = getResources().getString(R.string.rejected_receipts);
        String users = getResources().getString(R.string.users);
        String internal_auditors = getResources().getString(R.string.internal_auditors);
        String PL = getResources().getString(R.string.P_L);
        String invoices = getResources().getString(R.string.invoices);
        String company_infos = getResources().getString(R.string.company_infos);
        String payment_methods = getResources().getString(R.string.payment_methods);

        businessDashboardList.clear();
        if(permission == 3 || permission == 4) {
            businessDashboardList.add(new BusinessDashboardModel(R.drawable.upload_receipt_ico, upload_receipts, "camera", companyId, companyName, permission, userType, package_id));
            businessDashboardList.add(new BusinessDashboardModel(R.drawable.approvereceipt_ico, approved_receipts, "receipt", companyId, companyName, permission, userType, package_id));
            businessDashboardList.add(new BusinessDashboardModel(R.drawable.pending_receipt, pending_receipts, "pReceipt", companyId, companyName, permission, userType, package_id));
            businessDashboardList.add(new BusinessDashboardModel(R.drawable.rejectreceipt_ico, rejected_receipts, "rReceipt", companyId, companyName, permission, userType, package_id));
            businessDashboardList.add(new BusinessDashboardModel(R.drawable.icon_sub_user, users, "subUsers", companyId, companyName, permission, userType, package_id));
            businessDashboardList.add(new BusinessDashboardModel(R.drawable.auditors_ico, internal_auditors, "bk", companyId, companyName, permission, userType, package_id));
            businessDashboardList.add(new BusinessDashboardModel(R.drawable.pl_statement_ico, PL, "pnl", companyId, companyName, permission, userType, package_id));
            businessDashboardList.add(new BusinessDashboardModel(R.drawable.icon_payment_recipt, invoices, "pr", companyId, companyName, permission, userType, package_id));
            businessDashboardList.add(new BusinessDashboardModel(R.drawable.company_info, company_infos, "Company Info", companyId, companyName, permission, userType, package_id));
            businessDashboardList.add(new BusinessDashboardModel(R.drawable.payment_method, payment_methods, "Payment Method", companyId, companyName, permission, userType, package_id));
        } else if(permission == 5){
            businessDashboardList.add(new BusinessDashboardModel(R.drawable.upload_receipt_ico, upload_receipts, "camera", companyId, companyName, permission, userType, package_id));
            businessDashboardList.add(new BusinessDashboardModel(R.drawable.approvereceipt_ico, approved_receipts, "receipt", companyId, companyName, permission, userType, package_id));
            businessDashboardList.add(new BusinessDashboardModel(R.drawable.pending_receipt, pending_receipts, "pReceipt", companyId, companyName, permission, userType, package_id));
            businessDashboardList.add(new BusinessDashboardModel(R.drawable.rejectreceipt_ico, rejected_receipts, "rReceipt", companyId, companyName, permission, userType, package_id));
        } else if (permission == 7) {
            businessDashboardList.add(new BusinessDashboardModel(R.drawable.approvereceipt_ico, approved_receipts, "receipt", companyId, companyName, permission, userType, package_id));
            businessDashboardList.add(new BusinessDashboardModel(R.drawable.pending_receipt, pending_receipts, "pReceipt", companyId, companyName, permission, userType, package_id));
            businessDashboardList.add(new BusinessDashboardModel(R.drawable.rejectreceipt_ico, rejected_receipts, "rReceipt", companyId, companyName, permission, userType, package_id));
        } else {
            businessDashboardList.add(new BusinessDashboardModel(R.drawable.approvereceipt_ico, approved_receipts, "receipt", companyId, companyName, permission, userType, package_id));
            businessDashboardList.add(new BusinessDashboardModel(R.drawable.pending_receipt, pending_receipts, "pReceipt", companyId, companyName, permission, userType, package_id));
            businessDashboardList.add(new BusinessDashboardModel(R.drawable.rejectreceipt_ico, rejected_receipts, "rReceipt", companyId, companyName, permission, userType, package_id));
        }

        //businessDashboardList.add(new BusinessDashboards(R.drawable.icon_book_keepers, "Book Keepers", "employees", companyId, companyName));


        if(businessDashboardList.size() > 0) {
            businessDashboardAdapter = new BusinessDashboardAdapter(businessDashboardList, BusinessDashboards.this);
            grid.setAdapter(businessDashboardAdapter);
            GridLayoutManager gridLayoutManager = new GridLayoutManager(BusinessDashboards.this, 2);
            grid.setLayoutManager(gridLayoutManager);
            if(type == 0) {
                grid.addItemDecoration(new ItemOffsetDecoration(BusinessDashboards.this, R.dimen.item_offset));
            }
            businessDashboardAdapter.notifyDataSetChanged();
        }
    }

    private void updateCompanyLocal(String token) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("America/New_York"));
        Date currentDate = new Date();
        Call<CompanyResponse> call = ApiClient.getInstance().getBookKeepingApi().companies(token, "");
        call.enqueue(new Callback<CompanyResponse>() {
            @Override
            public void onResponse(Call<CompanyResponse> call, Response<CompanyResponse> response) {
                Log.e("RESPONSE COMPANY",""+response.code());
                if(response.code() == 200) {
                    //db.PaymentMethodAll();
                    if(response.body().getData().size() > 0) {
                        for (int j = 0; j < response.body().getData().size(); j++) {
                            int finalI = j;
                            Cursor cursor = db.fetchCompanyImage(response.body().getData().get(finalI).getId());
                            if (cursor.getCount() > 0) {
                                db.updateCompany(response.body().getData().get(finalI).getId());

                                int package_id = 0;
                                int price = 0, sub_user_price = 0;
                                if(response.body().getData().get(j).getCompanySubscription().getPackage().getSubsTypeData().getName() != null) {
                                    package_id = response.body().getData().get(j).getCompanySubscription().getPackage().getSubsTypeData().getId();
                                } else {
                                    package_id = 0;
                                }
                                if(response.body().getData().get(j).getCompanySubscription().getPackage().getPrice() != null) {
                                    price = response.body().getData().get(j).getCompanySubscription().getPackage().getPrice();
                                } else {
                                    price = 0;
                                }
                                if(response.body().getData().get(j).getCompanySubscription().getPackage().getSubUserPrice() != null) {
                                    sub_user_price = response.body().getData().get(j).getCompanySubscription().getPackage().getSubUserPrice();
                                } else {
                                    sub_user_price = 0;
                                }
                                db.updateCompanyData(response.body().getData().get(j).getName(),
                                        response.body().getData().get(j).getId(),
                                        response.body().getData().get(j).getRole().getId(),
                                        1,
                                        response.body().getData().get(j).getCompanySubscription().getPaymentStatus(),
                                        response.body().getData().get(j).getUpdatedAt(),package_id,price,sub_user_price,
                                        response.body().getData().get(j).getCurrency(),
                                        response.body().getData().get(j).getCompanyPermission().getAmount(),
                                        response.body().getData().get(j).getGovtId(), response.body().getData().get(j).getIs_govt_id_show(),
                                        response.body().getData().get(j).getDateFormat());
                                progressDialog.dismiss();
                            }
                        }
                    } else {
                        progressDialog.dismiss();
                    }
                } else if(response.code() == 401) {
                    String token = global.reCallList(shared.getString("refresh_token", ""));
                    if(token.equals("Refresh Token Expires")) {
                        clearShare();
                        startActivity(new Intent(BusinessDashboards.this, LoginScreen.class));
                    } else {
                        String vToken = global.validToken(token);
                        if(vToken.equals("Authentication token missing") || vToken.equals("Wrong authentication token")) {
                            clearShare();
                            startActivity(new Intent(BusinessDashboards.this, LoginScreen.class));
                        } else {
                            auth = vToken;
                            updateShare(vToken);
                            updateCompanyLocal("Bearer " + auth);
                        }
                    }
                } else {
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        Toast.makeText(BusinessDashboards.this, ""+jObjError.getString("message"), Toast.LENGTH_SHORT).show();
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            @Override
            public void onFailure(Call<CompanyResponse> call, Throwable t) {

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
        Manager db = new Manager(BusinessDashboards.this);
        db.removeAllCompany();
    }

    public void updateValue( String header) {
        header_title.setText(header);
        companyName = header;
        businessDashboardAdapter.notifyDataSetChanged();
    }

    private void showPopUp(View v) {
        PopupMenu popupMenu = new PopupMenu(BusinessDashboards.this, v);
        popupMenu.getMenuInflater().inflate(R.menu.navigation_item, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();
                if(id == R.id.companyList) {
                   //startActivity(new Intent(BusinessDashboards.this, Companies.class));
                    Intent company = new Intent(BusinessDashboards.this, Companies.class);
                    company.putExtra("enable","0");
                    startActivity(company);
                } else if(id == R.id.profileDtl) {
                    startActivity(new Intent(BusinessDashboards.this, Profile.class));
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
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(BusinessDashboards.this);
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
                    Manager compDb = new Manager(BusinessDashboards.this);
                    compDb.removeAllCompany();
                    SharedPreferences loginRef = getSharedPreferences("book_keeping", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = loginRef.edit();
                    editor.putString("access_token","");
                    editor.putString("fcm_token","");
                    editor.putBoolean("first_input",true);
                    editor.putInt("userId", 0);
                    editor.commit();
                    //ClearAppData.clearApplicationData(BusinessDashboards.this, getPackageName());
                    startActivity(new Intent(BusinessDashboards.this, LoginScreen.class));

            }
        });
        android.app.AlertDialog alert = builder.create();
        alert.show();
    }

    private void log_out() {
        progressDialog.show();
        String mac_address = global.getDeviceToken(BusinessDashboards.this);
        Call<LogoutResponse> logOut = ApiClient.getInstance().getBookKeepingApi().logOut("Bearer "+auth, fcm_token, mac_address);
        logOut.enqueue(new Callback<LogoutResponse>() {
            @Override
            public void onResponse(Call<LogoutResponse> call, Response<LogoutResponse> response) {
                Log.e("LOG OUT RESPONSE",""+response.code());
                if(response.code() == 200) {
                    progressDialog.dismiss();
//                    Manager compDb = new Manager(BusinessDashboards.this);
//                    compDb.removeAllCompany();
//                    SharedPreferences loginRef = getSharedPreferences("book_keeping", Context.MODE_PRIVATE);
//                    SharedPreferences.Editor editor = loginRef.edit();
//                    editor.putString("access_token","");
//                    editor.putString("fcm_token","");
//                    editor.putBoolean("first_input",true);
//                    editor.putInt("userId", 0);
//                    editor.commit();
//                    startActivity(new Intent(BusinessDashboards.this, LoginScreen.class));
                } else {
                    progressDialog.dismiss();
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        Log.e("RESPONSE ERROR",""+jObjError.getString("message"));
                       // Toast.makeText(BusinessDashboards.this, ""+jObjError.getString("message"), Toast.LENGTH_SHORT).show();
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
        sheetDialog = new BottomSheetDialog(BusinessDashboards.this, R.style.BottomSheetStyle);
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
                    if(global.isNetworkAvailable(BusinessDashboards.this)) {
                        try {
                            newEncrypt = cryptLib.encryptPlainTextWithRandomIV(password.getText().toString(), ApiClient.getInstance().getKey());
                            oldEncrypt = cryptLib.encryptPlainTextWithRandomIV(oldPassword.getText().toString(), ApiClient.getInstance().getKey());
                            passwordUpdate(newEncrypt, oldEncrypt);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    } else {
                        String please_Check_Your_Internet_Connection = getResources().getString(R.string.please_Check_Your_Internet_Connection);

                        Toast.makeText(BusinessDashboards.this, please_Check_Your_Internet_Connection, Toast.LENGTH_SHORT).show();
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
                    progressDialog.dismiss();
                    Toast.makeText(BusinessDashboards.this, getResources().getString(R.string.password_change_successfully), Toast.LENGTH_SHORT).show();
                    sheetDialog.dismiss();
                } else if (response.code() == 401) {
                    String token = global.reCallList(shared.getString("refresh_token", ""));
                    if(token.equals("Refresh Token Expires")) {
                        clearShare();
                        startActivity(new Intent(BusinessDashboards.this, LoginScreen.class));
                    } else {
                        String vToken = global.validToken(token);
                        if(vToken.equals("Authentication token missing") || vToken.equals("Wrong authentication token")) {
                            clearShare();
                            startActivity(new Intent(BusinessDashboards.this, LoginScreen.class));
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
                        Toast.makeText(BusinessDashboards.this, ""+jObjError.getString("message"), Toast.LENGTH_SHORT).show();
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

    @Override
    public void onBackPressed() {
        redirect();
    }

    public void redirect() {
        //startActivity(new Intent(BusinessDashboards.this, Companies.class));
        Intent company = new Intent(BusinessDashboards.this, Companies.class);
        company.putExtra("enable","0");
        startActivity(company);
    }

    private void callUser(String token) {
        Call<FilterSubUserResponse> subUserListCall = ApiClient.getInstance().getBookKeepingApi().filterSubUsers(token, String.valueOf(companyId));
        subUserListCall.enqueue(new Callback<FilterSubUserResponse>() {
            @Override
            public void onResponse(Call<FilterSubUserResponse> call, Response<FilterSubUserResponse> response) {
                Log.e("USERS LIST",""+response.code());
                if(response.code() == 200) {
                    galDb.removeAllSubUser();
                    for(int i = 0; i < response.body().getData().size(); i++) {
                        //filterUsers.add(new FilterUser(response.body().getData().get(i).getId(), response.body().getData().get(i).getName(), false, response.body().getData().get(i).getUserCompanyPermissions().get(0).getSubuserName()));
                        galDb.saveSubUser(response.body().getData().get(i).getId(), response.body().getData().get(i).getName(), response.body().getData().get(i).getUserCompanyPermissions().get(0).getSubuserName(), companyId);
                    }
                } else if (response.code() == 401) {
                    String token = global.reCallList(shared.getString("refresh_token", ""));
                    if(token.equals("Refresh Token Expires")) {
                        clearShare();
                        startActivity(new Intent(BusinessDashboards.this, LoginScreen.class));
                    } else {
                        String vToken = global.validToken(token);
                        if(vToken.equals("Authentication token missing") || vToken.equals("Wrong authentication token")) {
                            clearShare();
                            startActivity(new Intent(BusinessDashboards.this, LoginScreen.class));
                        } else {
                            auth = vToken;
                            updateShare(vToken);
                            callUser("Bearer " + auth);
                        }
                    }
                } else {
                    progressDialog.dismiss();
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        Toast.makeText(BusinessDashboards.this, ""+jObjError.getString("message"), Toast.LENGTH_SHORT).show();
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            @Override
            public void onFailure(Call<FilterSubUserResponse> call, Throwable t) {

            }
        });
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