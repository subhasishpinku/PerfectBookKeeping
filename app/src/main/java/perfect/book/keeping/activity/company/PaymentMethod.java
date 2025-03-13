package perfect.book.keeping.activity.company;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
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
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
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
import perfect.book.keeping.activity.pnl.ProfitLoss;
import perfect.book.keeping.activity.profile.Profile;
import perfect.book.keeping.adapter.CountryAdapter;
import perfect.book.keeping.adapter.StateAdapter;
import perfect.book.keeping.api.ApiClient;
import perfect.book.keeping.encrypt.CryptLib;
import perfect.book.keeping.global.Global;
import perfect.book.keeping.global.LocaleHelper;
import perfect.book.keeping.manager.Manager;
import perfect.book.keeping.model.Country;
import perfect.book.keeping.model.States;
import perfect.book.keeping.request.Address;
import perfect.book.keeping.request.Billing_Address;
import perfect.book.keeping.request.Card;
import perfect.book.keeping.request.CompanyMethodRequest;
import perfect.book.keeping.request.CompanyUpdateRequest;
import perfect.book.keeping.request.ModifyCompanies;
import perfect.book.keeping.response.ChangePassword;
import perfect.book.keeping.response.CompanyModifyResponse;
import perfect.book.keeping.response.CompanyResponse;
import perfect.book.keeping.response.LogoutResponse;
import perfect.book.keeping.response.ModifyCompany;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PaymentMethod extends AppCompatActivity {
    SharedPreferences shared;
    String auth,newEncrypt, oldEncrypt,fcm_token, billingCountryName= "",billingStateName="";
    Manager db;
    int companyId, permission, payment_status = 1;
    int card_issue_type = 0, selected_Card = 0, package_id, noOfRec = 0;

    String companyName,mode, currentYear, currentMonth,paymentStatus,PackagePrice,SubUserPrice,timestamp,timestamp2,existing_file_name;
    EditText billingAddressOne,billingAddressTwo,billingCountrySelect,billingStateSelect,billingState,
            billingCity,billingZip,card_number,expiry,cvv,password,cnfPassword,oldPassword,card_holder_name,searchByCities,searchByStates;
    CardView design, back_design;
    RelativeLayout back_container_bg,list_container_bg,mainView;
    TextView cvv_number,cardNumber,expiry_date,add_company,card_holderName,package_item_description,package_item_name,package_item_price;
    ImageView cardType,eye_btn_fp,eye_btn_cnf,eye_btn_fp_old,close;
    LinearLayout back,openMenu,updatePass;
    CircleImageView profile_image;
    BottomSheetDialog sheetDialog;
    CryptLib cryptLib;
    Dialog progressDialog, dialog, currencyDialog;
    Global global;
    private AnimatorSet backAnimator;
    private AnimatorSet frontAnimator;
    private static final String EMPTY_STRING = "";
    private static final String WHITE_SPACE = " ";
    private static final String SLASH_SPACE = "/";
    private String lastSource = EMPTY_STRING;
    private boolean isCardFlipped = false;
    private List<Country> countries= new ArrayList<>();
    private List<States> states = new ArrayList<>();

    AlertDialog alertDialog, alertDialogStates;
    RecyclerView country_list, state_list;
    CountryAdapter adapter;
    StateAdapter stateAdapter;
    Locale myLocale;
    String codeLang;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_method);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        android.graphics.drawable.Drawable background = PaymentMethod.this.getDrawable(R.drawable.gradiant_btn_alt);
        getWindow().setBackgroundDrawable(background);
        initialize();
    }
    private void initialize() {
        SimpleDateFormat monthFormat = new SimpleDateFormat("MM");
        SimpleDateFormat yearFormat = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            yearFormat = new SimpleDateFormat("YY");
        }
        shared = getSharedPreferences("book_keeping", Context.MODE_PRIVATE);
        auth = shared.getString("access_token", "");
        db = new Manager(PaymentMethod.this);

        companyId = getIntent().getIntExtra("companyId",0);
        permission = getIntent().getIntExtra("permission", 0);
        companyName = getIntent().getStringExtra("companyName");
        payment_status = getIntent().getIntExtra("payment_status", 1);
        mode = getIntent().getStringExtra("mode");
        System.out.println("PaymentMethod"+" "+companyId+" "+permission+" "+companyName+" "+mode);

        shared = getSharedPreferences("book_keeping", Context.MODE_PRIVATE);
        codeLang = shared.getString("code_lang", "");
        setLocale(codeLang);

        //Billing Address Block
        billingAddressOne = findViewById(R.id.billingAddressOne);
        billingAddressTwo = findViewById(R.id.billingAddressTwo);
        billingStateSelect = findViewById(R.id.billingStateSelect);
        billingCountrySelect = findViewById(R.id.billingCountrySelect);
        billingState = findViewById(R.id.billingState);
        billingCity = findViewById(R.id.billingCity);
        billingZip = findViewById(R.id.billingZip);
        design = findViewById(R.id.design);
        back_design = findViewById(R.id.back_design);
        back_container_bg = findViewById(R.id.back_container_bg);
        cvv_number = findViewById(R.id.cvv_number);
        list_container_bg = findViewById(R.id.list_container_bg);
        cardNumber = findViewById(R.id.cardNumber);
        card_holderName = findViewById(R.id.card_holder_name);
        card_holder_name = findViewById(R.id.cardHolderName);

        package_item_name = findViewById(R.id.package_item_name);
        package_item_description = findViewById(R.id.package_item_description);
        package_item_price = findViewById(R.id.package_item_price);

        expiry_date = findViewById(R.id.expiry_date);
        cardType = findViewById(R.id.card_type);
        card_number = findViewById(R.id.card_number);
        expiry = findViewById(R.id.expiry);
        cvv = findViewById(R.id.cvv);
        add_company = findViewById(R.id.add_company);
        back = findViewById(R.id.back);
        profile_image = findViewById(R.id.profile_image);
        openMenu = findViewById(R.id.openMenu);
        mainView = findViewById(R.id.mainView);
        backAnimator = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.flip_reverse);
        backAnimator.setTarget(back_design);
        currentYear = yearFormat.format(new Date());
        currentMonth = monthFormat.format(new Date());
        backAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        // Load front card animation
        frontAnimator = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.flip_reverse);
        frontAnimator.setTarget(design);
        // Load back card animation
        openMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopUp(v);
            }
        });
        add_company.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateCompany();
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redirect();
            }
        });
        card_holder_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.toString().equals("")) {
                    card_holderName.setText("xxxxxxxxxxxxx");
                } else {
                    card_holderName.setText(s.toString().toUpperCase());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        card_number.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String ccNum = s.toString();
                if(ccNum.equals("")) {
                    cardNumber.setText("XXXX XXXX XXXX XXXX");
                } else {
                    cardNumber.setText(ccNum);
                }
                int card_type = getCardType(ccNum);
                card_issue_type = card_type;
                if(card_type == 1) {
                    cardType.setImageDrawable(ContextCompat.getDrawable(PaymentMethod.this, R.drawable.logo_visa));
                    list_container_bg.setBackground(ContextCompat.getDrawable(PaymentMethod.this, R.drawable.visa_card_bg));
                    back_container_bg.setBackground(ContextCompat.getDrawable(PaymentMethod.this, R.drawable.visa_card_bg));
                } else if (card_type == 2) {
                    cardType.setImageDrawable(ContextCompat.getDrawable(PaymentMethod.this, R.drawable.logo_mastercard));
                    list_container_bg.setBackground(ContextCompat.getDrawable(PaymentMethod.this, R.drawable.master_card_bg));
                    back_container_bg.setBackground(ContextCompat.getDrawable(PaymentMethod.this, R.drawable.master_card_bg));
                } else if (card_type == 3) {
                    cardType.setImageDrawable(ContextCompat.getDrawable(PaymentMethod.this, R.drawable.logo_discover));
                    list_container_bg.setBackground(ContextCompat.getDrawable(PaymentMethod.this, R.drawable.discover_card_bg));
                    back_container_bg.setBackground(ContextCompat.getDrawable(PaymentMethod.this, R.drawable.discover_card_bg));
                } else if (card_type == 4) {
                    cardType.setImageDrawable(ContextCompat.getDrawable(PaymentMethod.this, R.drawable.logo_amex));
                    list_container_bg.setBackground(ContextCompat.getDrawable(PaymentMethod.this, R.drawable.amex_card_bg));
                    back_container_bg.setBackground(ContextCompat.getDrawable(PaymentMethod.this, R.drawable.amex_card_bg));
                } else if (card_type == 5) {
                    cardType.setImageDrawable(ContextCompat.getDrawable(PaymentMethod.this, R.drawable.logo_jcb));
                    list_container_bg.setBackground(ContextCompat.getDrawable(PaymentMethod.this, R.drawable.jcb_card_bg));
                    back_container_bg.setBackground(ContextCompat.getDrawable(PaymentMethod.this, R.drawable.jcb_card_bg));
                } else if (card_type == 6) {
                    cardType.setImageDrawable(ContextCompat.getDrawable(PaymentMethod.this, R.drawable.logo_diners));
                    list_container_bg.setBackground(ContextCompat.getDrawable(PaymentMethod.this, R.drawable.diners_club_card_bg));
                    back_container_bg.setBackground(ContextCompat.getDrawable(PaymentMethod.this, R.drawable.diners_club_card_bg));
                } else {
                    cardType.setImageDrawable(ContextCompat.getDrawable(PaymentMethod.this, R.drawable.logo_generic));
                    list_container_bg.setBackground(ContextCompat.getDrawable(PaymentMethod.this, R.color.purple_color));
                    back_container_bg.setBackground(ContextCompat.getDrawable(PaymentMethod.this, R.color.purple_color));
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
                makeCardNumber(s);
            }
        });
        expiry.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String current = s.toString();
                if(current.equals("")) {
                    expiry_date.setText("xxxxxxxxxxxxx");
                } else {
                    expiry_date.setText(current);
                }
                if (current.length() == 2 && start == 1) {
                    int now = Integer.parseInt(s.toString());
                    if(now<= 12 && now >=1) {
                        expiry.setText(current + SLASH_SPACE);
                        expiry.setSelection(current.length() + 1);
                    } else {
                        expiry.setText("");
                        Toast.makeText(PaymentMethod.this, getResources().getString(R.string.wrong_month_input), Toast.LENGTH_SHORT).show();
                    }

                } else if (current.length() == 2 && before == 1) {
                    current = current.substring(0, 1);
                    expiry.setText(current);
                    expiry.setSelection(current.length());
                }

            }
            @Override
            public void afterTextChanged(Editable s) {
                if(s.toString().length() == 5) {
                    String year = s.toString().split("/")[1];
                    String month = s.toString().split("/")[0];
                    //Toast.makeText(getActivity(), "Input Year"+year, Toast.LENGTH_SHORT).show();
                    if(Integer.parseInt(month) < Integer.parseInt(currentMonth) && Integer.parseInt(year) == Integer.parseInt(currentYear)) {
                        Toast.makeText(PaymentMethod.this, getResources().getString(R.string.card_already_expired), Toast.LENGTH_SHORT).show();
                        expiry.setText("");
                    } else if (Integer.parseInt(month) >= Integer.parseInt(currentMonth) && Integer.parseInt(year) == Integer.parseInt(currentYear)) {
                        cvv.requestFocus();
                        cvv.setCursorVisible(true);
                        Log.e("is card Flip",""+isCardFlipped);
                        backAnimator.start();
                        design.setVisibility(View.GONE);
                        back_design.setVisibility(View.VISIBLE);
                    } else if (Integer.parseInt(year) >= Integer.parseInt(currentYear) && Integer.parseInt(month) >= Integer.parseInt(currentMonth)) {
                        cvv.requestFocus();
                        cvv.setCursorVisible(true);
                        Log.e("is card Flip",""+isCardFlipped);
                        backAnimator.start();
                        design.setVisibility(View.GONE);
                        back_design.setVisibility(View.VISIBLE);
                    } else if (Integer.parseInt(year) >= Integer.parseInt(currentYear) && Integer.parseInt(month) <= Integer.parseInt(currentMonth)) {
                        cvv.requestFocus();
                        cvv.setCursorVisible(true);
                        Log.e("is card Flip",""+isCardFlipped);
                        backAnimator.start();
                        design.setVisibility(View.GONE);
                        back_design.setVisibility(View.VISIBLE);
                    } else if (Integer.parseInt(month) >= Integer.parseInt(currentMonth) && Integer.parseInt(year) < Integer.parseInt(currentYear)) {
                        Toast.makeText(PaymentMethod.this, getResources().getString(R.string.card_already_expired), Toast.LENGTH_SHORT).show();
                        expiry.setText("");
                    } else {
                        Toast.makeText(PaymentMethod.this, getResources().getString(R.string.card_already_expired), Toast.LENGTH_SHORT).show();
                        expiry.setText("");
                    }
                } else {
                    cvv.clearFocus();
                    expiry.requestFocus();
                    expiry.setCursorVisible(true);
                }
            }
        });
        cvv.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.toString().equals("")) {
                    cvv_number.setText("CVV");
                } else {
                    cvv_number.setText(s.toString());
                    if(s.toString().length() == 3) {
                        frontAnimator.start();
                        design.setVisibility(View.VISIBLE);
                        back_design.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.toString().equals("")) {
                    expiry.requestFocus();
                    expiry.setCursorVisible(true);
                    backAnimator.start();
                    back_design.setVisibility(View.GONE);
                    design.setVisibility(View.VISIBLE);
                }
            }
        });
        try {
            cryptLib = new CryptLib();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (NoSuchPaddingException e) {
            throw new RuntimeException(e);
        }
        global = new Global();
        progressDialog = new Dialog(PaymentMethod.this);
        progressDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        progressDialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        progressDialog.setContentView(R.layout.progress);
        if (progressDialog.getWindow() != null) {
            progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        Cursor cursor = db.fetchCompanyImage(companyId);
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
            paymentStatus = cursor.getString(6);
            PackagePrice = cursor.getString(8);
            SubUserPrice = cursor.getString(9);

            if (paymentStatus.equals("0")){
                String subscribe = getResources().getString(R.string.Subscribe);
                add_company.setText(subscribe);
            }else {
                String update = getResources().getString(R.string.update);
                add_company.setText(update);

            }
        }
        getCompany("Bearer "+auth);

        billingCountrySelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                countries.clear();
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getRootView().getContext());
                View dialogView = LayoutInflater.from(v.getRootView().getContext()).inflate(R.layout.dialog_searchable_spinner, null);
                builder.setView(dialogView);
                builder.setCancelable(true);
                alertDialog = builder.create();
                alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable((Color.TRANSPARENT)));
                alertDialog.show();
                searchByCities = dialogView.findViewById(R.id.searchBy);
                country_list = dialogView.findViewById(R.id.country_list);
                close = dialogView.findViewById(R.id.close);
                close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });
                Cursor cursor = db.fetchCountry();

                while (cursor.moveToNext()) {
                    Country country = new Country(cursor.getString(1));
                    countries.add(country);
                }
                if(countries.size() > 0) {
                    Log.e("TAG", ""+countries.size());
                    adapter = new CountryAdapter(countries, PaymentMethod.this, "PaymentMethod");
                    country_list.setAdapter(adapter);
                    country_list.setLayoutManager(new LinearLayoutManager(PaymentMethod.this));
                    adapter.notifyDataSetChanged();
                }


                searchByCities.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if(countries.size() > 0) {
                            adapter.getFilter().filter(s);
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });
            }
        });
        billingStateSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                states.clear();
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getRootView().getContext(), R.style.my_dialog);
                View dialogView = LayoutInflater.from(v.getRootView().getContext()).inflate(R.layout.dialog_searchable_spinner_states, null);
                builder.setView(dialogView);
                builder.setCancelable(true);
                alertDialogStates = builder.create();
                alertDialogStates.getWindow().setBackgroundDrawable(new ColorDrawable((Color.TRANSPARENT)));
                alertDialogStates.show();

                searchByStates = dialogView.findViewById(R.id.searchByStates);
                state_list = dialogView.findViewById(R.id.state_list);
                close = dialogView.findViewById(R.id.close);
                close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialogStates.dismiss();
                    }
                });

                Cursor cursor = db.fetchState();

                while (cursor.moveToNext()) {
                    States statesModel = new States(cursor.getString(1));
                    states.add(statesModel);
                }

                if(states.size() > 0) {
                    Log.e("TAG", ""+states.size());
                    stateAdapter = new StateAdapter(states, PaymentMethod.this, "PaymentMethod");
                    state_list.setAdapter(stateAdapter);
                    state_list.setLayoutManager(new LinearLayoutManager(PaymentMethod.this));
                    stateAdapter.notifyDataSetChanged();
                }

                searchByStates.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if(states.size() > 0) {
                            stateAdapter.getFilter().filter(s);
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });


            }
        });
    }

    public void setCountryBilling(String name) {
        billingCountryName = name;
        billingCountrySelect.setText(name);
        billingCountrySelect.setError("",null);
        billingCountrySelect.clearFocus();
        alertDialog.dismiss();
        if(name.equals("United States")) {
            billingStateSelect.setVisibility(View.VISIBLE);
            billingState.setVisibility(View.GONE);
        }
        else {
            billingStateSelect.setVisibility(View.GONE);
            billingState.setVisibility(View.VISIBLE);
        }
    }
    public void setStateBilling(String name) {
        if(!name.equals("")) {
            billingStateName = name;
            billingStateSelect.setText(name);

        }
        else {
            billingStateName = "";
        }
        billingStateSelect.setError("",null);
        billingStateSelect.clearFocus();
        alertDialogStates.dismiss();
    }
    private void makeCardNumber(Editable s) {
        String source = s.toString();
        if (!lastSource.equals(source)) {
            source = source.replace(WHITE_SPACE, EMPTY_STRING);
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < source.length(); i++) {
                if (i > 0 && i % 4 == 0) {
                    stringBuilder.append(WHITE_SPACE);
                }
                stringBuilder.append(source.charAt(i));
            }
            lastSource = stringBuilder.toString();
            s.replace(0, s.length(), lastSource);
        }
    }
    public static int getCardType(String getCardNumber) {
        String cardNumber = getCardNumber.replace(" ", "");
        // Define regular expressions for each card type
        String mastercardPattern = "^5[1-5][0-9]{14}$";
        String visaPattern = "^4[0-9]{12}(?:[0-9]{3})?$";
        String amexPattern = "^3[47][0-9]{13}$";
        String discoverPattern = "^6(?:011|5[0-9]{2})[0-9]{12}$";
        String jcbPattern = "^(?:2131|1800|35\\d{3})\\d{11}$";
        String dinersPattern = "^3(?:0[0-5]|[68][0-9])?[0-9]{11}$";

        // Check the card number against each pattern
        if (cardNumber.matches(mastercardPattern)) {
            return 2;
        } else if (cardNumber.matches(visaPattern)) {
            return 1;
        } else if (cardNumber.matches(amexPattern)) {
            return 4;
        } else if (cardNumber.matches(discoverPattern)) {
            return 3;
        } else if (cardNumber.matches(jcbPattern)) {
            return 5;
        } else if (cardNumber.matches(dinersPattern)) {
            return 6;
        } else {
            return 0;
        }
    }
    private void getCompany(String token) {
        progressDialog.show();
        Call<CompanyModifyResponse> companyModifyResponseCall = ApiClient.getInstance().getBookKeepingApi().getCompany(token, String.valueOf(companyId));
        companyModifyResponseCall.enqueue(new Callback<CompanyModifyResponse>() {
            @Override
            public void onResponse(Call<CompanyModifyResponse> call, Response<CompanyModifyResponse> response) {
                if(response.code() == 200) {
                    Log.e("RESPONSE1",""+response.body());
                    progressDialog.dismiss();
                    package_item_name.setText(response.body().getData().get(0).getCompanySubscription().getPackage().getTitle());
                    if(String.valueOf(getIntent().getIntExtra("package_price", 0)).contains(".")) {
                        package_item_price.setText("$" + response.body().getData().get(0).getCompanySubscription().getPackage().getPrice());
                    } else {
                        package_item_price.setText("$" + response.body().getData().get(0).getCompanySubscription().getPackage().getPrice()+ ".00");
                    }
                    package_item_description.setText(response.body().getData().get(0).getCompanySubscription().getPackage().getShortDesc());
                    card_holder_name.setText(response.body().getData().get(0).getCompanySubscription().getCard().getCardHolderName());
                    mainView.setBackgroundColor(Color.parseColor(response.body().getData().get(0).getCompanySubscription().getPackage().getColor_code()));
                    if(response.body().getData().get(0).getCompanySubscription().getPackage().getId() == 4) {
                        package_item_name.setTextColor(getResources().getColor(R.color.white));
                        package_item_price.setTextColor(getResources().getColor(R.color.white));
                        package_item_description.setTextColor(getResources().getColor(R.color.white));
                    } else {
                        package_item_name.setTextColor(getResources().getColor(R.color.black));
                        package_item_price.setTextColor(getResources().getColor(R.color.black));
                        package_item_description.setTextColor(getResources().getColor(R.color.black));
                    }
                    System.out.println("CardName"+"  " +response.body().getData().get(0).getCompanySubscription().getCard().getCardHolderName());
                    if (response.body().getData().get(0).getCompanySubscription().getCard().getCardHolderName() !=null){
                        try {
                            card_holder_name.setText(cryptLib.decryptCipherTextWithRandomIV(response.body().getData().get(0).getCompanySubscription().getCard().getCardHolderName(), ApiClient.getInstance().getKey()));
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }
                    if (response.body().getData().get(0).getCompanySubscription().getCard().getCardNumber() !=null){
                        try {
                            card_number.setText(cryptLib.decryptCipherTextWithRandomIV(response.body().getData().get(0).getCompanySubscription().getCard().getCardNumber(), ApiClient.getInstance().getKey()));
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }
                    if (response.body().getData().get(0).getCompanySubscription().getCard().getExpiry() !=null){
                        try {
                            expiry.setText(cryptLib.decryptCipherTextWithRandomIV(response.body().getData().get(0).getCompanySubscription().getCard().getExpiry(), ApiClient.getInstance().getKey()));
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }
                    if (response.body().getData().get(0).getCompanySubscription().getCard().getCvv() !=null){
                        try {
                            cvv.setText(cryptLib.decryptCipherTextWithRandomIV(response.body().getData().get(0).getCompanySubscription().getCard().getCvv(), ApiClient.getInstance().getKey()));
                            if(!response.body().getData().get(0).getCompanySubscription().getCard().getCvv().equals("")) {
                                cvv.clearFocus();
                                backAnimator.start();
                                // frontAnimator.cancel();
                            }
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }
                    if (response.body().getData().get(0).getCompanySubscription().getCard().getType() == null){

                    }
                    else if(response.body().getData().get(0).getCompanySubscription().getCard().getType() == 1) {
                        cardType.setImageDrawable(ContextCompat.getDrawable(PaymentMethod.this, R.drawable.logo_visa));
                        list_container_bg.setBackground(ContextCompat.getDrawable(PaymentMethod.this, R.drawable.visa_card_bg));
                        back_container_bg.setBackground(ContextCompat.getDrawable(PaymentMethod.this, R.drawable.visa_card_bg));
                    } else if (response.body().getData().get(0).getCompanySubscription().getCard().getType() == 2) {
                        cardType.setImageDrawable(ContextCompat.getDrawable(PaymentMethod.this, R.drawable.logo_mastercard));
                        list_container_bg.setBackground(ContextCompat.getDrawable(PaymentMethod.this, R.drawable.master_card_bg));
                        back_container_bg.setBackground(ContextCompat.getDrawable(PaymentMethod.this, R.drawable.master_card_bg));
                    } else if (response.body().getData().get(0).getCompanySubscription().getCard().getType() == 3) {
                        cardType.setImageDrawable(ContextCompat.getDrawable(PaymentMethod.this, R.drawable.logo_discover));
                        list_container_bg.setBackground(ContextCompat.getDrawable(PaymentMethod.this, R.drawable.discover_card_bg));
                        back_container_bg.setBackground(ContextCompat.getDrawable(PaymentMethod.this, R.drawable.discover_card_bg));
                    } else if (response.body().getData().get(0).getCompanySubscription().getCard().getType() == 4) {
                        cardType.setImageDrawable(ContextCompat.getDrawable(PaymentMethod.this, R.drawable.logo_amex));
                        list_container_bg.setBackground(ContextCompat.getDrawable(PaymentMethod.this, R.drawable.amex_card_bg));
                        back_container_bg.setBackground(ContextCompat.getDrawable(PaymentMethod.this, R.drawable.amex_card_bg));
                    } else if (response.body().getData().get(0).getCompanySubscription().getCard().getType() == 5) {
                        cardType.setImageDrawable(ContextCompat.getDrawable(PaymentMethod.this, R.drawable.logo_jcb));
                        list_container_bg.setBackground(ContextCompat.getDrawable(PaymentMethod.this, R.drawable.jcb_card_bg));
                        back_container_bg.setBackground(ContextCompat.getDrawable(PaymentMethod.this, R.drawable.jcb_card_bg));
                    } else if (response.body().getData().get(0).getCompanySubscription().getCard().getType() == 6) {
                        cardType.setImageDrawable(ContextCompat.getDrawable(PaymentMethod.this, R.drawable.logo_diners));
                        list_container_bg.setBackground(ContextCompat.getDrawable(PaymentMethod.this, R.drawable.diners_club_card_bg));
                        back_container_bg.setBackground(ContextCompat.getDrawable(PaymentMethod.this, R.drawable.diners_club_card_bg));
                    } else {
                        cardType.setImageDrawable(ContextCompat.getDrawable(PaymentMethod.this, R.drawable.logo_generic));
                        list_container_bg.setBackground(ContextCompat.getDrawable(PaymentMethod.this, R.color.purple_color));
                        back_container_bg.setBackground(ContextCompat.getDrawable(PaymentMethod.this, R.color.purple_color));
                        card_issue_type = response.body().getData().get(0).getCompanySubscription().getCard().getType();
                    }

                    //Billing Address Block
                    if(response.body().getData().get(0).getCompanyBillingAddress().getAddress1()!= null) {
                        billingAddressOne.setText(response.body().getData().get(0).getCompanyBillingAddress().getAddress1());
                    }
                    if(response.body().getData().get(0).getCompanyBillingAddress().getAddress2() != null) {
                        billingAddressTwo.setText(response.body().getData().get(0).getCompanyBillingAddress().getAddress2());
                    }
                    if(response.body().getData().get(0).getCompanyBillingAddress().getCountry() != null) {
                        billingCountrySelect.setText(response.body().getData().get(0).getCompanyBillingAddress().getCountry());
                        billingCountryName = response.body().getData().get(0).getCompanyBillingAddress().getCountry();
                        if(response.body().getData().get(0).getCompanyBillingAddress().getCountry().equals("United States")) {
                            billingStateSelect.setVisibility(View.VISIBLE);
                            billingState.setVisibility(View.GONE);
                            if(response.body().getData().get(0).getCompanyBillingAddress().getState() != null) {
                                billingStateSelect.setText(response.body().getData().get(0).getCompanyBillingAddress().getState());
                                billingStateName = response.body().getData().get(0).getCompanyBillingAddress().getState();
                            }
                        } else {
                            billingStateSelect.setVisibility(View.GONE);
                            billingState.setVisibility(View.VISIBLE);
                            if(response.body().getData().get(0).getCompanyBillingAddress().getState() != null) {
                                billingState.setText(response.body().getData().get(0).getCompanyBillingAddress().getState());
                                billingState.setText(response.body().getData().get(0).getCompanyBillingAddress().getState());
                            }
                        }
                    }
                    if(response.body().getData().get(0).getCompanyBillingAddress().getCity() != null) {
                        billingCity.setText(response.body().getData().get(0).getCompanyBillingAddress().getCity());
                    }
                    if(response.body().getData().get(0).getCompanyBillingAddress().getZipcode() != null) {
                        billingZip.setText(response.body().getData().get(0).getCompanyBillingAddress().getZipcode());
                    }
              
                } else if (response.code() == 401) {
                    String token = global.reCallList(shared.getString("refresh_token", ""));
                    if(token.equals("Refresh Token Expires")) {
                        clearShare();
                        startActivity(new Intent(PaymentMethod.this, LoginScreen.class));
                    } else {
                        String vToken = global.validToken(token);
                        if(vToken.equals("Authentication token missing") || vToken.equals("Wrong authentication token")) {
                            clearShare();
                            startActivity(new Intent(PaymentMethod.this, LoginScreen.class));
                        } else {
                            auth = vToken;
                            updateShare(vToken);
                            getCompany("Bearer "+auth);
                        }
                    }
                } else {
                    progressDialog.dismiss();
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        Toast.makeText(PaymentMethod.this, ""+jObjError.getString("message"), Toast.LENGTH_SHORT).show();
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            @Override
            public void onFailure(Call<CompanyModifyResponse> call, Throwable t) {

            }
        });
    }
    private void showPopUp(View v) {
        PopupMenu popupMenu = new PopupMenu(PaymentMethod.this, v);
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
                    Intent company = new Intent(PaymentMethod.this, Companies.class);
                    company.putExtra("enable","0");
                    startActivity(company);
                } else if(id == R.id.profileDtl) {
                    startActivity(new Intent(PaymentMethod.this, Profile.class));
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
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(PaymentMethod.this);
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
                Manager compDb = new Manager(PaymentMethod.this);
                compDb.removeAllCompany();
                SharedPreferences loginRef = getSharedPreferences("book_keeping", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = loginRef.edit();
                editor.putString("access_token","");
                editor.putString("fcm_token","");
                editor.putBoolean("first_input",true);
                editor.putInt("userId", 0);
                editor.commit();
                //ClearAppData.clearApplicationData(PaymentMethod.this, getPackageName());
                startActivity(new Intent(PaymentMethod.this, LoginScreen.class));
            }
        });
        android.app.AlertDialog alert = builder.create();
        alert.show();
    }
    private void log_out() {
        progressDialog.show();
        String mac_address = global.getDeviceToken(PaymentMethod.this);
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
                    startActivity(new Intent(PaymentMethod.this, LoginScreen.class));
                } else {
                    progressDialog.dismiss();
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        Toast.makeText(PaymentMethod.this, ""+jObjError.getString("message"), Toast.LENGTH_SHORT).show();
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
        sheetDialog = new BottomSheetDialog(PaymentMethod.this, R.style.BottomSheetStyle);
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
                    if(global.isNetworkAvailable(PaymentMethod.this)) {
                        try {
                            newEncrypt = cryptLib.encryptPlainTextWithRandomIV(password.getText().toString(), ApiClient.getInstance().getKey());
                            oldEncrypt = cryptLib.encryptPlainTextWithRandomIV(oldPassword.getText().toString(), ApiClient.getInstance().getKey());
                            passwordUpdate(newEncrypt, oldEncrypt);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    } else {
                        String please_Check_Your_Internet_Connection = getResources().getString(R.string.please_Check_Your_Internet_Connection);

                        Toast.makeText(PaymentMethod.this, please_Check_Your_Internet_Connection, Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(PaymentMethod.this, getResources().getString(R.string.password_change_successfully), Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                    sheetDialog.dismiss();
                } else if (response.code() == 401) {
                    String token = global.reCallList(shared.getString("refresh_token", ""));
                    if(token.equals("Refresh Token Expires")) {
                        clearShare();
                        startActivity(new Intent(PaymentMethod.this, LoginScreen.class));
                    } else {
                        String vToken = global.validToken(token);
                        if(vToken.equals("Authentication token missing") || vToken.equals("Wrong authentication token")) {
                            clearShare();
                            startActivity(new Intent(PaymentMethod.this, LoginScreen.class));
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
                        Toast.makeText(PaymentMethod.this, ""+jObjError.getString("message"), Toast.LENGTH_SHORT).show();
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

    private void validateCompany() {
        if (card_holder_name.getText().toString().equals("")) {
            card_holder_name.setError(getResources().getString(R.string.enter_company_card_holder));
            card_holder_name.requestFocus();
        } else if (card_number.getText().toString().equals("")) {
            card_number.setError(getResources().getString(R.string.enter_company_card_number));
            card_number.requestFocus();
        } else if (expiry.getText().toString().equals("")) {
            expiry.setError(getResources().getString(R.string.enter_company_card_expiry));
            expiry.requestFocus();
        } else if (cvv.getText().toString().equals("")) {
            cvv.setError(getResources().getString(R.string.enter_company_card_cvv));
            cvv.requestFocus();
        }  else if (card_number.getText().toString().contains("X")) {
            card_number.setError(getResources().getString(R.string.please_reenter_card_number));
            card_number.requestFocus();
        } else if (cvv.getText().toString().contains("X")) {
            cvv.setError(getResources().getString(R.string.please_reenter_card_number));
            cvv.requestFocus();
        } else if (billingAddressOne.getText().toString().equals("")) {
            billingAddressOne.setError(getResources().getString(R.string.enter_company_address));
            billingAddressOne.requestFocus();
        } else if (billingCountrySelect.getText().toString().equals("")) {
            billingCountrySelect.setError(getResources().getString(R.string.enter_company_country));
            billingCountrySelect.requestFocus();
        } else if (billingCountrySelect.getText().toString().equals("United States")) {
            if(billingStateSelect.getText().toString().equals("")) {
                billingStateSelect.setError(getResources().getString(R.string.enter_company_select_state));
                billingStateSelect.requestFocus();
            } else if (billingCity.getText().toString().equals("")) {
                billingCity.setError(getResources().getString(R.string.enter_company_city));
                billingCity.requestFocus();
            } else if (billingZip.getText().toString().equals("")) {
                billingZip.setError(getResources().getString(R.string.enter_company_zip));
                billingZip.requestFocus();
            }  else {
                //Update API CALL
                try {
                    updateCompany(auth, updateComp());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        } else if (billingState.getText().toString().equals("")) {
            billingState.setError(getResources().getString(R.string.please_enter_state));
            billingState.requestFocus();
        } else if (billingCity.getText().toString().equals("")) {
            billingCity.setError(getResources().getString(R.string.enter_company_city));
            billingCity.requestFocus();
        } else if (billingZip.getText().toString().equals("")) {
            billingZip.setError(getResources().getString(R.string.enter_company_zip));
            billingZip.requestFocus();
        }  else {
            //Update API CALL
            try {
                updateCompany(auth, updateComp());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public CompanyMethodRequest updateComp() throws Exception {
        Card card = new Card(
                cryptLib.encryptPlainTextWithRandomIV(card_number.getText().toString().replace(" ", ""), ApiClient.getInstance().getKey()),
                cryptLib.encryptPlainTextWithRandomIV(expiry.getText().toString(), ApiClient.getInstance().getKey()),
                cryptLib.encryptPlainTextWithRandomIV(cvv.getText().toString(), ApiClient.getInstance().getKey()),
                cryptLib.encryptPlainTextWithRandomIV(card_holder_name.getText().toString(), ApiClient.getInstance().getKey()),
                card_issue_type);
        Billing_Address billing_address = new Billing_Address(
                billingAddressOne.getText().toString(),
                billingAddressTwo.getText().toString(),
                billingCity.getText().toString(),
                billingCountryName,
                billingStateName,
                billingZip.getText().toString());
        CompanyMethodRequest companyMethodRequest = new CompanyMethodRequest(card,billing_address);
        return  companyMethodRequest;
    }
    private void updateCompany(String callToken, CompanyMethodRequest updateCompMethod) {
        progressDialog.show();
        Call<ModifyCompany> modifyCompanyCall = ApiClient.getInstance().getBookKeepingApi().updateCompanyMethod("Bearer "+callToken, companyId, updateCompMethod);
        modifyCompanyCall.enqueue(new Callback<ModifyCompany>() {
            @Override
            public void onResponse(Call<ModifyCompany> call, Response<ModifyCompany> response) {
                Log.e("RESPONSE COMPANY Method",""+response.code());
                if(response.code() == 200) {
                    progressDialog.dismiss();
                    Toast.makeText(PaymentMethod.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    updateCompanyLocal("Bearer "+auth);
                } else if (response.code() == 401) {
                    String token = global.reCallList(shared.getString("refresh_token", ""));
                    if(token.equals("Refresh Token Expires")) {
                        clearShare();
                        startActivity(new Intent(PaymentMethod.this, LoginScreen.class));
                    } else {
                        String vToken = global.validToken(token);
                        if(vToken.equals("Authentication token missing") || vToken.equals("Wrong authentication token")) {
                            clearShare();
                            startActivity(new Intent(PaymentMethod.this, LoginScreen.class));
                        } else {
                            auth = vToken;
                            updateShare(vToken);
                            try {
                                updateCompany(auth, updateComp());
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }
                } else {
                    progressDialog.dismiss();
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        Toast.makeText(PaymentMethod.this, ""+jObjError.getString("message"), Toast.LENGTH_SHORT).show();
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            @Override
            public void onFailure(Call<ModifyCompany> call, Throwable t) {

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
        Manager db = new Manager(PaymentMethod.this);
        db.removeAllCompany();
    }

    public void updateShare(String token) {
        SharedPreferences sharedPreferences = getSharedPreferences("book_keeping", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("access_token", token);
        editor.commit();
    }
    private void updateCompanyLocal(String token) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("America/New_York"));
        Date currentDate = new Date();
        timestamp = sdf.format(currentDate);
        Call<CompanyResponse> call = ApiClient.getInstance().getBookKeepingApi().companies(token, "");
        call.enqueue(new Callback<CompanyResponse>() {
            @Override
            public void onResponse(Call<CompanyResponse> call, Response<CompanyResponse> response) {
                Log.e("RESPONSE COMPANY",""+response.code());
                if(response.code() == 200) {
                    //db.PaymentMethodAll();
                    noOfRec = response.body().getData().size();
                    if(response.body().getData().size() > 0) {
                        for (int j = 0; j < response.body().getData().size(); j++) {
                            int finalI = j;
                            Cursor cursor = db.fetchCompanyImage(response.body().getData().get(finalI).getId());
                            timestamp2 = response.body().getData().get(j).getUpdatedAt();
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
                        Intent intent = getIntent();
                        finish(); // Close the current activity
                        startActivity(intent); // Start the activity again
                    } else {
                        progressDialog.dismiss();
                    }
                } else if (response.code() == 401) {
                    String token = global.reCallList(shared.getString("refresh_token", ""));
                    if(token.equals("Refresh Token Expires")) {
                        clearShare();
                        startActivity(new Intent(PaymentMethod.this, LoginScreen.class));
                    } else {
                        String vToken = global.validToken(token);
                        if(vToken.equals("Authentication token missing") || vToken.equals("Wrong authentication token")) {
                            clearShare();
                            startActivity(new Intent(PaymentMethod.this, LoginScreen.class));
                        } else {
                            auth = vToken;
                            updateShare(vToken);
                            updateCompanyLocal("Bearer " + auth);
                        }
                    }
                } else {
                    progressDialog.dismiss();
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        Toast.makeText(PaymentMethod.this, ""+jObjError.getString("message"), Toast.LENGTH_SHORT).show();
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
    @Override
    public void onBackPressed() {
        redirect();
    }
    public void redirect() {
        if(payment_status == 0) {
            startActivity(new Intent(PaymentMethod.this, Companies.class));
        } else {
            Intent compDashboard = new Intent(PaymentMethod.this, BusinessDashboards.class);
            compDashboard.putExtra("companyId", companyId);
            compDashboard.putExtra("companyPermission", permission);
            startActivity(compDashboard);
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