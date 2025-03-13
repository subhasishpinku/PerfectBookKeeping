package perfect.book.keeping.model;

public class TinderSliderModel {
    int id;
    String thumbnail;
    String original;
    String link;
    double amount;
    String title;
    String created_user_name;
    int payment_flag;
    String uploadAdd;
    int clickId;
    boolean imageShown;
    String file_name;
    String thumbnailLink;
    String originalLink;
    String created_at;
    int approval_status;
    String company_currency;
    int upload_by;
    int login_id;
    String imageUrl;
    String reject_reason;
    String company_dateFormat;
    public TinderSliderModel(int id, String file_name, String thumbnail, String original, String link, double amount, String title, String created_user_name, int payment_flag, String uploadAdd, int clickId, boolean imageShown, String thumbnailLink, String originalLink, String created_at,int approval_status, String company_currency, int upload_by, int login_id,String imageUrl, String reject_reason, String company_dateFormat) {
        this.id = id;
        this.file_name = file_name;
        this.thumbnail = thumbnail;
        this.original = original;
        this.link = link;
        this.amount = amount;
        this.title = title;
        this.created_user_name = created_user_name;
        this.payment_flag = payment_flag;
        this.uploadAdd = uploadAdd;
        this.clickId = clickId;
        this.imageShown = imageShown;
        this.thumbnailLink = thumbnailLink;
        this.originalLink = originalLink;
        this.created_at = created_at;
        this.approval_status=approval_status;
        this.company_currency = company_currency;
        this.upload_by = upload_by;
        this.login_id = login_id;
        this.imageUrl=imageUrl;
        this.reject_reason = reject_reason;
        this.company_dateFormat = company_dateFormat;
    }

    public int getId() {
        return id;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public String getOriginal() {
        return original;
    }

    public String getLink() {
        return link;
    }

    public double getAmount() {
        return amount;
    }

    public String getTitle() {
        return title;
    }

    public String getCreated_user_name() {
        return created_user_name;
    }

    public int getPayment_flag() {
        return payment_flag;
    }

    public String getUploadAdd() {
        return uploadAdd;
    }

    public int getClickId() {
        return clickId;
    }

    public boolean isImageShown() {
        return imageShown;
    }

    public String getFile_name() {
        return file_name;
    }

    public String getThumbnailLink() {
        return thumbnailLink;
    }

    public String getOriginalLink() {
        return originalLink;
    }

    public String getCreated_at() {
        return created_at;
    }

    public int getApproval_status() {
        return approval_status;
    }

    public void setApproval_status(int approval_status) {
        this.approval_status = approval_status;
    }

    public String getCompany_currency() {
        return company_currency;
    }

    public int getUpload_by() {
        return upload_by;
    }

    public int getLogin_id() {
        return login_id;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setPayment_flag(int payment_flag) {
        this.payment_flag = payment_flag;
    }

    public void setUploadAdd(String uploadAdd) {
        this.uploadAdd = uploadAdd;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getReject_reason() {
        return reject_reason;
    }

    public String getCompany_dateFormat() {
        return company_dateFormat;
    }
}
