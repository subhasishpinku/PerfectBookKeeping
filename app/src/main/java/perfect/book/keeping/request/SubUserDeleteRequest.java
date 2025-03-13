package perfect.book.keeping.request;

public class SubUserDeleteRequest {

    int company_id;
    int sub_user_id;

    public SubUserDeleteRequest(int company_id, int sub_user_id) {
        this.company_id = company_id;
        this.sub_user_id = sub_user_id;
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
}
