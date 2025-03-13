package perfect.book.keeping.model;


public class Gallery {

    int id;
    String imageData;
    String orginal;

    boolean isSelected = false;

    int position;


    public Gallery(int id, String imageData, String orginal, int position ) {
        this.id = id;
        this.imageData = imageData;
        this.orginal = orginal;
        this.position = position;
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


    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
