package perfect.book.keeping.model;

public class CompaniesModel {

    String companyName;
    int companyId;

    String logo;

    int permission;

    int cardId;
    int sub_type_data_id;
    int package_price;
    int sub_user_price;
    public CompaniesModel(String companyName, int companyId, String logo, int permission, int cardId, int sub_type_data_id, int package_price, int sub_user_price) {
        this.companyName = companyName;
        this.companyId = companyId;
        this.logo = logo;
        this.permission = permission;
        this.cardId = cardId;
        this.sub_type_data_id = sub_type_data_id;
        this.package_price = package_price;
        this.sub_user_price = sub_user_price;
    }

    public String getCompanyName() {
        return companyName;
    }

    public int getCompanyId() {
        return companyId;
    }

    public String getLogo() {
        return logo;
    }

    public int getPermission() {
        return permission;
    }

    public int getCardId() {
        return cardId;
    }

    public int getSub_type_data_id() {
        return sub_type_data_id;
    }

    public int getPackage_price() {
        return package_price;
    }

    public int getSub_user_price() {
        return sub_user_price;
    }
}
