package perfect.book.keeping.request;

import java.util.List;

import perfect.book.keeping.model.CompPermissions;

public class SubUserRequest {
    String email;
    int company_id;
    int role_id;
    String name;

    public SubUserRequest(String email, int company_id, int role_id, String name) {
        this.email = email;
        this.company_id = company_id;
        this.role_id = role_id;
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getCompany_id() {
        return company_id;
    }

    public void setCompany_id(int company_id) {
        this.company_id = company_id;
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
