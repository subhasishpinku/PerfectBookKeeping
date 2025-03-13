package perfect.book.keeping.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class BookKeeperResponse {

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
        @SerializedName("user_company_permissions")
        @Expose
        private UserCompanyPermissions userCompanyPermissions;
        @SerializedName("user_image")
        @Expose
        private UserImage userImage;

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

        public UserCompanyPermissions getUserCompanyPermissions() {
            return userCompanyPermissions;
        }

        public void setUserCompanyPermissions(UserCompanyPermissions userCompanyPermissions) {
            this.userCompanyPermissions = userCompanyPermissions;
        }

        public UserImage getUserImage() {
            return userImage;
        }

        public void setUserImage(UserImage userImage) {
            this.userImage = userImage;
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

        @SerializedName("id")
        @Expose
        private Integer id;
        @SerializedName("name")
        @Expose
        private String name;

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

    }

    public class UserCompanyPermissions {

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
        @SerializedName("role")
        @Expose
        private Role role;

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

        public Role getRole() {
            return role;
        }

        public void setRole(Role role) {
            this.role = role;
        }

    }

    public class UserImage {

        @SerializedName("thumbnail")
        @Expose
        private String thumbnail;
        @SerializedName("original")
        @Expose
        private String original;

        public String getThumbnail() {
            return thumbnail;
        }

        public void setThumbnail(String thumbnail) {
            this.thumbnail = thumbnail;
        }

        public String getOriginal() {
            return original;
        }

        public void setOriginal(String original) {
            this.original = original;
        }

    }
}
