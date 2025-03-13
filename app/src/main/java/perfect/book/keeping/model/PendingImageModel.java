package perfect.book.keeping.model;

public class PendingImageModel {

    int id;
    String file_name;
    String snap_image;
    String temp_snap_image;
    int bw_status;
    String mime_type;
    String file_path;
    String file_date;
    String title;
    int company_id;
    int image_position;
    int amount;
    int selfPaid;

    public PendingImageModel(int id, String file_name, String snap_image, String temp_snap_image, int bw_status, String mime_type, String file_path, String file_date, String title, int company_id, int image_position, int amount, int selfPaid) {
        this.id = id;
        this.file_name = file_name;
        this.snap_image = snap_image;
        this.temp_snap_image = temp_snap_image;
        this.bw_status = bw_status;
        this.mime_type = mime_type;
        this.file_path = file_path;
        this.file_date = file_date;
        this.title = title;
        this.company_id = company_id;
        this.image_position = image_position;
        this.amount = amount;
        this.selfPaid = selfPaid;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFile_name() {
        return file_name;
    }

    public void setFile_name(String file_name) {
        this.file_name = file_name;
    }

    public String getSnap_image() {
        return snap_image;
    }

    public void setSnap_image(String snap_image) {
        this.snap_image = snap_image;
    }

    public String getTemp_snap_image() {
        return temp_snap_image;
    }

    public void setTemp_snap_image(String temp_snap_image) {
        this.temp_snap_image = temp_snap_image;
    }

    public int getBw_status() {
        return bw_status;
    }

    public void setBw_status(int bw_status) {
        this.bw_status = bw_status;
    }

    public String getMime_type() {
        return mime_type;
    }

    public void setMime_type(String mime_type) {
        this.mime_type = mime_type;
    }

    public String getFile_path() {
        return file_path;
    }

    public void setFile_path(String file_path) {
        this.file_path = file_path;
    }

    public String getFile_date() {
        return file_date;
    }

    public void setFile_date(String file_date) {
        this.file_date = file_date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getCompany_id() {
        return company_id;
    }

    public void setCompany_id(int company_id) {
        this.company_id = company_id;
    }

    public int getImage_position() {
        return image_position;
    }

    public void setImage_position(int image_position) {
        this.image_position = image_position;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public int getSelfPaid() {
        return selfPaid;
    }

    public void setSelfPaid(int selfPaid) {
        this.selfPaid = selfPaid;
    }
}
