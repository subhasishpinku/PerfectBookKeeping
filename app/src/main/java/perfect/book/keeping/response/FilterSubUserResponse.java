package perfect.book.keeping.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class FilterSubUserResponse {

    public class Datum {

        @SerializedName("id")
        @Expose
        private Integer id;
        @SerializedName("name")
        @Expose
        private String name;
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


    public class UserCompanyPermission {

        @SerializedName("subuser_name")
        @Expose
        private String subuserName;

        public String getSubuserName() {
            return subuserName;
        }

        public void setSubuserName(String subuserName) {
            this.subuserName = subuserName;
        }

    }
}
