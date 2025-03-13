package perfect.book.keeping.request;

public class ModifyCompanies {
    String name;
    String govt_id;
    String watermark_text;
    String date_format;
    String currency;
    int is_govt_id_show;

    public ModifyCompanies(String name, String govt_id, String watermark_text, String date_format, String currency, int is_govt_id_show) {
        this.name = name;
        this.govt_id = govt_id;
        this.watermark_text = watermark_text;
        this.date_format = date_format;
        this.currency = currency;
        this.is_govt_id_show = is_govt_id_show;
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

    public String getWatermark_text() {
        return watermark_text;
    }

    public void setWatermark_text(String watermark_text) {
        this.watermark_text = watermark_text;
    }

    public String getDate_format() {
        return date_format;
    }

    public void setDate_format(String date_format) {
        this.date_format = date_format;
    }

    public int getIs_govt_id_show() {
        return is_govt_id_show;
    }

    public void setIs_govt_id_show(int is_govt_id_show) {
        this.is_govt_id_show = is_govt_id_show;
    }
}
