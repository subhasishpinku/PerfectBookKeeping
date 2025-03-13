package perfect.book.keeping.model;

import java.util.List;

public class PackageModel {

    public enum STATE {
        CLOSED,
        OPENED
    }

    List<PackageItem> packageItemList;
    String package_name;
    boolean isExpendable;

    STATE state = STATE.CLOSED;

    public PackageModel(List<PackageItem> packageItemList, String package_name, boolean isExpendable) {
        this.packageItemList = packageItemList;
        this.package_name = package_name;
        this.isExpendable = isExpendable;
    }

    public List<PackageItem> getPackageItemList() {
        return packageItemList;
    }
    public String getPackage_name() {
        return package_name;
    }

    public boolean isExpendable() {
        return isExpendable;
    }

    public void setExpendable(boolean expendable) {
        isExpendable = expendable;
    }

    public STATE getState() {
        return state;
    }

    public void setState(STATE state) {
        this.state = state;
    }
}
