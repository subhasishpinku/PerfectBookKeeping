package perfect.book.keeping.activity.company;

import static com.bumptech.glide.load.resource.bitmap.TransformationUtils.rotateImage;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
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
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
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

import javax.crypto.NoSuchPaddingException;

import de.hdodenhof.circleimageview.CircleImageView;
import perfect.book.keeping.R;
import perfect.book.keeping.activity.Companies;
import perfect.book.keeping.activity.LoginScreen;
import perfect.book.keeping.activity.company.subUser.SubUserModify;
import perfect.book.keeping.activity.company.subUser.SubUsersList;
import perfect.book.keeping.activity.company.subUser.SubUsersStore;
import perfect.book.keeping.activity.profile.Profile;
import perfect.book.keeping.adapter.CompanyDateFormatAdapter;
import perfect.book.keeping.adapter.CountryAdapter;
import perfect.book.keeping.adapter.CurrencyAdapter;
import perfect.book.keeping.adapter.StateAdapter;
import perfect.book.keeping.api.ApiClient;
import perfect.book.keeping.encrypt.CryptLib;
import perfect.book.keeping.global.ClearAppData;
import perfect.book.keeping.global.Global;
import perfect.book.keeping.global.LocaleHelper;
import perfect.book.keeping.manager.Manager;
import perfect.book.keeping.model.Country;
import perfect.book.keeping.model.Currency;
import perfect.book.keeping.model.DateFormat;
import perfect.book.keeping.model.States;
import perfect.book.keeping.request.AddCard;
import perfect.book.keeping.request.Address;
import perfect.book.keeping.request.Billing_Address;
import perfect.book.keeping.request.Card;
import perfect.book.keeping.request.Company;
import perfect.book.keeping.request.CreateCompanyRequest;
import perfect.book.keeping.request.Image;
import perfect.book.keeping.response.ChangePassword;
import perfect.book.keeping.response.CompanyResponse;
import perfect.book.keeping.response.CurrencyResponse;
import perfect.book.keeping.response.LogoutResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddCompany extends AppCompatActivity {

    TextView package_item_name, package_item_price_alt, package_item_description, changeMode, add_company_address, card_holderName,
            cardNumber, expiry_date, cvv_number, add_company, package_item_price,check_same_text;
    EditText comp_name, registration_no, date_format, addressOne, addressTwo, countrySelect, state, stateSelect, city, zip, searchByStates, searchByCities,
            billingAddressOne, billingAddressTwo, billingCity, billingState, billingZip, billingCountrySelect, billingStateSelect,
            card_holder_name, card_number, expiry, cvv, searchBy, password, cnfPassword, oldPassword, choose_currency, searchByCurrency;
    String imgData, countryName = "", stateName = "", billingCountryName = "", billingStateName = "", currentYear, currentMonth, fcm_token, newEncrypt, oldEncrypt, color_code, existing_file_name;
    BottomSheetDialog sheetDialog, cardSheetDialog, add_update_sheetDialog;
    BottomSheetBehavior<View> bottomSheetBehavior;
    LinearLayout add_address_layout, openCamera, gallery, cancel, back, updatePass;
    RelativeLayout list_container_bg, back_container_bg, mainView;
    RecyclerView dateList, currency_list, country_list, state_list;
    ImageView edit_address, edit_address_billing, ivPlayOverlay,cardType, eye_btn_fp, eye_btn_fp_old, eye_btn_cnf,close, currency_close, date_close;
    CircleImageView company_image;
    private static final int ACTIVITY_REQUEST_CODE = 1000;
    private static final int ACTIVITY_REQUEST_GALLERY_CODE = 2000;
    private static final int PERMISSION_REQUEST_CODE = 1203;
    boolean imageSelect = false;
    Image image;
    List<DateFormat> dateFormat = new ArrayList<>();
    Manager db;
    private AnimatorSet frontAnimator;
    private AnimatorSet backAnimator;
    private List<Country> countries= new ArrayList<>();
    private List<States> states = new ArrayList<>();
    private List<Currency> currencies = new ArrayList<>();
    LinearLayout openMenu;
    CheckBox check_same;
    CountryAdapter adapter;
    StateAdapter stateAdapter;
    CurrencyAdapter currencyAdapter;
    AlertDialog alertDialog, alertDialogStates;
    private static final String EMPTY_STRING = "";
    private static final String WHITE_SPACE = " ";
    private static final String SLASH_SPACE = "/";
    private String lastSource = EMPTY_STRING;
    private boolean isCardFlipped = false;
    CardView design, back_design;
    int card_issue_type = 0, selected_Card = 0, package_id, noOfRec = 0;
    Dialog progressDialog, dialog, currencyDialog;
    SharedPreferences shared;
    String auth, imgString, timestamp2, timestamp;
    CryptLib cryptLib;
    CompanyDateFormatAdapter datedApter;
    Global global;
    private List<String> currencyIds = new ArrayList<String>();
    Uri imageUri;
    AlertDialog cropDialog;
    CropImageView cropImageView;
    LinearLayout cancel_crop, apply_crop;
    ImageView rotate_image;
    Bitmap bitmap;
    int click = 1;
    boolean check_bill_address= false;
    Locale myLocale;
    String codeLang;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_company);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        android.graphics.drawable.Drawable background = AddCompany.this.getDrawable(R.drawable.gradiant_btn_alt);
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
        db = new Manager(AddCompany.this);
        package_item_name = findViewById(R.id.package_item_name);
        package_item_description = findViewById(R.id.package_item_description);
        codeLang = shared.getString("code_lang", "");
        setLocale(codeLang);
        global = new Global();

        progressDialog = new Dialog(AddCompany.this);
        progressDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        progressDialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        progressDialog.setContentView(R.layout.progress);
        if (progressDialog.getWindow() != null) {
            progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        saveCurrencies();

        try {
            cryptLib = new CryptLib();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (NoSuchPaddingException e) {
            throw new RuntimeException(e);
        }
        // Company Info Block
        comp_name = findViewById(R.id.comp_name);
        registration_no = findViewById(R.id.registration_no);
        date_format = findViewById(R.id.date_format);
        choose_currency = findViewById(R.id.choose_currency);
        ivPlayOverlay = findViewById(R.id.ivPlayOverlay);
        company_image = findViewById(R.id.company_image);
        //Company Address Block
        addressOne = findViewById(R.id.addressOne);
        addressTwo = findViewById(R.id.addressTwo);
        countrySelect = findViewById(R.id.countrySelect);
        stateSelect = findViewById(R.id.stateSelect);
        state = findViewById(R.id.state);
        city = findViewById(R.id.city);
        zip = findViewById(R.id.zip);
        //Card Details Block
        card_holder_name = findViewById(R.id.cardHolderName);
        card_number = findViewById(R.id.card_number);
        expiry = findViewById(R.id.expiry);
        cvv = findViewById(R.id.cvv);
        design = findViewById(R.id.design);
        back_design = findViewById(R.id.back_design);
        card_holderName = findViewById(R.id.card_holder_name);
        cardNumber = findViewById(R.id.cardNumber);
        expiry_date = findViewById(R.id.expiry_date);
        cvv_number = findViewById(R.id.cvv_number);
        cardType = findViewById(R.id.card_type);
        list_container_bg = findViewById(R.id.list_container_bg);
        back_container_bg = findViewById(R.id.back_container_bg);
        // Load front card animation
        frontAnimator = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.flip_reverse);
        frontAnimator.setTarget(design);
        // Load back card animation
        backAnimator = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.flip_reverse);
        backAnimator.setTarget(back_design);
        backAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        currentYear = yearFormat.format(new Date());
        currentMonth = monthFormat.format(new Date());
        //Billing Address Block
        billingAddressOne = findViewById(R.id.billingAddressOne);
        billingAddressTwo = findViewById(R.id.billingAddressTwo);
        billingCountrySelect = findViewById(R.id.billingCountrySelect);
        billingStateSelect = findViewById(R.id.billingStateSelect);
        billingState = findViewById(R.id.billingState);
        billingCity = findViewById(R.id.billingCity);
        billingZip = findViewById(R.id.billingZip);
        check_same = findViewById(R.id.check_same);
        add_company = findViewById(R.id.add_company);

        //Package Information
        package_item_name = findViewById(R.id.package_item_name);
        package_item_description = findViewById(R.id.package_item_description);
        package_item_price = findViewById(R.id.package_item_price);
        check_same_text = findViewById(R.id.check_same_text);
        mainView = findViewById(R.id.mainView);

        //Menu
        openMenu = findViewById(R.id.openMenu);
        back = findViewById(R.id.back);
        package_item_name.setText(getIntent().getStringExtra("package_name"));
        package_item_description.setText(getIntent().getStringExtra("package_description"));
        color_code = getIntent().getStringExtra("color_code");
        mainView.setBackgroundColor(Color.parseColor(color_code));
        if(String.valueOf(getIntent().getIntExtra("package_price", 0)).contains(".")) {
            package_item_price.setText("$" + getIntent().getIntExtra("package_price", 0));
        } else {
            package_item_price.setText("$" + getIntent().getIntExtra("package_price", 0) + ".00");
        }
        package_id = getIntent().getIntExtra("packageId", 0);
        if (package_id == 4) {
            package_item_name.setTextColor(getResources().getColor(R.color.white));
            package_item_description.setTextColor(getResources().getColor(R.color.white));
            package_item_price.setTextColor(getResources().getColor(R.color.white));
        } else {
            package_item_name.setTextColor(getResources().getColor(R.color.black));
            package_item_description.setTextColor(getResources().getColor(R.color.black));
            package_item_price.setTextColor(getResources().getColor(R.color.black));
        }
        choose_currency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currencyDialog = new Dialog(AddCompany.this);
                currencyDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                currencyDialog.setContentView(R.layout.dialog_currency_spinner);
                currencyDialog.getWindow().setBackgroundDrawable(new ColorDrawable((Color.TRANSPARENT)));

                searchByCurrency = currencyDialog.findViewById(R.id.searchBy);
                currency_list = currencyDialog.findViewById(R.id.currency_list);
                currency_close = currencyDialog.findViewById(R.id.close);
                currencies();

                searchByCurrency.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if(currencies.size() > 0) {
                            currencyAdapter.getFilter().filter(s);
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });

                currency_close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        currencyDialog.dismiss();
                    }
                });

                currencyAdapter=new CurrencyAdapter(currencies, AddCompany.this, "createCompany");
                currency_list.setAdapter(currencyAdapter);
                currency_list.setLayoutManager(new LinearLayoutManager(AddCompany.this));
                currencyAdapter.notifyDataSetChanged();

                currencyDialog.show();
            }
        });
        date_format.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog = new Dialog(AddCompany.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.dialog_searchable_spinner_date);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable((Color.TRANSPARENT)));

                dateList=dialog.findViewById(R.id.date_list);
                searchBy=dialog.findViewById(R.id.searchBy);
                date_close = dialog.findViewById(R.id.close);

                date_formats();

                searchBy.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if(dateFormat.size() > 0) {
                            datedApter.getFilter().filter(s);
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });

                date_close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                datedApter=new CompanyDateFormatAdapter(dateFormat, AddCompany.this, "createCompany");
                dateList.setAdapter(datedApter);
                dateList.setLayoutManager(new LinearLayoutManager(AddCompany.this));
                datedApter.notifyDataSetChanged();
                dialog.show();
            }
        });
        ivPlayOverlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sheetDialog = new BottomSheetDialog(AddCompany.this, R.style.BottomSheetStyle);
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
        company_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sheetDialog = new BottomSheetDialog(AddCompany.this, R.style.BottomSheetStyle);
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
        countrySelect.setOnClickListener(new View.OnClickListener() {
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
                    adapter = new CountryAdapter(countries, AddCompany.this, "createComp");
                    country_list.setAdapter(adapter);
                    country_list.setLayoutManager(new LinearLayoutManager(AddCompany.this));
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
                    adapter = new CountryAdapter(countries, AddCompany.this, "createCompBilling");
                    country_list.setAdapter(adapter);
                    country_list.setLayoutManager(new LinearLayoutManager(AddCompany.this));
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
                    stateAdapter = new StateAdapter(states, AddCompany.this, "createCompBilling");
                    state_list.setAdapter(stateAdapter);
                    state_list.setLayoutManager(new LinearLayoutManager(AddCompany.this));
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
        stateSelect.setOnClickListener(new View.OnClickListener() {
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
                    stateAdapter = new StateAdapter(states, AddCompany.this, "createComp");
                    state_list.setAdapter(stateAdapter);
                    state_list.setLayoutManager(new LinearLayoutManager(AddCompany.this));
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
                    cardType.setImageDrawable(ContextCompat.getDrawable(AddCompany.this, R.drawable.logo_visa));
                    list_container_bg.setBackground(ContextCompat.getDrawable(AddCompany.this, R.drawable.visa_card_bg));
                    back_container_bg.setBackground(ContextCompat.getDrawable(AddCompany.this, R.drawable.visa_card_bg));
                } else if (card_type == 2) {
                    cardType.setImageDrawable(ContextCompat.getDrawable(AddCompany.this, R.drawable.logo_mastercard));
                    list_container_bg.setBackground(ContextCompat.getDrawable(AddCompany.this, R.drawable.master_card_bg));
                    back_container_bg.setBackground(ContextCompat.getDrawable(AddCompany.this, R.drawable.master_card_bg));
                } else if (card_type == 3) {
                    cardType.setImageDrawable(ContextCompat.getDrawable(AddCompany.this, R.drawable.logo_discover));
                    list_container_bg.setBackground(ContextCompat.getDrawable(AddCompany.this, R.drawable.discover_card_bg));
                    back_container_bg.setBackground(ContextCompat.getDrawable(AddCompany.this, R.drawable.discover_card_bg));
                } else if (card_type == 4) {
                    cardType.setImageDrawable(ContextCompat.getDrawable(AddCompany.this, R.drawable.logo_amex));
                    list_container_bg.setBackground(ContextCompat.getDrawable(AddCompany.this, R.drawable.amex_card_bg));
                    back_container_bg.setBackground(ContextCompat.getDrawable(AddCompany.this, R.drawable.amex_card_bg));
                } else if (card_type == 5) {
                    cardType.setImageDrawable(ContextCompat.getDrawable(AddCompany.this, R.drawable.logo_jcb));
                    list_container_bg.setBackground(ContextCompat.getDrawable(AddCompany.this, R.drawable.jcb_card_bg));
                    back_container_bg.setBackground(ContextCompat.getDrawable(AddCompany.this, R.drawable.jcb_card_bg));
                } else if (card_type == 6) {
                    cardType.setImageDrawable(ContextCompat.getDrawable(AddCompany.this, R.drawable.logo_diners));
                    list_container_bg.setBackground(ContextCompat.getDrawable(AddCompany.this, R.drawable.diners_club_card_bg));
                    back_container_bg.setBackground(ContextCompat.getDrawable(AddCompany.this, R.drawable.diners_club_card_bg));
                } else {
                    cardType.setImageDrawable(ContextCompat.getDrawable(AddCompany.this, R.drawable.logo_generic));
                    list_container_bg.setBackground(ContextCompat.getDrawable(AddCompany.this, R.color.purple_color));
                    back_container_bg.setBackground(ContextCompat.getDrawable(AddCompany.this, R.color.purple_color));
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
                    expiry_date.setText("xxxxxxxxxx");
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
                        String wrong_month_input = getResources().getString(R.string.sub_User_updated_successfully);
                        Toast.makeText(AddCompany.this, wrong_month_input, Toast.LENGTH_SHORT).show();
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
                        String card_already_expired = getResources().getString(R.string.card_already_expired);
                        Toast.makeText(AddCompany.this, card_already_expired, Toast.LENGTH_SHORT).show();
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
                        String card_already_expired = getResources().getString(R.string.card_already_expired);

                        Toast.makeText(AddCompany.this, card_already_expired, Toast.LENGTH_SHORT).show();
                        expiry.setText("");
                    } else {
                        String card_already_expired = getResources().getString(R.string.card_already_expired);

                        Toast.makeText(AddCompany.this, card_already_expired, Toast.LENGTH_SHORT).show();
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
        check_same.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    check_bill_address = true;
                    if(addressOne.getText().toString().equals("") && countrySelect.getText().toString().equals("") && stateSelect.getText().toString().equals("") || state.getText().toString().equals("") && city.getText().toString().equals("") && zip.getText().toString().equals("")) {
                        String please_fill_up_company_address = getResources().getString(R.string.sub_User_updated_successfully);
                        Toast.makeText(AddCompany.this, please_fill_up_company_address, Toast.LENGTH_SHORT).show();
                        check_same.setChecked(false);
                    } else {
                        //
                        copyAddress();
                    }
                } else{
                    check_bill_address = false;
                    clearCopyAddress();
                }
            }
        });
        check_same_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (check_same.isChecked()) {
                    check_same.setChecked(false);
                    check_bill_address = false;
                    clearCopyAddress();
                } else {
                    if(addressOne.getText().toString().equals("") && countrySelect.getText().toString().equals("") && stateSelect.getText().toString().equals("") || state.getText().toString().equals("") && city.getText().toString().equals("") && zip.getText().toString().equals("")) {
                        String please_fill_up_company_address = getResources().getString(R.string.please_fill_up_company_address);
                        Toast.makeText(AddCompany.this, please_fill_up_company_address, Toast.LENGTH_SHORT).show();
                        check_same.setChecked(false);
                    } else {
                        //
                        copyAddress();
                    }
                    check_same.setChecked(true);
                }

            }
        });

        add_company.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateCompany();
            }
        });
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
    }
    private void date_formats() {
        dateFormat.clear();
        Cursor cursor = db.fetchDateFormat();
        while (cursor.moveToNext()) {
            dateFormat.add(new DateFormat(cursor.getString(1)));
        }
    }
    private void currencies() {
        currencies.clear();
        Cursor currencyCursor = db.fetchCurrency();
        while (currencyCursor.moveToNext()) {
            currencies.add(new Currency(currencyCursor.getString(1), currencyCursor.getString(2)));
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
                } else if (response.code()==401) {
                    String token = global.reCallList(shared.getString("refresh_token", ""));
                    if(token.equals("Refresh Token Expires")) {
                        clearShare();
                        startActivity(new Intent(AddCompany.this, LoginScreen.class));
                    } else {
                        String vToken = global.validToken(token);
                        if(vToken.equals("Authentication token missing") || vToken.equals("Wrong authentication token")) {
                            clearShare();
                            startActivity(new Intent(AddCompany.this, LoginScreen.class));
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
                        Toast.makeText(AddCompany.this, ""+jObjError.getString("message"), Toast.LENGTH_SHORT).show();
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
    private void openCameraReq() {
        if(ContextCompat.checkSelfPermission(AddCompany.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(AddCompany.this, new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST_CODE);
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
    private void openGalleryReq() {
        if(ContextCompat.checkSelfPermission(AddCompany.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(AddCompany.this, new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST_CODE);
            return;
        } else {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, ACTIVITY_REQUEST_GALLERY_CODE);}
    }
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            openCrop(imageUri.toString());
            sheetDialog.dismiss();
        } else if(requestCode == ACTIVITY_REQUEST_GALLERY_CODE && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            openCrop(uri.toString());
            sheetDialog.dismiss();
        } else {
            sheetDialog.dismiss();
        }
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
        bitmap = rotateImageIfRequired(AddCompany.this, new_Image, savedUri);

        //  crop_layout.setVisibility(View.VISIBLE);

        cropImageView.setImageBitmap(bitmap);

        Log.e("FILE URI",""+savedUri.getPath());
        apply_crop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap cropBitmap = cropImageView.getCroppedImage();
                cropImageView.setImageBitmap(cropBitmap);
                company_image.setImageBitmap(cropBitmap);
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
        image = new Image(file_name, imgString, "image/"+extension);
        imageSelect = true;
    }
    public void selectedFormat(String dateFormat) {
        date_format.setText(dateFormat);
        date_format.setError("",null);
        dialog.dismiss();
    }
    public void selectedCurrency(String dateFormat) {
        choose_currency.setText(dateFormat);
        choose_currency.setError("",null);
        currencyDialog.dismiss();
    }
    public void setCountry(String name) {
        countryName = name;
        countrySelect.setText(name);
        countrySelect.setError("",null);
        countrySelect.clearFocus();
        alertDialog.dismiss();
        if(name.equals("United States")) {
            stateSelect.setVisibility(View.VISIBLE);
            state.setVisibility(View.GONE);
        }
        else {
            stateSelect.setVisibility(View.GONE);
            state.setVisibility(View.VISIBLE);
        }
    }
    public void setState(String name) {

        if(!name.equals("")) {
            stateName = name;
            stateSelect.setText(name);
        }
        else {
            stateName = "";
        }
        stateSelect.setError("",null);
        stateSelect.clearFocus();
        alertDialogStates.dismiss();
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
    private void copyAddress() {
        billingAddressOne.setText(addressOne.getText().toString());
        billingAddressTwo.setText(addressTwo.getText().toString());
        billingCity.setText(city.getText().toString());
        billingCountrySelect.setText(countryName);
        billingCountryName = countryName;
        if(countryName.equals("United States")) {
            billingStateSelect.setVisibility(View.VISIBLE);
            billingState.setVisibility(View.GONE);
            billingStateSelect.setText(stateName);
            billingStateName = stateName;
        } else {
            billingStateSelect.setVisibility(View.GONE);
            billingState.setVisibility(View.VISIBLE);
            billingState.setText(state.getText().toString());
            billingStateName = state.getText().toString();
        }
        billingZip.setText(zip.getText().toString());
    }
    private void clearCopyAddress() {
        billingAddressOne.setText("");
        billingAddressTwo.setText("");
        billingCity.setText("");
        billingCountrySelect.setText("");
        billingStateSelect.setVisibility(View.GONE);
        billingState.setVisibility(View.VISIBLE);
        billingState.setText("");
        billingZip.setText("");
    }
    private void validateCompany() {
        if(comp_name.getText().toString().equals("")){
            comp_name.setError(getResources().getString(R.string.enter_company_name));
            comp_name.requestFocus();
        } else if (registration_no.getText().toString().equals("")) {
            registration_no.setError(getResources().getString(R.string.enter_company_gov));
            registration_no.requestFocus();
        } else if (date_format.getText().toString().equals("")) {
            date_format.setError(getResources().getString(R.string.enter_company_date_format));
        } else if (choose_currency.getText().toString().equals("")) {
            choose_currency.setError(getResources().getString(R.string.enter_company_currency));
        } else if (addressOne.getText().toString().equals("")) {
            addressOne.setError(getResources().getString(R.string.enter_company_address));
            addressOne.requestFocus();
        } else if (countrySelect.getText().toString().equals("")) {
            countrySelect.setError(getResources().getString(R.string.enter_company_country));
            countrySelect.requestFocus();
        } else if (countrySelect.getText().toString().equals("United States")) {
            if(stateSelect.getText().toString().equals("")) {
                stateSelect.setError(getResources().getString(R.string.enter_company_select_state));
                stateSelect.requestFocus();
            } else if (city.getText().toString().equals("")) {
                city.setError(getResources().getString(R.string.enter_company_city));
                city.requestFocus();
            } else if (zip.getText().toString().equals("")) {
                zip.setError(getResources().getString(R.string.enter_company_zip));
                zip.requestFocus();
            } else if (card_holder_name.getText().toString().equals("")) {
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
            } else if (billingAddressOne.getText().toString().equals("")) {
                billingAddressOne.setError(getResources().getString(R.string.enter_company_billing_address));
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
                } else {
                    try {
                        createCompany(auth, createComp());
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            } else if (billingState.getText().toString().equals("")) {
                billingState.setError(getResources().getString(R.string.enter_company_select_state));
                billingState.requestFocus();
            } else if (billingCity.getText().toString().equals("")) {
                billingCity.setError(getResources().getString(R.string.enter_company_city));
                billingCity.requestFocus();
            } else if (billingZip.getText().toString().equals("")) {
                billingZip.setError(getResources().getString(R.string.enter_company_zip));
                billingZip.requestFocus();
            }  else {
                try {
                    createCompany(auth, createComp());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        } else if (state.getText().toString().equals("")) {
            state.setError(getResources().getString(R.string.enter_company_select_state));
            state.requestFocus();
        } else if (city.getText().toString().equals("")) {
            city.setError(getResources().getString(R.string.enter_company_city));
            city.requestFocus();
        } else if (zip.getText().toString().equals("")) {
            zip.setError(getResources().getString(R.string.enter_company_zip));
            zip.requestFocus();
        } else if (card_holder_name.getText().toString().equals("")) {
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
        } else if (billingAddressOne.getText().toString().equals("")) {
            billingAddressOne.setError(getResources().getString(R.string.enter_company_billing_address));
            billingAddressOne.requestFocus();
        }  else if (billingCountrySelect.getText().toString().equals("")) {
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
            } else {
                try {
                    createCompany(auth, createComp());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        } else if (billingState.getText().toString().equals("")) {
            billingState.setError(getResources().getString(R.string.enter_company_select_state));
            billingState.requestFocus();
        } else if (billingCity.getText().toString().equals("")) {
            billingCity.setError(getResources().getString(R.string.enter_company_city));
            billingCity.requestFocus();
        } else if (billingZip.getText().toString().equals("")) {
            billingZip.setError(getResources().getString(R.string.enter_company_zip));
            billingZip.requestFocus();
        } else {
            try {
                createCompany(auth, createComp());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
    public CreateCompanyRequest createComp() throws Exception {
        String states = "";
        Company company = new Company(comp_name.getText().toString(), registration_no.getText().toString(),package_id, date_format.getText().toString(), choose_currency.getText().toString());
        Card card = new Card(
                cryptLib.encryptPlainTextWithRandomIV(card_number.getText().toString().replace(" ", ""), ApiClient.getInstance().getKey()),
                cryptLib.encryptPlainTextWithRandomIV(expiry.getText().toString(), ApiClient.getInstance().getKey()),
                cryptLib.encryptPlainTextWithRandomIV(cvv.getText().toString(), ApiClient.getInstance().getKey()),
                cryptLib.encryptPlainTextWithRandomIV(card_holder_name.getText().toString(), ApiClient.getInstance().getKey()),
                card_issue_type);
        if(stateName.equals("")) {
            states = state.getText().toString();
        } else {
            states = stateName;
        }
        Billing_Address billing_address = new Billing_Address(
                billingAddressOne.getText().toString(),
                billingAddressTwo.getText().toString(),
                billingCity.getText().toString(),
                billingCountryName,
                billingStateName,
                billingZip.getText().toString());
        Address address = new Address(
                addressOne.getText().toString(),
                addressTwo.getText().toString(),
                countryName,
                states,
                city.getText().toString(),
                zip.getText().toString());
        CreateCompanyRequest createCompanyRequest = new CreateCompanyRequest(company, address,card, billing_address, image);
        return createCompanyRequest;
    }
    private void createCompany(String callAuth, CreateCompanyRequest createComp) {
        progressDialog.show();
        Log.e("BILLING ADD ONE",""+new Gson().toJson(createComp));
        Call<perfect.book.keeping.response.AddCompany> addCompanyCall = ApiClient.getInstance().getBookKeepingApi().addCompany("Bearer "+callAuth, createComp);
        addCompanyCall.enqueue(new Callback<perfect.book.keeping.response.AddCompany>() {
            @Override
            public void onResponse(Call<perfect.book.keeping.response.AddCompany> call, Response<perfect.book.keeping.response.AddCompany> response) {
                Log.e("RESPONSE COMPANY ADD", ""+response.code());
                if(response.code() == 200) {
                    progressDialog.dismiss();
                    Toast.makeText(AddCompany.this, ""+response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    loadDataInLocal("Bearer "+auth);
                } else if (response.code()==401) {
                    String token = global.reCallList(shared.getString("refresh_token", ""));
                    if(token.equals("Refresh Token Expires")) {
                        clearShare();
                        startActivity(new Intent(AddCompany.this, LoginScreen.class));
                    } else {
                        String vToken = global.validToken(token);
                        if(vToken.equals("Authentication token missing") || vToken.equals("Wrong authentication token")) {
                            clearShare();
                            startActivity(new Intent(AddCompany.this, LoginScreen.class));
                        } else {
                            auth = vToken;
                            updateShare(vToken);
                            try {
                                createCompany(auth, createComp());
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }
                } else {
                    progressDialog.dismiss();
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        Toast.makeText(AddCompany.this, ""+jObjError.getString("message"), Toast.LENGTH_SHORT).show();
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            @Override
            public void onFailure(Call<perfect.book.keeping.response.AddCompany> call, Throwable t) {

            }
        });
    }
    private void loadDataInLocal(String token) {
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
                    noOfRec = response.body().getData().size();
                    if(response.body().getData().size() > 0) {
                        for (int j = 0; j < response.body().getData().size(); j++) {
                            int finalI = j;
                            Cursor cursor = db.fetchCompanyImage(response.body().getData().get(finalI).getId());
                            timestamp2 = response.body().getData().get(j).getUpdatedAt();
                            if (cursor.getCount() > 0) {
                                db.updateCompany(response.body().getData().get(finalI).getId());
                                try {
                                    Date date1 = sdf.parse(timestamp);
                                    Date date2 = sdf.parse(timestamp2);
                                    int comparisonResult = date2.compareTo(date1);
                                    while (cursor.moveToNext()) {
                                        existing_file_name = cursor.getString(10);
                                    }
                                    if (comparisonResult >= 0) {
                                        if(response.body().getData().get(finalI).getCompanyImage() != null) {
                                            downloadImage(response.body().getData().get(finalI).getCompanyImage().getThumbnail(), finalI, response.body().getData().get(j).getName(), response.body().getData().get(j).getUpdatedAt(), existing_file_name);
                                        }
                                        //if (userType == 0) {
                                        db.updateImageForCompany(response.body().getData().get(j).getId(), imgString, response.body().getData().get(j).getUpdatedAt()+".png");
                                    }
                                } catch (ParseException e) {
                                    throw new RuntimeException(e);
                                }
                                startActivity(new Intent(AddCompany.this, Companies.class));
                                progressDialog.dismiss();
                            }
                            else {
                                db.removeCompany();
                                if(response.body().getData().get(finalI).getCompanyImage() != null) {
                                    downloadImage(response.body().getData().get(finalI).getCompanyImage().getThumbnail(), finalI, response.body().getData().get(j).getName(), response.body().getData().get(j).getUpdatedAt(), "");
                                }

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
                                db.addCompanyList(response.body().getData().get(j).getName(),
                                        response.body().getData().get(j).getId(),
                                        imgString,
                                        response.body().getData().get(j).getRole().getId(), 1,
                                        response.body().getData().get(j).getUpdatedAt(),
                                        response.body().getData().get(j).getCompanySubscription().getPaymentStatus(),
                                        package_id,
                                        price,
                                        sub_user_price,
                                        response.body().getData().get(j).getUpdatedAt()+".png",
                                        response.body().getData().get(j).getCurrency(),
                                        response.body().getData().get(j).getCompanyPermission().getAmount(),
                                        response.body().getData().get(j).getGovtId(), response.body().getData().get(j).getIs_govt_id_show(),
                                        response.body().getData().get(j).getDateFormat());
                                startActivity(new Intent(AddCompany.this, Companies.class));
                            }
                        }
                    } else {
                        progressDialog.dismiss();
                    }

                } else if (response.code()==401) {
                    String token = global.reCallList(shared.getString("refresh_token", ""));
                    if(token.equals("Refresh Token Expires")) {
                        clearShare();
                        startActivity(new Intent(AddCompany.this, LoginScreen.class));
                    } else {
                        String vToken = global.validToken(token);
                        if(vToken.equals("Authentication token missing") || vToken.equals("Wrong authentication token")) {
                            clearShare();
                            startActivity(new Intent(AddCompany.this, LoginScreen.class));
                        } else {
                            auth = vToken;
                            updateShare(vToken);
                            loadDataInLocal("Bearer "+auth);
                        }
                    }
                } else {
                    progressDialog.dismiss();
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        Toast.makeText(AddCompany.this, ""+jObjError.getString("message"), Toast.LENGTH_SHORT).show();
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
    public String downloadImage(String imageUrl, int position, String company_name, String timestamp2, String existing_file_name) {
        try {
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();

            InputStream inputStream = connection.getInputStream();
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

            String fileName = timestamp2+".png";

            File directory = new File(AddCompany.this.getExternalFilesDir(Environment.DIRECTORY_PICTURES).toString(), "Company");
            if (!directory.exists()) {
                if (!directory.mkdirs()) {
                    return null;
                }
            }

            File imageFile = new File(directory, fileName);

            // Delete any previous file with the same name
//            File removeFile = new File(directory, existing_file_name);
//
//            // Delete any previous file with the same name
//            if (removeFile.exists()) {
//                removeFile.delete();
//            }

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
    private void showPopUp(View v) {
        PopupMenu popupMenu = new PopupMenu(AddCompany.this, v);
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
                    Intent company = new Intent(AddCompany.this, Companies.class);
                    company.putExtra("enable","0");
                    startActivity(company);
                } else if(id == R.id.profileDtl) {
                    startActivity(new Intent(AddCompany.this, Profile.class));
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
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(AddCompany.this);
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
                Manager compDb = new Manager(AddCompany.this);
                compDb.removeAllCompany();
                SharedPreferences loginRef = getSharedPreferences("book_keeping", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = loginRef.edit();
                editor.putString("access_token","");
                editor.putString("fcm_token","");
                editor.putBoolean("first_input",true);
                editor.putInt("userId", 0);
                editor.commit();
                //ClearAppData.clearApplicationData(AddCompany.this, getPackageName());
                startActivity(new Intent(AddCompany.this, LoginScreen.class));
            }
        });
        android.app.AlertDialog alert = builder.create();
        alert.show();
    }
    private void log_out() {
        progressDialog.show();
        String mac_address = global.getDeviceToken(AddCompany.this);
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
                    startActivity(new Intent(AddCompany.this, LoginScreen.class));
                } else {
                    progressDialog.dismiss();
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        Toast.makeText(AddCompany.this, ""+jObjError.getString("message"), Toast.LENGTH_SHORT).show();
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
        sheetDialog = new BottomSheetDialog(AddCompany.this, R.style.BottomSheetStyle);
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
                    if(global.isNetworkAvailable(AddCompany.this)) {
                        try {
                            newEncrypt = cryptLib.encryptPlainTextWithRandomIV(password.getText().toString(), ApiClient.getInstance().getKey());
                            oldEncrypt = cryptLib.encryptPlainTextWithRandomIV(oldPassword.getText().toString(), ApiClient.getInstance().getKey());
                            passwordUpdate(newEncrypt, oldEncrypt);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    } else {
                        String please_Check_Your_Internet_Connection = getResources().getString(R.string.please_Check_Your_Internet_Connection);

                        Toast.makeText(AddCompany.this, please_Check_Your_Internet_Connection, Toast.LENGTH_SHORT).show();
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
                    String password_change_successfully = getResources().getString(R.string.password_change_successfully);

                    Toast.makeText(AddCompany.this, password_change_successfully, Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                    sheetDialog.dismiss();
                } else if (response.code()==401) {
                    String token = global.reCallList(shared.getString("refresh_token", ""));
                    if(token.equals("Refresh Token Expires")) {
                        clearShare();
                        startActivity(new Intent(AddCompany.this, LoginScreen.class));
                    } else {
                        String vToken = global.validToken(token);
                        if(vToken.equals("Authentication token missing") || vToken.equals("Wrong authentication token")) {
                            clearShare();
                            startActivity(new Intent(AddCompany.this, LoginScreen.class));
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
                        Toast.makeText(AddCompany.this, ""+jObjError.getString("message"), Toast.LENGTH_SHORT).show();
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
        startActivity(new Intent(AddCompany.this, Companies.class));
    }

    public void clearShare() {
        SharedPreferences loginRef = getSharedPreferences("book_keeping", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = loginRef.edit();
        editor.putString("access_token","");
        editor.putString("fcm_token","");
        editor.putBoolean("first_input",true);
        editor.putInt("userId", 0);
        editor.commit();
        Manager db = new Manager(AddCompany.this);
        db.removeAllCompany();
    }
    public void updateShare(String token) {
        SharedPreferences sharedPreferences = getSharedPreferences("book_keeping", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("access_token", token);
        editor.commit();
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