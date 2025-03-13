package perfect.book.keeping.model;

public class BusinessDashboardModel {

    int image;

    String name;

    String mode;

    int companyId;

    String companyName;

    int permission;

    int userType;
    int package_id;

    public BusinessDashboardModel(int image, String name, String mode, int companyId, String companyName, int permission, int userType, int package_id) {
        this.image = image;
        this.name = name;
        this.mode = mode;
        this.companyId = companyId;
        this.companyName = companyName;
        this.permission = permission;
        this.userType = userType;
        this.package_id = package_id;
    }


    public int getImage() {
        return image;
    }

    public String getName() {
        return name;
    }

    public String getMode() {
        return mode;
    }

    public int getCompanyId() {
        return companyId;
    }

    public String getCompanyName() {
        return companyName;
    }

    public int getPermission() {
        return permission;
    }

    public int getUserType() {
        return userType;
    }

    public int getPackage_id() {
        return package_id;
    }
}
