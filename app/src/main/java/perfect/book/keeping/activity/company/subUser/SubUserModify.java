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
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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
import com.google.android.material.switchmaterial.SwitchMaterial;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import perfect.book.keeping.R;
import perfect.book.keeping.activity.Companies;
import perfect.book.keeping.activity.LoginScreen;
import perfect.book.keeping.activity.pnl.ProfitLoss;
import perfect.book.keeping.activity.profile.Profile;
import perfect.book.keeping.adapter.SelectedAllCompanies;
import perfect.book.keeping.api.ApiClient;
import perfect.book.keeping.encrypt.CryptLib;
import perfect.book.keeping.global.Global;
import perfect.book.keeping.global.LocaleHelper;
import perfect.book.keeping.global.RefreshToken;
import perfect.book.keeping.manager.Manager;
import perfect.book.keeping.model.CompPermissions;
import perfect.book.keeping.model.CompanyPermission;
import perfect.book.keeping.model.RoleList;
import perfect.book.keeping.request.SubUserModifyRequest;
import perfect.book.keeping.response.ChangePassword;
import perfect.book.keeping.response.LogoutResponse;
import perfect.book.keeping.response.Role;
import perfect.book.keeping.response.SubUser;
import perfect.book.keeping.response.SubUserList;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SubUserModify extends AppCompatActivity {

    EditText password, emailAddress, name;

    LinearLayout sectionPermission;

    TextView store_sub_user, selectCompany;

    RecyclerView selectedPermission;

    Dialog progressDialog;

    AlertDialog alertDialog;

    SelectedAllCompanies selectedCompanies;

    String auth, companyName, fcm_token;

    SharedPreferences shared;

    List<CompanyPermission> companyPermissions = new ArrayList<>();

    List<CompPermissions> compPermissions = new ArrayList<>();


    public List<String> selectedComp = new ArrayList<String>();

    CryptLib cryptLib;

    int companyId;

    int subUser_id = 0, userType = 0, givenPermission = 0, compPermission;

    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

    RefreshToken refreshToken;

    LinearLayout openMenu;

    BottomSheetDialog sheetDialog;

    EditText passwords, cnfPassword, oldPassword;

    ImageView eye_btn_fp, eye_btn_fp_old, eye_btn_cnf;
    CircleImageView profile_image;

    LinearLayout updatePass, back;


    String newEncrypt, oldEncrypt;


    TextView header_title;

    List<RoleList> roles = new ArrayList<>();

    RoleListAdapter roleListAdapter;
    Global global;
    private List<Integer> userIds = new ArrayList<Integer>();
    Manager db;
    Locale myLocale;
    String codeLang;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_user_modify);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        android.graphics.drawable.Drawable background = SubUserModify.this.getDrawable(R.drawable.gradiant_btn_alt);
        getWindow().setBackgroundDrawable(background);


        companyId = getIntent().getIntExtra("companyId",0);
        companyName = getIntent().getStringExtra("companyName");
        compPermission = getIntent().getIntExtra("permission",0);

        header_title = findViewById(R.id.header_title);
        subUser_id = getIntent().getIntExtra("subUser", 0);
        shared = getSharedPreferences("book_keeping", Context.MODE_PRIVATE);
        auth = shared.getString("access_token", "");
        fcm_token = shared.getString("fcm_token", "");
        userType = shared.getInt("userType",0);

        shared = getSharedPreferences("book_keeping", Context.MODE_PRIVATE);
        codeLang = shared.getString("code_lang", "");
        setLocale(codeLang);


        refreshToken = new RefreshToken();
        name = findViewById(R.id.name);
        emailAddress = findViewById(R.id.emailAddress);
        openMenu = findViewById(R.id.openMenu);
        global = new Global();
        store_sub_user = findViewById(R.id.store_sub_user);
        selectedPermission = findViewById(R.id.selectedPermission);
        back = findViewById(R.id.back);
        profile_image = findViewById(R.id.profile_image);
        selectCompany = findViewById(R.id.company);
        selectCompany.setText(companyName);
        db = new Manager(SubUserModify.this);
        //sectionPermission = findViewById(R.id.sectionPermission);

        header_title.setText("Update User");

        Manager compDb = new Manager(SubUserModify.this);
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
            companyName = cursor.getString(1);

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

        progressDialog = new Dialog(SubUserModify.this);
        progressDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        progressDialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        progressDialog.setContentView(R.layout.progress);
        if (progressDialog.getWindow() != null) {
            progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        progressDialog = new Dialog(SubUserModify.this);
        progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        progressDialog.setContentView(R.layout.progress);
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable((Color.TRANSPARENT)));

        store_sub_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String please_enter_name = getResources().getString(R.string.please_enter_name);
                String please_enter_email_address = getResources().getString(R.string.please_enter_email_address);
                String please_enter_valid_email_address = getResources().getString(R.string.please_enter_valid_email_address);
                String please_select_any_one_permissions  = getResources().getString(R.string.please_select_any_one_permissions);
                if(name.getText().toString().equals("")) {
                    name.setError(please_enter_name);
                } else if (emailAddress.getText().toString().equals("")) {
                    emailAddress.setError(please_enter_email_address);
                } else if (!emailAddress.getText().toString().matches(emailPattern)) {
                    emailAddress.setError(please_enter_valid_email_address);
                } else if (givenPermission == 0) {
                    Toast.makeText(SubUserModify.this, please_select_any_one_permissions, Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        modify_subUser();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }

            }
        });

        callSubUserApi("Bearer "+auth);
    }


    private void callSubUserApi(String token) {
        progressDialog.show();
        Call<SubUserList> call = ApiClient.getInstance().getBookKeepingApi().subUserList(token, String.valueOf(companyId), String.valueOf(subUser_id), "");
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<SubUserList> call, Response<SubUserList> response) {

                if (response.code() == 200) {
                    subUser_id = response.body().getData().get(0).getId();
                    name.setText(response.body().getData().get(0).getName());
                    emailAddress.setText(response.body().getData().get(0).getEmail());
                    givenPermission = response.body().getData().get(0).getUserCompanyPermissions().get(0).getRoleId();

                    getRole(companyId);
                } else if (response.code()==401){
                    String token = global.reCallList(shared.getString("refresh_token", ""));
                    if(token.equals("Refresh Token Expires")) {
                        clearShare();
                        startActivity(new Intent(SubUserModify.this, LoginScreen.class));
                    } else {
                        String vToken = global.validToken(token);
                        if(vToken.equals("Authentication token missing") || vToken.equals("Wrong authentication token")) {
                            clearShare();
                            startActivity(new Intent(SubUserModify.this, LoginScreen.class));
                        } else {
                            auth = vToken;
                            updateShare(vToken);
                            callSubUserApi("Bearer "+auth);
                        }
                    }
                } else {
                    progressDialog.dismiss();
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        Toast.makeText(SubUserModify.this, ""+jObjError.getString("message"), Toast.LENGTH_SHORT).show();
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
        Manager db = new Manager(SubUserModify.this);
        db.removeAllCompany();
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
                        roleListAdapter = new RoleListAdapter(roles, SubUserModify.this, givenPermission);
                        selectedPermission.setAdapter(roleListAdapter);
                        selectedPermission.setLayoutManager(new LinearLayoutManager(SubUserModify.this));
                        roleListAdapter.notifyDataSetChanged();
                    }
                } else {
                    try {
                        JSONObject jObjError = new  JSONObject(response.errorBody().string());
                        Toast.makeText(SubUserModify.this, ""+jObjError.getString("message"), Toast.LENGTH_SHORT).show();
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

    private void reCallList(String refresh_token, String mode) {
        String result = refreshToken.performAsyncTask(refresh_token, "Profile Details");
        if(result.equals("")) {
            updateShare("");
        } else {
            updateShare(result);
           // checkValidToken(result, mode);
        }
    }




    private void modify_subUser() {
        progressDialog.show();
        SubUserModifyRequest subUserModifyRequest = new SubUserModifyRequest(companyId, subUser_id,  givenPermission, name.getText().toString());
        Call<SubUser> updateSubUser = ApiClient.getInstance().getBookKeepingApi().updateSubUser("Bearer "+auth, subUserModifyRequest);
        updateSubUser.enqueue(new Callback<SubUser>() {
            @Override
            public void onResponse(Call<SubUser> call, Response<SubUser> response) {
                Log.e("Modify RESPONSE",""+response.code());
                if(response.code() == 200) {
                    String sub_User_updated_successfully = getResources().getString(R.string.sub_User_updated_successfully);
                    Toast.makeText(SubUserModify.this, sub_User_updated_successfully, Toast.LENGTH_SHORT).show();
                    callSubUserSave("Bearer "+auth);

                } else {
                    progressDialog.dismiss();
                    String token = global.reCallList(shared.getString("refresh_token", ""));
                    if(token.equals("Refresh Token Expires")) {
                        clearShare();
                        startActivity(new Intent(SubUserModify.this, LoginScreen.class));
                    } else if (response.code()==401){
                        String vToken = global.validToken(token);
                        if(vToken.equals("Authentication token missing") || vToken.equals("Wrong authentication token")) {
                            clearShare();
                            startActivity(new Intent(SubUserModify.this, LoginScreen.class));
                        } else {
                            auth = vToken;
                            updateShare(vToken);
                            try {
                                modify_subUser();
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        }
                    } else {
                        progressDialog.dismiss();
                        try {
                            JSONObject jObjError = new JSONObject(response.errorBody().string());
                            Toast.makeText(SubUserModify.this, ""+jObjError.getString("message"), Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<SubUser> call, Throwable t) {

            }
        });
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
                        Intent subUsers = new Intent(SubUserModify.this, SubUsersList.class);
                        subUsers.putExtra("companyId",companyId);
                        subUsers.putExtra("companyName","");
                        subUsers.putExtra("permission",compPermission);
                        startActivity(subUsers);
                        progressDialog.dismiss();

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
        Log.e("COMPNANY REMOVE",""+selectedComp.toString());
    }

    public void updateShare(String token) {
        SharedPreferences sharedPreferences = getSharedPreferences("book_keeping", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("access_token", token);
        editor.commit();
    }

    private void showPopUp(View v) {
        PopupMenu popupMenu = new PopupMenu(SubUserModify.this, v);
        popupMenu.getMenuInflater().inflate(R.menu.navigation_item, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();
                if(id == R.id.companyList) {
                    //startActivity(new Intent(SubUserModify.this, perfect.book.keeping.activity.Companies.class));
                    Intent company = new Intent(SubUserModify.this, Companies.class);
                    company.putExtra("enable","0");
                    startActivity(company);
                } else if(id == R.id.profileDtl) {
                    startActivity(new Intent(SubUserModify.this, Profile.class));
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
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(SubUserModify.this);
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
                Manager compDb = new Manager(SubUserModify.this);
                compDb.removeAllCompany();
                SharedPreferences loginRef = getSharedPreferences("book_keeping", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = loginRef.edit();
                editor.putString("access_token","");
                editor.putString("fcm_token","");
                editor.putBoolean("first_input",true);
                editor.putInt("userId", 0);
                editor.commit();
                //ClearAppData.clearApplicationData(SubUserModify.this, getPackageName());
                startActivity(new Intent(SubUserModify.this, LoginScreen.class));

            }
        });
        android.app.AlertDialog alert = builder.create();
        alert.show();
    }

    private void log_out() {
        progressDialog.show();
        String mac_address = global.getDeviceToken(SubUserModify.this);
        Call<LogoutResponse> logOut = ApiClient.getInstance().getBookKeepingApi().logOut("Bearer "+auth, fcm_token, mac_address);
        logOut.enqueue(new Callback<LogoutResponse>() {
            @Override
            public void onResponse(Call<LogoutResponse> call, Response<LogoutResponse> response) {
                Log.e("LOG OUT RESPONSE",""+response.code());
                if(response.code() == 200) {
                    progressDialog.dismiss();
                    Manager compDb = new Manager(SubUserModify.this);
                    compDb.removeAllCompany();
                    SharedPreferences loginRef = getSharedPreferences("book_keeping", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = loginRef.edit();
                    editor.putString("access_token","");
                    editor.putString("fcm_token","");
                    editor.putBoolean("first_input",true);
                    editor.putInt("userId", 0);
                    editor.commit();
                    startActivity(new Intent(SubUserModify.this, LoginScreen.class));
                } else {
                    progressDialog.dismiss();
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        Toast.makeText(SubUserModify.this, ""+jObjError.getString("message"), Toast.LENGTH_SHORT).show();
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
        sheetDialog = new BottomSheetDialog(SubUserModify.this, R.style.BottomSheetStyle);
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
                    if(global.isNetworkAvailable(SubUserModify.this)) {
                        try {
                            newEncrypt = cryptLib.encryptPlainTextWithRandomIV(password.getText().toString(), ApiClient.getInstance().getKey());
                            oldEncrypt = cryptLib.encryptPlainTextWithRandomIV(oldPassword.getText().toString(), ApiClient.getInstance().getKey());
                            passwordUpdate(newEncrypt, oldEncrypt);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    } else {
                        String please_Check_Your_Internet_Connection = getResources().getString(R.string.please_Check_Your_Internet_Connection);

                        Toast.makeText(SubUserModify.this, please_Check_Your_Internet_Connection, Toast.LENGTH_SHORT).show();
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
                    String password_change_successfully =  getResources().getString(R.string.password_change_successfully);
                    Toast.makeText(SubUserModify.this, password_change_successfully, Toast.LENGTH_SHORT).show();
                    sheetDialog.dismiss();
                } else if (response.code()==401) {
                    String token = global.reCallList(shared.getString("refresh_token", ""));
                    if(token.equals("Refresh Token Expires")) {
                        clearShare();
                        startActivity(new Intent(SubUserModify.this, LoginScreen.class));
                    } else {
                        String vToken = global.validToken(token);
                        if(vToken.equals("Authentication token missing") || vToken.equals("Wrong authentication token")) {
                            clearShare();
                            startActivity(new Intent(SubUserModify.this, LoginScreen.class));
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
                        Toast.makeText(SubUserModify.this, ""+jObjError.getString("message"), Toast.LENGTH_SHORT).show();
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
        Intent subUsers = new Intent(SubUserModify.this, SubUsersList.class);
        subUsers.putExtra("companyId",companyId);
        subUsers.putExtra("companyName",companyName);
        subUsers.putExtra("permission", compPermission);
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
                holder.permission.setChecked(false);
            }
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
            givenPermission = permission;
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