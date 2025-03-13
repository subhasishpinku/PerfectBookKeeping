package perfect.book.keeping.request;

public class User {
    String name;
    String phone;
    String image;
    String def_lang;

    public User(String name, String phone, String def_lang) {
        this.name = name;
        this.phone = phone;
        this.def_lang = def_lang;
    }

    public String getCompany_name() {
        return name;
    }

    public void setCompany_name(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDef_lang() {
        return def_lang;
    }

    public void setDef_lang(String def_lang) {
        this.def_lang = def_lang;
    }
}
