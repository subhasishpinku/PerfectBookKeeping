package perfect.book.keeping.request;

import java.util.List;

import perfect.book.keeping.model.CompPermissions;

public class SubUserModifyRequest {

    int company_id;
    int sub_user_id;
    int role_id;
    String name;

    public SubUserModifyRequest(int company_id, int sub_user_id, int role_id, String name) {
        this.company_id = company_id;
        this.sub_user_id = sub_user_id;
        this.role_id = role_id;
        this.name = name;
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

    public int getRole_id() {
        return role_id;
    }

    public void setRole_id(int role_id) {
        this.role_id = role_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
