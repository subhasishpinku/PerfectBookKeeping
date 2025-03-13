package perfect.book.keeping.request;

public class UpdateImageRequest {

    private Image image;

    public UpdateImageRequest(Image image) {
        this.image = image;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }
}
