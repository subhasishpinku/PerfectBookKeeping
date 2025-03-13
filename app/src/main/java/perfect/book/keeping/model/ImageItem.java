package perfect.book.keeping.model;

import java.io.Serializable;

public class ImageItem implements Serializable {

    int id;
    String imageData;
    String orginal;

    boolean isChecked = false;

    public ImageItem(int id, String imageData, String orginal, boolean isChecked) {
        this.id = id;
        this.imageData = imageData;
        this.orginal = orginal;
    }


    public String getImageData() {
        return imageData;
    }

    public void setImageData(String imageData) {
        this.imageData = imageData;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getOrginal() {
        return orginal;
    }

    public void setOrginal(String orginal) {
        this.orginal = orginal;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }
}
