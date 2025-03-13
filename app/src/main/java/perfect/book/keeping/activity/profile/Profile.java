package perfect.book.keeping.activity.profile;

import static com.bumptech.glide.load.resource.bitmap.TransformationUtils.rotateImage;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Base64;
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
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.crypto.NoSuchPaddingException;

import de.hdodenhof.circleimageview.CircleImageView;
import perfect.book.keeping.R;
import perfect.book.keeping.activity.BookKeepers;
import perfect.book.keeping.activity.Companies;
import perfect.book.keeping.activity.LoginScreen;
import perfect.book.keeping.activity.company.PaymentMethod;
import perfect.book.keeping.activity.company.UpdateCompany;
import perfect.book.keeping.activity.company.gallery.PendingGallery;
import perfect.book.keeping.adapter.CurrencyAdapter;
import perfect.book.keeping.adapter.LanguageAdapter;
import perfect.book.keeping.api.ApiClient;
import perfect.book.keeping.encrypt.CryptLib;
import perfect.book.keeping.global.Global;
import perfect.book.keeping.global.LocaleHelper;
import perfect.book.keeping.global.RefreshToken;
import perfect.book.keeping.manager.Manager;
import perfect.book.keeping.model.Country;
import perfect.book.keeping.model.LanguageList;
import perfect.book.keeping.model.States;
import perfect.book.keeping.request.Image;
import perfect.book.keeping.request.UpdateImageRequest;
import perfect.book.keeping.request.UpdateUserRequest;
import perfect.book.keeping.request.User;
import perfect.book.keeping.response.ChangePassword;
import perfect.book.keeping.response.LogoutResponse;
import perfect.book.keeping.response.UserProfileUpdate;
import perfect.book.keeping.response.UserResponse;
import perfect.book.keeping.response.UserUpdateResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Profile extends AppCompatActivity {

    ImageView ivPlayOverlay;
    BottomSheetDialog sheetDialog;
    LinearLayout openCamera, gallery, cancel;
    private static final int ACTIVITY_REQUEST_CODE = 1000;
    private static final int ACTIVITY_REQUEST_GALLERY_CODE = 2000;
    private static final int PERMISSION_REQUEST_CODE = 1203;
    EditText phoneNumber,userName;
    TextView email;
    EditText countrySelect;
    CircleImageView profile_image;
    AlertDialog cropDialog;
    CropImageView cropImageView;
    LinearLayout cancel_crop, apply_crop;
    ImageView rotate_image;
    Bitmap bitmap;
    private static final String EMPTY_STRING = "";
    private String lastSource = EMPTY_STRING;
    SharedPreferences shared;
    Global global;
    String auth, fcm_token,codeLang;
    int userId, click = 1;
    private List<Country> countries= new ArrayList<>();
    private List<States> states = new ArrayList<>();
    AlertDialog alertDialog, alertDialogStates;

    String imgData, countryName = "", stateName = "", langCode;
    TextView btn_otp;
    Boolean isExisting;
    CryptLib cryptLib;
    Dialog progressDialog;
    RefreshToken refreshToken;
    TextView header_title;
    LinearLayout openMenu;
    int userType = 0;
    EditText password, cnfPassword, oldPassword,searchByLanguage;
    ImageView eye_btn_fp, eye_btn_fp_old, eye_btn_cnf,language_close;
    LinearLayout updatePass, back;
    String newEncrypt, oldEncrypt;
    Uri imageUri;
    EditText select_language;
    Dialog dialogLanguage;
    RecyclerView language_list;
    LanguageAdapter languageAdapter;
    List<LanguageList> languageList;
    String currentLanguage = "en" , currentLang;
    Locale myLocale;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        android.graphics.drawable.Drawable background = Profile.this.getDrawable(R.drawable.gradiant_btn_alt);
        getWindow().setBackgroundDrawable(background);

        try {
            cryptLib = new CryptLib();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (NoSuchPaddingException e) {
            throw new RuntimeException(e);
        }

        refreshToken = new RefreshToken();
        ivPlayOverlay = findViewById(R.id.ivPlayOverlay);
        profile_image = findViewById(R.id.profile_image);
        select_language = findViewById(R.id.select_language);

        languageList = new ArrayList<>();
        languageList.add(
                new LanguageList(
                        "Bengali",
                        "bn"));
        languageList.add(
                new LanguageList(
                        "English",
                        "en"));
        languageList.add(
                new LanguageList(
                        "Spanish",
                        "es"));
        languageList.add(
                new LanguageList(
                        "German",
                        "de"));
        select_language.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogLanguage = new Dialog(Profile.this);
                dialogLanguage.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialogLanguage.setContentView(R.layout.dialog_language_spinner);
                dialogLanguage.getWindow().setBackgroundDrawable(new ColorDrawable((Color.TRANSPARENT)));
                searchByLanguage = dialogLanguage.findViewById(R.id.searchBy);
                language_list = dialogLanguage.findViewById(R.id.language_list);
                language_close = dialogLanguage.findViewById(R.id.close);

                searchByLanguage.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if(languageList.size() > 0) {
                            languageAdapter.getFilter().filter(s);
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });
                language_close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialogLanguage.dismiss();
                    }
                });

                languageAdapter= new LanguageAdapter(languageList, Profile.this);
                language_list.setAdapter(languageAdapter);
                language_list.setLayoutManager(new LinearLayoutManager(Profile.this));
                languageAdapter.notifyDataSetChanged();
                dialogLanguage.show();

            }
        });
        global = new Global();

        header_title = findViewById(R.id.header_title);
        openMenu = findViewById(R.id.openMenu);
        back = findViewById(R.id.back);
        // header_title.setText("Profile Details");

        btn_otp = findViewById(R.id.btn_otp);

        progressDialog = new Dialog(Profile.this);
        progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        progressDialog.setContentView(R.layout.progress);
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable((Color.TRANSPARENT)));


        openMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //  drawerLayout.openDrawer(Gravity.LEFT);

                showPopUp(v);
            }
        });

        email = findViewById(R.id.email);
        phoneNumber = findViewById(R.id.phoneNumber);
        phoneNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String prefix = "+";
                int count = (s == null) ? 0 : s.toString().length();
                if (count < prefix.length()) {
                    phoneNumber.setText(prefix);
                    int selectionIndex = Math.max(count + 1, prefix.length());
                    phoneNumber.setSelection(selectionIndex);
                }
            }
        });
        userName = findViewById(R.id.userName);

        shared = getSharedPreferences("book_keeping", Context.MODE_PRIVATE);
        auth = shared.getString("access_token", "");
        fcm_token = shared.getString("fcm_token","");
        userType = shared.getInt("userType",0);

        codeLang = shared.getString("code_lang", "");
        currentLanguage = getIntent().getStringExtra(currentLang);

        ivPlayOverlay.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("MissingInflatedId")
            @Override
            public void onClick(View v) {
                sheetDialog = new BottomSheetDialog(Profile.this, R.style.BottomSheetStyle);
                View choose = getLayoutInflater().inflate(R.layout.image_options, null);

                openCamera = choose.findViewById(R.id.openCamera);
                gallery = choose.findViewById(R.id.gallery);
                cancel = choose.findViewById(R.id.cancel);

                openCamera.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openCameraReq();
                    }
                });

                gallery.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openGalleryReq();
                    }
                });

                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sheetDialog.dismiss();
                    }
                });

                sheetDialog.setContentView(choose);
                sheetDialog.show();
            }
        });

        profile_image.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("MissingInflatedId")
            @Override
            public void onClick(View v) {
                sheetDialog = new BottomSheetDialog(Profile.this, R.style.BottomSheetStyle);
                View choose = getLayoutInflater().inflate(R.layout.image_options, null);

                openCamera = choose.findViewById(R.id.openCamera);
                gallery = choose.findViewById(R.id.gallery);
                cancel = choose.findViewById(R.id.cancel);

                openCamera.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openCameraReq();
                    }
                });

                gallery.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openGalleryReq();
                    }
                });

                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sheetDialog.dismiss();
                    }
                });

                sheetDialog.setContentView(choose);
                sheetDialog.show();
            }
        });

        btn_otp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(userName.getText().toString().equals("")) {
                    userName.setError("Please enter your name");
                } else if(phoneNumber.getText().toString().equals("")) {
                    phoneNumber.setError("Please enter phone number");
                } else {
                    if(global.isNetworkAvailable(Profile.this)) {
                        try {
                            updateProfile(updateUsers());
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    } else {
                        Toast.makeText(Profile.this, "Please Check Your Internet Connection", Toast.LENGTH_SHORT).show();
                    }
                }
            }

        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        getUserInfo("Bearer "+auth, 0);
    }

    public void selectedLanguage(String codeLanguage, String selectLanguage) {
        langCode = codeLanguage;
        select_language.setText(selectLanguage);
        dialogLanguage.dismiss();
    }
    @SuppressLint("MissingInflatedId")
    private void openSheet() {
        sheetDialog = new BottomSheetDialog(Profile.this, R.style.BottomSheetStyle);
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
                    if(global.isNetworkAvailable(Profile.this)) {
                        try {
                            newEncrypt = cryptLib.encryptPlainTextWithRandomIV(password.getText().toString(), ApiClient.getInstance().getKey());
                            oldEncrypt = cryptLib.encryptPlainTextWithRandomIV(oldPassword.getText().toString(), ApiClient.getInstance().getKey());
                            passwordUpdate(newEncrypt, oldEncrypt);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    } else {
                        String please_Check_Your_Internet_Connection = getResources().getString(R.string.please_Check_Your_Internet_Connection);

                        Toast.makeText(Profile.this, please_Check_Your_Internet_Connection, Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(Profile.this, getResources().getString(R.string.password_change_successfully), Toast.LENGTH_SHORT).show();
                    sheetDialog.dismiss();
                } else if (response.code()==401){
                    String token = global.reCallList(shared.getString("refresh_token", ""));
                    if(token.equals("Refresh Token Expires")) {
                        clearShare();
                        startActivity(new Intent(Profile.this, LoginScreen.class));
                    } else {
                        String vToken = global.validToken(token);
                        if(vToken.equals("Authentication token missing") || vToken.equals("Wrong authentication token")) {
                            clearShare();
                            startActivity(new Intent(Profile.this, LoginScreen.class));
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
                        Toast.makeText(Profile.this, ""+jObjError.getString("message"), Toast.LENGTH_SHORT).show();
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
    private void showPopUp(View v) {
        PopupMenu popupMenu = new PopupMenu(Profile.this, v);
        popupMenu.getMenuInflater().inflate(R.menu.navigation_item, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();
                if(id == R.id.companyList) {
                    //startActivity(new Intent(Profile.this, Companies.class));
                    Intent company = new Intent(Profile.this, Companies.class);
                    company.putExtra("enable","0");
                    startActivity(company);
                } else if(id == R.id.profileDtl) {
                    startActivity(new Intent(Profile.this, Profile.class));
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
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(Profile.this);
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
                Manager compDb = new Manager(Profile.this);
                compDb.removeAllCompany();
                SharedPreferences loginRef = getSharedPreferences("book_keeping", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = loginRef.edit();
                editor.putString("access_token","");
                editor.putString("fcm_token","");
                editor.putBoolean("first_input",true);
                editor.putInt("userId", 0);
                editor.commit();
                //ClearAppData.clearApplicationData(Profile.this, getPackageName());
                startActivity(new Intent(Profile.this, LoginScreen.class));
            }
        });
        android.app.AlertDialog alert = builder.create();
        alert.show();
    }
    private void log_out() {
        progressDialog.show();
        String mac_address = global.getDeviceToken(Profile.this);
        Call<LogoutResponse> logOut = ApiClient.getInstance().getBookKeepingApi().logOut("Bearer "+auth, fcm_token, mac_address);
        logOut.enqueue(new Callback<LogoutResponse>() {
            @Override
            public void onResponse(Call<LogoutResponse> call, Response<LogoutResponse> response) {
                Log.e("LOG OUT RESPONSE",""+response.code());
                if(response.code() == 200) {
                    progressDialog.dismiss();

                    SharedPreferences loginRef = getSharedPreferences("book_keeping", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = loginRef.edit();
                    editor.putString("access_token","");
                    editor.putString("fcm_token","");
                    editor.putBoolean("first_input",true);
                    editor.putInt("userId", 0);
                    editor.commit();
                    startActivity(new Intent(Profile.this, LoginScreen.class));
                } else {
                    progressDialog.dismiss();
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        Toast.makeText(Profile.this, ""+jObjError.getString("message"), Toast.LENGTH_SHORT).show();
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
    private void getUserInfo(String token, int mode) {
        Log.e("Profile Token",""+token);
        progressDialog.show();
        Call<UserResponse> userProf = ApiClient.getInstance().getBookKeepingApi().userProfile(token);
        userProf.enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                Log.e("Profile Response", ""+response.code());
                if(response.code() == 200) {
                    progressDialog.dismiss();
                    phoneNumber.setText(response.body().getData().getPhone());
                    userName.setText(response.body().getData().getName());
                    email.setText(response.body().getData().getEmail());
                    String language = "English";
                    if(response.body().getData().getDef_lang().equals("de")) {
                        language = "German";
                    } else if (response.body().getData().getDef_lang().equals("bn")) {
                        language = "Bengali";
                    } else if (response.body().getData().getDef_lang().equals("es")) {
                        language = "Spanish";
                    } else {
                        language = "English";
                    }
                    select_language.setText(language);
                    SharedPreferences loginRef = getSharedPreferences("book_keeping", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = loginRef.edit();
                    editor.putString("code_lang",response.body().getData().getDef_lang());
                    editor.commit();
                    codeLang = shared.getString("code_lang", "");
                    currentLanguage = getIntent().getStringExtra(currentLang);
                    setLocale(codeLang);
                    if(response.body().getData().getUserImage() != null) {
                        Glide.with(Profile.this)
                                .load(response.body().getData().getUserImage().getThumbnail())
                                .apply(new RequestOptions()
                                        .placeholder(R.drawable.user_prof)
                                        .error(R.drawable.user_prof))
                                .into(profile_image);
                    }
                    if(response.body().getData().getUserAddress()!= null) {
//                        if (response.body().getData().getUserAddress().getCountry() == null) {
//                            countryName = "";
//                        } else {
//                            countrySelect.setText(response.body().getData().getUserAddress().getCountry());
//                            countryName = response.body().getData().getUserAddress().getCountry();
//                        }
                    }
                } else if (response.code()==401){
                    String token = global.reCallList(shared.getString("refresh_token", ""));
                    if(token.equals("Refresh Token Expires")) {
                        clearShare();
                        startActivity(new Intent(Profile.this, LoginScreen.class));
                    } else {
                        String vToken = global.validToken(token);
                        if(vToken.equals("Authentication token missing") || vToken.equals("Wrong authentication token")) {
                            clearShare();
                            startActivity(new Intent(Profile.this, LoginScreen.class));
                        } else {
                            auth = vToken;
                            updateShare(vToken);
                            getUserInfo("Bearer "+auth, 0);
                        }
                    }

                } else {
                    progressDialog.dismiss();
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        Toast.makeText(Profile.this, ""+jObjError.getString("message"), Toast.LENGTH_SHORT).show();
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                Log.e("Profile Response", ""+t.getMessage());
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
        Manager db = new Manager(Profile.this);
        db.removeAllCompany();
    }
    private void reCallList(String refresh_token) {
        String result = refreshToken.performAsyncTask(refresh_token, "Profile Details");
        if(result.equals("")) {
            updateShare("");
        } else {
            updateShare(result);
            //checkValidToken(result);
        }
    }


    private void decodeImage(String userImage) {
        if(userImage != null) {
            final String pureBase64Encoded = userImage.substring(userImage.indexOf(",") + 1);
            final byte[] decodedBytes = Base64.decode(pureBase64Encoded, Base64.DEFAULT);
            Bitmap decodedImage = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
            profile_image.setImageBitmap(decodedImage);
            Log.e("Image Byte", "" + decodedBytes);
        }
    }
    private void openCameraReq() {
        if(ContextCompat.checkSelfPermission(Profile.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(Profile.this, new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST_CODE);
            return;
        } else {

            try {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                File imageFile = createImageFile();
                String imageFilePath = imageFile.getAbsolutePath();
                imageUri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName()+".provider", imageFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(intent, ACTIVITY_REQUEST_CODE);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }
    }
    private void openGalleryReq() {
        if(ContextCompat.checkSelfPermission(Profile.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(Profile.this, new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST_CODE);
            return;
        } else {
            Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(galleryIntent, ACTIVITY_REQUEST_GALLERY_CODE);
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            if(global.isNetworkAvailable(Profile.this)) {
                openCrop(imageUri.toString());
            } else {
                Toast.makeText(this, "Please Check Your Internet Connection", Toast.LENGTH_SHORT).show();
            }
            sheetDialog.dismiss();
        } else if(requestCode == ACTIVITY_REQUEST_GALLERY_CODE && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            if(global.isNetworkAvailable(Profile.this)) {
                openCrop(uri.toString());
            } else {
                Toast.makeText(Profile.this, "Please Check Your Internet Connection", Toast.LENGTH_SHORT).show();
            }
            sheetDialog.dismiss();
        } else {
            sheetDialog.dismiss();
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";

        // Get the directory where the image file will be saved
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        // Create the image file
        File imageFile = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        return imageFile;
    }

    private void openCrop(String imageString) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this,android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        View cropPhoto = LayoutInflater.from(this).inflate(R.layout.crop_layout, null);
        builder.setView(cropPhoto);
        builder.setCancelable(true);
        cropDialog = builder.create();
        cropDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        cancel_crop = cropPhoto.findViewById(R.id.cancel_crop);
        apply_crop = cropPhoto.findViewById(R.id.apply_crop);
        rotate_image = cropPhoto.findViewById(R.id.rotate_image);
        cropImageView = cropPhoto.findViewById(R.id.cropImageView);
        Log.e("Image String",""+imageString);
        if(imageString != null) {
            try {
                convertBitmap(Uri.parse(imageString));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        // cropImageView.setImageBitmap(captureImage);

        cropDialog.show();
    }

    private void convertBitmap(Uri savedUri) throws IOException {

        Bitmap new_Image = MediaStore.Images.Media.getBitmap(this.getContentResolver(), savedUri);
        bitmap = rotateImageIfRequired(Profile.this, new_Image, savedUri);

        //  crop_layout.setVisibility(View.VISIBLE);

        cropImageView.setImageBitmap(bitmap);

        Log.e("FILE URI",""+savedUri.getPath());
        apply_crop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap cropBitmap = cropImageView.getCroppedImage();
                cropImageView.setImageBitmap(cropBitmap);
                profile_image.setImageBitmap(cropBitmap);
                cropDialog.dismiss();
                Matrix matrix = new Matrix();
                matrix.postRotate(0);
                bitmap = Bitmap.createBitmap(cropBitmap, 0, 0, cropBitmap.getWidth(), cropBitmap.getHeight(), matrix, true);
                String mimeType = "image/jpg";
                String filename = savedUri.toString().substring(savedUri.toString().lastIndexOf("/")+1);
                getEncoded64ImageStringFromBitmap(bitmap,  mimeType, filename);
                Log.e("IMAGE BY",""+bitmap.getWidth() + "X"+ bitmap.getHeight());

            }
        });
        cancel_crop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cropDialog.dismiss();
            }
        });
        rotate_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Matrix matrix = new Matrix();
                if(click == 1) {
                    click++;
                    matrix.postRotate(180);
                } else {
                    matrix.postRotate(90);
                }
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

                cropImageView.setImageBitmap(bitmap);
            }
        });
    }

    private static Bitmap rotateImageIfRequired(Context context, Bitmap img, Uri selectedImage) throws IOException {

        InputStream input = context.getContentResolver().openInputStream(selectedImage);
        ExifInterface ei;
        if (Build.VERSION.SDK_INT > 23)
            ei = new ExifInterface(input);
        else
            ei = new ExifInterface(selectedImage.getPath());

        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                return rotateImage(img, 90);
            case ExifInterface.ORIENTATION_ROTATE_180:
                return rotateImage(img, 180);
            case ExifInterface.ORIENTATION_ROTATE_270:
                return rotateImage(img, 270);
            default:
                return img;
        }
    }

    public String getFileExtension(String filePath) {
        int lastDotIndex = filePath.lastIndexOf(".");
        if (lastDotIndex != -1 && lastDotIndex < filePath.length() - 1) {
            return filePath.substring(lastDotIndex + 1);
        }
        return "";
    }
    private File saveBitmapToFile(Bitmap bitmap) {
        try {
            // Create a file to save the image
            File imageFile = new File(getExternalFilesDir(null), "image.jpg");

            // Compress the bitmap to JPEG format
            FileOutputStream outputStream = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            outputStream.close();

            return imageFile;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }
    public String getRealPathFromURI(Uri uri) {
        String path = "";
        if (getContentResolver() != null) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();
                int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                path = cursor.getString(idx);
                cursor.close();
            }
        }
        return path;
    }
    private void getEncoded64ImageStringFromBitmap(Bitmap bitmapImage, String extension, String file_name) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmapImage.compress(Bitmap.CompressFormat.JPEG, 70, stream);
        byte[] byteFormat = stream.toByteArray();
        String imgString = Base64.encodeToString(byteFormat, Base64.NO_WRAP);
        //String imgData = "data:image"+extension+";base64,"+imgString;
        //String imgData = imgString;
        Image image = new Image(file_name, imgString, "image/"+extension);
        JSONObject imageData = new JSONObject();
        try {
            imageData.put("file_name", file_name);
            imageData.put("blobdata", imgString);
            imageData.put("mimetype", "image/"+extension);
            //Log.e("Image Object",""+jsonObject.toString());
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        UpdateImageRequest imageRequest = new UpdateImageRequest(image);
        uploadProfImage(imageRequest, "Bearer "+auth);
    }
    private void uploadProfImage(UpdateImageRequest imageRequest, String token) {
        progressDialog.show();
        Call<UserUpdateResponse> updateImageCall = ApiClient.getInstance().getBookKeepingApi().updateProfileImage(imageRequest, token);
        updateImageCall.enqueue(new Callback<UserUpdateResponse>() {
            @Override
            public void onResponse(Call<UserUpdateResponse> call, Response<UserUpdateResponse> response) {
                Log.e("IMAGE API RESPONSE",""+response.code());
                if(response.code() == 200) {
                    progressDialog.dismiss();
                } else if (response.code()==401) {
                    String token = global.reCallList(shared.getString("refresh_token", ""));
                    if(token.equals("Refresh Token Expires")) {
                        clearShare();
                        startActivity(new Intent(Profile.this, LoginScreen.class));
                    } else {
                        String vToken = global.validToken(token);
                        if(vToken.equals("Authentication token missing") || vToken.equals("Wrong authentication token")) {
                            clearShare();
                            startActivity(new Intent(Profile.this, LoginScreen.class));
                        } else {
                            auth = vToken;
                            updateShare(vToken);
                            uploadProfImage(imageRequest, "Bearer "+auth);
                        }
                    }
                } else {
                    progressDialog.dismiss();
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        Toast.makeText(Profile.this, ""+jObjError.getString("message"), Toast.LENGTH_SHORT).show();
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                }
            }

            @Override
            public void onFailure(Call<UserUpdateResponse> call, Throwable t) {
                Log.e("API FAILED RESPONSE",""+t.getMessage());
            }
        });
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openCameraReq();
                }
                break;
        }
    }

    public UpdateUserRequest updateUsers() throws Exception {
        User user = new User(userName.getText().toString(), phoneNumber.getText().toString(), langCode);
        UpdateUserRequest updateUsers = new UpdateUserRequest(user, isExisting);
        return updateUsers;
    }
    private void updateProfile(UpdateUserRequest updateUsers) {
        progressDialog.show();
        Call<UserProfileUpdate> profileUpdateCall = ApiClient.getInstance().getBookKeepingApi().profileUpdate("Bearer "+auth, updateUsers);
        profileUpdateCall.enqueue(new Callback<UserProfileUpdate>() {
            @Override
            public void onResponse(Call<UserProfileUpdate> call, Response<UserProfileUpdate> response) {
                Log.e("PROFILE UPDATE", "SUCCESS"+response.code());
                if(response.code() == 200) {
                    Toast.makeText(Profile.this, "Profile update successfully", Toast.LENGTH_SHORT).show();
                    SharedPreferences loginRef = getSharedPreferences("book_keeping", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = loginRef.edit();
                    editor.putString("code_lang",langCode);
                    editor.commit();
                    codeLang = shared.getString("code_lang", "");
                    currentLanguage = getIntent().getStringExtra(currentLang);
                    setLocale(codeLang);
                    progressDialog.dismiss();
                } else if (response.code()==401) {
                    String token = global.reCallList(shared.getString("refresh_token", ""));
                    if(token.equals("Refresh Token Expires")) {
                        clearShare();
                        startActivity(new Intent(Profile.this, LoginScreen.class));
                    } else {
                        String vToken = global.validToken(token);
                        if(vToken.equals("Authentication token missing") || vToken.equals("Wrong authentication token")) {
                            clearShare();
                            startActivity(new Intent(Profile.this, LoginScreen.class));
                        } else {
                            auth = vToken;
                            updateShare(vToken);
                            try {
                                updateProfile(updateUsers());
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }
                } else {
                    progressDialog.dismiss();
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        Toast.makeText(Profile.this, ""+jObjError.getString("message"), Toast.LENGTH_SHORT).show();
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            @Override
            public void onFailure(Call<UserProfileUpdate> call, Throwable t) {
                Log.e("P UPDATE","FAIL"+t.getMessage().toString());
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    public void redirect() {
        startActivity(new Intent(Profile.this, Companies.class));
    }

    public void setLocale(String localeName) {
        System.out.println("GET_Code"+" "+localeName);
        if (!localeName.equals(currentLanguage)) {
            Context context = LocaleHelper.setLocale(this, localeName);
            //Resources resources = context.getResources();
            myLocale = new Locale(localeName);
            Resources res = context.getResources();
            DisplayMetrics dm = res.getDisplayMetrics();
            Configuration conf = res.getConfiguration();
            conf.locale = myLocale;
            res.updateConfiguration(conf, dm);



            Intent refresh = new Intent(this, Profile.class);
            refresh.putExtra(currentLang, localeName);
            startActivity(refresh);
        } else {
          Toast.makeText(Profile.this, "Language already selected!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base));
    }

}