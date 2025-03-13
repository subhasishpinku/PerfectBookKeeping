package perfect.book.keeping.model;

public class PNL {
    int id;
    String year_month;
    String file_path;
    String file_url;

    public PNL(int id, String year_month, String file_path, String file_url) {
        this.id = id;
        this.year_month = year_month;
        this.file_path = file_path;
        this.file_url = file_url;

    }

    public int getId() {
        return id;
    }


    public String getYear_month() {
        return year_month;
    }

    public String getFile_path() {
        return file_path;
    }

    public String getFile_url() {
        return file_url;
    }
}
