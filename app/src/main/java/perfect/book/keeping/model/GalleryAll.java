package perfect.book.keeping.model;

public class GalleryAll {
    int id;
    String imageData;
    String original;
    boolean isSelected;
    String date;
    boolean isHeader;
    boolean isHeaderSelected;

    int approval_status;
    int amount;
    String title;
    String createdBy;
    String link;

    String upload_by;
    int paymentFlag;
    String snap_image;

    public GalleryAll(int id, String imageData, String original, boolean isSelected, String date, boolean isHeader, boolean isHeaderSelected, int approval_status, int amount, String title, String createdBy,String link,String upload_by,int paymentFlag, String snap_image) {
        this.id = id;
        this.imageData = imageData;
        this.original = original;
        this.isSelected = isSelected;
        this.date = date;
        this.isHeader = isHeader;
        this.isHeaderSelected = isHeaderSelected;
        this.approval_status = approval_status;
        this.amount = amount;
        this.title = title;
        this.createdBy = createdBy;
        this.link=link;
        this.upload_by=upload_by;
        this.paymentFlag=paymentFlag;
        this.snap_image = snap_image;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getImageData() {
        return imageData;
    }

    public void setImageData(String imageData) {
        this.imageData = imageData;
    }

    public String getOriginal() {
        return original;
    }

    public void setOriginal(String original) {
        this.original = original;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public boolean isHeader() {
        return isHeader;
    }

    public void setHeader(boolean header) {
        isHeader = header;
    }

    public boolean isHeaderSelected() {
        return isHeaderSelected;
    }

    public void setHeaderSelected(boolean headerSelected) {
        isHeaderSelected = headerSelected;
    }

    public int getApproval_status() {
        return approval_status;
    }

    public void setApproval_status(int approval_status) {
        this.approval_status = approval_status;
    }

    public int getAmount() {
        return amount;
    }

    public String getTitle() {
        return title;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public String getLink() {
        return link;
    }

    public String getUpload_by() {
        return upload_by;
    }

    public int getPaymentFlag() {
        return paymentFlag;
    }

    public String getSnap_image() {
        return snap_image;
    }
}
