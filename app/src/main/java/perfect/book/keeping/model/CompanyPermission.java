package perfect.book.keeping.model;

public class CompanyPermission {

    int companyId;

    int permission;

    String companyName;

    int amount;
    String currencySymbol;
    String currency;



    public CompanyPermission(int companyId, int permission, String companyName, int amount, String currencySymbol, String currency) {
        this.companyId = companyId;
        this.permission = permission;
        this.companyName = companyName;
        this.amount = amount;
        this.currencySymbol = currencySymbol;
        this.currency = currency;
    }

    public int getCompanyId() {
        return companyId;
    }

    public void setCompanyId(int companyId) {
        this.companyId = companyId;
    }

    public int getPermission() {
        return permission;
    }

    public void setPermission(int permission) {
        this.permission = permission;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getCurrencySymbol() {
        return currencySymbol;
    }

    public void setCurrencySymbol(String currencySymbol) {
        this.currencySymbol = currencySymbol;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }
}
