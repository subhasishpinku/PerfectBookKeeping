package perfect.book.keeping.activity;

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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.provider.Settings;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import javax.crypto.NoSuchPaddingException;

import perfect.book.keeping.R;
import perfect.book.keeping.adapter.UserTypeAdapter;
import perfect.book.keeping.api.ApiClient;
import perfect.book.keeping.encrypt.CryptLib;
import perfect.book.keeping.global.DataTransferService;
import perfect.book.keeping.global.Global;
import perfect.book.keeping.global.LocaleHelper;
import perfect.book.keeping.manager.Manager;
import perfect.book.keeping.model.UserType;
import perfect.book.keeping.request.Verify;
import perfect.book.keeping.response.AccessCode;
import perfect.book.keeping.response.AuthResponse;
import perfect.book.keeping.response.CompanyResponse;
import perfect.book.keeping.response.CountryStateResponse;
import perfect.book.keeping.response.ForgotPassResponse;
import perfect.book.keeping.response.OTPResponse;
import perfect.book.keeping.response.UserResponse;
import perfect.book.keeping.response.UserUpdateResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginScreen extends AppCompatActivity {

    TextView loginPassword, emailAddress, forgotPass, loginText, timer;
    LinearLayout loginBtn, forgotPassLayout, sectionEmail, sectionOTP, sectionPassword, next, verifyOTP, updatePass, verifyEmail, timer_layout, resend_layout;
    ProgressBar progressBar;
    RelativeLayout login_screen, passBody;
    String encrypt, enterOtp, fcm_token;

    EditText otp1, otp2, otp3, otp4, otp5, otp6, forgotPassEmailAddress, password, cnfPassword, chooseUserType, searchBy, otp;

    ImageView eye_btn, eye_btn_fp, eye_btn_cnf;

    CryptLib cryptLib;

    BottomSheetDialog sheetDialog;
    String authToken, dId, access_code;
    int package_id,  price, sub_user_price;
    boolean passwordVisible;

    Global global = new Global();

    Dialog progressDialog;

    String emailPattern = "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +

            "\\@" +

            "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +

            "(" +

            "\\." +

            "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +

            ")+";

    List<UserType> userTypes = new ArrayList<>();

    AlertDialog alertDialog;

    RecyclerView user_type_list;

    UserTypeAdapter userTypeAdapter;

    int userType = 0;

    TextView joinUs, joinUs_Alt;
    private List<Integer> compIds = new ArrayList<Integer>();
    String imgString, existing_file_name;
    Manager compDb;
    String timestamp2, timestamp;
    Locale myLocale;
    String currentLanguage = "en", currentLang;
    private CountDownTimer countDownTimer;
    SharedPreferences shared;
    String codeLang;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String languageToLoad  = "en"; // your language
        Locale locale = new Locale(languageToLoad);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
        setContentView(R.layout.activity_login_screen);


        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        android.graphics.drawable.Drawable background = LoginScreen.this.getDrawable(R.drawable.gradiant_btn_alt);
        getWindow().setBackgroundDrawable(background);
        currentLanguage = getIntent().getStringExtra(currentLang);
        //shared = getSharedPreferences("book_keeping", Context.MODE_PRIVATE);
        //codeLang = shared.getString("code_lang", "");
       // setLocale(codeLang);
        compDb = new Manager(LoginScreen.this);
        login_screen = findViewById(R.id.login_screen);
        emailAddress = findViewById(R.id.emailAddress);
        loginPassword = findViewById(R.id.loginPassword);
        forgotPass = findViewById(R.id.forgotPass);
        loginBtn = findViewById(R.id.loginBtn);
        progressBar = findViewById(R.id.progress);
        loginText = findViewById(R.id.loginText);
        verifyEmail = findViewById(R.id.verifyEmail);
        forgotPassLayout = findViewById(R.id.forgotPassLayout);
        eye_btn = findViewById(R.id.eye_btn);
        passBody = findViewById(R.id.passBody);
        chooseUserType = findViewById(R.id.userType);
        joinUs = findViewById(R.id.joinUs);
        joinUs_Alt = findViewById(R.id.joinUs_Alt);
        chooseUserType.setFocusable(false);
        chooseUserType.setClickable(true);

        userTypes.add(new UserType(0, "Customer"));
        userTypes.add(new UserType(1, "Sub User"));

        //fcm_token = getFCMToken();

        Log.e("FCM TOKEN",""+global.getFCMToken());



        chooseUserType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //userTypes.clear();
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getRootView().getContext(), R.style.my_dialog);
                View dialogView = LayoutInflater.from(v.getRootView().getContext()).inflate(R.layout.dialog_user_spinner, null);
                builder.setView(dialogView);
                builder.setCancelable(true);
                alertDialog = builder.create();
                alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable((Color.TRANSPARENT)));
                alertDialog.show();
                //searchBy = dialogView.findViewById(R.id.searchBy);
                user_type_list = dialogView.findViewById(R.id.user_type_list);
                if(userTypes.size() > 0) {
                    userTypeAdapter = new UserTypeAdapter(userTypes, LoginScreen.this, LoginScreen.this);
                    user_type_list.setAdapter(userTypeAdapter);
                    user_type_list.setLayoutManager(new LinearLayoutManager(LoginScreen.this));
                    userTypeAdapter.notifyDataSetChanged();
                }

            }
        });

        joinUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callAccount();
            }
        });

        joinUs_Alt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callAccount();
            }
        });


        progressDialog = new Dialog(LoginScreen.this);
        progressDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        progressDialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        progressDialog.setContentView(R.layout.progress);
        //progressDialog.setCancelable(false);
        if (progressDialog.getWindow() != null) {
            progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        dId = global.getDeviceToken(LoginScreen.this);

        Log.e("MAC",""+dId);

        try {
            cryptLib = new CryptLib();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (NoSuchPaddingException e) {
            throw new RuntimeException(e);
        }

        String helloText = getResources().getString(R.string.forgotPassword);
        forgotPass.setText(Html.fromHtml("<u>"+helloText+"</u>"));

        eye_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(loginPassword.getTransformationMethod().equals(HideReturnsTransformationMethod.getInstance())){
                    loginPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    eye_btn.setImageResource(R.drawable.hide);
                } else {
                    loginPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    eye_btn.setImageResource(R.drawable.show);
                }
            }
        });

        verifyEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String please_enter_email_address_user_name = getResources().getString(R.string.please_enter_email_address_user_name);
                String please_enter_valid_email_address = getResources().getString(R.string.please_enter_valid_email_address);
                String try_after_few_times = getResources().getString(R.string.try_after_few_times);

                if(emailAddress.getText().toString().equals("")) {
                    emailAddress.setError(please_enter_email_address_user_name);
                } else if(!emailAddress.getText().toString().matches(emailPattern)) {
                    emailAddress.setError(please_enter_valid_email_address);
                } else {
                    //Log.e("TIME ZONE",""+global.timeZone());
                    fcm_token = global.getFCMToken();
                    if(fcm_token != null) {
                        if(global.isNetworkAvailable(LoginScreen.this)) {
                            verifyEmail(emailAddress.getText().toString());
                        } else {
                            Toast.makeText(LoginScreen.this, getResources().getString(R.string.please_Check_Your_Internet_Connection), Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        Toast.makeText(LoginScreen.this, try_after_few_times, Toast.LENGTH_SHORT).show();
                    }

                }
//                if (userType == 0) {
//                    if(emailAddress.getText().toString().equals("")) {
//                        emailAddress.setError("Please enter email address");
//                    } else if(!emailAddress.getText().toString().matches(emailPattern)) {
//                        emailAddress.setError("Please enter valid email address");
//                    } else {
//                        verifyEmail(emailAddress.getText().toString());
//                    }
//                } else {
//                    if(emailAddress.getText().toString().equals("")) {
//                        emailAddress.setError("Please enter user name");
//                    } else {
//                        verifyEmail(emailAddress.getText().toString());
//                    }
//                }
            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String please_enter_email_address = getResources().getString(R.string.please_enter_email_address);
                String please_enter_password = getResources().getString(R.string.please_enter_password);
                if(emailAddress.getText().toString().equals("")) {
                    emailAddress.setError(please_enter_email_address);
                } else if(loginPassword.getText().toString().equals("")) {
                    loginPassword.setError(please_enter_password);
                } else {
                    if(global.isNetworkAvailable(LoginScreen.this)) {
                        try {
                            encrypt = cryptLib.encryptPlainTextWithRandomIV(loginPassword.getText().toString(), ApiClient.getInstance().getKey());
                            callLogin(emailAddress.getText().toString(), encrypt);

                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    } else {
                        Toast.makeText(LoginScreen.this, getResources().getString(R.string.please_Check_Your_Internet_Connection), Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });

        forgotPassLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sheetDialog = new BottomSheetDialog(LoginScreen.this, R.style.BottomSheetStyle);
                View forgotPass = getLayoutInflater().inflate(R.layout.forgot_pass, null);

                forgotPassEmailAddress = forgotPass.findViewById(R.id.forgotPassEmailAddress);
                next = forgotPass.findViewById(R.id.next);
                sectionEmail = forgotPass.findViewById(R.id.sectionEmail);
                sectionOTP = forgotPass.findViewById(R.id.sectionOTP);
                //OTP SECTION START
                timer = forgotPass.findViewById(R.id.timer);
                timer_layout = forgotPass.findViewById(R.id.timer_layout);
                resend_layout = forgotPass.findViewById(R.id.resend_layout);
                otp = forgotPass.findViewById(R.id.otp);
                otp1 = forgotPass.findViewById(R.id.otp1);
                otp2 = forgotPass.findViewById(R.id.otp2);
                otp3 = forgotPass.findViewById(R.id.otp3);
                otp4 = forgotPass.findViewById(R.id.otp4);
                otp5 = forgotPass.findViewById(R.id.otp5);
                otp6 = forgotPass.findViewById(R.id.otp6);
                verifyOTP = forgotPass.findViewById(R.id.verifyOTP);
                //OTP SECTION END
                //Password Section Start
                sectionPassword = forgotPass.findViewById(R.id.sectionPassword);
                password = forgotPass.findViewById(R.id.password);
                eye_btn_fp = forgotPass.findViewById(R.id.eye_btn_fp);
                cnfPassword = forgotPass.findViewById(R.id.cnfPassword);
                eye_btn_cnf = forgotPass.findViewById(R.id.eye_btn_cnf);
                updatePass = forgotPass.findViewById(R.id.updatePass);
                //Password Section End
                //MOVE NEXT START
                otp1.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                        if (otp1.getText().toString().length() == 1) {
                            otp1.clearFocus();
                            otp2.requestFocus();
                            otp2.setCursorVisible(true);
                        } else {
                            otp1.clearFocus();

                        }
                    }
                });
                otp2.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                        if (otp2.getText().toString().length() == 1) {
                            otp2.clearFocus();
                            otp3.requestFocus();
                            otp3.setCursorVisible(true);
                        } else {
                            otp2.clearFocus();
                            otp1.requestFocus();
                            otp1.setCursorVisible(true);
                        }
                    }
                });
                otp3.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                        if (otp3.getText().toString().length() == 1) {
                            otp3.clearFocus();
                            otp4.requestFocus();
                            otp4.setCursorVisible(true);
                        } else {
                            otp3.clearFocus();
                            otp2.requestFocus();
                            otp2.setCursorVisible(true);
                        }
                    }
                });
                otp4.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                        if (otp4.getText().toString().length() == 1) {
                            otp4.clearFocus();
                            otp5.requestFocus();
                            otp5.setCursorVisible(true);
                        } else {
                            otp4.clearFocus();
                            otp3.requestFocus();
                            otp3.setCursorVisible(true);
                        }
                    }
                });
                otp5.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                        if (otp5.getText().toString().length() == 1) {
                            otp5.clearFocus();
                            otp6.requestFocus();
                            otp6.setCursorVisible(true);
                        } else {
                            otp5.clearFocus();
                            otp4.requestFocus();
                            otp4.setCursorVisible(true);
                        }
                    }
                });
                otp6.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                        if (otp6.getText().toString().length() == 1) {
                            otp6.requestFocus();
                            otp6.setCursorVisible(true);
                        } else {
                            otp6.clearFocus();
                            otp5.requestFocus();
                            otp5.setCursorVisible(true);
                        }
                    }
                });


                resend_layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        requestOTP(forgotPassEmailAddress.getText().toString());
                    }
                });

                if(!emailAddress.getText().toString().equals("")) {
                    forgotPassEmailAddress.setText(emailAddress.getText().toString());
                }

                //MOVE NEXT END
                next.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String please_enter_email_address = getResources().getString(R.string.please_enter_email_address);
                        if(forgotPassEmailAddress.getText().toString().equals("")) {
                            forgotPassEmailAddress.setError(please_enter_email_address);
                        } else {
                            if(global.isNetworkAvailable(LoginScreen.this)) {
                                requestOTP(forgotPassEmailAddress.getText().toString());
                            } else {
                                Toast.makeText(LoginScreen.this, getResources().getString(R.string.please_Check_Your_Internet_Connection), Toast.LENGTH_SHORT).show();
                            }

                        }
                    }
                });

                verifyOTP.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String please_enter_OTP = getResources().getString(R.string.please_enter_OTP);
                        String invalid_OTP = getResources().getString(R.string.invalid_OTP);
                        //enterOtp =  otp1.getText().toString() + otp2.getText().toString() + otp3.getText().toString() + otp4.getText().toString() + otp5.getText().toString() + otp6.getText().toString();
                        enterOtp = otp.getText().toString();
                        if(enterOtp.equals("")) {
                            Toast.makeText(LoginScreen.this, please_enter_OTP, Toast.LENGTH_SHORT).show();
                        } else if(enterOtp.length() < 6) {
                            Toast.makeText(LoginScreen.this, invalid_OTP, Toast.LENGTH_SHORT).show();
                        } else {
                            if(global.isNetworkAvailable(LoginScreen.this)) {
                                try {
                                    encrypt = cryptLib.encryptPlainTextWithRandomIV(enterOtp, ApiClient.getInstance().getKey());
                                    otpVerify(encrypt, forgotPassEmailAddress.getText().toString());
                                } catch (Exception e) {
                                    throw new RuntimeException(e);
                                }
                            } else {
                                Toast.makeText(LoginScreen.this, getResources().getString(R.string.please_Check_Your_Internet_Connection), Toast.LENGTH_SHORT).show();
                            }
                        }

                    }
                });

                //Password Toggle
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

                updatePass.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String please_enter_new_password = getResources().getString(R.string.please_enter_new_password);
                        String confirm_password_mismatch = getResources().getString(R.string.confirm_password_mismatch);
                        if(password.getText().toString().equals("")) {
                            password.setError(please_enter_new_password);
                        } else if(cnfPassword.getText().toString().equals("")) {
                            cnfPassword.setError(confirm_password_mismatch);
                        } else if(password.getText().toString().equals(cnfPassword.getText().toString())) {
                            if(global.isNetworkAvailable(LoginScreen.this)) {
                                try {
                                    encrypt = cryptLib.encryptPlainTextWithRandomIV(password.getText().toString(), ApiClient.getInstance().getKey());
                                    passwordUpdate(encrypt, forgotPassEmailAddress.getText().toString());
                                } catch (Exception e) {
                                    throw new RuntimeException(e);
                                }
                            } else {
                                Toast.makeText(LoginScreen.this, getResources().getString(R.string.please_Check_Your_Internet_Connection), Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            cnfPassword.setError(confirm_password_mismatch);
                        }
                    }
                });
                sheetDialog.setContentView(forgotPass);
                sheetDialog.show();
            }
        });

    }



    private void callAccount() {
        startActivity(new Intent(LoginScreen.this, Account.class));
    }


    private void passwordUpdate(String password, String email) {
        progressDialog.show();
        Call<UserUpdateResponse> updatePass = ApiClient.getInstance().getBookKeepingApi().updatePass(email, access_code, password);
        updatePass.enqueue(new Callback<UserUpdateResponse>() {
            @Override
            public void onResponse(Call<UserUpdateResponse> call, Response<UserUpdateResponse> response) {
                if(response.code() == 200) {
                    progressDialog.dismiss();
                    sectionPassword.setVisibility(View.GONE);
                    sheetDialog.dismiss();
                    String password_changed_successfully = getResources().getString(R.string.password_changed_successfully);
                    Toast.makeText(LoginScreen.this, password_changed_successfully, Toast.LENGTH_SHORT).show();
                } else {
                    progressDialog.dismiss();
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        Toast.makeText(LoginScreen.this, ""+jObjError.getString("message"), Toast.LENGTH_SHORT).show();
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            @Override
            public void onFailure(Call<UserUpdateResponse> call, Throwable t) {

            }
        });

    }

    private void requestOTP(String email) {
        progressDialog.show();
        Call<ForgotPassResponse> emailVerify = ApiClient.getInstance().getBookKeepingApi().requestOTP(email, dId, "android");
        emailVerify.enqueue(new Callback<ForgotPassResponse>() {
            @Override
            public void onResponse(Call<ForgotPassResponse> call, Response<ForgotPassResponse> response) {
                if(response.code() == 200) {
                    progressDialog.dismiss();
                    if (countDownTimer != null) {
                        countDownTimer.cancel();  // Cancel the previous timer if any
                    }
                    Toast.makeText(LoginScreen.this, ""+response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    access_code = response.body().getData().getAccessCode();
                    sectionEmail.setVisibility(View.GONE);
                    sectionOTP.setVisibility(View.VISIBLE);
                    timer_layout.setVisibility(View.VISIBLE);
                    resend_layout.setVisibility(View.GONE);
                    long duration = TimeUnit.MINUTES.toMillis(1);
                    countDownTimer = new CountDownTimer(duration, 1000) {

                        @Override
                        public void onTick(long millisUntilFinished) {
                            String sDuration = String.format(Locale.ENGLISH, "%02d : %02d",
                                    TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished),
                                    TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished),
                                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)));
                            timer.setText(sDuration);

                        }

                        @Override
                        public void onFinish() {
                            timer_layout.setVisibility(View.GONE);
                            resend_layout.setVisibility(View.VISIBLE);
                        }
                    }.start();
                } else {
                    progressDialog.dismiss();
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        Toast.makeText(LoginScreen.this, ""+jObjError.getString("message"), Toast.LENGTH_SHORT).show();
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            @Override
            public void onFailure(Call<ForgotPassResponse> call, Throwable t) {

            }
        });
    }

    private void otpVerify(String otp, String email) {
        progressDialog.show();
        Call<OTPResponse> otpVerify = ApiClient.getInstance().getBookKeepingApi().verify(email, otp, access_code, dId, "android");
        otpVerify.enqueue(new Callback<OTPResponse>() {
            @Override
            public void onResponse(Call<OTPResponse> call, Response<OTPResponse> response) {
                if(response.code() == 200) {
                    progressDialog.dismiss();
                    access_code = response.body().getData().getAccessCode();
                    sectionOTP.setVisibility(View.GONE);
                    sectionPassword.setVisibility(View.VISIBLE);
                    String OTP_verified_Successfully = getResources().getString(R.string.OTP_verified_Successfully);
                    Toast.makeText(LoginScreen.this, OTP_verified_Successfully, Toast.LENGTH_SHORT).show();
                } else {
                    progressDialog.dismiss();
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        Toast.makeText(LoginScreen.this, ""+jObjError.getString("message"), Toast.LENGTH_SHORT).show();
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            @Override
            public void onFailure(Call<OTPResponse> call, Throwable t) {

            }
        });
    }
    private void verifyEmail(String email) {
        int i = 0;
        progressDialog.show();
        Verify verify = new Verify(email , dId, "android");
        Call<AccessCode> accessCodeCall = ApiClient.getInstance().getBookKeepingApi().accessCode(verify);
        accessCodeCall.enqueue(new Callback<AccessCode>() {
            @Override
            public void onResponse(Call<AccessCode> call, Response<AccessCode> response) {
                Log.e("ACCESS RESPONSE",""+response.code());
                if(response.code() == 200) {
                    progressDialog.dismiss();
                    access_code = response.body().getData().getAccessCode();
                    userType = response.body().getData().getUser_type();
                    loginPassword.setVisibility(View.VISIBLE);
                    loginBtn.setVisibility(View.VISIBLE);
                    passBody.setVisibility(View.VISIBLE);
                    verifyEmail.setVisibility(View.GONE);
                    chooseUserType.setVisibility(View.GONE);
                    //emailAddress.setFocusable(false);
                    emailAddress.setEnabled(false);
                } else {
                    progressDialog.dismiss();
                    try {
                        JSONObject jObjError = new  JSONObject(response.errorBody().string());
                        Toast.makeText(LoginScreen.this, ""+jObjError.getString("message"), Toast.LENGTH_SHORT).show();
                    } catch (JSONException e) {
                         throw new RuntimeException(e);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                }
            }

            @Override
            public void onFailure(Call<AccessCode> call, Throwable t) {

            }
        });
    }
    private void callLogin(String email, String password) {
        progressDialog.show();
        Call<AuthResponse> call = ApiClient.getInstance().getBookKeepingApi().authUser(access_code, password, email, fcm_token);
        call.enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                Log.e("RESPONSE CODE",""+response.code());
                if(response.code() == 200) {
                    progressDialog.dismiss();
                    SharedPreferences loginRef = getSharedPreferences("book_keeping", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = loginRef.edit();
                    editor.putString("access_token",response.body().getData().getAccessToken());
                    editor.putString("refresh_token",response.body().getData().getRefreshToken());
                    editor.putString("fcm_token",fcm_token);
                    //editor.putString("first_input","true");
                    editor.commit();
                    getUserId("Bearer "+response.body().getData().getAccessToken());

                } else {
                    progressDialog.dismiss();
                    loginPassword.setVisibility(View.GONE);
                    loginBtn.setVisibility(View.GONE);
                    passBody.setVisibility(View.GONE);
                    verifyEmail.setVisibility(View.VISIBLE);

                    emailAddress.setEnabled(true);
                    access_code = "";
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        Toast.makeText(LoginScreen.this, ""+jObjError.getString("message"), Toast.LENGTH_SHORT).show();
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                    loginText.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                }

            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {

            }
        });
    }
    private void getUserId(String token) {
        Call<UserResponse> userId = ApiClient.getInstance().getBookKeepingApi().userProfile(token);
        userId.enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                Log.e("Password Status CODE",""+response.code());
                if(response.code() == 200) {
                    Log.e("Password Status",""+response.body().getData().getChanged_password());
                    SharedPreferences loginRef = getSharedPreferences("book_keeping", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = loginRef.edit();
                    editor.putInt("login_user_id",response.body().getData().getId());
                    editor.putInt("password_need_to_change",response.body().getData().getChanged_password());
                    editor.commit();
                    Intent intent = new Intent(LoginScreen.this, Companies.class);
                    intent.putExtra("openGallery", "false");
                    intent.putExtra("openDashBoard", "false");
                    intent.putExtra("companyId", 0);
                    intent.putExtra("companyName", "");
                    startActivity(intent);
                }
            }
            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {

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

    public String downloadImage(String imageUrl, int position, String company_name, String timestamp, String existing_file_name) {
        try {
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();

            InputStream inputStream = connection.getInputStream();
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

            String fileName = timestamp+".png";

            File directory = new File(LoginScreen.this.getExternalFilesDir(Environment.DIRECTORY_PICTURES).toString(), "Company");
            if (!directory.exists()) {
                if (!directory.mkdirs()) {
                    return null;
                }
            }

            File imageFile = new File(directory, fileName);
            // Delete any previous file with the same name
//            if (directory != null && existing_file_name != null) {
//                File removeFile = new File(directory, existing_file_name);
//                if (removeFile.exists()) {
//                    removeFile.delete();
//                }
//            } else {
//
//            }
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

    public void updateType(String name, int id) {
        userType = id;
        chooseUserType.setText(name);
        String enter_Your_User_Name = getResources().getString(R.string.enter_Your_User_Name);
        if(id == 1) {
            emailAddress.setHint(enter_Your_User_Name);
        }
        alertDialog.dismiss();
    }

    @Override
    public void onBackPressed() {
        if (passBody.getVisibility() == View.VISIBLE) {
            // Its visible
            Log.e("PASSWORD","VISIBLE");
            loginPassword.setVisibility(View.GONE);
            loginBtn.setVisibility(View.GONE);
            passBody.setVisibility(View.GONE);
            verifyEmail.setVisibility(View.VISIBLE);

            emailAddress.setEnabled(true);
            access_code = "";
        } else {
            String are_you_sure_you_want_to_exit = getResources().getString(R.string.are_you_sure_you_want_to_exit);
            String no = getResources().getString(R.string.no);
            String yes = getResources().getString(R.string.yes);
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(LoginScreen.this);
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
    }
    public void setLocale(String localeName) {
        if (!localeName.equals(currentLanguage)) {
            Context context = LocaleHelper.setLocale(this, localeName);
            //Resources resources = context.getResources();
            myLocale = new Locale(localeName);
            Resources res = context.getResources();
            DisplayMetrics dm = res.getDisplayMetrics();
            Configuration conf = res.getConfiguration();
            conf.locale = myLocale;
            res.updateConfiguration(conf, dm);
            Intent refresh = new Intent(this, LoginScreen.class);
            refresh.putExtra(currentLang, localeName);
            startActivity(refresh);
        } else {
            //  Toast.makeText(LoginScreen.this, "Language already selected!", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base));
    }
}