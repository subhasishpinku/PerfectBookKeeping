package perfect.book.keeping.model;

public class SnapImages {

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
    double amount;
    int selfPaid;
    int amount_typed;
    String company_currency;

    String company_dateFormat;

    public SnapImages(int id, String file_name, String snap_image, String temp_snap_image, int bw_status, String mime_type, String file_path, String file_date, String title, int company_id, int image_position, double amount, int selfPaid, int amount_typed, String company_currency, String company_dateFormat) {
        this.id =  id;
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
        this.amount_typed = amount_typed;
        this.company_currency = company_currency;
        this.company_dateFormat = company_dateFormat;
    }

    public int getId() {
        return id;
    }

    public String getFile_name() {
        return file_name;
    }

    public String getSnap_image() {
        return snap_image;
    }

    public String getMime_type() {
        return mime_type;
    }

    public String getFile_path() {
        return file_path;
    }

    public String getFile_date() {
        return file_date;
    }

    public String getTitle() {
        return title;
    }

    public int getCompany_id() {
        return company_id;
    }

    public void setSnap_image(String snap_image) {
        this.snap_image = snap_image;
    }

    public String getTemp_snap_image() {
        return temp_snap_image;
    }

    public int getBw_status() {
        return bw_status;
    }

    public int getImage_position() {
        return image_position;
    }

    public double getAmount() {
        return amount;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public int getSelfPaid() {
        return selfPaid;
    }

    public void setSelfPaid(int selfPaid) {
        this.selfPaid = selfPaid;
    }

    public int getAmount_typed() {
        return amount_typed;
    }

    public void setAmount_typed(int amount_typed) {
        this.amount_typed = amount_typed;
    }

    public String getCompany_currency() {
        return company_currency;
    }

    public String getCompany_dateFormat() {
        return company_dateFormat;
    }
}
