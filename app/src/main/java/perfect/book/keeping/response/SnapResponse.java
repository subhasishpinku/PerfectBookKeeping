package perfect.book.keeping.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SnapResponse {

    public class Data {

        @SerializedName("id")
        @Expose
        private Integer id;
        @SerializedName("thumbnail")
        @Expose
        private String thumbnail;
        @SerializedName("file_name")
        @Expose
        private String file_name;
        @SerializedName("original")
        @Expose
        private String original;
        @SerializedName("link")
        @Expose
        private String link;

        @SerializedName("amount")
        @Expose
        private double amount;

        @SerializedName("created_by")
        @Expose
        private Integer created_by;

        @SerializedName("created_by_name")
        @Expose
        private String created_by_name;

        @SerializedName("created_at")
        @Expose
        private String created_at;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

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

        public String getLink() {
            return link;
        }

        public void setLink(String link) {
            this.link = link;
        }

        public String getFile_name() {
            return file_name;
        }

        public void setFile_name(String file_name) {
            this.file_name = file_name;
        }

        public Integer getCreated_by() {
            return created_by;
        }

        public void setCreated_by(Integer created_by) {
            this.created_by = created_by;
        }

        public String getCreated_by_name() {
            return created_by_name;
        }

        public void setCreated_by_name(String created_by_name) {
            this.created_by_name = created_by_name;
        }

        public String getCreated_at() {
            return created_at;
        }

        public void setCreated_at(String created_at) {
            this.created_at = created_at;
        }

        public double getAmount() {
            return amount;
        }

        public void setAmount(double amount) {
            this.amount = amount;
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
