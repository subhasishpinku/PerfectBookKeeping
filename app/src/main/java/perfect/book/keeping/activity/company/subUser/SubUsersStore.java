package perfect.book.keeping.activity.company.subUser;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.dynamic.IFragmentWrapper;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.switchmaterial.SwitchMaterial;

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
import perfect.book.keeping.activity.Account;
import perfect.book.keeping.activity.Companies;
import perfect.book.keeping.activity.LoginScreen;
import perfect.book.keeping.activity.company.AddCompany;
import perfect.book.keeping.activity.company.BusinessDashboards;
import perfect.book.keeping.activity.company.gallery.PendingGallery;
import perfect.book.keeping.activity.profile.Profile;
import perfect.book.keeping.adapter.CompanyListAdapter;
import perfect.book.keeping.adapter.SelectedCompanies;
import perfect.book.keeping.api.ApiClient;
import perfect.book.keeping.encrypt.CryptLib;
import perfect.book.keeping.global.ClearAppData;
import perfect.book.keeping.global.Global;
import perfect.book.keeping.global.LocaleHelper;
import perfect.book.keeping.manager.Manager;
import perfect.book.keeping.model.CompaniesModel;
import perfect.book.keeping.model.FilterUser;
import perfect.book.keeping.model.RoleList;
import perfect.book.keeping.model.SearchUserNameEmail;
import perfect.book.keeping.request.SubUserRequest;
import perfect.book.keeping.response.ChangePassword;
import perfect.book.keeping.response.LogoutResponse;
import perfect.book.keeping.response.Role;
import perfect.book.keeping.response.SubUser;
import perfect.book.keeping.response.SubUserList;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SubUsersStore extends AppCompatActivity {
    EditText emailAddress, name, searchBy;

    TextView company;

    LinearLayout sectionPermission;

    TextView store_sub_user;

    RecyclerView permission, company_list,nameSearch,emailSearch;

    Dialog progressDialog;

    AlertDialog alertDialog;

    List<CompaniesModel> companies = new ArrayList<>();

    CompanyListAdapter companyListAdapter;

    SelectedCompanies selectedCompanies;

    String auth, fcm_token;

    SharedPreferences shared;

    public List<String> selectedComp = new ArrayList<String>();

    CryptLib cryptLib;

    int companyId, userType = 0;

    SwitchMaterial viewPermission, viewEditPermission;

    int view = 1, permissions;

    String emailPattern = "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +

            "\\@" +

            "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +

            "(" +

            "\\." +

            "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +

            ")+";

    String newEncrypt, oldEncrypt, permission_name;

    LinearLayout openMenu;

    EditText passwords, cnfPassword, oldPassword, password;

    ImageView eye_btn_fp, eye_btn_fp_old, eye_btn_cnf;

    BottomSheetDialog sheetDialog;

    LinearLayout updatePass, back, tvHeader;

    TextView header_title;
    String compName,mode;
    List<RoleList> roles = new ArrayList<>();
    RoleListAdapter roleListAdapter;
    nameSearchAdapter nameSearchAdapter;
    int givenPermission = 0,package_id, package_price, sub_user_package_price;
    Global global;
    TextView tv,tv1;
    CircleImageView profile_image;
    private List<Integer> userIds = new ArrayList<Integer>();
    Manager db;
    List<SearchUserNameEmail> searchUserNameEmails = new ArrayList<>();
    Locale myLocale;
    String codeLang;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_users_store);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        android.graphics.drawable.Drawable background = SubUsersStore.this.getDrawable(R.drawable.gradiant_btn_alt);
        getWindow().setBackgroundDrawable(background);


        shared = getSharedPreferences("book_keeping", Context.MODE_PRIVATE);
        auth = shared.getString("access_token", "");
        fcm_token = shared.getString("fcm_token","");
        userType = shared.getInt("userType",0);

        shared = getSharedPreferences("book_keeping", Context.MODE_PRIVATE);
        codeLang = shared.getString("code_lang", "");
        setLocale(codeLang);

        global = new Global();

        header_title = findViewById(R.id.header_title);
        name = findViewById(R.id.name);
        emailAddress = findViewById(R.id.emailAddress);
        company = findViewById(R.id.company);
        password = findViewById(R.id.password);
        openMenu = findViewById(R.id.openMenu);
        store_sub_user = findViewById(R.id.store_sub_user);
        permission = findViewById(R.id.permission);
        nameSearch = findViewById(R.id.nameSearch);
        emailSearch = findViewById(R.id.emailSearch);
        back = findViewById(R.id.back);
        tvHeader = findViewById(R.id.tvHeader);
        tv = findViewById(R.id.tv);
        profile_image = findViewById(R.id.profile_image);
        header_title.setText("Add Users");
        db = new Manager(SubUsersStore.this);
        companyId = getIntent().getIntExtra("companyId",0);
        compName = getIntent().getStringExtra("companyName");
        permissions = getIntent().getIntExtra("permission", 0);
        mode = getIntent().getStringExtra("mode");
        System.out.println("CheckBox"+mode);
        Manager compDb = new Manager(SubUsersStore.this);
        company.setText(compName);


        Cursor cursor = compDb.fetchCompanyImage(companyId);
        while (cursor.moveToNext()) {
            package_id = cursor.getInt(7);
            package_price = cursor.getInt(8);
            sub_user_package_price = cursor.getInt(9);
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
        if(package_id == 1) {
            tvHeader.setVisibility(View.VISIBLE);
            String packagePrice = String.valueOf(package_price); // Replace with your actual package price
            String currency = getResources().getString(R.string.usd);
            String currency_alt = getResources().getString(R.string.plus_USD);
            String plus_mod = getResources().getString(R.string.plus);
            String subUserPackagePrice = String.valueOf(sub_user_package_price);
            String receiptText = getResources().getString(R.string.receipt_App_ONLY);
            String noteText = getResources().getString(R.string.note_Your_subscription_for_this_company_is)+receiptText+getResources().getString(R.string.it_is) + currency + " "+ package_price + getResources().getString(R.string.per_months) + currency_alt + " "+sub_user_package_price + getResources().getString(R.string.per_month_for_each_additional_sub_user);
            SpannableString spannableString = new SpannableString(noteText);
             // Replace with your actual sub-user package price
            // Set color for the "Note" part
            int noteColor = Color.BLACK; // You can change this to any color you want
            ForegroundColorSpan noteColorSpan = new ForegroundColorSpan(noteColor);
            spannableString.setSpan(noteColorSpan, 0, 5, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            // Set color for the USD
            int currencyColor = Color.BLUE; // You can change this to any color you want
            ForegroundColorSpan currencyColorSpan = new ForegroundColorSpan(currencyColor);
            int currencyStart = noteText.indexOf(currency);
            int currencyEnd = currencyStart + currency.length();
            spannableString.setSpan(currencyColorSpan, currencyStart, currencyEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            // Set color for the USD ALT
            int currency_altColor = Color.BLUE; // You can change this to any color you want
            ForegroundColorSpan currency_altColorSpan = new ForegroundColorSpan(currency_altColor);
            int currency_altStart = noteText.indexOf(currency_alt);
            int currency_altEnd = currency_altStart + currency_alt.length();
            spannableString.setSpan(currency_altColorSpan, currency_altStart, currency_altEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            // Set color for the PLUS ALT
            int plusModColor = Color.RED; // You can change this to any color you want
            ForegroundColorSpan plusModColorSpan = new ForegroundColorSpan(plusModColor);
            int plusModStart = noteText.indexOf(plus_mod);
            int plusModEnd = plusModStart + plus_mod.length();
            spannableString.setSpan(plusModColorSpan, plusModStart, plusModEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            // Set color for the package_price
            int packagePriceColor = Color.BLUE; // You can change this to any color you want
            ForegroundColorSpan packagePriceColorSpan = new ForegroundColorSpan(packagePriceColor);
            int packagePriceStart = noteText.indexOf(packagePrice);
            int packagePriceEnd = packagePriceStart + packagePrice.length();
            spannableString.setSpan(packagePriceColorSpan, packagePriceStart, packagePriceEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            // Set color for the RECEIPT TEXT
            int receiptColor = Color.BLUE; // You can change this to any color you want
            ForegroundColorSpan receiptColorSpan = new ForegroundColorSpan(receiptColor);
            int receiptStart = noteText.indexOf(receiptText);
            int receiptEnd = receiptStart + receiptText.length();
            spannableString.setSpan(receiptColorSpan, receiptStart, receiptEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            // Set color for the sub_user_package_price
            int subUserPackagePriceColor = Color.BLUE; // You can change this to any color you want
            ForegroundColorSpan subUserPackagePriceColorSpan = new ForegroundColorSpan(subUserPackagePriceColor);
            int subUserPackagePriceStart = noteText.indexOf(subUserPackagePrice);
            int subUserPackagePriceEnd = subUserPackagePriceStart + subUserPackagePrice.length();
            spannableString.setSpan(subUserPackagePriceColorSpan, subUserPackagePriceStart, subUserPackagePriceEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            tv.setText(spannableString);
        } else {
            tvHeader.setVisibility(View.GONE);
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

        try {
            cryptLib = new CryptLib();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (NoSuchPaddingException e) {
            throw new RuntimeException(e);
        }

        progressDialog = new Dialog(SubUsersStore.this);
        progressDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        progressDialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        progressDialog.setContentView(R.layout.progress);
        if (progressDialog.getWindow() != null) {
            progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        getRole(companyId);

        store_sub_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(name.getText().toString().equals("")) {
                    name.setError(getResources().getString(R.string.please_enter_name));
                } else if (emailAddress.getText().toString().equals("")) {
                    emailAddress.setError(getResources().getString(R.string.please_enter_email_address));
                } else if(!emailAddress.getText().toString().matches(emailPattern)) {
                    emailAddress.setError(getResources().getString(R.string.please_enter_valid_email_address));
                } else if (givenPermission == 0) {
                    Toast.makeText(SubUsersStore.this, getResources().getString(R.string.please_select_any_one_permissions), Toast.LENGTH_SHORT).show();
                } else {
                    save_subUser();
                }
            }
        });
        nameSearch.setVisibility(View.GONE);
        emailSearch.setVisibility(View.GONE);
        name.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    nameSearch.setVisibility(View.VISIBLE);
                    emailSearch.setVisibility(View.GONE);
                    searchUserNameEmails.clear();
                    LoadList("ModeName");
                } else {
                    nameSearch.setVisibility(View.GONE);
                    emailSearch.setVisibility(View.GONE);

                }
            }
        });

        emailAddress.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    nameSearch.setVisibility(View.GONE);
                    emailSearch.setVisibility(View.VISIBLE);
                    searchUserNameEmails.clear();
                    LoadList("ModeEmail");
                } else {
                    nameSearch.setVisibility(View.GONE);
                    emailSearch.setVisibility(View.GONE);

                }
            }
        });
        name.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {}

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if(searchUserNameEmails.size() > 0) {
                    nameSearchAdapter.getFilter().filter(s);
                }
//                nameSearch.setVisibility(View.VISIBLE);
//                emailSearch.setVisibility(View.GONE);
//                searchUserNameEmails.clear();
//                if(s.length() == 3){
//                    String name= String.valueOf(s);
//                    System.out.println("NAMES"+name);
//                   // nameEmailWishSearch(name);
//                    LoadList();
//                }
//                if(s.length() == 0){
//                    searchUserNameEmails.clear();
//                    nameSearch.setVisibility(View.GONE);
//                }
            }
        });
        emailAddress.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {}

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if(searchUserNameEmails.size() > 0) {
                    nameSearchAdapter.getFilter().filter(s);
                }
//                nameSearch.setVisibility(View.GONE);
//                emailSearch.setVisibility(View.VISIBLE);
//                searchUserNameEmails.clear();
//                if(s.length() == 3){
//                    String email= String.valueOf(s);
//                    System.out.println("NAMES"+email);
//                    //nameEmailWishSearch(email);
//
//                }
//                if(s.length() == 0){
//                    searchUserNameEmails.clear();
//                    emailSearch.setVisibility(View.GONE);
//                }

            }
        });



    }
    private void nameEmailWishSearch(String nameSearchs) {
        Cursor subUser = db.searchUser(nameSearchs);
        if(subUser.getCount() > 0) {
            while (subUser.moveToNext()) {
                String names = subUser.getString(2);
                String emails = subUser.getString(3);
                searchUserNameEmails.add(new SearchUserNameEmail(subUser.getString(1),
                        subUser.getString(2),
                        subUser.getString(3),
                        subUser.getString(8)
                ));
            }

            if (searchUserNameEmails.size() > 0) {
                nameSearchAdapter = new nameSearchAdapter(searchUserNameEmails, SubUsersStore.this);
                nameSearch.setAdapter(nameSearchAdapter);
                nameSearch.setLayoutManager(new LinearLayoutManager(SubUsersStore.this));
                nameSearchAdapter.notifyDataSetChanged();
                //
                nameSearchAdapter = new nameSearchAdapter(searchUserNameEmails, SubUsersStore.this);
                emailSearch.setAdapter(nameSearchAdapter);
                emailSearch.setLayoutManager(new LinearLayoutManager(SubUsersStore.this));
                nameSearchAdapter.notifyDataSetChanged();
            }
        }
    }
    private void LoadList(String mode){
        Cursor subUser = db.fetchAllUser(companyId);
        if(subUser.getCount() > 0) {
            while (subUser.moveToNext()) {
                searchUserNameEmails.add(new SearchUserNameEmail(subUser.getString(1),
                        subUser.getString(2),
                        subUser.getString(3),
                        subUser.getString(8)
                ));
            }
            if (searchUserNameEmails.size() > 0) {
                if (mode.equals("ModeName")) {
                    nameSearchAdapter = new nameSearchAdapter(searchUserNameEmails, SubUsersStore.this);
                    nameSearch.setAdapter(nameSearchAdapter);
                    nameSearch.setLayoutManager(new LinearLayoutManager(SubUsersStore.this));
                    nameSearchAdapter.notifyDataSetChanged();
                }
                if (mode.equals("ModeEmail")) {
                    nameSearchAdapter = new nameSearchAdapter(searchUserNameEmails, SubUsersStore.this);
                    emailSearch.setAdapter(nameSearchAdapter);
                    emailSearch.setLayoutManager(new LinearLayoutManager(SubUsersStore.this));
                    nameSearchAdapter.notifyDataSetChanged();
                }
            }
        }
    }
    private void getRole(int companyId) {
        Call<Role> getRole = ApiClient.getInstance().getBookKeepingApi().getRole("Bearer "+auth, companyId);
        getRole.enqueue(new Callback<Role>() {
            @Override
            public void onResponse(Call<Role> call, Response<Role> response) {
                if(response.code() == 200) {
                    for(int i = 0; i < response.body().getData().size(); i++) {
                        roles.add(new RoleList(response.body().getData().get(i).getId(), response.body().getData().get(i).getName(),response.body().getData().get(i).getDescription()));
                    }
                    if(roles.size() > 0) {
                        progressDialog.dismiss();
                        roleListAdapter = new RoleListAdapter(roles, SubUsersStore.this, 0);
                        permission.setAdapter(roleListAdapter);
                        permission.setLayoutManager(new LinearLayoutManager(SubUsersStore.this));
                        roleListAdapter.notifyDataSetChanged();
                    }
                } else if (response.code() == 401) {
                    String token = global.reCallList(shared.getString("refresh_token", ""));
                    if(token.equals("Refresh Token Expires")) {
                        clearShare();
                        startActivity(new Intent(SubUsersStore.this, LoginScreen.class));
                    } else {
                        String vToken = global.validToken(token);
                        if(vToken.equals("Authentication token missing") || vToken.equals("Wrong authentication token")) {
                            clearShare();
                            startActivity(new Intent(SubUsersStore.this, LoginScreen.class));
                        } else {
                            auth = vToken;
                            updateShare(vToken);
                            getRole(companyId);
                        }
                    }
                } else {
                    try {
                        JSONObject jObjError = new  JSONObject(response.errorBody().string());
                        Toast.makeText(SubUsersStore.this, ""+jObjError.getString("message"), Toast.LENGTH_SHORT).show();
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            @Override
            public void onFailure(Call<Role> call, Throwable t) {

            }
        });
    }
    private void save_subUser() {
        progressDialog.show();
        SubUserRequest subUserRequest = new SubUserRequest(emailAddress.getText().toString(), companyId, givenPermission, name.getText().toString());
        Call<SubUser> sub_user = ApiClient.getInstance().getBookKeepingApi().subUser("Bearer "+auth, subUserRequest);
        sub_user.enqueue(new Callback<SubUser>() {
            @Override
            public void onResponse(Call<SubUser> call, Response<SubUser> response) {
                Log.e("RESPONSE ADD", ""+response.code());
                if(response.code() == 200) {
                    callSubUserSave("Bearer "+auth);
                    showAlertDialog(response.body().getMessage());
                } else if (response.code() == 401) {
                    String token = global.reCallList(shared.getString("refresh_token", ""));
                    if(token.equals("Refresh Token Expires")) {
                        clearShare();
                        startActivity(new Intent(SubUsersStore.this, LoginScreen.class));
                    } else {
                        String vToken = global.validToken(token);
                        if(vToken.equals("Authentication token missing") || vToken.equals("Wrong authentication token")) {
                            clearShare();
                            startActivity(new Intent(SubUsersStore.this, LoginScreen.class));
                        } else {
                            auth = vToken;
                            updateShare(vToken);
                            save_subUser();
                        }
                    }
                } else {
                    progressDialog.dismiss();
                    try {
                        JSONObject jObjError = new  JSONObject(response.errorBody().string());
                        Toast.makeText(SubUsersStore.this, ""+jObjError.getString("message"), Toast.LENGTH_SHORT).show();
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            @Override
            public void onFailure(Call<SubUser> call, Throwable t) {

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
        Manager db = new Manager(SubUsersStore.this);
        db.removeAllCompany();
    }
    public void updateShare(String token) {
        SharedPreferences sharedPreferences = getSharedPreferences("book_keeping", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("access_token", token);
        editor.commit();
    }
    public  void showAlertDialog(String message){
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(SubUsersStore.this);
        builder.setMessage(message);
        builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                redirect();
                dialog.dismiss();
            }
        });
        android.app.AlertDialog alert = builder.create();
        alert.show();
    }
    public void callSubUserSave(String token) {
        Call<SubUserList> call = ApiClient.getInstance().getBookKeepingApi().subUserList(token, String.valueOf(companyId), "", "");
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<SubUserList> call, Response<SubUserList> response) {
                if (response.code() == 200) {
                    for (int i = 0; i < response.body().getData().size(); i++) {
                        userIds.add(response.body().getData().get(i).getId());
                        Cursor checkUser = db.checkUser(companyId, response.body().getData().get(i).getId());
                        if(checkUser.getCount() > 0) {
                            //Update Query
                            String sub_user_name;
                            if(response.body().getData().get(i).getUserCompanyPermissions().get(0).getSubuserName() != null)
                            {
                                sub_user_name =response.body().getData().get(i).getUserCompanyPermissions().get(0).getSubuserName();
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
                            if(response.body().getData().get(i).getUserCompanyPermissions().get(0).getSubuserName() != null)
                            {
                                sub_user_name =response.body().getData().get(i).getUserCompanyPermissions().get(0).getSubuserName();
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
                        String removeResponse = db.removeUser(companyId, userIDsSpaceRemove);
                        progressDialog.dismiss();

                    }
                } else if (response.code() == 401) {
                    String token = global.reCallList(shared.getString("refresh_token", ""));
                    if(token.equals("Refresh Token Expires")) {
                        clearShare();
                        startActivity(new Intent(SubUsersStore.this, LoginScreen.class));
                    } else {
                        String vToken = global.validToken(token);
                        if(vToken.equals("Authentication token missing") || vToken.equals("Wrong authentication token")) {
                            clearShare();
                            startActivity(new Intent(SubUsersStore.this, LoginScreen.class));
                        } else {
                            auth = vToken;
                            updateShare(vToken);
                            callSubUserSave(auth);
                        }
                    }
                } else {
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        Toast.makeText(SubUsersStore.this, ""+jObjError.getString("message"), Toast.LENGTH_SHORT).show();
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
    public void remove(String companyName) {
        selectedComp.remove(companyName);
    }
    private void showPopUp(View v) {
        PopupMenu popupMenu = new PopupMenu(SubUsersStore.this, v);
        popupMenu.getMenuInflater().inflate(R.menu.navigation_item, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();
                if(id == R.id.companyList) {
                    //startActivity(new Intent(SubUsersStore.this, perfect.book.keeping.activity.Companies.class));
                    Intent company = new Intent(SubUsersStore.this, Companies.class);
                    company.putExtra("enable","0");
                    startActivity(company);
                } else if(id == R.id.profileDtl) {
                    startActivity(new Intent(SubUsersStore.this, Profile.class));
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
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(SubUsersStore.this);
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
                Manager compDb = new Manager(SubUsersStore.this);
                compDb.removeAllCompany();
                SharedPreferences loginRef = getSharedPreferences("book_keeping", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = loginRef.edit();
                editor.putString("access_token","");
                editor.putString("fcm_token","");
                editor.putBoolean("first_input",true);
                editor.putInt("userId", 0);
                editor.commit();
                //ClearAppData.clearApplicationData(SubUsersStore.this, getPackageName());
                startActivity(new Intent(SubUsersStore.this, LoginScreen.class));

            }
        });
        android.app.AlertDialog alert = builder.create();
        alert.show();
    }
    private void log_out() {
        progressDialog.show();
        String mac_address = global.getDeviceToken(SubUsersStore.this);
        Call<LogoutResponse> logOut = ApiClient.getInstance().getBookKeepingApi().logOut("Bearer "+auth, fcm_token, mac_address);
        logOut.enqueue(new Callback<LogoutResponse>() {
            @Override
            public void onResponse(Call<LogoutResponse> call, Response<LogoutResponse> response) {
                Log.e("LOG OUT RESPONSE",""+response.code());
                if(response.code() == 200) {
                    progressDialog.dismiss();
                    Manager compDb = new Manager(SubUsersStore.this);
                    compDb.removeAllCompany();
                    SharedPreferences loginRef = getSharedPreferences("book_keeping", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = loginRef.edit();
                    editor.putString("access_token","");
                    editor.putString("fcm_token","");
                    editor.putBoolean("first_input",true);
                    editor.putInt("userId", 0);
                    editor.commit();
                    startActivity(new Intent(SubUsersStore.this, LoginScreen.class));
                } else {
                    progressDialog.dismiss();
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        Toast.makeText(SubUsersStore.this, ""+jObjError.getString("message"), Toast.LENGTH_SHORT).show();
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
        sheetDialog = new BottomSheetDialog(SubUsersStore.this, R.style.BottomSheetStyle);
        View changePass = getLayoutInflater().inflate(R.layout.change_password, null);

        passwords = changePass.findViewById(R.id.password);
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
                if(passwords.getTransformationMethod().equals(HideReturnsTransformationMethod.getInstance())){
                    passwords.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    eye_btn_fp.setImageResource(R.drawable.hide);
                } else {
                    passwords.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    eye_btn_fp.setImageResource(R.drawable.show);
                }
            }
        });

        passwords.addTextChangedListener(new TextWatcher() {
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
                    if(global.isNetworkAvailable(SubUsersStore.this)) {
                        try {
                            newEncrypt = cryptLib.encryptPlainTextWithRandomIV(password.getText().toString(), ApiClient.getInstance().getKey());
                            oldEncrypt = cryptLib.encryptPlainTextWithRandomIV(oldPassword.getText().toString(), ApiClient.getInstance().getKey());
                            passwordUpdate(newEncrypt, oldEncrypt);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    } else {
                        String please_Check_Your_Internet_Connection = getResources().getString(R.string.please_Check_Your_Internet_Connection);

                        Toast.makeText(SubUsersStore.this, please_Check_Your_Internet_Connection, Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(SubUsersStore.this, getResources().getString(R.string.password_change_successfully), Toast.LENGTH_SHORT).show();
                    sheetDialog.dismiss();
                } else if (response.code() == 401) {
                    String token = global.reCallList(shared.getString("refresh_token", ""));
                    if(token.equals("Refresh Token Expires")) {
                        clearShare();
                        startActivity(new Intent(SubUsersStore.this, LoginScreen.class));
                    } else {
                        String vToken = global.validToken(token);
                        if(vToken.equals("Authentication token missing") || vToken.equals("Wrong authentication token")) {
                            clearShare();
                            startActivity(new Intent(SubUsersStore.this, LoginScreen.class));
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
                        Toast.makeText(SubUsersStore.this, ""+jObjError.getString("message"), Toast.LENGTH_SHORT).show();
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
    @Override
    public void onBackPressed() {
        redirect();
    }
    public void redirect() {
        Intent subUsers = new Intent(SubUsersStore.this, SubUsersList.class);
        subUsers.putExtra("companyId",companyId);
        subUsers.putExtra("companyName",compName);
        subUsers.putExtra("permission", permissions);
        startActivity(subUsers);
    }
    public class RoleListAdapter extends RecyclerView.Adapter<RoleListAdapter.ViewHolder> {
        List<RoleList> roles;
        Context context;
        int selectedPosition = -1;

        int permission;

        public RoleListAdapter(List<RoleList> roles, Context context, int permission) {
            this.roles = roles;
            this.context = context;
            this.permission = permission;
        }


        @NonNull
        @Override
        public RoleListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.role_list, parent, false);
            return new RoleListAdapter.ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull RoleListAdapter.ViewHolder holder, int position) {
            holder.roleName.setText(roles.get(position).getName());

            if(position == selectedPosition) {
                holder.permission.setChecked(true);
            } else if (permission == roles.get(position).getId()) {
                holder.permission.setChecked(true);
            } else {
                if (mode.equals(roles.get(position).getName())){
                    holder.permission.setChecked(true);
                }else {
                    holder.permission.setChecked(false);
                }
            }
            System.out.println("getDescriptionId"+roles.get(position).getDescriptionId());

            if (roles.get(position).getDescriptionId() !=null){
                holder.tvDescriptionId.setVisibility(View.VISIBLE);
                holder.tvDescriptionId.setText(roles.get(position).getDescriptionId());

            }else {
                holder.tvDescriptionId.setVisibility(View.GONE);
            }

        }

        @Override
        public int getItemCount() {
            return roles.size();
        }


        public class ViewHolder extends RecyclerView.ViewHolder {

            SwitchMaterial permission;
            TextView roleName,tvDescriptionId;
            LinearLayout permission_layout;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                permission = (itemView).findViewById(R.id.permission);
                roleName = (itemView).findViewById(R.id.roleName);
                permission_layout = (itemView).findViewById(R.id.permission_layout);
                tvDescriptionId = (itemView).findViewById(R.id.tvDescriptionId);
                permission_layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setItemSelect(getAdapterPosition());
                    }
                });

                permission.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setItemSelect(getAdapterPosition());

                    }
                });
            }
        }

        private void setItemSelect(int adapterPosition) {
            if(adapterPosition == RecyclerView.NO_POSITION) return;
            selectedPosition = adapterPosition;
            notifyDataSetChanged();
            permission = roles.get(adapterPosition).getId();
            givenPermission = roles.get(adapterPosition).getId();
        }
    }
    public class nameSearchAdapter extends RecyclerView.Adapter<nameSearchAdapter.ViewHolder> implements Filterable {

        List<SearchUserNameEmail> searchUserNameEmails;
        List<SearchUserNameEmail> filterSearchUserNameEmails;
        Context context;
        public nameSearchAdapter(List<SearchUserNameEmail> searchUserNameEmails, Context context) {
            this.searchUserNameEmails = searchUserNameEmails;
            this.context = context;
            this.filterSearchUserNameEmails=searchUserNameEmails;
        }


        @NonNull
        @Override
        public nameSearchAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_seach_name_email, parent, false);
            return new nameSearchAdapter.ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull nameSearchAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
            holder.name.setText(searchUserNameEmails.get(position).getUser_name());
            holder.email.setText(searchUserNameEmails.get(position).getUser_email());
            holder.layoutRec.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    name.setText(searchUserNameEmails.get(position).getUser_name());
                    emailAddress.setText(searchUserNameEmails.get(position).getUser_email());
                    nameSearch.setVisibility(View.GONE);
                    emailSearch.setVisibility(View.GONE);
                }
            });

        }
        @Override
        public int getItemCount() {
            return searchUserNameEmails.size();
        }

        @Override
        public Filter getFilter() {
            Filter filter = new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence charSequence) {
                    FilterResults filterResults = new FilterResults();
                    if(charSequence == null || charSequence.length() == 0) {
                        filterResults.values = filterSearchUserNameEmails;
                        filterResults.count = filterSearchUserNameEmails.size();
                    } else {
                        String search = charSequence.toString().toLowerCase();
                        Log.e("SEARCh BY",""+search);
                        List<SearchUserNameEmail> cList = new ArrayList<>();
                        for(SearchUserNameEmail cFilterList: filterSearchUserNameEmails) {
                            if(cFilterList.getUser_name().toLowerCase().contains(search) || cFilterList.getUser_email().toLowerCase().contains(search)) {
                                cList.add(cFilterList);
                            }
                        }
                        filterResults.values = cList;
                        filterResults.count = cList.size();
                    }
                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    searchUserNameEmails = (List<SearchUserNameEmail>) results.values;
                    notifyDataSetChanged();
                }
            };
            return filter;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView name,email;
            LinearLayout layoutRec;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                name = (itemView).findViewById(R.id.name);
                email = (itemView).findViewById(R.id.email);
                layoutRec = (itemView).findViewById(R.id.layoutRec);
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