package perfect.book.keeping.activity.receipt;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
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
import android.os.Bundle;
import android.os.StrictMode;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import javax.crypto.NoSuchPaddingException;

import de.hdodenhof.circleimageview.CircleImageView;
import perfect.book.keeping.R;
import perfect.book.keeping.activity.Companies;
import perfect.book.keeping.activity.LoginScreen;
import perfect.book.keeping.activity.company.BusinessDashboards;
import perfect.book.keeping.activity.company.gallery.PendingGallery;
import perfect.book.keeping.activity.company.subUser.SubUsersList;
import perfect.book.keeping.activity.pnl.ProfitLoss;
import perfect.book.keeping.activity.profile.Profile;
import perfect.book.keeping.adapter.PaymentReceiptAdapter;
import perfect.book.keeping.adapter.YearAdapter;
import perfect.book.keeping.api.ApiClient;
import perfect.book.keeping.encrypt.CryptLib;
import perfect.book.keeping.global.Global;
import perfect.book.keeping.global.LocaleHelper;
import perfect.book.keeping.global.RefreshToken;
import perfect.book.keeping.manager.Manager;
import perfect.book.keeping.model.Receipt;
import perfect.book.keeping.model.Year;
import perfect.book.keeping.response.ChangePassword;
import perfect.book.keeping.response.LogoutResponse;
import perfect.book.keeping.response.ReceiptResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PaymentReceipt extends AppCompatActivity {

    ImageView imageview,openSearch;
    RecyclerView recyclerView, year_list;
    SharedPreferences shared;
    String auth, fcm_token;
    ArrayList<Receipt> receipt = new ArrayList<>();
    PaymentReceiptAdapter pAdapter;
    Global global;

    AlertDialog alertDialog;
    List<Year> years = new ArrayList<>();
    EditText searchBy;
    YearAdapter yearAdapter;

    TextView date;

    Dialog progressDialog;

    SwipeRefreshLayout swipeRefresh;

    int counter = 0, userType = 0;

    RefreshToken refreshToken;

    int thisYear;

    LinearLayout openMenu;

    BottomSheetDialog sheetDialog;

    EditText password, cnfPassword, oldPassword;

    ImageView eye_btn_fp, eye_btn_fp_old, eye_btn_cnf,close;

    LinearLayout updatePass, back;

    CryptLib cryptLib;

    String newEncrypt, oldEncrypt;

    TextView header_title;

    int companyId, permission;

    String companyName;

    RelativeLayout noInvoice;
    CircleImageView profile_image;
    EditText search_by;
    Locale myLocale;
    String codeLang;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_receipt);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        android.graphics.drawable.Drawable background = PaymentReceipt.this.getDrawable(R.drawable.gradiant_btn_alt);
        getWindow().setBackgroundDrawable(background);
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        recyclerView = findViewById(R.id.recyclerView);
        header_title = findViewById(R.id.header_title);
        imageview = findViewById(R.id.imageview);
        date = findViewById(R.id.date);
        swipeRefresh = findViewById(R.id.swipeRefresh);
        openMenu = findViewById(R.id.openMenu);
        back = findViewById(R.id.back);
        noInvoice = findViewById(R.id.noInvoice);
        profile_image = findViewById(R.id.profile_image);
        search_by = findViewById(R.id.search_by);
        refreshToken = new RefreshToken();
        shared = getSharedPreferences("book_keeping", Context.MODE_PRIVATE);
        auth = shared.getString("access_token", "");
        fcm_token = shared.getString("fcm_token","");
        codeLang = shared.getString("code_lang", "");
        setLocale(codeLang);

        global = new Global();
        companyId = getIntent().getIntExtra("companyId",0);
        permission = getIntent().getIntExtra("permission", 0);
        companyName = getIntent().getStringExtra("companyName");
        System.out.println("Log "+companyId+" "+permission+" "+companyName);
        header_title.setText("Invoices");
        try {
          cryptLib = new CryptLib();
        } catch (NoSuchAlgorithmException e) {
          throw new RuntimeException(e);
        } catch (NoSuchPaddingException e) {
          throw new RuntimeException(e);
        }
        thisYear = Calendar.getInstance().get(Calendar.YEAR);
        progressDialog = new Dialog(PaymentReceipt.this);
        progressDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        progressDialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        progressDialog.setContentView(R.layout.progress);
        if (progressDialog.getWindow() != null) {
            progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
      if (global.isNetworkAvailable(PaymentReceipt.this)){
          getReceipt(auth, "", 0);
       }else {
          Toast.makeText(PaymentReceipt.this, getResources().getString(R.string.please_Check_Your_Internet_Connection), Toast.LENGTH_SHORT).show();
      }

        date.setText(String.valueOf(thisYear));

        openMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopUp(v);
            }
        });

        imageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getRootView().getContext(), R.style.my_dialog);
                View dialogView = LayoutInflater.from(v.getRootView().getContext()).inflate(R.layout.receipt_filter, null);
                builder.setView(dialogView);
                builder.setCancelable(true);


                alertDialog = builder.create();
                alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable((Color.TRANSPARENT)));
                alertDialog.show();

                year_list = alertDialog.findViewById(R.id.year_list);
                searchBy = alertDialog.findViewById(R.id.searchBy);
                close = dialogView.findViewById(R.id.close);

                close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });
                searchBy.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if(years.size() > 0) {
                            yearAdapter.getFilter().filter(s);
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });

                years.clear();
                for (int i = thisYear; i <= (thisYear+10); i++) {
                    Year year = new Year(String.valueOf(i));
                    years.add(year);
                }

                if(years.size() > 0) {
                    yearAdapter = new YearAdapter(years, PaymentReceipt.this, "inv");
                    year_list.setAdapter(yearAdapter);
                    year_list.setLayoutManager(new LinearLayoutManager(PaymentReceipt.this));
                    yearAdapter.notifyDataSetChanged();
                }

            }

        });

        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (global.isNetworkAvailable(PaymentReceipt.this)){
                    getReceipt(auth, "", 0);
                }else {
                    Toast.makeText(PaymentReceipt.this, getResources().getString(R.string.please_Check_Your_Internet_Connection), Toast.LENGTH_SHORT).show();
                }
                counter = counter+1;
                swipeRefresh.setRefreshing(false);
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redirect();
            }
        });
        Manager compDb = new Manager(PaymentReceipt.this);
        Cursor cursor = compDb.fetchCompanyImage(companyId);
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

        }
        search_by.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(receipt.size() > 0) {
                    pAdapter.getFilter().filter(s);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
    public void getReceipt(String token, String year, int mode) {
        progressDialog.show();
        receipt.clear();
        Call<ReceiptResponse> call = ApiClient.getInstance().getBookKeepingApi().receipt("Bearer " + token, year, companyId);
        call.enqueue(new Callback<ReceiptResponse>() {
            @Override
            public void onResponse(Call<ReceiptResponse> call, Response<ReceiptResponse> response) {
                if (response.code() == 200) {
                    if(mode == 1) {
                        SharedPreferences loginRef = getSharedPreferences("book_keeping", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = loginRef.edit();
                        editor.putString("access_token",token);
                        editor.commit();
                    }
                    progressDialog.dismiss();
                    for (int i = 0; i < response.body().getData().size(); i++) {
                        Receipt comp = new Receipt(response.body().getData().get(i).getTransactionId(),
                                response.body().getData().get(i).getAmount(), response.body().getData().get(i).getCreatedAt(),
                                response.body().getData().get(i).getId(), companyId, companyName, permission);
                        receipt.add(comp);
                    }

                    if (receipt.size() > 0) {
                        recyclerView.setVisibility(View.VISIBLE);
                        noInvoice.setVisibility(View.GONE);
                        pAdapter = new PaymentReceiptAdapter(PaymentReceipt.this, receipt);
                        recyclerView.setAdapter(pAdapter);
                        recyclerView.setLayoutManager(new GridLayoutManager(PaymentReceipt.this, 2));
                        pAdapter.notifyDataSetChanged();

                    } else {
                        progressDialog.dismiss();
                        recyclerView.setVisibility(View.GONE);
                        noInvoice.setVisibility(View.VISIBLE);
                        receipt.clear();
                    }
                } else if(response.code() == 401) {
                    String token = global.reCallList(shared.getString("refresh_token", ""));
                    if(token.equals("Refresh Token Expires")) {
                        clearShare();
                        startActivity(new Intent(PaymentReceipt.this, LoginScreen.class));
                    } else {
                        String vToken = global.validToken(token);
                        if(vToken.equals("Authentication token missing") || vToken.equals("Wrong authentication token")) {
                            clearShare();
                            startActivity(new Intent(PaymentReceipt.this, LoginScreen.class));
                        } else {
                            auth = vToken;
                            updateShare(vToken);
                            getReceipt(auth, "", 0);
                        }
                    }
                } else {
                    progressDialog.dismiss();
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        Toast.makeText(PaymentReceipt.this, ""+jObjError.getString("message"), Toast.LENGTH_SHORT).show();
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }

            }

            @Override
            public void onFailure(Call<ReceiptResponse> call, Throwable t) {
                Toast.makeText(PaymentReceipt.this, t.getMessage(), Toast.LENGTH_SHORT).show();
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
        Manager db = new Manager(PaymentReceipt.this);
        db.removeAllCompany();
    }


    public void dismissal(String selectedDate) {
        alertDialog.dismiss();
        date.setText(selectedDate);
    }

    private void showPopUp(View v) {
        PopupMenu popupMenu = new PopupMenu(PaymentReceipt.this, v);
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
                    //startActivity(new Intent(PaymentReceipt.this, Companies.class));
                    Intent company = new Intent(PaymentReceipt.this, Companies.class);
                    company.putExtra("enable","0");
                    startActivity(company);
                } else if(id == R.id.profileDtl) {
                    startActivity(new Intent(PaymentReceipt.this, Profile.class));
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
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(PaymentReceipt.this);
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
                    Manager compDb = new Manager(PaymentReceipt.this);
                    compDb.removeAllCompany();
                    SharedPreferences loginRef = getSharedPreferences("book_keeping", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = loginRef.edit();
                    editor.putString("access_token","");
                    editor.putString("fcm_token","");
                    editor.putBoolean("first_input",true);
                    editor.putInt("userId", 0);
                    editor.commit();
                    //ClearAppData.clearApplicationData(PaymentReceipt.this, getPackageName());
                    startActivity(new Intent(PaymentReceipt.this, LoginScreen.class));
            }
        });
        android.app.AlertDialog alert = builder.create();
        alert.show();
    }

    private void log_out() {
        progressDialog.show();
        String mac_address = global.getDeviceToken(PaymentReceipt.this);
        Call<LogoutResponse> logOut = ApiClient.getInstance().getBookKeepingApi().logOut("Bearer "+auth, fcm_token, mac_address);
        logOut.enqueue(new Callback<LogoutResponse>() {
            @Override
            public void onResponse(Call<LogoutResponse> call, Response<LogoutResponse> response) {
                Log.e("LOG OUT RESPONSE",""+response.code());
                if(response.code() == 200) {
                    progressDialog.dismiss();
                    Manager compDb = new Manager(PaymentReceipt.this);
                    compDb.removeAllCompany();
                    SharedPreferences loginRef = getSharedPreferences("book_keeping", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = loginRef.edit();
                    editor.putString("access_token","");
                    editor.putString("fcm_token","");
                    editor.putBoolean("first_input",true);
                    editor.putInt("userId", 0);
                    editor.commit();
                    startActivity(new Intent(PaymentReceipt.this, LoginScreen.class));
                } else {
                    progressDialog.dismiss();
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        Toast.makeText(PaymentReceipt.this, ""+jObjError.getString("message"), Toast.LENGTH_SHORT).show();
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
        sheetDialog = new BottomSheetDialog(PaymentReceipt.this, R.style.BottomSheetStyle);
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
                    if(global.isNetworkAvailable(PaymentReceipt.this)) {
                        try {
                            newEncrypt = cryptLib.encryptPlainTextWithRandomIV(password.getText().toString(), ApiClient.getInstance().getKey());
                            oldEncrypt = cryptLib.encryptPlainTextWithRandomIV(oldPassword.getText().toString(), ApiClient.getInstance().getKey());
                            passwordUpdate(newEncrypt, oldEncrypt);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    } else {
                        String please_Check_Your_Internet_Connection = getResources().getString(R.string.please_Check_Your_Internet_Connection);

                        Toast.makeText(PaymentReceipt.this, please_Check_Your_Internet_Connection, Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(PaymentReceipt.this, getResources().getString(R.string.password_change_successfully), Toast.LENGTH_SHORT).show();
                    sheetDialog.dismiss();
                } else if (response.code() == 401) {
                    String token = global.reCallList(shared.getString("refresh_token", ""));
                    if(token.equals("Refresh Token Expires")) {
                        clearShare();
                        startActivity(new Intent(PaymentReceipt.this, LoginScreen.class));
                    } else {
                        String vToken = global.validToken(token);
                        if(vToken.equals("Authentication token missing") || vToken.equals("Wrong authentication token")) {
                            clearShare();
                            startActivity(new Intent(PaymentReceipt.this, LoginScreen.class));
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
                        Toast.makeText(PaymentReceipt.this, ""+jObjError.getString("message"), Toast.LENGTH_SHORT).show();
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
        Intent businessDashboard = new Intent(PaymentReceipt.this, BusinessDashboards.class);
        businessDashboard.putExtra("companyId",companyId);
        businessDashboard.putExtra("companyPermission", permission);
        startActivity(businessDashboard);
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