package perfect.book.keeping.model;

import java.util.List;

public class GalleryDate  {
    String gDate;

    boolean isSelected = false;

    private List<Gallery> galleries;

    public GalleryDate(String gDate, List<Gallery> galleries) {
        this.gDate = gDate;
        this.galleries = galleries;
    }

    public String getGDate() {
        return gDate;
    }

    public void setGDate(String gDate) {
        this.gDate = gDate;
    }

    public List<Gallery> getGalleries() {
        return galleries;
    }

    public void setGalleries(List<Gallery> galleries) {
        this.galleries = galleries;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
