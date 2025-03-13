package perfect.book.keeping.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
//import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.gson.JsonParser;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;

import perfect.book.keeping.R;
import perfect.book.keeping.activity.company.TakeSnapDate;
import perfect.book.keeping.adapter.ImageAdapter;
import perfect.book.keeping.api.ApiClient;
import perfect.book.keeping.global.CropUtils;
import perfect.book.keeping.global.LocaleHelper;
import perfect.book.keeping.manager.Manager;
import perfect.book.keeping.model.Image;
import perfect.book.keeping.request.Snap;
import perfect.book.keeping.response.SnapResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OpenCamera extends AppCompatActivity {

    ImageView camera, rotate_image;

    LinearLayout apply_crop, cancel_crop;

    RecyclerView showImage;
    EditText cancel;

    ArrayList<Image> list = new ArrayList<>();

    private static final int ACTIVITY_REQUEST_GALLERY_CODE = 2000;


    private MediaPlayer mp = null;

    private String rotate = "Back";

    String auth, fileDate, companyName;

    int companyId, replacePos, redirectPos;


    RelativeLayout crop_layout;

    //CropUtils.CropImageView imageview1;

    CropImageView imageview1;

    ImageView imageViewDemo;

    Bitmap bitmap;

    int click = 1, imagePos = 0, companyPermission = 0;

    String imageUri;
    String mode;
    SharedPreferences shared;
    Locale myLocale;
    String codeLang;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_camera);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        android.graphics.drawable.Drawable background = OpenCamera.this.getDrawable(R.drawable.gradiant_btn_alt);
        getWindow().setBackgroundDrawable(background);

        shared = getSharedPreferences("book_keeping", MODE_PRIVATE);
        if(shared.getString("access_token", "").equals("")) {
            startActivity(new Intent(OpenCamera.this, LoginScreen.class));
        } else {
            auth = shared.getString("access_token", "");
        }
        codeLang = shared.getString("code_lang", "");
        setLocale(codeLang);
        showImage = findViewById(R.id.showImage);
        imageview1 = findViewById(R.id.cropImageView);
        imageViewDemo = findViewById(R.id.imageViewDemo);
        crop_layout = findViewById(R.id.crop_layout);
        apply_crop = findViewById(R.id.apply_crop);
        cancel_crop = findViewById(R.id.cancel_crop);
        rotate_image = findViewById(R.id.rotate_image);
        cancel = findViewById(R.id.cancel);
        fileDate = getIntent().getStringExtra("fileDate");
        companyName = getIntent().getStringExtra("companyName");
        companyId = getIntent().getIntExtra("companyId", 0);
        companyPermission = getIntent().getIntExtra("companyPermission", 0);
        mode = getIntent().getStringExtra("mode");
        replacePos = getIntent().getIntExtra("replacePos", 0);
        redirectPos = getIntent().getIntExtra("redirectPos", 0);

        imageUri = getIntent().getStringExtra("image");
        if(imageUri != null) {
            try {
                convertBitmap(Uri.parse(imageUri));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

    }
    private void capturePhoto(View view) {
       File photoDir = new File(OpenCamera.this.getExternalFilesDir(Environment.DIRECTORY_PICTURES).toURI());
        //File photoDir = new File("/storage/emulated/0/DCIM/Camera/");

        //Check Photo Directory is Exists or Not
        if (!photoDir.exists())
            photoDir.mkdir();
        //Whenever new photo clicked
        Date date = new Date();
        String timeStamp = String.valueOf(date.getTime());
        String photoFilePath = photoDir.getAbsolutePath() + "/" + timeStamp + ".jpg";
        //New File Based on File Path
        File photoFile = new File(photoFilePath);
    }
    private static Bitmap rotateImage(Bitmap img, int degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        Bitmap rotatedImg = Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), matrix, true);
        img.recycle();
        return rotatedImg;
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
    private void convertBitmap(Uri savedUri) throws IOException {

        Bitmap new_Image = MediaStore.Images.Media.getBitmap(this.getContentResolver(), savedUri);
        bitmap = rotateImageIfRequired(OpenCamera.this, new_Image, savedUri);

        crop_layout.setVisibility(View.VISIBLE);

        imageview1.setImageBitmap(bitmap);

        Log.e("FILE URI",""+savedUri.getPath());
        apply_crop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap cropBitmap = imageview1.getCroppedImage();
                imageview1.setImageBitmap(cropBitmap);
                try {
                    saveBitmapToCache(cropBitmap);
                    Matrix matrix = new Matrix();
                    matrix.postRotate(0);
                    bitmap = Bitmap.createBitmap(cropBitmap, 0, 0, cropBitmap.getWidth(), cropBitmap.getHeight(), matrix, true);
                    String mimeType = "image/jpg";
                    String filename = savedUri.toString().substring(savedUri.toString().lastIndexOf("/")+1);
                    String path = savedUri.getPath();
                    getEncoded64ImageStringFromBitmap(bitmap, 0, mimeType, filename, path, "Camera");

                    Log.e("IMAGE BY",""+bitmap.getWidth() + "X"+ bitmap.getHeight());

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        cancel_crop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openCamera = new Intent(OpenCamera.this, TakeSnapDate.class);
                openCamera.putExtra("fileDate", fileDate);
                openCamera.putExtra("companyId", companyId);
                openCamera.putExtra("companyName", companyName);
                openCamera.putExtra("permission",companyPermission);
                openCamera.putExtra("replacePos", 0);
                openCamera.putExtra("redirectPos",0);
                startActivity(openCamera);
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

                imageview1.setImageBitmap(bitmap);
            }
        });

        Log.e("List Length",""+list.size());
    }
    public String resizeBase64Image(String base64image){

        byte [] encodeByte=Base64.decode(base64image,Base64.NO_WRAP); //out of memory exception...

        BitmapFactory.Options options=new BitmapFactory.Options();
        options.inPurgeable = true;
        Bitmap image = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length,options);

        int height = (image.getHeight() / 2);
        int width = (image.getWidth() / 2);
        image = Bitmap.createScaledBitmap(image, width, height, false);

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 80, stream);
        byte[] byteFormat = stream.toByteArray();

        return Base64.encodeToString(byteFormat, Base64.NO_WRAP);

    }
    public void saveBitmapToCache(Bitmap bitmap) throws IOException {
        String filename = "final_image.jpg";
        File cacheFile = new File(getApplicationContext().getCacheDir(), filename);
        OutputStream out = new FileOutputStream(cacheFile);
        bitmap.compress(Bitmap.CompressFormat.JPEG, (int)100, out);
        out.flush();
        out.close();
    }
    private void getEncoded64ImageStringFromBitmap(Bitmap bitmapImage, int bw_status, String mimeType, String filename, String path, String type) {
         // Replace R.drawable.your_image with the actual resource ID of your image
        int width = bitmapImage.getWidth();
        int height = bitmapImage.getHeight();

        Log.d("Image Size", "Width: " + width + " Height: " + height);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        if(type.equals("ReceiptGallery")) {
            bitmapImage.compress(Bitmap.CompressFormat.JPEG, 50, stream);
        } else {
            bitmapImage.compress(Bitmap.CompressFormat.JPEG, 80, stream);
        }
        byte[] byteFormat = stream.toByteArray();
        String base64 = Base64.encodeToString(byteFormat, Base64.NO_WRAP);

        String imgString = resizeBase64Image(base64);
        //cancel.setText(imgString);
        Log.e("IMAGE STRING",""+imgString);
        Log.e("IMAGE MIME",""+mimeType);
        Log.e("IMAGE FILE",""+filename);
        Log.e("IMAGE PATH",""+path);
        Log.e("IMAGE DATE",""+fileDate);
        Log.e("IMAGE COMPANY ID",""+companyId);
        Manager img = new Manager(OpenCamera.this);
        if(replacePos == 0) {
            Cursor cursor = img.getLastId(companyId);
            while (cursor.moveToNext()) {
                Log.e("CURSOR LENGTH",""+cursor.getInt(0));
                imagePos = cursor.getInt(0);
            }
            String response = img.addImages(filename, imgString,  bw_status,  mimeType, path, fileDate, "", companyId, imagePos, 0);
            if(response.equals("Image Added Successfully")) {
                Intent cropIntent = new Intent(OpenCamera.this, CropImage.class);
                cropIntent.putExtra("file_date", fileDate);
                cropIntent.putExtra("company_name", companyName);
                cropIntent.putExtra("company_id", companyId);
                cropIntent.putExtra("redirectPos", redirectPos);
                cropIntent.putExtra("companyPermission", companyPermission);
                startActivity(cropIntent);
            } else {
            }
        } else {
            String response = img.updateImages(replacePos, filename, imgString, imgString, bw_status, mimeType, path, fileDate, "", companyId);
            Log.e("IMAGE STORE", ""+response);
            if(response.equals("Update successfully")) {
                Intent cropIntent = new Intent(OpenCamera.this, CropImage.class);
                cropIntent.putExtra("file_date", fileDate);
                cropIntent.putExtra("company_id", companyId);
                cropIntent.putExtra("company_name", companyName);
                cropIntent.putExtra("redirectPos", redirectPos);
                cropIntent.putExtra("companyPermission", companyPermission);
                startActivity(cropIntent);
            } else {

            }
        }



        //uploadProfImage(imgData);
    }
    @Override
    public void onBackPressed() {
        Intent openCamera = new Intent(OpenCamera.this, TakeSnapDate.class);
        openCamera.putExtra("fileDate", fileDate);
        openCamera.putExtra("companyId", companyId);
        openCamera.putExtra("companyName", companyName);
        openCamera.putExtra("permission",companyPermission);
        openCamera.putExtra("replacePos", 0);
        openCamera.putExtra("redirectPos",0);
        startActivity(openCamera);

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