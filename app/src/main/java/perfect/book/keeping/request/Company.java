package perfect.book.keeping.request;

public class Company {

    String name;
    String govt_id;
    int package_id;
    String date_format;
    String currency;
    public Company(String name, String govt_id, int package_id, String date_format, String currency) {
        this.name = name;
        this.govt_id = govt_id;
        this.package_id = package_id;
        this.date_format = date_format;
        this.currency = currency;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGovt_id() {
        return govt_id;
    }

    public void setGovt_id(String govt_id) {
        this.govt_id = govt_id;
    }

    public int getPackage_id() {
        return package_id;
    }

    public void setPackage_id(int package_id) {
        this.package_id = package_id;
    }

    public String getDate_format() {
        return date_format;
    }

    public void setDate_format(String date_format) {
        this.date_format = date_format;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }
}
