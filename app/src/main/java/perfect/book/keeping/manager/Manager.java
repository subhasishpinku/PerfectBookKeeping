package perfect.book.keeping.manager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;


public class Manager extends SQLiteOpenHelper {

    private static String DB_NAME="book_keeping.db";

    public Manager(@Nullable Context context) {
        //Here First Context as context, Database Name as DB_NAME, Factory as null and version as 1
        super(context, DB_NAME, null, 6);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String qry = "CREATE TABLE SNAP(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "file_name TEXT DEFAULT NULL, " +
                "snap_image TEXT, " +
                "temp_snap_image TEXT, " +
                "bw_status INTEGER DEFAULT 0, " +
                "mime_type TEXT DEFAULT NULL, " +
                "file_path TEXT DEFAULT NULL, " +
                "file_date TEXT, " +
                "title TEXT DEFAULT NULL, " +
                "company_id INTEGER, " +
                "upload_status INTEGER DEFAULT 0, " +
                "for_upload INTEGER DEFAULT 0," +
                "reading_state INTEGER DEFAULT 0, " +
                "image_position INTEGER NOT NULL, " +
                "amount DOUBLE (20,2) DEFAULT 0.00, " +
                "amount_typed INTEGR DEFAULT 0,"+
                "selfPaid INTEGER DEFAULT 0)";

        String companyList = "CREATE TABLE COMPANY(company_id INTEGER PRIMARY KEY,company_name TEXT,company_pic TEXT DEFAULT NULL,user_permission INTEGER, updated_at TEXT, isExists INTEGER DEFAULT 1, payment_status INTEGER DEFAULT 0, subs_type_data_id INTEGER, package_price INTEGER, sub_user_price INTEGER, company_pic_name TEXT DEFAULT NULL, company_currency TEXT DEFAULT NULL, user_company_amount NUMERIC (10,2) DEFAULT 0.00, company_ein TEXT DEFAULT NULL, show_status INTEGER DEFAULT 0, date_format TEXT DEFAULT NULL)";

        String countryList = "CREATE TABLE COUNTRY(country_id INTEGER PRIMARY KEY AUTOINCREMENT, country_name TEXT)";

        String stateList = "CREATE TABLE STATES(states_id INTEGER PRIMARY KEY AUTOINCREMENT, states_name TEXT)";

        String currency = "CREATE TABLE CURRENCY(currency_id INTEGER PRIMARY KEY AUTOINCREMENT, currency_name TEXT, currency_code TEXT, currency_symbol TEXT)";

        String datFormat = "CREATE TABLE DATE_FORMAT(date_format_id INTEGER PRIMARY KEY AUTOINCREMENT, date_format TEXT)";

        String gallery = "CREATE TABLE GALLERY(id INTEGER PRIMARY KEY AUTOINCREMENT, image_id INTEGER DEFAULT 0, file_name TEXT, snap_image TEXT DEFAULT NULL, mime_type TEXT DEFAULT NULL, file_path TEXT DEFAULT NULL, approval_status INTEGER, created_user_id INTEGER, amount DOUBLE (20,2) DEFAULT 0.00, title TEXT, company_id INTEGER, payment_flag INTEGER, created_user_name TEXT, thumbnail TEXT, original TEXT DEFAULT NULL, link TEXT, file_date TEXT, thumbnailLink TEXT, originalLink TEXT DEFAULT NULL, created_at TEXT DEFAULT NULL, reject_reason TEXT DEFAULT NULL, current_upload_time TEXT DEFAULT NULL)";

        String filterSubUser = "CREATE TABLE SUB_USER(id INTEGER PRIMARY KEY AUTOINCREMENT, sub_user_id INTEGER, name TEXT, sub_user_name TEXT, company_id INTEGER)";

        String user = "CREATE TABLE USER(id INTEGER PRIMARY KEY AUTOINCREMENT, user_id INTEGER, user_name TEXT, user_email TEXT, user_permission_company_id INTEGER, user_permission_role_id INTEGER, user_amount NUMERIC (10,2) DEFAULT 0.00, user_amount_currency TEXT, user_prefer_name TEXT, user_role_name TEXT,addedBy INTEGER,isLogin INTEGER DEFAULT 0)";

        String settings = "CREATE TABLE SETTINGS(id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT DEFAULT NULL)";


        db.execSQL(qry);
        db.execSQL(companyList);
        db.execSQL(countryList);
        db.execSQL(stateList);
        db.execSQL(currency);
        db.execSQL(datFormat);
        db.execSQL(gallery);
        db.execSQL(filterSubUser);
        db.execSQL(user);
        db.execSQL(settings);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS SNAP");
        db.execSQL("DROP TABLE IF EXISTS COMPANY");
        db.execSQL("DROP TABLE IF EXISTS COUNTRY");
        db.execSQL("DROP TABLE IF EXISTS STATES");
        db.execSQL("DROP TABLE IF EXISTS CURRENCY");
        db.execSQL("DROP TABLE IF EXISTS DATE_FORMAT");
        db.execSQL("DROP TABLE IF EXISTS GALLERY");
        db.execSQL("DROP TABLE IF EXISTS SUB_USER");
        db.execSQL("DROP TABLE IF EXISTS USER");
        db.execSQL("DROP TABLE IF EXISTS SETTINGS");
        onCreate(db);
    }

    /*---------START GLOBAL SECTIONS------------*/
    //ADD IMAGE(S)
    public String addImages(String file_name, String snap_image, int bw_status, String mime_type, String file_path, String file_date, String title, int company_id, int image_position, int amount) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("file_name",file_name);
        cv.put("snap_image",snap_image);
        cv.put("temp_snap_image", snap_image);
        cv.put("bw_status",bw_status);
        cv.put("mime_type",mime_type);
        cv.put("file_path",file_path);
        cv.put("file_date",file_date);
        cv.put("title",title);
        cv.put("company_id",company_id);
        cv.put("for_upload",0);
        cv.put("image_position", image_position);
        cv.put("amount", amount);

        long res = db.insert("SNAP", null, cv);
        if(res == -1){
            return "Unable to take snap right now";
        } else {
            return "Image Added Successfully";
        }
    }
    //SELECT LAST RECORD
    public Cursor getLastId(int company_id) {
        Cursor cursor = null;
        SQLiteDatabase db = this.getReadableDatabase();
        if(db != null) {
            cursor = db.rawQuery("select MAX(id) as max_id from SNAP WHERE company_id ="+ company_id, null);
        }
        return cursor;
    }
    //Update Title
    public String updateTitle(int image_position, int company_id, String title){
        SQLiteDatabase db = this.getWritableDatabase();
        String myTable = "SNAP";

        ContentValues img = new ContentValues();
        img.put("title",title);

        String whereClause = "image_position = ? AND company_id + ?";
        String[] whereArgs = {String.valueOf(image_position),String.valueOf(company_id)};

        long res = db.update(myTable, img, whereClause, whereArgs);
        if(res == -1){
            return "Title update failed try again";
        } else {
            return "Title updated successfully";
        }
    }
    //Update Amount
    public String updateAmount(int image_position, int company_id, double amount, int amount_typed){
        SQLiteDatabase db = this.getWritableDatabase();
        String myTable = "SNAP";

        ContentValues img = new ContentValues();
        img.put("amount",amount);
        img.put("amount_typed", amount_typed);

        String whereClause = "image_position = ? AND company_id + ?";
        String[] whereArgs = {String.valueOf(image_position),String.valueOf(company_id)};

        long res = db.update(myTable, img, whereClause, whereArgs);
        if(res == -1){
            return "Amount update failed try again";
        } else {
            return "Amount updated successfully";
        }
    }
    public String updateIsPaid(int image_position, int company_id, int selfPaid){
        SQLiteDatabase db = this.getWritableDatabase();
        String myTable = "SNAP";

        ContentValues img = new ContentValues();
        img.put("selfPaid",selfPaid);

        String whereClause = "image_position = ? AND company_id + ?";
        String[] whereArgs = {String.valueOf(image_position),String.valueOf(company_id)};

        long res = db.update(myTable, img, whereClause, whereArgs);
        if(res == -1){
            return "Self Paid update failed try again";
        } else {
            return "Self Paid updated successfully";
        }
    }
    //IMAGE(S) LIST
    public Cursor getImages(int companyId) {
        Cursor cursor = null;
        SQLiteDatabase db = this.getReadableDatabase();
        if(db != null) {
            cursor = db.rawQuery("select * from SNAP WHERE company_id = " + companyId + " order by id desc", null);
        }
        return cursor;
    }
    //UPDATE IMAGE(S)
    public String updateImages(int imgId, String file_name, String snap_image, String temp_snap_image, int bw_status, String mime_type, String file_path, String file_date, String title, int company_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        String myTable = "SNAP";

        ContentValues img = new ContentValues();
        img.put("file_name",file_name);
        img.put("snap_image",snap_image);
        img.put("temp_snap_image",temp_snap_image);
        img.put("bw_status",bw_status);
        img.put("mime_type",mime_type);
        img.put("file_path",file_path);
        img.put("file_date",file_date);
        img.put("title",title);
        img.put("company_id",company_id);

        String whereClause = "id = ?";
        String[] whereArgs = {String.valueOf(imgId)};

        long res = db.update(myTable, img, whereClause, whereArgs);
        if(res == -1){
            return "Retake Failed Try Again";
        } else {
            return "Update successfully";
        }

    }
    //REMOVE IMAGE(S)
    public String removePhoto(int photoId) {
        SQLiteDatabase db = this.getWritableDatabase();
        String myTable = "SNAP";
        String whereClause = "id = ?";
        String[] whereArgs = {String.valueOf(photoId)};
        long res = db.delete(myTable, whereClause, whereArgs);
        if(res == -1){
            return "Photo removed failed";
        } else {
            return "Photo removed successfully";
        }
    }

    //Update For Upload Status IMAGE(S)
    public String updateImageForUpload(int imgId, int for_upload, int company_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        String myTable = "SNAP";

        ContentValues img = new ContentValues();
        img.put("for_upload",for_upload);

        String whereClause = "id = ? AND company_id + ?";
        String[] whereArgs = {String.valueOf(imgId),String.valueOf(company_id)};

        long res = db.update(myTable, img, whereClause, whereArgs);
        if(res == -1){
            return "Retake Failed Try Again";
        } else {
            return "Update successfully";
        }
    }
    //All Images
    public Cursor getAllImages() {
        Cursor cursor = null;
        SQLiteDatabase db = this.getReadableDatabase();
        if (db != null) {
            ContentValues img = new ContentValues();
            img.put("reading_state",1);
            db.update("SNAP", img, null, null);
            cursor = db.rawQuery("select * from SNAP WHERE upload_status = 0 AND for_upload = 1 order by id desc", null);

        }
        return cursor;
    }
    public String removeAllSnap() {
        SQLiteDatabase db = this.getWritableDatabase();
        long res = db.delete("SNAP", null, null);
        if(res == -1){
            return "Image removed failed";
        } else {
            return "Image removed successfully";
        }
    }
    //Company Store
    public void addCompanyList(String company_name, int company_id,String img, int user_permission, int isExits, String updated_at, int payment_status, int subs_type_data_id, int package_price, int sub_user_price, String company_pic_name, String company_currency, double user_company_amount, String company_ein, int show_status, String date_format) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("company_name", company_name);
        cv.put("company_id", company_id);
        cv.put("company_pic", img);
        cv.put("updated_at", updated_at);
        cv.put("user_permission", user_permission);
        cv.put("payment_status", payment_status);
        cv.put("subs_type_data_id",subs_type_data_id);
        cv.put("package_price",package_price);
        cv.put("sub_user_price",sub_user_price);
        cv.put("company_pic_name",company_pic_name);
        cv.put("company_currency",company_currency);
        cv.put("user_company_amount", user_company_amount);
        cv.put("company_ein", company_ein);
        cv.put("show_status", show_status);
        cv.put("date_format", date_format);

        db.insert("COMPANY", null, cv);

        db.close();

    }
    //Company Remove
    public String removeCompany() {

        SQLiteDatabase db = this.getWritableDatabase();
        String myTable = "COMPANY";
        String whereClause = "isExists = ?";
        String[] whereArgs = {String.valueOf(0)};
        long res = db.delete(myTable, whereClause, whereArgs);
        if(res == -1){
            return "Company removed failed";
        } else {
            return "Company removed successfully";
        }
    }

    public String removeCompanyData(String companyIds) {
        String removeResponse = null;
        SQLiteDatabase db = this.getWritableDatabase();
        if(db!= null) {
            removeResponse = "Delete from COMPANY WHERE company_id NOT IN " + companyIds;
            db.execSQL(removeResponse);
            //cursor =  db.rawQuery( , null);
        }
        return removeResponse;
    }
    //Company List
    public Cursor fetchData()
    {

        Cursor cursor = null;
        SQLiteDatabase db = this.getReadableDatabase();
        if (db != null) {
            cursor = db.rawQuery("select * from "+ "COMPANY", null);
        }
        return cursor;
    }

    //Company Fetch By ID
    public Cursor fetchCompanyImage(int company_id)
    {

        Cursor cursor = null;
        SQLiteDatabase db = this.getReadableDatabase();
        if(db != null) {
            cursor = db.rawQuery("select * from COMPANY WHERE company_id = "+company_id, null);
        }
        return cursor;
    }

    public String updateCompany(int company_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        String myTable = "COMPANY";

        ContentValues img = new ContentValues();
        img.put("isExists",1);

        String whereClause = "company_id + ?";
        String[] whereArgs = {String.valueOf(company_id)};

        long res = db.update(myTable, img, whereClause, whereArgs);
        if(res == -1){
            return "Retake Failed Try Again";
        } else {
            return "Update successfully";
        }
    }

    public String updateCompanyAll() {
        SQLiteDatabase db = this.getWritableDatabase();
        String myTable = "COMPANY";

        ContentValues img = new ContentValues();
        img.put("isExists",0);



        long res = db.update(myTable, img, null, null);
        if(res == -1){
            return "Retake Failed Try Again";
        } else {
            return "Update successfully";
        }
    }

    public String removeAllCompany() {
        SQLiteDatabase db = this.getWritableDatabase();
        long res = db.delete("COMPANY", null, null);
        if(res == -1){
            return "Company removed failed";
        } else {
            return "Company removed successfully";
        }
    }

    public String updateImageForCompany(Integer company_id, String image, String company_pic_name) {
        SQLiteDatabase db = this.getWritableDatabase();
        String myTable = "COMPANY";

        ContentValues img = new ContentValues();
        img.put("company_pic",image);
        img.put("company_pic_name", company_pic_name);

        String whereClause = "company_id = ?";
        String[] whereArgs = {String.valueOf(company_id)};

        long res = db.update(myTable, img, whereClause, whereArgs);
        if(res == -1){
            return "Update Failed Try Again";
        } else {
            return "Update successfully";
        }
    }

    public String updateCompanyData(String company_name, Integer company_id, int user_permission, int isExits, int payment_status, String updated_at, int subs_type_data_id, int package_price, int sub_user_price, String company_currency, double user_company_amount, String company_ein, int show_status, String date_format) {
        SQLiteDatabase db = this.getWritableDatabase();
        String myTable = "COMPANY";

        ContentValues img = new ContentValues();
        img.put("company_name", company_name);
        img.put("user_permission", user_permission);
        img.put("updated_at", updated_at);
        img.put("isExists",isExits);
        img.put("payment_status", payment_status);
        img.put("subs_type_data_id",subs_type_data_id);
        img.put("package_price",package_price);
        img.put("sub_user_price",sub_user_price); // company_currency
        img.put("company_currency",company_currency);
        img.put("user_company_amount", user_company_amount);
        img.put("company_ein", company_ein);
        img.put("show_status", show_status);
        img.put("date_format", date_format);

        String whereClause = "company_id = ?";
        String[] whereArgs = {String.valueOf(company_id)};

        long res = db.update(myTable, img, whereClause, whereArgs);
        if(res == -1){
            return "Update Failed Try Again";
        } else {
            return "Update successfully";
        }
    }

    //Get Company Ids
    public Cursor fetchCompanyId()
    {

        Cursor cursor = null;
        SQLiteDatabase db = this.getReadableDatabase();
        if(db != null) {
            cursor = db.rawQuery("SELECT Group_concat(company_id) AS compIds FROM COMPANY", null);
        }
        return cursor;
    }

    public String updateForCompany(String company_name, Integer company_id, int user_permission) {
        SQLiteDatabase db = this.getWritableDatabase();
        String myTable = "COMPANY";

        ContentValues img = new ContentValues();
        img.put("company_name", company_name);
        img.put("user_permission", user_permission);

        String whereClause = "company_id = ?";
        String[] whereArgs = {String.valueOf(company_id)};

        long res = db.update(myTable, img, whereClause, whereArgs);
        if(res == -1){
            return "Update Failed Try Again";
        } else {
            return "Update successfully";
        }
    }

    //Country Store
    public String saveCountry(String country_name) {
        String myTable = "COUNTRY";
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("country_name",country_name);

        long res = db.insert("COUNTRY", null, cv);
        if(res == -1){
            return "Unable to save country right now";
        } else {
            return "Country Added Successfully";
        }
    }
    //Country Remove
    public String removeCountry() {
        SQLiteDatabase db = this.getWritableDatabase();
        long res = db.delete("COUNTRY", null, null);
        if(res == -1){
            return "Country removed failed";
        } else {
            return "Country removed successfully";
        }
    }
    //Country List
    public Cursor fetchCountry() {
        Cursor cursor = null;
        SQLiteDatabase db = this.getReadableDatabase();
        if (db != null) {
            cursor = db.rawQuery("select * from "+ "COUNTRY", null);
        }
        return cursor;
    }

    //State Store
    public String saveState(String states_name) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("states_name",states_name);

        long res = db.insert("STATES", null, cv);
        if(res == -1){
            return "Unable to save state right now";
        } else {
            return "State Added Successfully";
        }
    }

    //State Remove
    public String removeState() {
        SQLiteDatabase db = this.getWritableDatabase();
        long res = db.delete("STATES", null, null);
        if(res == -1){
            return "State removed failed";
        } else {
            return "State removed successfully";
        }
    }
    //Country List
    public Cursor fetchState() {
        Cursor cursor = null;
        SQLiteDatabase db = this.getReadableDatabase();
        if (db != null) {
            cursor = db.rawQuery("select * from "+ "STATES", null);
        }
        return cursor;
    }

    //Add Currency
    public String saveCurrency(String currency_name, String currency_code, String currency_symbol) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("currency_name",currency_name);
        cv.put("currency_code",currency_code);
        cv.put("currency_symbol",currency_symbol);

        long res = db.insert("CURRENCY", null, cv);
        if(res == -1){
            return "Unable to save currency right now";
        } else {
            return "Currency Added Successfully";
        }
    }
    //Check If Currency Exist or Not
    public Cursor checkCurrency(String currency_code) {
        Cursor cursor = null;
        SQLiteDatabase db = this.getReadableDatabase();
        if(db != null) {
            cursor = db.rawQuery("select * from CURRENCY WHERE currency_code = '" + currency_code +"'", null);
        }
        return cursor;
    }
    //Update Gallery Approval Status
    public String updateCurrencies(String currency_name, String currency_code, String currency_symbol) {
        SQLiteDatabase db = this.getWritableDatabase();
        String myTable = "CURRENCY";

        ContentValues img = new ContentValues();
        img.put("currency_name",currency_name);
        img.put("currency_code",currency_code);
        img.put("currency_symbol",currency_symbol);

        String whereClause = "currency_code = ?";
        String[] whereArgs = {String.valueOf(currency_code)};

        long res = db.update(myTable, img, whereClause, whereArgs);
        if(res == -1){
            return "Update Failed Try Again";
        } else {
            return "Update successfully";
        }
    }
    //Remove From Currency
    public String removeCurrency(String currency_code) {
        String removeResponse = null;
        SQLiteDatabase db = this.getWritableDatabase();
        if(db!= null) {
            removeResponse = "Delete from CURRENCY WHERE currency_code NOT IN " + currency_code;
            db.execSQL(removeResponse);
            //cursor =  db.rawQuery( , null);
        }
        return removeResponse;
    }
    //Get Currency
    public Cursor fetchCurrency() {
        Cursor cursor = null;
        SQLiteDatabase db = this.getReadableDatabase();
        if (db != null) {
            cursor = db.rawQuery("select * from "+ "CURRENCY", null);
        }
        return cursor;
    }

    //Add Date Format
    public String addDateFormat(String date_format) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("date_format",date_format);

        long res = db.insert("DATE_FORMAT", null, cv);
        if(res == -1){
            return "Unable to save Date Format right now";
        } else {
            return "Date Format Added Successfully";
        }
    }

    //Remove Currency
    public String removeDateFormat() {
        SQLiteDatabase db = this.getWritableDatabase();
        long res = db.delete("DATE_FORMAT", null, null);
        if(res == -1){
            return "Date Format removed failed";
        } else {
            return "Date Format removed successfully";
        }
    }
    //Get Currency
    public Cursor fetchDateFormat() {
        Cursor cursor = null;
        SQLiteDatabase db = this.getReadableDatabase();
        if (db != null) {
            cursor = db.rawQuery("select * from "+ "DATE_FORMAT", null);
        }
        return cursor;
    }

    //ReceiptGallery LIST

    //GALLERY INSERT
    public String saveGallery(int image_id, String file_name, String snap_image, String mime_type, String file_path, int approval_status, int created_user_id, double amount, String title, int company_id,
                              int payment_flag, String created_user_name, String thumbnail, String original, String link, String file_date, String thumbnailLink, String originalLink, String created_at, String reject_reason) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("image_id",image_id);
        cv.put("file_name",file_name);
        cv.put("snap_image",snap_image);
        cv.put("mime_type",mime_type);
        cv.put("file_path",file_path);
        cv.put("approval_status",approval_status);
        cv.put("reject_reason",reject_reason);
        cv.put("created_user_id",created_user_id);
        cv.put("amount",amount);
        cv.put("title",title);
        cv.put("company_id",company_id);
        cv.put("payment_flag",payment_flag);
        cv.put("created_user_name",created_user_name);
        cv.put("thumbnail",thumbnail);
        cv.put("original",original);
        cv.put("link",link);
        cv.put("file_date",file_date);
        cv.put("thumbnailLink",thumbnailLink);
        cv.put("originalLink",originalLink);
        cv.put("created_at",created_at);
        cv.put("current_upload_time","00:00:00");

        long res = db.insert("GALLERY", null, cv);
        if(res == -1){
            return "Unable to save image right now";
        } else {
            return "Image Added Successfully";
        }
    }

    //Update Gallery Image
    public String updateGalleryImage(int image_id, String file_path) {
        SQLiteDatabase db = this.getWritableDatabase();
        String myTable = "GALLERY";

        ContentValues img = new ContentValues();
        img.put("thumbnail",file_path);

        String whereClause = "image_id = ?";
        String[] whereArgs = {String.valueOf(image_id)};

        long res = db.update(myTable, img, whereClause, whereArgs);
        if(res == -1){
            return "Update Failed Try Again";
        } else {
            return "Update successfully";
        }
    }

    //Resubmit Gallery
    public String reSubmitGallery(int image_id, int approval_status, double amount, String title, int payment_flag, String file_date, String reject_reason) {
        SQLiteDatabase db = this.getWritableDatabase();
        String myTable = "GALLERY";

        ContentValues img = new ContentValues();
        img.put("approval_status",approval_status);
        img.put("amount",amount);
        img.put("title",title);
        img.put("payment_flag",payment_flag);
        img.put("file_date",file_date);
        img.put("reject_reason",reject_reason);
        String whereClause = "image_id = ?";
        String[] whereArgs = {String.valueOf(image_id)};

        long res = db.update(myTable, img, whereClause, whereArgs);
        if(res == -1){
            return "Update Failed Try Again";
        } else {
            return "Update successfully";
        }
    }

    //current_upload_time
    public String updateTime(String current_upload_time, int rowId) {
        SQLiteDatabase db = this.getWritableDatabase();
        String myTable = "GALLERY";

        ContentValues img = new ContentValues();
        img.put("current_upload_time",current_upload_time);
        String whereClause = "id = ?";
        String[] whereArgs = {String.valueOf(rowId)};
        long res = db.update(myTable, img, whereClause, whereArgs);
        if(res == -1){
            return "Update Failed Try Again";
        } else {
            return "Update successfully";
        }
    }
    //Gallery Image Set Based on Company
    public Cursor fetchGallery(int company_id, String order_by, String approval_status) {
        Cursor cursor = null;
        SQLiteDatabase db = this.getReadableDatabase();
        if (db != null) {
            cursor = db.rawQuery("select * from GALLERY WHERE company_id = "+company_id + " AND approval_status IN "+approval_status+" ORDER BY file_date "+order_by + ", id "+order_by, null );
        }
        return cursor;
    }
    //Check If Image Exist or Not
    public Cursor checkGalleryImage(int companyId, int approval_status) {
        Cursor cursor = null;
        SQLiteDatabase db = this.getReadableDatabase();
        if(db != null) {
            cursor = db.rawQuery("select * from GALLERY WHERE company_id = " + companyId + " AND approval_status = "+approval_status, null);
        }
        return cursor;
    }
    //Gallery Image Get Based on Company and image id 0
    public Cursor getGalleryPending(int companyId) {
        Cursor cursor = null;
        SQLiteDatabase db = this.getReadableDatabase();
        if(db != null) {
            cursor = db.rawQuery("select * from GALLERY WHERE company_id = " + companyId + " AND image_id = 0", null);
        }
        return cursor;
    }
    public Cursor getGalleryPendingResCom(String companyId) {
        Cursor cursor = null;
        SQLiteDatabase db = this.getReadableDatabase();
        if(db != null) {
            cursor = db.rawQuery("select * from GALLERY WHERE company_id IN " + companyId + " AND image_id = 0", null);
        }
        return cursor;
    }
    //Gallery Image Get Based on Company and image id 0
    public Cursor getGalleryAllPending(int companyId,String order_by) {
        Cursor cursor = null;
        SQLiteDatabase db = this.getReadableDatabase();
        if(db != null) {
            cursor = db.rawQuery("select * from GALLERY WHERE company_id = " + companyId + " AND approval_status = 0" + " ORDER BY file_date "+order_by + ", id "+order_by, null);
        }
        return cursor;
    }
    //Gallery Image Get Based on Company and image id 0 with Specific Image Id at Top
    public Cursor getGalleryAllPendingSort(int companyId, int img_id) {
        Cursor cursor = null;
        SQLiteDatabase db = this.getReadableDatabase();
        if(db != null) {
            cursor = db.rawQuery("select * from GALLERY WHERE company_id = " + companyId + " AND approval_status = 0" + " ORDER BY CASE WHEN image_id = "+img_id + " THEN 0 ELSE id END", null);
        }
        return cursor;
    }
    //Update Gallery Pending Image After Response
    public String updateGallery(int image_id, int created_user_id, String created_user_name, String created_at,  String thumbnail, String original, String link, int aId, String thumbnailLink, String originalLink, double amount) {
        SQLiteDatabase db = this.getWritableDatabase();
        String myTable = "GALLERY";

        ContentValues img = new ContentValues();
        img.put("image_id",image_id);
        img.put("snap_image","");
        img.put("mime_type","");
        img.put("file_path","");
        img.put("amount",amount);
        img.put("created_user_id",created_user_id);
        img.put("created_user_name",created_user_name);
        img.put("created_at",created_at);
        img.put("thumbnail",thumbnail);
        img.put("original",original);
        img.put("link",link);
        img.put("thumbnailLink",thumbnailLink);
        img.put("originalLink",originalLink);

        String whereClause = "id = ?";
        String[] whereArgs = {String.valueOf(aId)};

        long res = db.update(myTable, img, whereClause, whereArgs);
        if(res == -1){
            return "Update Failed Try Again";
        } else {
            return "Update successfully";
        }
    }

    //Check If Image Exist or Not
    public Cursor checkGallery(int companyId, int image_id) {
        Cursor cursor = null;
        SQLiteDatabase db = this.getReadableDatabase();
        if(db != null) {
            cursor = db.rawQuery("select * from GALLERY WHERE company_id = " + companyId + " AND image_id = "+image_id, null);
        }
        return cursor;
    }

    //Update Gallery Approval Status
    public String updateGalleryStatus(int image_id, int approval_status, String reject_reason) {
        SQLiteDatabase db = this.getWritableDatabase();
        String myTable = "GALLERY";

        ContentValues img = new ContentValues();
        img.put("approval_status",approval_status);
        img.put("reject_reason",reject_reason);

        String whereClause = "image_id = ?";
        String[] whereArgs = {String.valueOf(image_id)};

        long res = db.update(myTable, img, whereClause, whereArgs);
        if(res == -1){
            return "Update Failed Try Again";
        } else {
            return "Update successfully";
        }
    }

    //Remove From Gallery
    public String removeGalleryPhoto(int companyId, String imgIds, String approval_status) {
        String removeResponse = null;
        SQLiteDatabase db = this.getWritableDatabase();
        if(db!= null) {
            removeResponse = "Delete from GALLERY WHERE company_id ="+companyId +" AND image_id NOT IN " + imgIds + " AND image_id != 0 AND approval_status IN ("+approval_status + ")";
            db.execSQL(removeResponse);
            //cursor =  db.rawQuery( , null);
        }
        Log.e("REMOVE RESPONSE",""+"Delete from GALLERY WHERE company_id ="+companyId +" AND image_id NOT IN " + imgIds + " AND image_id != 0 AND approval_status IN ("+approval_status + ")");
        return removeResponse;
    }

    //Remove From Gallery
    public String removeGalleryPhotos(int companyId, String imgIds) {
        String removeResponse = null;
        SQLiteDatabase db = this.getWritableDatabase();
        if(db!= null) {
            removeResponse = "Delete from GALLERY WHERE company_id ="+companyId +" AND image_id IN " + imgIds + " AND image_id != 0";
            db.execSQL(removeResponse);
            //cursor =  db.rawQuery( , null);
        }
        return removeResponse;
    }

    //Update Image Original in Gallery
    public String updateGalleryOriginal(int image_id, String original) {
        SQLiteDatabase db = this.getWritableDatabase();
        String myTable = "GALLERY";

        ContentValues img = new ContentValues();
        img.put("original",original);

        String whereClause = "image_id = ?";
        String[] whereArgs = {String.valueOf(image_id)};

        long res = db.update(myTable, img, whereClause, whereArgs);
        if(res == -1){
            return "Update Failed Try Again";
        } else {
            return "Update successfully";
        }
    }

    public String removeAllGallery() {
        SQLiteDatabase db = this.getWritableDatabase();
        long res = db.delete("GALLERY", null, null);
        if(res == -1){
            return "Gallery removed failed";
        } else {
            return "Gallery removed successfully";
        }
    }
    public Cursor getFilterGallery(String toDate, String fromDate, String approvalStatus, String uploadById, String searchBy,String orderBy, int company_id){
        Cursor cursor = null;
        String conditions = "company_id = "+company_id;
        if(!toDate.equals("") && !fromDate.equals("")) {
            if(!conditions.equals("")) {
                conditions = conditions + " AND ";
            }
            conditions = conditions + "file_date BETWEEN '" +toDate+ "' AND '"+ fromDate + "'";
        }
        if(!approvalStatus.equals("")) {
            if(!conditions.equals("")) {
                conditions = conditions + " AND ";
            }
            conditions = conditions + "approval_status IN " +approvalStatus;
        }
        if(!uploadById.equals("")) {
            if(!conditions.equals("")) {
                conditions = conditions + " AND ";
            }
            conditions = conditions + "created_user_id IN " +uploadById;
        }
        if(!searchBy.equals("")) {
            if(!conditions.equals("")) {
                conditions = conditions + " AND ";
            }
            conditions = conditions + "(title LIKE '%" +searchBy + "%' OR amount LIKE '%" + searchBy + "%')";
        }
        String subSelect = "(SELECT SUM(amount) FROM GALLERY WHERE company_id = "+ company_id +" AND approval_status IN " + approvalStatus + " AND "+conditions +")";

        String columns = " GALLERY.*, " + subSelect + " as total_amount";
        String queryBase = "SELECT "+ columns +" FROM GALLERY WHERE ";
        String order_by = "ORDER BY file_date "+ orderBy; //AND image_id != 0

//        if(toDate.equals("") && fromDate.equals("") && approvalStatus.equals("") && uploadById.equals("") && searchBy.equals("")) {
//            conditions = "1";
//        }
        SQLiteDatabase db = this.getReadableDatabase();
        if(db != null) {
            cursor = db.rawQuery(queryBase + conditions + order_by, null);
        }
        Log.e("FILTER QUERY",""+queryBase + conditions);
        return cursor;
    }
    //Sub User Store
    public String saveSubUser(int sub_user_id, String name, String sub_user_name, int company_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("sub_user_id",sub_user_id);
        cv.put("name",name);
        cv.put("sub_user_name",sub_user_name);
        cv.put("company_id",company_id);

        long res = db.insert("SUB_USER", null, cv);
        if(res == -1){
            return "Unable to save sub user right now";
        } else {
            return "Sub User Added Successfully";
        }
    }
    //Get Sub User
    public Cursor fetchSubUser(int company_id) {
        Cursor cursor = null;
        SQLiteDatabase db = this.getReadableDatabase();
        if (db != null) {
            cursor = db.rawQuery("select * from SUB_USER WHERE company_id = "+company_id, null );
        }
        return cursor;
    }

    //Remove Sub Users
    public String removeAllSubUser() {
        SQLiteDatabase db = this.getWritableDatabase();
        long res = db.delete("SUB_USER", null, null);
        if(res == -1){
            return "Sub User removed failed";
        } else {
            return "Sub User removed successfully";
        }
    }
    //ADD USER(S)
    public String addUser(int user_id, String user_name, String user_email, int user_permission_company_id, int user_permission_role_id,
                          double user_amount, String user_amount_currency, String user_prefer_name, String user_role_name,int addedBy,int isLogin) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("user_id",user_id);
        cv.put("user_name",user_name);
        cv.put("user_email", user_email);
        cv.put("user_permission_company_id",user_permission_company_id);
        cv.put("user_permission_role_id",user_permission_role_id);
        cv.put("user_amount",user_amount);
        cv.put("user_amount_currency",user_amount_currency);
        cv.put("user_prefer_name",user_prefer_name);
        cv.put("user_role_name",user_role_name);
        cv.put("addedBy",addedBy);
        cv.put("isLogin",isLogin);
        long res = db.insert("USER", null, cv);
        if(res == -1){
            return "Unable to save user";
        } else {
            return "User Saved Successfully";
        }
    }
    //Get User
    public Cursor fetchUser(int company_id) {
        Cursor cursor = null;
        SQLiteDatabase db = this.getReadableDatabase();
        if (db != null) {
            cursor = db.rawQuery("select * from USER WHERE user_permission_company_id = "+company_id, null );
        }
        return cursor;
    }

    //Get User
    public Cursor fetchAllUser(int company_id) {
        Cursor cursor = null;
        SQLiteDatabase db = this.getReadableDatabase();
        if (db != null) {
            cursor = db.rawQuery("select * from USER WHERE user_email NOT IN (select  user_email from USER WHERE user_permission_company_id =" +company_id+")", null );
        }
        Log.e("FILTER QUERY","select * from USER WHERE user_email NOT IN (select  user_email from USER WHERE user_permission_company_id =" +company_id+")");
        return cursor;
    }

    //Search User By Name, Email and Sub User Name
    public Cursor searchUser(String input) {
        Cursor cursor = null;
        SQLiteDatabase db = this.getReadableDatabase();
        String conditions = "";
        String queryBase = "SELECT * FROM USER WHERE ";

        if(!input.equals("")) {
            conditions = conditions + "user_name LIKE '%" +input + "%' OR user_email LIKE '%" + input + "%' OR user_prefer_name LIKE '%" + input + "%'";
        }
        if(db != null) {
            cursor = db.rawQuery(queryBase + conditions, null);
        }
        Log.e("FILTER QUERY1",""+queryBase + conditions);
        return cursor;
    }

    //Check User
    public Cursor checkUser(int companyId, int user_id) {
        Cursor cursor = null;
        SQLiteDatabase db = this.getReadableDatabase();
        if(db != null) {
            cursor = db.rawQuery("select * from USER WHERE user_permission_company_id = " + companyId + " AND user_id = "+user_id, null);
        }
        return cursor;
    }

    //Update User
    public String updateUser(int user_id, String user_name, String user_email, int user_permission_company_id, int user_permission_role_id,
                             double user_amount, String user_amount_currency, String user_prefer_name, String user_role_name,int addedBy,int isLogin) {
        SQLiteDatabase db = this.getWritableDatabase();
        String myTable = "USER";

        ContentValues img = new ContentValues();
        img.put("user_id",user_id);
        img.put("user_name",user_name);
        img.put("user_email", user_email);
        img.put("user_permission_role_id",user_permission_role_id);
        img.put("user_amount",user_amount);
        img.put("user_amount_currency",user_amount_currency);
        img.put("user_prefer_name",user_prefer_name);
        img.put("user_role_name",user_role_name);
        img.put("addedBy",addedBy);
        img.put("isLogin",isLogin);
        String whereClause = "user_id = ? AND user_permission_company_id = ?";
        String[] whereArgs = {String.valueOf(user_id),String.valueOf(user_permission_company_id)};

        long res = db.update(myTable, img, whereClause, whereArgs);
        if(res == -1){
            return "Update Failed Try Again";
        } else {
            return "Update successfully";
        }
    }
    //Remove User
    public String removeUser(int companyId, String user_id) {
        String removeResponse = null;
        SQLiteDatabase db = this.getWritableDatabase();
        if(db!= null) {
            removeResponse = "Delete from USER WHERE user_permission_company_id ="+companyId +" AND user_id NOT IN " + user_id;
            db.execSQL(removeResponse);
        }
        return removeResponse;
    }
    //Remove User Particular
    public String removeSingleUser(int companyId, int user_id) {
        String removeResponse = null;
        SQLiteDatabase db = this.getWritableDatabase();
        if(db!= null) {
            removeResponse = "Delete from USER WHERE user_permission_company_id ="+companyId +" AND user_id = " + user_id;
            db.execSQL(removeResponse);
        }
        return removeResponse;
    }
    //Settings
    public String addSettings(String name, String value) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("name",name);
        cv.put("value",value);
        long res = db.insert("SETTINGS", null, cv);
        if(res == -1){
            return "Unable to save settings";
        } else {
            return "Settings Saved Successfully";
        }
    }
    //check Settings
    public Cursor checkSettings(String key) {
        Cursor cursor = null;
        SQLiteDatabase db = this.getReadableDatabase();
        if(db != null) {
            cursor = db.rawQuery("select * from SETTINGS WHERE name = '"+ key + "'", null);
        }
        return cursor;
    }

    //Update Settings
    public String updateSettings(String name, String value) {
        SQLiteDatabase db = this.getWritableDatabase();
        String myTable = "SETTINGS";

        ContentValues img = new ContentValues();
        img.put("value",value);

        String whereClause = "name = ?";
        String[] whereArgs = {String.valueOf(name)};

        long res = db.update(myTable, img, whereClause, whereArgs);
        if(res == -1){
            return "Update Failed Try Again";
        } else {
            return "Update successfully";
        }
    }
    /*----------END GLOBAL SECTIONS-------------*/
}
