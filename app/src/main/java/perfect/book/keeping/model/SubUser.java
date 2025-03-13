package perfect.book.keeping.model;

public class SubUser {
    String username;
    String sub_user_email;
    String name;
    int id;
    int companyId;
    String companyName;
    Double amount;
    String currency;
    int permission;
    String sub_user_name;
    String user_rolName;
    int user_role_id;
    String company_currency;
    int addedBy;
    int isLogin;

    public SubUser(String username, String sub_user_email, String name, int user_id, int companyId, String companyName, Double amount, String currency, int permission, String sub_user_name,String user_rolName, int user_role_id, String company_currency,int addedBy,int isLogin) {
        this.username = username;
        this.sub_user_email = sub_user_email;
        this.name = name;
        this.id = user_id;
        this.companyId = companyId;
        this.companyName = companyName;
        this.amount = amount;
        this.currency = currency;
        this.permission = permission;
        this.sub_user_name = sub_user_name;
        this.user_rolName=user_rolName;
        this.user_role_id = user_role_id;
        this.company_currency = company_currency;
        this.addedBy=addedBy;
        this.isLogin=isLogin;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getSub_user_email() {
        return sub_user_email;
    }

    public void setSub_user_email(String sub_user_email) {
        this.sub_user_email = sub_user_email;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCompanyId() {
        return companyId;
    }

    public String getCompanyName() {
        return companyName;
    }

    public Double getAmount() {
        return amount;
    }

    public String getCurrency() {
        return currency;
    }

    public int getPermission() {
        return permission;
    }

    public String getSub_user_name() {
        return sub_user_name;
    }

    public String getUser_rolName() {
        return user_rolName;
    }

    public int getUser_role_id() {
        return user_role_id;
    }

    public String getCompany_currency() {
        return company_currency;
    }

    public int getAddedBy() {
        return addedBy;
    }

    public int getIsLogin() {
        return isLogin;
    }
}
