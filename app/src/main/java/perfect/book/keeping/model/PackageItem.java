package perfect.book.keeping.model;

public class PackageItem {

    String package_item_name;
    int package_item_id;

    String package_item_description;

    int package_item_price;

    boolean setBackground;

    boolean isOpenToolTip;
    String long_desc;
    String color_code;

    public PackageItem(String package_item_name, int package_item_id, String package_item_description, int package_item_price, String long_desc, String color_code) {
        this.package_item_name = package_item_name;
        this.package_item_id = package_item_id;
        this.package_item_description = package_item_description;
        this.package_item_price = package_item_price;
        this.long_desc = long_desc;
        this.isOpenToolTip = false;
        this.color_code = color_code;
    }

    public String getPackage_item_name() {
        return package_item_name;
    }

    public int getPackage_item_id() {
        return package_item_id;
    }

    public String getPackage_item_description() {
        return package_item_description;
    }

    public int getPackage_item_price() {
        return package_item_price;
    }

    public boolean isSetBackground() {
        return setBackground;
    }

    public void setSetBackground(boolean setBackground) {
        this.setBackground = setBackground;
    }

    public boolean isOpenToolTip() {
        return isOpenToolTip;
    }

    public void setOpenToolTip(boolean openToolTip) {
        isOpenToolTip = openToolTip;
    }

    public String getLong_desc() {
        return long_desc;
    }

    public String getColor_code() {
        return color_code;
    }
}
