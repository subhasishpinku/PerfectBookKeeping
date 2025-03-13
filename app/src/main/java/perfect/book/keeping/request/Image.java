package perfect.book.keeping.request;

public class Image {
    String file_name;
    String blobdata;
    String mimetype;

    public Image(String file_name, String blobdata, String mimetype) {
        this.file_name = file_name;
        this.blobdata = blobdata;
        this.mimetype = mimetype;
    }

    public String getFile_name() {
        return file_name;
    }

    public void setFile_name(String file_name) {
        this.file_name = file_name;
    }

    public String getBlobdata() {
        return blobdata;
    }

    public void setBlobdata(String blobdata) {
        this.blobdata = blobdata;
    }

    public String getMimetype() {
        return mimetype;
    }

    public void setMimetype(String mimetype) {
        this.mimetype = mimetype;
    }
}
