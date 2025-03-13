package perfect.book.keeping.model;

import android.graphics.Bitmap;
import android.net.Uri;

public class Image {

    Bitmap image;

    public Image(Bitmap image) {
        this.image = image;
    }

    public Bitmap getImage() {
        return image;
    }
}
