package perfect.book.keeping.request;

import java.io.Serializable;

public class Snap implements Serializable {
    String file_name;
    String blobdata;
    String mimetype;
    String path;
    String file_date;
    int company_id;
    String title;


    public Snap (String file_name, String blobdata, String mimetype, String path, String file_date, String title, int company_id) {
        this.file_name = file_name;
        this.blobdata = blobdata;
        this.mimetype = mimetype;
        this.path = path;
        this.file_date = file_date;
        this.title = title;
        this.company_id = company_id;
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

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getFile_date() {
        return file_date;
    }

    public void setFile_date(String file_date) {
        this.file_date = file_date;
    }

    public int getCompany_id() {
        return company_id;
    }

    public void setCompany_id(int company_id) {
        this.company_id = company_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
