package perfect.book.keeping.global;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.JsonParser;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import perfect.book.keeping.activity.LoginScreen;
import perfect.book.keeping.activity.company.PaymentMethod;
import perfect.book.keeping.api.ApiClient;
import perfect.book.keeping.manager.Manager;
import perfect.book.keeping.model.SnapImages;
import perfect.book.keeping.response.CompanyResponse;
import perfect.book.keeping.response.CountryStateResponse;
import perfect.book.keeping.response.SnapResponse;
import perfect.book.keeping.response.StateResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DataTransferService extends Service {
    private static final String TAG = "DataTransferService";
    List<SnapImages> snapImages = new ArrayList<>();
    String auth;
    SharedPreferences shared;
    Manager compDb;
    private List<Integer> compIds = new ArrayList<Integer>();
    String imgString, existing_file_name;
    String timestamp2, timestamp;
    int package_id,  price, sub_user_price;
    DownloadTask downloadImage;
    URL url;
    String compId;
    Global global;
    @Override
    public void onCreate() {
        super.onCreate();
        // Initialize your SQLite database
        Log.d(TAG, "Service starteds");
        }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        // Start listening for network state changes
        NetworkStateReceiver.register(this);
        // Check the network state immediately upon starting the service
        compDb = new Manager(getApplicationContext());
        if (NetworkUtil.isConnectedToNetwork(this)) {
            shared = getSharedPreferences("book_keeping", MODE_PRIVATE);
            auth = shared.getString("access_token", "");
            global = new Global();
            compIds.clear();
            loadDataInLocal("Bearer "+auth);
            storeState();
            storeCountry();
        }

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "Service destroyed");
        // Stop listening for network state changes
        NetworkStateReceiver.unregister(this);
    }

    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }

    public void loadDataInLocal(String token) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("America/New_York"));
        //progressDialog.show();
        Call<CompanyResponse> call = ApiClient.getInstance().getBookKeepingApi().companies(token, "");
        call.enqueue(new Callback<CompanyResponse>() {
            @Override
            public void onResponse(Call<CompanyResponse> call, Response<CompanyResponse> response) {
                if(response.code() == 200) {
                    compIds.clear();
                    if(response.body().getData().size() > 0) {
                        for (int j = 0; j < response.body().getData().size(); j++) {
                            compIds.add(response.body().getData().get(j).getId());
                            Cursor cursor = compDb.fetchCompanyImage(response.body().getData().get(j).getId());
                            timestamp = response.body().getData().get(j).getUpdatedAt();
                            while (cursor.moveToNext()) {
                                timestamp2 = cursor.getString(4);
                            }
                            if (cursor.getCount() > 0) {
                                compDb.updateCompany(response.body().getData().get(j).getId());
                                try {
                                    Date date1 = sdf.parse(timestamp);
                                    Date date2 = sdf.parse(timestamp2);
                                    while (cursor.moveToNext()) {
                                        existing_file_name = cursor.getString(10);
                                    }
                                    Log.e("FILE EXIST BY",""+existing_file_name);
                                    int comparisonResult = date1.compareTo(date2);
                                    checkValue(response.body().getData().get(j).getCompanySubscription().getPackage().getSubsTypeData().getName(),
                                            response.body().getData().get(j).getCompanySubscription().getPackage().getPrice(),
                                            response.body().getData().get(j).getCompanySubscription().getPackage().getSubUserPrice(),
                                            response.body().getData().get(j).getCompanySubscription().getPackage().getSubsTypeData().getId()
                                    );
                                    compDb.updateCompanyData(
                                            response.body().getData().get(j).getName(),
                                            response.body().getData().get(j).getId(),
                                            response.body().getData().get(j).getRole().getId(),
                                            1,
                                            response.body().getData().get(j).getCompanySubscription().getPaymentStatus(),
                                            response.body().getData().get(j).getUpdatedAt(),
                                            package_id,
                                            price,
                                            sub_user_price,
                                            response.body().getData().get(j).getCurrency(),
                                            response.body().getData().get(j).getCompanyPermission().getAmount(),
                                            response.body().getData().get(j).getGovtId(), response.body().getData().get(j).getIs_govt_id_show(),
                                            response.body().getData().get(j).getDateFormat()
                                    );
                                    if (comparisonResult > 0) {
                                        imgString = "";
                                        if(response.body().getData().get(j).getCompanyImage() != null) {
                                            downloadImage = (DownloadTask) new DownloadTask(getApplicationContext())
                                                    .execute(
                                                            response.body().getData().get(j).getCompanyImage().getOriginal(),
                                                            response.body().getData().get(j).getUpdatedAt(),
                                                            String.valueOf(response.body().getData().get(j).getId())
                                                    );
                                        }
                                    }
                                } catch (ParseException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                            else {
                                imgString = "";
                                checkValue(response.body().getData().get(j).getCompanySubscription().getPackage().getSubsTypeData().getName(),
                                        response.body().getData().get(j).getCompanySubscription().getPackage().getPrice(),
                                        response.body().getData().get(j).getCompanySubscription().getPackage().getSubUserPrice(),
                                        response.body().getData().get(j).getCompanySubscription().getPackage().getSubsTypeData().getId()
                                );
                                compDb.addCompanyList(response.body().getData().get(j).getName(),
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
                                if(response.body().getData().get(j).getCompanyImage() != null) {
                                    downloadImage = (DownloadTask) new DownloadTask(getApplicationContext())
                                            .execute(
                                                    response.body().getData().get(j).getCompanyImage().getOriginal(),
                                                    response.body().getData().get(j).getUpdatedAt(),
                                                    String.valueOf(response.body().getData().get(j).getId())
                                            );
                                }
                            }

                        }
                        String approvalStatus = String.valueOf(compIds);
                        String approvalStatusOld = approvalStatus.replace("[", "(");
                        String approvalStatusNew = approvalStatusOld.replace("]", ")");
                        String approvalStatusNewSpaceRemove = approvalStatusNew.replace(" ", "");
                        Log.e("COMPANY IDS",""+approvalStatusNewSpaceRemove);
                        String removeResponse = compDb.removeCompanyData(approvalStatusNewSpaceRemove);
                        Log.e("DELETE RESPONSE",""+removeResponse);
                    }
                } else if (response.code() == 401) {
                    String token = global.reCallList(shared.getString("refresh_token", ""));
                    if(token.equals("Refresh Token Expires")) {
                        clearShare();
                        Intent redirect = new Intent(getApplicationContext(), LoginScreen.class);
                        redirect.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(redirect);
                    } else {
                        String vToken = global.validToken(token);
                        if(vToken.equals("Authentication token missing") || vToken.equals("Wrong authentication token")) {
                            clearShare();
                            Intent redirect = new Intent(getApplicationContext(), LoginScreen.class);
                            redirect.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(redirect);
                        } else {
                            auth = vToken;
                            updateShare(vToken);
                            loadDataInLocal("Bearer "+auth);
                        }
                    }
                } else {
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        Toast.makeText(getApplicationContext(), ""+jObjError.getString("message"), Toast.LENGTH_SHORT).show();
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

    public void clearShare() {
        SharedPreferences loginRef = getSharedPreferences("book_keeping", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = loginRef.edit();
        editor.putString("access_token","");
        editor.putString("fcm_token","");
        editor.putBoolean("first_input",true);
        editor.putInt("userId", 0);
        editor.commit();
        Manager db = new Manager(getApplicationContext());
        db.removeAllCompany();
    }

    public void updateShare(String token) {
        SharedPreferences sharedPreferences = getSharedPreferences("book_keeping", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("access_token", token);
        editor.commit();
    }

    private void storeState() {
        compDb.removeState();
        Call<StateResponse> countryState = ApiClient.getInstance().getBookKeepingApi().state( "states");
        countryState.enqueue(new Callback<StateResponse>() {
            @Override
            public void onResponse(Call<StateResponse> call, Response<StateResponse> response) {
                if(response.code() == 200) {
                    for(int i=0; i< response.body().getData().getStates().size(); i++ ) {
                        compDb.saveState(response.body().getData().getStates().get(i).getStateName());
                    }
                } else if (response.code() == 401) {
                    String token = global.reCallList(shared.getString("refresh_token", ""));
                    if(token.equals("Refresh Token Expires")) {
                        clearShare();
                        Intent redirect = new Intent(getApplicationContext(), LoginScreen.class);
                        redirect.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(redirect);
                    } else {
                        String vToken = global.validToken(token);
                        if(vToken.equals("Authentication token missing") || vToken.equals("Wrong authentication token")) {
                            clearShare();
                            Intent redirect = new Intent(getApplicationContext(), LoginScreen.class);
                            redirect.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(redirect);
                        } else {
                            auth = vToken;
                            updateShare(vToken);
                            loadDataInLocal("Bearer "+auth);
                        }
                    }
                } else {
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        Toast.makeText(getApplicationContext(), ""+jObjError.getString("message"), Toast.LENGTH_SHORT).show();
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            @Override
            public void onFailure(Call<StateResponse> call, Throwable t) {

            }
        });
    }

    private void storeCountry() {
        compDb.removeCountry();
        Call<CountryStateResponse> countryState = ApiClient.getInstance().getBookKeepingApi().countryState("countries");
        countryState.enqueue(new Callback<CountryStateResponse>() {
            @Override
            public void onResponse(Call<CountryStateResponse> call, Response<CountryStateResponse> response) {
                if(response.code() == 200) {
                    for(int i=0; i< response.body().getData().getCountries().size(); i++ ) {

                        compDb.saveCountry(response.body().getData().getCountries().get(i).getNicename());
                    }
                } else if (response.code() == 401) {
                    String token = global.reCallList(shared.getString("refresh_token", ""));
                    if(token.equals("Refresh Token Expires")) {
                        clearShare();
                        Intent redirect = new Intent(getApplicationContext(), LoginScreen.class);
                        redirect.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(redirect);
                    } else {
                        String vToken = global.validToken(token);
                        if(vToken.equals("Authentication token missing") || vToken.equals("Wrong authentication token")) {
                            clearShare();
                            Intent redirect = new Intent(getApplicationContext(), LoginScreen.class);
                            redirect.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(redirect);
                        } else {
                            auth = vToken;
                            updateShare(vToken);
                            loadDataInLocal("Bearer "+auth);
                        }
                    }
                } else {
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        Toast.makeText(getApplicationContext(), ""+jObjError.getString("message"), Toast.LENGTH_SHORT).show();
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            @Override
            public void onFailure(Call<CountryStateResponse> call, Throwable t) {
                Log.e("TAG",""+t.getMessage());
            }
        });
    }

    private void checkValue(String package_names, Integer unit_price, Integer subUserPrice, int package_ids) {
        if(package_names != null) {
            package_id = package_ids;
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


}

















