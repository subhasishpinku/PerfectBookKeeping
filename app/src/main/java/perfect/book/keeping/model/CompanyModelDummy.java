package perfect.book.keeping.model;

public class CompanyModelDummy {

    String companyName;
    int companyId;
    String logo;
    int permission;

    public CompanyModelDummy(String companyName, int companyId, String logo, int permission) {
        this.companyName = companyName;
        this.companyId = companyId;
        this.logo = logo;
        this.permission = permission;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public int getCompanyId() {
        return companyId;
    }

    public void setCompanyId(int companyId) {
        this.companyId = companyId;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public int getPermission() {
        return permission;
    }

    public void setPermission(int permission) {
        this.permission = permission;
    }
}
