package perfect.book.keeping.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SubPackage {

    public class Data {

        @SerializedName("id")
        @Expose
        private Integer id;
        @SerializedName("subs_type_id")
        @Expose
        private Integer subsTypeId;
        @SerializedName("title")
        @Expose
        private String title;
        @SerializedName("price")
        @Expose
        private Integer price;
        @SerializedName("short_desc")
        @Expose
        private String shortDesc;
        @SerializedName("long_desc")
        @Expose
        private String longDesc;
        @SerializedName("status")
        @Expose
        private Integer status;
        @SerializedName("createdAt")
        @Expose
        private String createdAt;
        @SerializedName("updatedAt")
        @Expose
        private String updatedAt;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public Integer getSubsTypeId() {
            return subsTypeId;
        }

        public void setSubsTypeId(Integer subsTypeId) {
            this.subsTypeId = subsTypeId;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public Integer getPrice() {
            return price;
        }

        public void setPrice(Integer price) {
            this.price = price;
        }

        public String getShortDesc() {
            return shortDesc;
        }

        public void setShortDesc(String shortDesc) {
            this.shortDesc = shortDesc;
        }

        public String getLongDesc() {
            return longDesc;
        }

        public void setLongDesc(String longDesc) {
            this.longDesc = longDesc;
        }

        public Integer getStatus() {
            return status;
        }

        public void setStatus(Integer status) {
            this.status = status;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(String createdAt) {
            this.createdAt = createdAt;
        }

        public String getUpdatedAt() {
            return updatedAt;
        }

        public void setUpdatedAt(String updatedAt) {
            this.updatedAt = updatedAt;
        }

    }

    @SerializedName("data")
    @Expose
    private Data data;
    @SerializedName("message")
    @Expose
    private String message;

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
