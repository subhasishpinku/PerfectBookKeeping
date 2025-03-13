package perfect.book.keeping.model;

public class CompPermissions {

    int company_id;
    int role_id;
    int amount;
    String currency;

    public CompPermissions(int company_id, int role_id, int amount, String currency) {
        this.company_id = company_id;
        this.role_id = role_id;
        this.amount = amount;
        this.currency = currency;
    }

    public int getCompanyId() {
        return company_id;
    }

    public void setCompanyId(int companyId) {
        this.company_id = companyId;
    }

    public int getPermission() {
        return role_id;
    }

    public void setPermission(int role_id) {
        this.role_id = role_id;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }
}
