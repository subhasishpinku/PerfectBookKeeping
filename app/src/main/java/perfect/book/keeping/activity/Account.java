package perfect.book.keeping.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hbb20.CountryCodePicker;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;

import javax.crypto.NoSuchPaddingException;

import perfect.book.keeping.R;
import perfect.book.keeping.api.ApiClient;
import perfect.book.keeping.encrypt.CryptLib;
import perfect.book.keeping.response.Signup;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Account extends AppCompatActivity {

    TextView login,textMessage;
    LinearLayout createAccount;
    EditText name, emailAddress, phoneNumber, accountPassword, cnf_Password;
    CountryCodePicker cpp;
    ImageView eye_btn, eye_btn_cnf;
    String emailPattern = "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +

            "\\@" +

            "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +

            "(" +

            "\\." +

            "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +

            ")+";
    String newEncrypt, phone_number;
    CryptLib cryptLib;
    Dialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String languageToLoad  = "en"; // your language
        Locale locale = new Locale(languageToLoad);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
        setContentView(R.layout.activity_account);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        android.graphics.drawable.Drawable background = Account.this.getDrawable(R.drawable.gradiant_btn_alt);
        getWindow().setBackgroundDrawable(background);

        try {
            cryptLib = new CryptLib();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (NoSuchPaddingException e) {
            throw new RuntimeException(e);
        }

        initialize();
    }

    private void initialize() {
        name = findViewById(R.id.name);
        emailAddress = findViewById(R.id.emailAddress);
        cpp = findViewById(R.id.cpp);
        phoneNumber = findViewById(R.id.phoneNumber);
        accountPassword = findViewById(R.id.accountPassword);
        eye_btn = findViewById(R.id.eye_btn);
        cnf_Password = findViewById(R.id.cnf_Password);
        eye_btn_cnf = findViewById(R.id.eye_btn_cnf);
        login = findViewById(R.id.login);
        createAccount = findViewById(R.id.createAccount);

        eye_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                togglePassword("Password");
            }
        });

        eye_btn_cnf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                togglePassword("Confirm Password");
            }
        });

        createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validate();
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Account.this, LoginScreen.class));
            }
        });

        progressDialog = new Dialog(Account.this);
        progressDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        progressDialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        progressDialog.setContentView(R.layout.progress);
        if (progressDialog.getWindow() != null) {
            progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

    }

    private void validate() {
        String please_enter_your_name = getResources().getString(R.string.please_enter_your_name);
        String please_enter_email_address = getResources().getString(R.string.please_enter_email_address);
        String please_enter_valid_email_address = getResources().getString(R.string.please_enter_valid_email_address);
        String please_enter_your_phone_number = getResources().getString(R.string.please_enter_your_phone_number);
        String please_enter_a_valid_phone_number = getResources().getString(R.string.please_enter_a_valid_phone_number);
        String please_enter_a_password = getResources().getString(R.string.please_enter_a_password);
        String new_password_must_be_6_character_long = getResources().getString(R.string.new_password_must_be_6_character_long);
        String please_reenter_the_password = getResources().getString(R.string.please_reenter_the_password);
        String confirm_password_mismatch = getResources().getString(R.string.confirm_password_mismatch);
        if(name.getText().toString().equals("")) {
            name.setError(please_enter_your_name);
        } else if (emailAddress.getText().toString().equals("")) {
            emailAddress.setError(please_enter_email_address);
        } else if(!emailAddress.getText().toString().matches(emailPattern)) {
            emailAddress.setError(please_enter_valid_email_address);
        } else if (phoneNumber.getText().toString().equals("")) {
            phoneNumber.setError(please_enter_your_phone_number);
        } else if (phoneNumber.getText().toString().length() < 10) {
            phoneNumber.setError(please_enter_a_valid_phone_number);
        } else if (accountPassword.getText().toString().equals("")) {
            eye_btn.setVisibility(View.GONE);
            accountPassword.setError(please_enter_a_password);
        } else if (accountPassword.getText().toString().length() < 6) {
            eye_btn.setVisibility(View.GONE);
            accountPassword.setError(new_password_must_be_6_character_long);
        } else if(cnf_Password.getText().toString().equals("")) {
            eye_btn_cnf.setVisibility(View.GONE);
            cnf_Password.setError(please_reenter_the_password);
        } else if (cnf_Password.getText().toString().length() < 6) {
            eye_btn_cnf.setVisibility(View.GONE);
            cnf_Password.setError(please_reenter_the_password);
        } else if(accountPassword.getText().toString().equals(cnf_Password.getText().toString())) {
            eye_btn_cnf.setVisibility(View.VISIBLE);
            eye_btn.setVisibility(View.VISIBLE);
            try {
                phone_number = cpp.getSelectedCountryCodeWithPlus() + phoneNumber.getText().toString();
                newEncrypt = cryptLib.encryptPlainTextWithRandomIV(accountPassword.getText().toString(), ApiClient.getInstance().getKey());
                create_account(name.getText().toString(), emailAddress.getText().toString(), phone_number, newEncrypt);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        } else {
            eye_btn_cnf.setVisibility(View.GONE);
            cnf_Password.setError(confirm_password_mismatch);
        }
    }

    private void create_account(String name, String email, String phone, String password) {
        progressDialog.show();
        Call<Signup> create = ApiClient.getInstance().getBookKeepingApi().createAccount(email, password, name, phone);
        create.enqueue(new Callback<Signup>() {
            @Override
            public void onResponse(Call<Signup> call, Response<Signup> response) {
                if(response.code() == 200) {
                    progressDialog.dismiss();
                    showAlertDialog(response.body().getMessage());
                } else {
                    progressDialog.dismiss();
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        Toast.makeText(Account.this, ""+jObjError.getString("message"), Toast.LENGTH_SHORT).show();

                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            @Override
            public void onFailure(Call<Signup> call, Throwable t) {

            }
        });
    }
    public  void showAlertDialog(String message){
        String ok = getResources().getString(R.string.ok);

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(Account.this);
        builder.setMessage(message);
        builder.setNegativeButton(ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                startActivity(new Intent(Account.this, LoginScreen.class));
                dialog.dismiss();
            }
        });
        android.app.AlertDialog alert = builder.create();
        alert.show();
    }

    private void togglePassword(String type) {
        if(type.equals("Password")) {
            if(accountPassword.getTransformationMethod().equals(HideReturnsTransformationMethod.getInstance())){
                accountPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                eye_btn.setImageResource(R.drawable.hide);
            } else {
                accountPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                eye_btn.setImageResource(R.drawable.show);
            }
        } else {
            if(cnf_Password.getTransformationMethod().equals(HideReturnsTransformationMethod.getInstance())){
                cnf_Password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                eye_btn_cnf.setImageResource(R.drawable.hide);
            } else {
                cnf_Password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                eye_btn_cnf .setImageResource(R.drawable.show);
            }
        }
    }
}