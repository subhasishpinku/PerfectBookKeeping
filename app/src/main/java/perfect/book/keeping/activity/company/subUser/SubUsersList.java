package perfect.book.keeping.activity.company.subUser;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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
import java.util.List;
import java.util.Locale;

import javax.crypto.NoSuchPaddingException;

import de.hdodenhof.circleimageview.CircleImageView;
import perfect.book.keeping.R;
import perfect.book.keeping.activity.Companies;
import perfect.book.keeping.activity.LoginScreen;
import perfect.book.keeping.activity.company.BusinessDashboards;
import perfect.book.keeping.activity.company.gallery.PendingGallery;
import perfect.book.keeping.activity.company.gallery.RejectGallery;
import perfect.book.keeping.activity.pnl.ProfitLoss;
import perfect.book.keeping.activity.profile.Profile;
import perfect.book.keeping.adapter.SubUserListAdapter;
import perfect.book.keeping.api.ApiClient;
import perfect.book.keeping.encrypt.CryptLib;
import perfect.book.keeping.global.Global;
import perfect.book.keeping.global.LocaleHelper;
import perfect.book.keeping.global.RefreshToken;
import perfect.book.keeping.manager.Manager;
import perfect.book.keeping.model.SubUser;
import perfect.book.keeping.response.ChangePassword;
import perfect.book.keeping.response.CurrencyResponse;
import perfect.book.keeping.response.LogoutResponse;
import perfect.book.keeping.response.SubUserList;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SubUsersList extends AppCompatActivity {

    RecyclerView subUser;
    String auth, fcm_token;
    SharedPreferences shared;

    Global global = new Global();

    List<SubUser> subUserList = new ArrayList<>();
    SubUserListAdapter subUserAdapter;

    int compId, userType = 0, permission;
    String compName;

    ImageView add_sub_user;

    RefreshToken refreshToken;

    BottomSheetDialog sheetDialog;

    EditText password, cnfPassword, oldPassword;

    ImageView eye_btn_fp, eye_btn_fp_old, eye_btn_cnf;

    LinearLayout updatePass;

    CryptLib cryptLib;

    String newEncrypt, oldEncrypt, company_currency;

    LinearLayout openMenu, back, addSubUser;

    Dialog progressDialog;

    TextView header_title;

    Manager db;

    RelativeLayout noUsers;
    CircleImageView profile_image;
    private List<Integer> userIds = new ArrayList<Integer>();
    EditText searchBy;
    ImageView openSearch;
    SwipeRefreshLayout swipeRefresh;
    private List<String> currencyIds = new ArrayList<String>();
    Locale myLocale;
    String codeLang;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_users);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        android.graphics.drawable.Drawable background = SubUsersList.this.getDrawable(R.drawable.gradiant_btn_alt);
        getWindow().setBackgroundDrawable(background);

        header_title = findViewById(R.id.header_title);
        subUser = findViewById(R.id.subUser);
        openMenu = findViewById(R.id.openMenu);
        back = findViewById(R.id.back);
        noUsers = findViewById(R.id.noUsers);
        swipeRefresh = findViewById(R.id.swipeRefresh);
        shared = getSharedPreferences("book_keeping", Context.MODE_PRIVATE);
        auth = shared.getString("access_token", "");
        fcm_token = shared.getString("fcm_token","");
        userType = shared.getInt("userType",0);
        profile_image = findViewById(R.id.profile_image);
        addSubUser = findViewById(R.id.addSubUser);
        searchBy = findViewById(R.id.searchBy);
        db = new Manager(SubUsersList.this);
        header_title.setText("Users");

        progressDialog = new Dialog(SubUsersList.this);
        progressDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        progressDialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        progressDialog.setContentView(R.layout.progress);
        if (progressDialog.getWindow() != null) {
            progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        try {
            cryptLib = new CryptLib();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (NoSuchPaddingException e) {
            throw new RuntimeException(e);
        }

        refreshToken = new RefreshToken();
        compId = getIntent().getIntExtra("companyId",0);
        compName = getIntent().getStringExtra("companyName");
        permission = getIntent().getIntExtra("permission", 0);

        shared = getSharedPreferences("book_keeping", Context.MODE_PRIVATE);
        codeLang = shared.getString("code_lang", "");
        setLocale(codeLang);

        Cursor cursor = db.fetchCompanyImage(compId);
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
        }
        openMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopUp(v);
            }
        });

        if (global.isNetworkAvailable(SubUsersList.this)) {
            saveCurrencies();
        }

        retrieveData();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redirect();
            }
        });

        searchBy.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(subUserList.size() > 0) {
                    subUserAdapter.getFilter().filter(s);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        addSubUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(global.isNetworkAvailable(SubUsersList.this)) {
                    Intent subUsers = new Intent(SubUsersList.this, SubUsersStore.class);
                    subUsers.putExtra("companyId", compId);
                    subUsers.putExtra("companyName", compName);
                    subUsers.putExtra("permission", permission);
                    subUsers.putExtra("mode", "User");
                    startActivity(subUsers);
                } else {
                    Toast.makeText(SubUsersList.this, getResources().getString(R.string.please_Check_Your_Internet_Connection), Toast.LENGTH_SHORT).show();
                }
            }
        });

        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(global.isNetworkAvailable(SubUsersList.this)) {
                    Intent intent = getIntent();
                    finish(); // Close the current activity
                    startActivity(intent); // Start the activity again
                    //callSubUserSave("Bearer " + auth);
                } else {
                    progressDialog.dismiss();
                    subUser.setVisibility(View.GONE);
                    noUsers.setVisibility(View.VISIBLE);
                }
                swipeRefresh.setRefreshing(false);
            }
        });
    }

    public void retrieveData() {
        Cursor user = db.fetchUser(compId);
        if(user.getCount() > 0) {
            progressDialog.show();
           // syncData();
            callSubUserSave("Bearer " + auth);
        } else {
            if(global.isNetworkAvailable(SubUsersList.this)) {
                callSubUserSave("Bearer " + auth);
            } else {
                progressDialog.dismiss();
                subUser.setVisibility(View.GONE);
                noUsers.setVisibility(View.VISIBLE);
            }
        }
    }

    private void syncData() {
        subUserList.clear();
        Cursor userRec = db.fetchUser(compId);
        while (userRec.moveToNext()) {
            SubUser users = new SubUser(
                    userRec.getString(2),
                    userRec.getString(3),
                    userRec.getString(2),
                    userRec.getInt(1),
                    userRec.getInt(4),
                    compName,
                    Double.parseDouble(userRec.getString(6)),
                    userRec.getString(7),
                    permission,
                    userRec.getString(8),
                    userRec.getString(9),
                    userRec.getInt(5),
                    company_currency,
                    userRec.getInt(10),
                    userRec.getInt(11));
            subUserList.add(users);
        }
        if (subUserList.size() > 0) {
            progressDialog.dismiss();
            noUsers.setVisibility(View.GONE);
            subUser.setVisibility(View.VISIBLE);
            subUserAdapter = new SubUserListAdapter(subUserList, SubUsersList.this);
            subUser.setAdapter(subUserAdapter);
            subUser.setLayoutManager(new LinearLayoutManager(SubUsersList.this));
            subUserAdapter.notifyDataSetChanged();
        } else {
            progressDialog.dismiss();
            subUser.setVisibility(View.GONE);
            noUsers.setVisibility(View.VISIBLE);
        }
    }

    private void saveCurrencies() {
        progressDialog.show();
        Call<CurrencyResponse> currencyResponse = ApiClient.getInstance().getBookKeepingApi().getCurrency();
        currencyResponse.enqueue(new Callback<CurrencyResponse>() {
            @Override
            public void onResponse(Call<CurrencyResponse> call, Response<CurrencyResponse> response) {
                if(response.code() == 200) {
                    Log.e("RESPONSE",""+response.code());
                    for(int i = 0 ; i < response.body().getData().size(); i++) {
                        currencyIds.add("'"+response.body().getData().get(i).getCode()+"'");
                        Cursor checkCurrencies = db.checkCurrency(response.body().getData().get(i).getCode());
                        if(checkCurrencies.getCount() > 0) {
                            //Update
                            db.updateCurrencies(response.body().getData().get(i).getName(), response.body().getData().get(i).getCode(), response.body().getData().get(i).getSymbol());
                        } else {
                            //Insert
                            db.saveCurrency(response.body().getData().get(i).getName(), response.body().getData().get(i).getCode(), response.body().getData().get(i).getSymbol());
                        }
                    }
                    String currencyId = String.valueOf(currencyIds);
                    String currencyIdOld = currencyId.replace("[", "(");
                    String currencyIdNew = currencyIdOld.replace("]", ")");
                    String currencyIdNewSpaceRemove = currencyIdNew.replace(" ", "");
                    Log.e("IMAGE IDS",""+currencyIdNewSpaceRemove);
                    db.removeCurrency(currencyIdNewSpaceRemove);
                    progressDialog.dismiss();
                } else if (response.code() == 401) {
                    String token = global.reCallList(shared.getString("refresh_token", ""));
                    if(token.equals("Refresh Token Expires")) {
                        clearShare();
                        startActivity(new Intent(SubUsersList.this, LoginScreen.class));
                    } else {
                        String vToken = global.validToken(token);
                        if(vToken.equals("Authentication token missing") || vToken.equals("Wrong authentication token")) {
                            clearShare();
                            startActivity(new Intent(SubUsersList.this, LoginScreen.class));
                        } else {
                            auth = vToken;
                            updateShare(vToken);
                            saveCurrencies();
                        }
                    }
                } else {
                    progressDialog.dismiss();
                    try {
                        JSONObject jObjError = new  JSONObject(response.errorBody().string());
                        Toast.makeText(SubUsersList.this, ""+jObjError.getString("message"), Toast.LENGTH_SHORT).show();
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            @Override
            public void onFailure(Call<CurrencyResponse> call, Throwable t) {

            }
        });
    }

    public void callSubUserSave(String token) {
        progressDialog.show();
        Call<SubUserList> call = ApiClient.getInstance().getBookKeepingApi().subUserList(token, String.valueOf(compId), "", "");
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<SubUserList> call, Response<SubUserList> response) {
                if (response.code() == 200) {
                    if(response.body().getData().size() > 0) {
                        for (int i = 0; i < response.body().getData().size(); i++) {
                            userIds.add(response.body().getData().get(i).getId());
                            Cursor checkUser = db.checkUser(compId, response.body().getData().get(i).getId());
                            if (checkUser.getCount() > 0) {
                                //Update Query
                                String sub_user_name;
                                if (response.body().getData().get(i).getUserCompanyPermissions().get(0).getSubuserName() != null) {
                                    sub_user_name = response.body().getData().get(i).getUserCompanyPermissions().get(0).getSubuserName();
                                } else {
                                    sub_user_name = "";
                                }
                                db.updateUser(response.body().getData().get(i).getId(),
                                        response.body().getData().get(i).getName(),
                                        response.body().getData().get(i).getEmail(),
                                        response.body().getData().get(i).getUserCompanyPermissions().get(0).getCompanyId(),
                                        response.body().getData().get(i).getUserCompanyPermissions().get(0).getRoleId(),
                                        response.body().getData().get(i).getUserCompanyPermissions().get(0).getAmount(),
                                        response.body().getData().get(i).getUserCompanyPermissions().get(0).getCurrency(),
                                        sub_user_name,
                                        response.body().getData().get(i).getUserCompanyPermissions().get(0).getRole().getName(),
                                        response.body().getData().get(i).getUserCompanyPermissions().get(0).getAddedBy(),
                                        response.body().getData().get(i).getIsLogin());
                            } else {
                                //Insert Query
                                String sub_user_name;
                                if (response.body().getData().get(i).getUserCompanyPermissions().get(0).getSubuserName() != null) {
                                    sub_user_name = response.body().getData().get(i).getUserCompanyPermissions().get(0).getSubuserName();
                                } else {
                                    sub_user_name = "";
                                }
                                db.addUser(response.body().getData().get(i).getId(),
                                        response.body().getData().get(i).getName(),
                                        response.body().getData().get(i).getEmail(),
                                        response.body().getData().get(i).getUserCompanyPermissions().get(0).getCompanyId(),
                                        response.body().getData().get(i).getUserCompanyPermissions().get(0).getRoleId(),
                                        response.body().getData().get(i).getUserCompanyPermissions().get(0).getAmount(),
                                        response.body().getData().get(i).getUserCompanyPermissions().get(0).getCurrency(),
                                        sub_user_name,
                                        response.body().getData().get(i).getUserCompanyPermissions().get(0).getRole().getName(),
                                        response.body().getData().get(i).getUserCompanyPermissions().get(0).getAddedBy(),
                                        response.body().getData().get(i).getIsLogin());
                            }
                            String userIDs = String.valueOf(userIds);
                            String userIDsOld = userIDs.replace("[", "(");
                            String userIDsNew = userIDsOld.replace("]", ")");
                            String userIDsSpaceRemove = userIDsNew.replace(" ", "");
                            String removeResponse = db.removeUser(compId, userIDsSpaceRemove);
                            progressDialog.dismiss();
                            subUserList.clear();
                            syncData();
                        }
                    } else {
                        progressDialog.dismiss();
                        subUserList.clear();
                        syncData();
                    }
                } else if (response.code() == 401){
                    progressDialog.dismiss();
                    String token = global.reCallList(shared.getString("refresh_token", ""));
                    if(token.equals("Refresh Token Expires")) {
                        clearShare();
                        startActivity(new Intent(SubUsersList.this, LoginScreen.class));
                    } else {
                        String vToken = global.validToken(token);
                        if(vToken.equals("Authentication token missing") || vToken.equals("Wrong authentication token")) {
                            clearShare();
                            startActivity(new Intent(SubUsersList.this, LoginScreen.class));
                        } else {
                            auth = vToken;
                            updateShare(vToken);
                            callSubUserSave("Bearer " + auth);
                        }
                    }
                } else {
                    progressDialog.dismiss();
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        Toast.makeText(SubUsersList.this, ""+jObjError.getString("message"), Toast.LENGTH_SHORT).show();
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            @Override
            public void onFailure(Call<SubUserList> call, Throwable t) {

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
        Manager db = new Manager(SubUsersList.this);
        db.removeAllCompany();
    }

    public void updateShare(String token) {
        SharedPreferences sharedPreferences = getSharedPreferences("book_keeping", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("access_token", token);
        editor.commit();
    }


    private void showPopUp(View v) {
        PopupMenu popupMenu = new PopupMenu(SubUsersList.this, v);
        popupMenu.getMenuInflater().inflate(R.menu.navigation_item, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();
                if(id == R.id.companyList) {
                    //startActivity(new Intent(SubUsersList.this, Companies.class));
                    Intent company = new Intent(SubUsersList.this, Companies.class);
                    company.putExtra("enable","0");
                    startActivity(company);
                } else if(id == R.id.profileDtl) {
                    startActivity(new Intent(SubUsersList.this, Profile.class));
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
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(SubUsersList.this);
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
                Manager compDb = new Manager(SubUsersList.this);
                compDb.removeAllCompany();
                SharedPreferences loginRef = getSharedPreferences("book_keeping", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = loginRef.edit();
                editor.putString("access_token","");
                editor.putString("fcm_token","");
                editor.putBoolean("first_input",true);
                editor.putInt("userId", 0);
                editor.commit();
                //ClearAppData.clearApplicationData(SubUsersList.this, getPackageName());
                startActivity(new Intent(SubUsersList.this, LoginScreen.class));

            }
        });
        android.app.AlertDialog alert = builder.create();
        alert.show();
    }

    private void log_out() {
        progressDialog.show();
        String mac_address = global.getDeviceToken(SubUsersList.this);
        Call<LogoutResponse> logOut = ApiClient.getInstance().getBookKeepingApi().logOut("Bearer "+auth, fcm_token, mac_address);
        logOut.enqueue(new Callback<LogoutResponse>() {
            @Override
            public void onResponse(Call<LogoutResponse> call, Response<LogoutResponse> response) {
                Log.e("LOG OUT RESPONSE",""+response.code());
                if(response.code() == 200) {
                    progressDialog.dismiss();
                    Manager compDb = new Manager(SubUsersList.this);
                    compDb.removeAllCompany();
                    SharedPreferences loginRef = getSharedPreferences("book_keeping", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = loginRef.edit();
                    editor.putString("access_token","");
                    editor.putString("fcm_token","");
                    editor.putBoolean("first_input",true);
                    editor.putInt("userId", 0);
                    editor.commit();
                    startActivity(new Intent(SubUsersList.this, LoginScreen.class));
                } else {
                    progressDialog.dismiss();
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        Toast.makeText(SubUsersList.this, ""+jObjError.getString("message"), Toast.LENGTH_SHORT).show();
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
        sheetDialog = new BottomSheetDialog(SubUsersList.this, R.style.BottomSheetStyle);
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
                if(oldPassword.getText().toString().equals("")) {
                    eye_btn_fp_old.setVisibility(View.GONE);
                    oldPassword.setError(getResources().getString(R.string.please_enter_your_current_password));
                } else if(password.getText().toString().equals("")) {
                    eye_btn_fp.setVisibility(View.GONE);
                    password.setError(getResources().getString(R.string.please_enter_new_password));
                } else if (password.getText().toString().length() < 6) {
                    eye_btn_fp.setVisibility(View.GONE);
                    password.setError(getResources().getString(R.string.new_password_must_be_6_character_long));
                } else if(cnfPassword.getText().toString().equals("")) {
                    eye_btn_cnf.setVisibility(View.GONE);
                    cnfPassword.setError(getResources().getString(R.string.please_enter_the_new_password_again));
                } else if (cnfPassword.getText().toString().length() < 6) {
                    eye_btn_cnf.setVisibility(View.GONE);
                    cnfPassword.setError(getResources().getString(R.string.please_enter_the_new_password_again));
                } else if(password.getText().toString().equals(cnfPassword.getText().toString())) {
                    eye_btn_cnf.setVisibility(View.VISIBLE);
                    eye_btn_fp_old.setVisibility(View.VISIBLE);
                    eye_btn_fp.setVisibility(View.VISIBLE);
                    if(global.isNetworkAvailable(SubUsersList.this)) {
                        try {
                            newEncrypt = cryptLib.encryptPlainTextWithRandomIV(password.getText().toString(), ApiClient.getInstance().getKey());
                            oldEncrypt = cryptLib.encryptPlainTextWithRandomIV(oldPassword.getText().toString(), ApiClient.getInstance().getKey());
                            passwordUpdate(newEncrypt, oldEncrypt);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    } else {
                        Toast.makeText(SubUsersList.this, getResources().getString(R.string.please_Check_Your_Internet_Connection), Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(SubUsersList.this, getResources().getString(R.string.password_change_successfully), Toast.LENGTH_SHORT).show();
                    sheetDialog.dismiss();
                } else if (response.code() == 401) {
                    String token = global.reCallList(shared.getString("refresh_token", ""));
                    if(token.equals("Refresh Token Expires")) {
                        clearShare();
                        startActivity(new Intent(SubUsersList.this, LoginScreen.class));
                    } else {
                        String vToken = global.validToken(token);
                        if(vToken.equals("Authentication token missing") || vToken.equals("Wrong authentication token")) {
                            clearShare();
                            startActivity(new Intent(SubUsersList.this, LoginScreen.class));
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
                        Toast.makeText(SubUsersList.this, ""+jObjError.getString("message"), Toast.LENGTH_SHORT).show();
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

    public void redirect(){
        Intent subUsers = new Intent(SubUsersList.this, BusinessDashboards.class);
        subUsers.putExtra("companyId",compId);
        subUsers.putExtra("companyName",compName);
        subUsers.putExtra("companyPermission",permission);
        startActivity(subUsers);
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