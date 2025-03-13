package perfect.book.keeping.activity;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.Settings;
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

import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.UpdateAvailability;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.crypto.NoSuchPaddingException;

import perfect.book.keeping.R;
import perfect.book.keeping.activity.company.AddCompany;
import perfect.book.keeping.activity.company.BusinessDashboards;
import perfect.book.keeping.activity.company.gallery.PendingGallery;
import perfect.book.keeping.activity.pnl.ProfitLoss;
import perfect.book.keeping.activity.profile.Profile;
import perfect.book.keeping.adapter.CompaniesAdapter;
import perfect.book.keeping.adapter.PackageAdapter;
import perfect.book.keeping.api.ApiClient;
import perfect.book.keeping.encrypt.CryptLib;
import perfect.book.keeping.global.DataTransferService;
import perfect.book.keeping.global.DownloadTask;
import perfect.book.keeping.global.Global;
import perfect.book.keeping.global.LocaleHelper;
import perfect.book.keeping.global.RefreshToken;
import perfect.book.keeping.manager.Manager;
import perfect.book.keeping.model.BusinessDashboardModel;
import perfect.book.keeping.model.CompaniesModel;
import perfect.book.keeping.model.PackageItem;
import perfect.book.keeping.model.PackageModel;
import perfect.book.keeping.response.ChangePassword;
import perfect.book.keeping.response.CompanyResponse;
import perfect.book.keeping.response.LogoutResponse;
import perfect.book.keeping.response.Packages;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Companies extends AppCompatActivity {

    RecyclerView companyList, companyList_alt;
    List<CompaniesModel> companies = new ArrayList<>();
    private CompaniesAdapter companiesAdapter;
    String auth, fcm_token;
    int password_change_require;
    boolean first_input;
    SharedPreferences shared;
    RelativeLayout company_list;
    TextView addCompany, header_title;
    ImageView add_sub_user;
    RefreshToken refreshToken;
    SwipeRefreshLayout swipeRefresh;
    int counter = 0;
    Dialog progressDialog;
    String imgString;
    Manager compDb;
    Global global;
    LinearLayout openMenu, add_company;
    BottomSheetDialog sheetDialog, packageDialog;
    EditText password, cnfPassword, oldPassword;
    ImageView eye_btn_fp, eye_btn_fp_old, eye_btn_cnf, addCompany_alt;
    LinearLayout updatePass;
    CryptLib cryptLib;
    String newEncrypt, oldEncrypt;
    String timestamp2, timestamp;
    List<PackageModel> packageModels = new ArrayList<>();
    PackageAdapter packageAdapter;
    RecyclerView main_recyclerView;
    LinearLayout continuePackage, back;
    String package_name, package_description, color_code;
    int packageId = 0, package_price, package_id,  price, sub_user_price;
    List<BusinessDashboardModel> businessDashboardList = new ArrayList<>();
    Manager db;
    BottomSheetBehavior<View> bottomSheetBehavior;
    String Enable = "1", existing_file_name;
    private List<Integer> compIds = new ArrayList<Integer>();
    RelativeLayout noCompany;
    EditText searchBy;
    DownloadTask downloadTask;
    private static final int MY_REQUEST_CODE = 100;
    Locale myLocale;
    String codeLang;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_companies);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        android.graphics.drawable.Drawable background = Companies.this.getDrawable(R.drawable.gradiant_btn_alt);
        getWindow().setBackgroundDrawable(background);

        checkForAppUpdate();

        requestPermissionNotification();

        progressDialog = new Dialog(Companies.this);
        progressDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        progressDialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        progressDialog.setContentView(R.layout.progress);
        if (progressDialog.getWindow() != null) {
            progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        header_title = findViewById(R.id.header_title);
       // header_title.setText("Companies");
        openMenu = findViewById(R.id.openMenu);
        noCompany=  findViewById(R.id.noCompany);
        companyList = findViewById(R.id.companyList);
        companyList_alt = findViewById(R.id.companyList_alt);
        company_list = findViewById(R.id.company_list);
        swipeRefresh = findViewById(R.id.swipeRefresh);
        addCompany_alt = findViewById(R.id.addCompany_alt);
        add_company = findViewById(R.id.add_company);
        addCompany = findViewById(R.id.addCompany);
        searchBy = findViewById(R.id.searchBy);
        back = findViewById(R.id.back);
        shared = getSharedPreferences("book_keeping", Context.MODE_PRIVATE);
        auth = shared.getString("access_token", "");
        fcm_token = shared.getString("fcm_token","");
        first_input = shared.getBoolean("first_input", true);
        password_change_require = shared.getInt("password_need_to_change", 0);
        codeLang = shared.getString("code_lang", "");
        setLocale(codeLang);
        Log.e("Password Require Status",""+shared.getInt("password_need_to_change", 0));
        if(getIntent().getStringExtra("enable") != null) {
            Enable = getIntent().getStringExtra("enable");
        } else {
            Enable = "1";
        }

        if(password_change_require == 0) {
            openSheet();
        }

        Log.e("FCM TOKEN",""+fcm_token);

        db = new Manager(Companies.this);
        try {
            cryptLib = new CryptLib();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (NoSuchPaddingException e) {
            throw new RuntimeException(e);
        }

        add_company.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                add_company(v);
            }
        });

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
                if(companies.size() > 0) {
                    companiesAdapter.getFilter().filter(s);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        refreshToken = new RefreshToken();
        global = new Global();

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll()
                .build();
        StrictMode.setThreadPolicy(policy);

        compDb = new Manager(Companies.this);

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

        openMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopUp(v);
            }
        });
        if(global.isNetworkAvailable(Companies.this)) {
            Intent companyImport = new Intent(this, DataTransferService.class);
            startService(companyImport);
            getCompanyPackages();
        }

        retrieveData();
    }
    private void checkForAppUpdate() {
        AppUpdateManager appUpdateManager = AppUpdateManagerFactory.create(this);
        // Returns an intent object that you use to check for an update.
        Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();
        // Checks that the platform will allow the specified type of update.
        appUpdateInfoTask.addOnSuccessListener(appUpdateInfo -> {
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                    // This example applies an immediate update. To apply a flexible update
                    // instead, pass in AppUpdateType.FLEXIBLE
                    && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                    // Request the update.
                    try {
                        appUpdateManager.startUpdateFlowForResult(
                                // Pass the intent that is returned by 'getAppUpdateInfo()'.
                                appUpdateInfo,
                                // Or pass 'AppUpdateType.FLEXIBLE' to newBuilder() for
                                AppUpdateType.IMMEDIATE,
                                this,
                                // flexible updates.
                                MY_REQUEST_CODE);
                    } catch (IntentSender.SendIntentException e) {
                        throw new RuntimeException(e);
                    }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MY_REQUEST_CODE) {
            if (requestCode != RESULT_OK) {
                Log.e("UPDATE RESPONSE", "" + resultCode);
            }
        }
    }

    private void requestPermissionNotification() {
        if(ContextCompat.checkSelfPermission(Companies.this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {

        } else {
            if(shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {

            }
            requestPermission.launch(Manifest.permission.POST_NOTIFICATIONS);
        }
    }
    private ActivityResultLauncher<String> requestPermission = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted->{
        if(!isGranted) {
            showPermissionDialog("Notification Permission");
        }
    });
    private void showPermissionDialog(String notification_permission) {
        new AlertDialog.Builder(Companies.this)
        .setTitle("Alert for Permission")
        .setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                startActivity(intent);
                dialog.dismiss();
            }
        })
        .setNegativeButton("Exit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
    }
    private void getCompanyPackages() {
        progressDialog.show();
        Call<Packages> companyPackage = ApiClient.getInstance().getBookKeepingApi().packages();
        companyPackage.enqueue(new Callback<Packages>() {
            @Override
            public void onResponse(Call<Packages> call, Response<Packages> response) {
                Log.e("Response Package",""+response.code());
                if(response.code() == 200) {
                    for(int i = 0; i < response.body().getData().size(); i++) {
                        List<PackageItem> packageItems = new ArrayList<>();
                        for(int j = 0; j < response.body().getData().get(i).getSubsPackagesData().size(); j++) {
                            packageItems.add(new PackageItem(response.body().getData().get(i).getSubsPackagesData().get(j).getTitle(),
                                    response.body().getData().get(i).getSubsPackagesData().get(j).getId(),
                                    response.body().getData().get(i).getSubsPackagesData().get(j).getShortDesc(),
                                    response.body().getData().get(i).getSubsPackagesData().get(j).getPrice(),
                                    response.body().getData().get(i).getSubsPackagesData().get(j).getLongDesc(),
                                    response.body().getData().get(i).getSubsPackagesData().get(j).getColor_code()
                                    ));
                        }
                        if(i == 0) {
                            packageModels.add(new PackageModel(packageItems, response.body().getData().get(i).getName(), false));
                        } else {
                            packageModels.add(new PackageModel(packageItems, response.body().getData().get(i).getName(), true));
                        }
                        progressDialog.dismiss();
                    }
                } else if (response.code() == 401) {
                    String token = global.reCallList(shared.getString("refresh_token", ""));
                    if(token.equals("Refresh Token Expires")) {
                        clearShare();
                        startActivity(new Intent(Companies.this, LoginScreen.class));
                    } else {
                        String vToken = global.validToken(token);
                        if(vToken.equals("Authentication token missing") || vToken.equals("Wrong authentication token")) {
                            clearShare();
                            startActivity(new Intent(Companies.this, LoginScreen.class));
                        } else {
                            auth = vToken;
                            updateShare(vToken);
                            getCompanyPackages();
                        }
                    }
                } else {
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        Toast.makeText(Companies.this, ""+jObjError.getString("message"), Toast.LENGTH_SHORT).show();
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            @Override
            public void onFailure(Call<Packages> call, Throwable t) {

            }
        });
    }
    @SuppressLint("MissingInflatedId")
    private void add_company(View v) {

        packageDialog = new BottomSheetDialog(Companies.this, R.style.BottomSheetStyle);
        View choosePackage = getLayoutInflater().inflate(R.layout.package_type, null);

        main_recyclerView = choosePackage.findViewById(R.id.main_recyclerView);
        continuePackage = choosePackage.findViewById(R.id.continuePackage);

        if(packageModels.size() > 0) {
            packageAdapter = new PackageAdapter(packageModels, Companies.this);
            main_recyclerView.setLayoutManager(new LinearLayoutManager(Companies.this));
//            main_recyclerView.setHasFixedSize(true);
            main_recyclerView.setAdapter(packageAdapter);
            packageAdapter.notifyDataSetChanged();
        }

        continuePackage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(packageId == 0) {
                    Toast.makeText(Companies.this, getResources().getString(R.string.please_choose_a_package_before_continue), Toast.LENGTH_SHORT).show();
                } else {
                    Intent createComp = new Intent(Companies.this, AddCompany.class);
                    createComp.putExtra("packageId", packageId);
                    createComp.putExtra("package_name", package_name);
                    createComp.putExtra("package_description", package_description);
                    createComp.putExtra("package_price", package_price);
                    createComp.putExtra("color_code", color_code);
                    startActivity(createComp);
                    packageDialog.dismiss();
                    saveDateFormat();
                }
            }
        });
        packageDialog.setContentView(choosePackage);
        bottomSheetBehavior = BottomSheetBehavior.from((View)choosePackage.getParent());
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        packageDialog.show();
    }
    private void saveDateFormat() {
        db.removeDateFormat();
        Call<perfect.book.keeping.response.DateFormat> dateFormatCall = ApiClient.getInstance().getBookKeepingApi().getDateFormat("Bearer "+auth);
        dateFormatCall.enqueue(new Callback<perfect.book.keeping.response.DateFormat>() {
            @Override
            public void onResponse(Call<perfect.book.keeping.response.DateFormat> call, Response<perfect.book.keeping.response.DateFormat> response) {
                if(response.code() == 200) {
                    for(int i = 0 ; i< response.body().getData().size() ; i++) {
                        db.addDateFormat(response.body().getData().get(i).getFormat());
                    }
                } else if(response.code() == 401) {
                    String token = global.reCallList(shared.getString("refresh_token", ""));
                    if(token.equals("Refresh Token Expires")) {
                        clearShare();
                        startActivity(new Intent(Companies.this, LoginScreen.class));
                    } else {
                        String vToken = global.validToken(token);
                        if(vToken.equals("Authentication token missing") || vToken.equals("Wrong authentication token")) {
                            clearShare();
                            startActivity(new Intent(Companies.this, LoginScreen.class));
                        } else {
                            auth = vToken;
                            updateShare(vToken);
                            saveDateFormat();
                        }
                    }
                } else {

                }
            }

            @Override
            public void onFailure(Call<perfect.book.keeping.response.DateFormat> call, Throwable t) {

            }
        });
    }
    public void updatePackageId(int package_id, String package_item_name, String package_item_description, int package_item_price, String color_codes) {
        packageId = package_id;
        package_name = package_item_name;
        package_description = package_item_description;
        package_price = package_item_price;
        color_code = color_codes;
    }
    private void showPopUp(View v) {
        PopupMenu popupMenu = new PopupMenu(Companies.this, v);
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
                   //startActivity(new Intent(Companies.this, Companies.class));
                    Intent company = new Intent(Companies.this, Companies.class);
                    company.putExtra("enable","0");
                    startActivity(company);
                } else if(id == R.id.profileDtl) {
                    startActivity(new Intent(Companies.this, Profile.class));
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
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(Companies.this);
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
                    Manager compDb = new Manager(Companies.this);
                    compDb.removeAllCompany();
                    SharedPreferences loginRef = getSharedPreferences("book_keeping", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = loginRef.edit();
                    editor.putString("access_token","");
                    editor.putString("fcm_token","");
                    editor.putBoolean("first_input",true);
                    editor.putInt("userId", 0);
                    editor.commit();
                    startActivity(new Intent(Companies.this, LoginScreen.class));

            }
        });
        android.app.AlertDialog alert = builder.create();
        alert.show();
    }

    private void log_out() {
        String mac_address = global.getDeviceToken(Companies.this);
        Call<LogoutResponse> logOut = ApiClient.getInstance().getBookKeepingApi().logOut("Bearer "+auth, fcm_token, mac_address);
        logOut.enqueue(new Callback<LogoutResponse>() {
            @Override
            public void onResponse(Call<LogoutResponse> call, Response<LogoutResponse> response) {
                Log.e("LOG OUT RESPONSE",""+response.code());
                if(response.code() == 200) {

                    SharedPreferences loginRef = getSharedPreferences("book_keeping", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = loginRef.edit();
                    editor.putString("access_token","");
                    editor.putString("fcm_token","");
                    editor.putBoolean("first_input",true);
                    editor.putInt("userId", 0);
                    editor.commit();
                    startActivity(new Intent(Companies.this, LoginScreen.class));
                } else {
                    progressDialog.dismiss();
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        Toast.makeText(Companies.this, ""+jObjError.getString("message"), Toast.LENGTH_SHORT).show();
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
    private void retrieveData() {
        Cursor company = db.fetchData();
        if(company.getCount() > 0) {
            loadFromLocal(false);
        } else {
            if(global.isNetworkAvailable(Companies.this)) {
                companyLists("Bearer " + auth);
            } else {
                progressDialog.dismiss();
                noCompany.setVisibility(View.VISIBLE);
            }
        }
    }
    public void companyLists(String token) {
        progressDialog.show();
        Log.e("CALL TOKEN", "" + token);
        Call<CompanyResponse> call = ApiClient.getInstance().getBookKeepingApi().companies(token, "");
        call.enqueue(new Callback<CompanyResponse>() {
            @Override
            public void onResponse(Call<CompanyResponse> call, Response<CompanyResponse> response) {
                if (response.code() == 200) {
                    companies.clear();
                    if (response.body().getData().size() > 0) {
                        for (int j = 0; j < response.body().getData().size(); j++) {
                            String dummyLogo;
                            if (response.body().getData().get(j).getCompanyImage() != null) {
                                dummyLogo = response.body().getData().get(j).getCompanyImage().getOriginal();
                            } else {
                                dummyLogo = "";
                            }
                            checkValue(response.body().getData().get(j).getCompanySubscription().getPackage().getSubsTypeData().getName(),
                                    response.body().getData().get(j).getCompanySubscription().getPackage().getPrice(),
                                    response.body().getData().get(j).getCompanySubscription().getPackage().getSubUserPrice(),
                                    response.body().getData().get(j).getCompanySubscription().getPackage().getSubsTypeData().getId()
                            );
                            //String dummyLogo = String.valueOf(R.drawable.company);
                            CompaniesModel company = new CompaniesModel(
                                    response.body().getData().get(j).getName(),
                                    response.body().getData().get(j).getId(),
                                    dummyLogo, response.body().getData().get(j).getRole().getId(),
                                    response.body().getData().get(j).getCompanySubscription().getPaymentStatus(),
                                    package_id,
                                    price,
                                    sub_user_price);
                            companies.add(company);
                        }
                        if (companies.size() > 0) {
                            noCompany.setVisibility(View.GONE);
                            companiesAdapter = new CompaniesAdapter(companies, Companies.this, businessDashboardList, true);
                            companyList.setAdapter(companiesAdapter);
                            //companyList.setItemViewCacheSize(10000000);
                            GridLayoutManager gridLayoutManager = new GridLayoutManager(Companies.this, 3);
                            companyList.setLayoutManager(gridLayoutManager);
                            companiesAdapter.notifyDataSetChanged();
                        } else {
                            noCompany.setVisibility(View.VISIBLE);
                        }
                        progressDialog.dismiss();
                        SharedPreferences loginRef = getSharedPreferences("book_keeping", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = loginRef.edit();
                        editor.putBoolean("first_input", false);
                        editor.commit();

                    } else {
                        progressDialog.dismiss();
                        noCompany.setVisibility(View.VISIBLE);
                    }
                } else if (response.code() == 401) {
                    String token = global.reCallList(shared.getString("refresh_token", ""));
                    if(token.equals("Refresh Token Expires")) {
                        clearShare();
                        startActivity(new Intent(Companies.this, LoginScreen.class));
                    } else {
                        String vToken = global.validToken(token);
                        if(vToken.equals("Authentication token missing") || vToken.equals("Wrong authentication token")) {
                            clearShare();
                            startActivity(new Intent(Companies.this, LoginScreen.class));
                        } else {
                            auth = vToken;
                            updateShare(vToken);
                            companyLists("Bearer " + auth);
                        }
                    }
                } else {
                    progressDialog.dismiss();
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        Toast.makeText(Companies.this, ""+jObjError.getString("message"), Toast.LENGTH_SHORT).show();
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            @Override
            public void onFailure(Call<CompanyResponse> call, Throwable t) {
                Log.e("TAG",""+t.getMessage());
               // Toast.makeText(Companies.this, ""+t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkValue(String package_names, Integer unit_price, Integer subUserPrice, int package_ids) {
        if(package_names != null) {
            package_id = package_id;
        } else {
            package_id = 0;
        }
        if(unit_price != null) {
            price = unit_price;
        } else {
            price = 0;
        }
        if(subUserPrice != null) {
            sub_user_price = subUserPrice;
        } else {
            sub_user_price = 0;
        }
    }
    private void loadFromLocal(boolean mode) {
        companies.clear();
        Manager compDb = new Manager(Companies.this);
        Cursor cursor = compDb.fetchData();
        while (cursor.moveToNext()) {
            CompaniesModel company = new CompaniesModel(
                    cursor.getString(1),
                    cursor.getInt(0),
                    cursor.getString(2),
                    cursor.getInt(3),
                    cursor.getInt(6),
                    cursor.getInt(7),
                    cursor.getInt(8),
                    cursor.getInt(9)
                    );
            companies.add(company);
        }

        if(companies.size() > 0) {
            companiesAdapter = new CompaniesAdapter(companies, Companies.this, businessDashboardList,mode);
            companyList.setAdapter(companiesAdapter);
            GridLayoutManager gridLayoutManager = new GridLayoutManager(Companies.this, 3);
            companyList.setLayoutManager(gridLayoutManager);
            companiesAdapter.notifyDataSetChanged();
        }

    }

    @SuppressLint("MissingInflatedId")
    private void openSheet() {
        sheetDialog = new BottomSheetDialog(Companies.this, R.style.BottomSheetStyle);
        View changePass = getLayoutInflater().inflate(R.layout.change_password, null);

        Log.e("Password Change Required", ""+password_change_require);
        if(password_change_require == 0) {
            sheetDialog.setCancelable(false);
        }

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
                    if(global.isNetworkAvailable(Companies.this)) {
                        try {
                            newEncrypt = cryptLib.encryptPlainTextWithRandomIV(password.getText().toString(), ApiClient.getInstance().getKey());
                            oldEncrypt = cryptLib.encryptPlainTextWithRandomIV(oldPassword.getText().toString(), ApiClient.getInstance().getKey());
                            passwordUpdate(newEncrypt, oldEncrypt);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    } else {
                        String please_Check_Your_Internet_Connection = getResources().getString(R.string.please_Check_Your_Internet_Connection);

                        Toast.makeText(Companies.this, please_Check_Your_Internet_Connection, Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(Companies.this, getResources().getString(R.string.password_change_successfully), Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                    sheetDialog.dismiss();
                    SharedPreferences loginRef = getSharedPreferences("book_keeping", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = loginRef.edit();
                    editor.putInt("password_need_to_change",1);
                    editor.commit();
                } else if (response.code() == 401) {
                    String token = global.reCallList(shared.getString("refresh_token", ""));
                    if(token.equals("Refresh Token Expires")) {
                        clearShare();
                        startActivity(new Intent(Companies.this, LoginScreen.class));
                    } else {
                        String vToken = global.validToken(token);
                        if(vToken.equals("Authentication token missing") || vToken.equals("Wrong authentication token")) {
                            clearShare();
                            startActivity(new Intent(Companies.this, LoginScreen.class));
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
                        Toast.makeText(Companies.this, ""+jObjError.getString("message"), Toast.LENGTH_SHORT).show();
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
        Manager db = new Manager(Companies.this);
        db.removeAllCompany();
    }
    public void redirect(){
        String are_you_sure_you_want_to_exit = getResources().getString(R.string.are_you_sure_you_want_to_exit);
        String no = getResources().getString(R.string.no);
        String yes = getResources().getString(R.string.yes);
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(Companies.this);
        builder.setMessage(are_you_sure_you_want_to_exit);
        builder.setPositiveButton(no, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                // Do nothing, but close the dialog
            }
        });
        builder.setNegativeButton(yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finishAffinity();
            }
        });
        android.app.AlertDialog alert = builder.create();
        alert.show();
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