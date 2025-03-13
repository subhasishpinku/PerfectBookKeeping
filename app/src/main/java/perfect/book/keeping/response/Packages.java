package perfect.book.keeping.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Packages {

    public class Datum {

        @SerializedName("id")
        @Expose
        private Integer id;
        @SerializedName("name")
        @Expose
        private String name;
        @SerializedName("subs_packages_data")
        @Expose
        private List<SubsPackagesDatum> subsPackagesData;

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

        public List<SubsPackagesDatum> getSubsPackagesData() {
            return subsPackagesData;
        }

        public void setSubsPackagesData(List<SubsPackagesDatum> subsPackagesData) {
            this.subsPackagesData = subsPackagesData;
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

    public class SubsPackagesDatum {

        @SerializedName("id")
        @Expose
        private Integer id;
        @SerializedName("title")
        @Expose
        private String title;
        @SerializedName("price")
        @Expose
        private Integer price;
        @SerializedName("subs_type_id")
        @Expose
        private Integer subsTypeId;
        @SerializedName("short_desc")
        @Expose
        private String shortDesc;
        @SerializedName("long_desc")
        @Expose
        private String longDesc;

        @SerializedName("color_code")
        @Expose
        private String color_code;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
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

        public Integer getSubsTypeId() {
            return subsTypeId;
        }

        public void setSubsTypeId(Integer subsTypeId) {
            this.subsTypeId = subsTypeId;
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

        public String getColor_code() {
            return color_code;
        }

        public void setColor_code(String color_code) {
            this.color_code = color_code;
        }
    }
}
