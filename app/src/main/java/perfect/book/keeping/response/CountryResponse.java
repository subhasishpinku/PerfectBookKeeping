package perfect.book.keeping.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CountryResponse {

    public class Country {

        @SerializedName("id")
        @Expose
        private Integer id;
        @SerializedName("iso")
        @Expose
        private String iso;
        @SerializedName("name")
        @Expose
        private String name;
        @SerializedName("nicename")
        @Expose
        private String nicename;
        @SerializedName("iso3")
        @Expose
        private String iso3;
        @SerializedName("numcode")
        @Expose
        private Integer numcode;
        @SerializedName("phonecode")
        @Expose
        private Integer phonecode;
        @SerializedName("deleted_at")
        @Expose
        private Object deletedAt;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getIso() {
            return iso;
        }

        public void setIso(String iso) {
            this.iso = iso;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getNicename() {
            return nicename;
        }

        public void setNicename(String nicename) {
            this.nicename = nicename;
        }

        public String getIso3() {
            return iso3;
        }

        public void setIso3(String iso3) {
            this.iso3 = iso3;
        }

        public Integer getNumcode() {
            return numcode;
        }

        public void setNumcode(Integer numcode) {
            this.numcode = numcode;
        }

        public Integer getPhonecode() {
            return phonecode;
        }

        public void setPhonecode(Integer phonecode) {
            this.phonecode = phonecode;
        }

        public Object getDeletedAt() {
            return deletedAt;
        }

        public void setDeletedAt(Object deletedAt) {
            this.deletedAt = deletedAt;
        }

    }

    public class Data {

        @SerializedName("countries")
        @Expose
        private List<Country> countries;

        public List<Country> getCountries() {
            return countries;
        }

        public void setCountries(List<Country> countries) {
            this.countries = countries;
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
