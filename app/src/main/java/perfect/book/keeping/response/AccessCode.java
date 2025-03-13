package perfect.book.keeping.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AccessCode {

    public class Data {

        @SerializedName("access_code")
        @Expose
        private String accessCode;


        @SerializedName("user_type")
        @Expose
        private int user_type;

        public String getAccessCode() {
            return accessCode;
        }

        public void setAccessCode(String accessCode) {
            this.accessCode = accessCode;
        }

        public int getUser_type() {
            return user_type;
        }

        public void setUser_type(int user_type) {
            this.user_type = user_type;
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
