package perfect.book.keeping.request;

public class AmountUpdateRequest {
    int company_id;
    int sub_user_id;
    double amount;
    boolean type;

    public AmountUpdateRequest(int company_id, int sub_user_id, double amount, boolean type) {
        this.company_id = company_id;
        this.sub_user_id = sub_user_id;
        this.amount = amount;
        this.type = type;
    }

    public int getCompany_id() {
        return company_id;
    }

    public void setCompany_id(int company_id) {
        this.company_id = company_id;
    }

    public int getSub_user_id() {
        return sub_user_id;
    }

    public void setSub_user_id(int sub_user_id) {
        this.sub_user_id = sub_user_id;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public boolean isType() {
        return type;
    }

    public void setType(boolean type) {
        this.type = type;
    }
}
