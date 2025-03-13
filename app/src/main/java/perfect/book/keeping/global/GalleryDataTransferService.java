package perfect.book.keeping.global;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;


import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import perfect.book.keeping.activity.BookKeepers;
import perfect.book.keeping.activity.LoginScreen;
import perfect.book.keeping.activity.company.BusinessDashboards;
import perfect.book.keeping.activity.company.gallery.PendingGallery;
import perfect.book.keeping.api.ApiClient;
import perfect.book.keeping.manager.Manager;
import perfect.book.keeping.response.SnapResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GalleryDataTransferService extends Service {

    String auth;
    SharedPreferences shared;
    Manager galDb;
    String imgString = "", approvalStatus = "1,2";
    private List<Integer> imageIds = new ArrayList<>();
    int companyId = 0;
    DownloadGalleryTask galleryTask;

    Global global;
    @Override
    public void onCreate() {
        super.onCreate();
        // Initialize your SQLite database
        Log.d("TAG", "Service starteds");

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Start listening for network state changes
        NetworkStateReceiver.register(this);
        // Check the network state immediately upon starting the service
        galDb = new Manager(getApplicationContext());
        global = new Global();
        if (intent != null) {
            if(String.valueOf(intent.getIntExtra("companyId",0)) != null) {
                companyId = intent.getIntExtra("companyId",0);
            } else {
                companyId = 0;
            }
            if(intent.getStringExtra("approval_status") != null) {
                approvalStatus = intent.getStringExtra("approval_status");
            } else {
                approvalStatus = "0,1,2,3";
            }
        }
        if (NetworkUtil.isConnectedToNetwork(this)) {
            shared = getSharedPreferences("book_keeping", MODE_PRIVATE);
            auth = shared.getString("access_token", "");
            if(companyId != 0) {
                loadGalleryDownload("Bearer " + auth, "", "", "", "", "", companyId, approvalStatus, "", "", "");
            }

        }

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("TAG", "Service destroyed");
        // Stop listening for network state changes
        NetworkStateReceiver.unregister(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void loadGalleryDownload(String verify, String ids, String orderType, String orderField, String month, String year, int company_id,String approvalStatusNewSpaceRemove,String filterNamesIdSpaceRemove,String title, String mode) {
        Log.e("APPROVAL STATUS",""+approvalStatusNewSpaceRemove);
        Call<JsonObject> gallery = ApiClient.getInstance().getBookKeepingApi().getGallery(verify, ids, orderType, orderField, month, year, company_id,"",filterNamesIdSpaceRemove,title);
        gallery.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                Log.e("ClickID"+"RESPONSE CODE",""+response.code());
                if (response.code() == 200) {
                    imageIds.clear();
                    if (response.body().get("data").isJsonObject() == false) {
                    } else {
                        try {
                            JSONObject dataObject = new JSONObject(String.valueOf(response.body().get("data")));
                            JSONArray dataObj = dataObject.names();
                            for (int i = 0; i < dataObj.length(); i++) { //Year Loop
                                JSONObject yearObject = dataObject.getJSONObject(String.valueOf(dataObj.get(i)));
                                JSONArray monthObj = yearObject.names();
                                for (int j = 0; j < monthObj.length(); j++) { //MONTH Loop
                                    JSONObject monthObject = yearObject.getJSONObject(String.valueOf(monthObj.get(j)));
                                    JSONArray dayObject = monthObject.names();
                                    for (int k = 0; k < dayObject.length(); k++) { //Day Loop
                                        JSONArray ImageObject = monthObject.getJSONArray(String.valueOf(dayObject.get(k)));
                                        for(int l = 0; l < ImageObject.length(); l++) {
                                            imageIds.add(Integer.parseInt(ImageObject.getJSONObject(l).getString("id")));
                                            Log.e("IMAGE IDS",""+imageIds);
                                            Cursor checkImg = galDb.checkGallery(company_id, Integer.parseInt(ImageObject.getJSONObject(l).getString("id")));
                                            Log.e("COUNTER",""+checkImg.getCount());
                                            if(checkImg.getCount() > 0) {
                                                //Update
                                                while (checkImg.moveToNext()){
                                                    //Log.e("IMAGE Thumbnail",""+checkImg.getString(13));
                                                    if(checkImg.getString(13).equals("")) {
                                                      //  Log.e("IMAGE Thumbnail",""+checkImg.getString(13));
                                                        galleryTask = (DownloadGalleryTask) new DownloadGalleryTask(getApplicationContext()).execute(
                                                                ImageObject.getJSONObject(l).getString("thumbnail"),
                                                                ImageObject.getJSONObject(l).getString("file_name"),
                                                                ImageObject.getJSONObject(l).getString("id"));
                                                    }
                                                    if(checkImg.getInt(6) != Integer.parseInt(ImageObject.getJSONObject(l).getString("approval_status"))) {
//                                                        galDb.updateGalleryStatus(Integer.parseInt(ImageObject.getJSONObject(l).getString("id")),
//                                                                Integer.parseInt(ImageObject.getJSONObject(l).getString("approval_status")));
                                                        String reject_reason;
                                                        if(ImageObject.getJSONObject(l).getString("reason") == null) {
                                                            reject_reason = "";
                                                        } else {
                                                            reject_reason = ImageObject.getJSONObject(l).getString("reason");
                                                        }
                                                        galDb.reSubmitGallery(
                                                                        Integer.parseInt(ImageObject.getJSONObject(l).getString("id")),
                                                                        Integer.parseInt(ImageObject.getJSONObject(l).getString("approval_status")),
                                                                        Double.parseDouble(ImageObject.getJSONObject(l).getString("amount")),
                                                                        ImageObject.getJSONObject(l).getString("title"),
                                                                        Integer.parseInt(ImageObject.getJSONObject(l).getString("payment_flag")),
                                                                dayObject.get(k).toString(),
                                                                reject_reason);
                                                    }
                                                }
                                            }
                                            else {
                                                //Insert
                                                String reject_reason;
                                                if(ImageObject.getJSONObject(l).getString("reason") == null) {
                                                    reject_reason = "";
                                                } else {
                                                    reject_reason = ImageObject.getJSONObject(l).getString("reason");
                                                }
                                                Log.e("REJECT REASON", ""+reject_reason);
                                                galDb.saveGallery(
                                                        Integer.parseInt(ImageObject.getJSONObject(l).getString("id")),
                                                        ImageObject.getJSONObject(l).getString("file_name"),
                                                        "",
                                                        "",
                                                        "",
                                                        Integer.parseInt(ImageObject.getJSONObject(l).getString("approval_status")),
                                                        Integer.parseInt(ImageObject.getJSONObject(l).getJSONObject("created_user").getString("id")),
                                                        Double.parseDouble(ImageObject.getJSONObject(l).getString("amount")),
                                                        ImageObject.getJSONObject(l).getString("title"),
                                                        Integer.parseInt(ImageObject.getJSONObject(l).getString("company_id")),
                                                        Integer.parseInt(ImageObject.getJSONObject(l).getString("payment_flag")),
                                                        ImageObject.getJSONObject(l).getJSONObject("created_user").getString("name"),
                                                        "",
                                                        "",
                                                        ImageObject.getJSONObject(l).getString("link"),
                                                        dayObject.get(k).toString(),
                                                        ImageObject.getJSONObject(l).getString("thumbnail"),
                                                        ImageObject.getJSONObject(l).getString("original"), ImageObject.getJSONObject(l).getString("createdAt"),
                                                        reject_reason
                                                );
                                                galleryTask = (DownloadGalleryTask) new DownloadGalleryTask(getApplicationContext()).execute(
                                                        ImageObject.getJSONObject(l).getString("thumbnail"),
                                                        ImageObject.getJSONObject(l).getString("file_name"),
                                                        ImageObject.getJSONObject(l).getString("id")
                                                );
                                                //syncData();
                                                Log.e("IMAGES RESPONSE","Insert"+ImageObject.getJSONObject(l).getString("id"));
                                            }
                                        }
                                    }
                                }
                            }
                            String acceptImageIds = String.valueOf(imageIds);
                            String acceptImageIdsOld = acceptImageIds.replace("[", "(");
                            String acceptImageIdsNew = acceptImageIdsOld.replace("]", ")");
                            String acceptImageIdsNewSpaceRemove = acceptImageIdsNew.replace(" ", "");
                            Log.e("IMAGE IDS",""+acceptImageIdsNewSpaceRemove);
                            String removeResponse = galDb.removeGalleryPhoto(company_id, acceptImageIdsNewSpaceRemove, approvalStatusNewSpaceRemove);
//                            businessDashboards.recallDB();
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
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
                            loadGalleryDownload("Bearer " + auth, "", "", "", "", "", companyId, approvalStatus, "", "", "");
                        }
                    }
                } else {
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        //Toast.makeText(this, ""+jObjError.getString("message"), Toast.LENGTH_SHORT).show();
                        Log.e("Download Error",""+jObjError.getString("message"));
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    // reCallList(shared.getString("refresh_token", ""));
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

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
}
