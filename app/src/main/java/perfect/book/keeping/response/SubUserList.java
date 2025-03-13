package perfect.book.keeping.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import perfect.book.keeping.response.CompanyResponse;

public class SubUserList {

    public class Datum {

        @SerializedName("id")
        @Expose
        private Integer id;
        @SerializedName("name")
        @Expose
        private String name;
        @SerializedName("email")
        @Expose
        private String email;
        @SerializedName("phone")
        @Expose
        private String phone;
        @SerializedName("image")
        @Expose
        private Integer image;
        @SerializedName("is_login")
        @Expose
        private Integer isLogin;
        @SerializedName("user_company_permissions")
        @Expose
        private List<UserCompanyPermission> userCompanyPermissions;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public Integer getImage() {
            return image;
        }

        public void setImage(Integer image) {
            this.image = image;
        }

        public Integer getIsLogin() {
            return isLogin;
        }

        public void setIsLogin(Integer isLogin) {
            this.isLogin = isLogin;
        }

        public List<UserCompanyPermission> getUserCompanyPermissions() {
            return userCompanyPermissions;
        }

        public void setUserCompanyPermissions(List<UserCompanyPermission> userCompanyPermissions) {
            this.userCompanyPermissions = userCompanyPermissions;
        }

    }


        @SerializedName("data")
        @Expose
        private List<Datum> data;
        @SerializedName("message")
        @Expose
        private String message;

        public List<Datum> getData() {
            return data;
        }

        public void setData(List<Datum> data) {
            this.data = data;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }


    public class Role {

        @SerializedName("name")
        @Expose
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

    }

    public class UserCompanyPermission {

        @SerializedName("company_id")
        @Expose
        private Integer companyId;
        @SerializedName("role_id")
        @Expose
        private Integer roleId;
        @SerializedName("amount")
        @Expose
        private Double amount;
        @SerializedName("currency")
        @Expose
        private String currency;
        @SerializedName("subuser_name")
        @Expose
        private String subuserName;
        @SerializedName("role")
        @Expose
        private Role role;

        @SerializedName("added_by")
        @Expose
        private Integer addedBy;
        public Integer getCompanyId() {
            return companyId;
        }

        public void setCompanyId(Integer companyId) {
            this.companyId = companyId;
        }

        public Integer getRoleId() {
            return roleId;
        }

        public void setRoleId(Integer roleId) {
            this.roleId = roleId;
        }

        public Double getAmount() {
            return amount;
        }

        public void setAmount(Double amount) {
            this.amount = amount;
        }

        public String getCurrency() {
            return currency;
        }

        public void setCurrency(String currency) {
            this.currency = currency;
        }

        public String getSubuserName() {
            return subuserName;
        }

        public void setSubuserName(String subuserName) {
            this.subuserName = subuserName;
        }

        public Role getRole() {
            return role;
        }

        public void setRole(Role role) {
            this.role = role;
        }

        public Integer getAddedBy() {
            return addedBy;
        }

        public void setAddedBy(Integer addedBy) {
            this.addedBy = addedBy;
        }
    }
}













