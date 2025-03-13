package perfect.book.keeping.model;

public class SearchUserNameEmail {
    String user_id;
    String user_name;
    String user_email;
    String user_prefer_name;

    public SearchUserNameEmail(String user_id, String user_name, String user_email, String user_prefer_name) {
        this.user_id = user_id;
        this.user_name = user_name;
        this.user_email = user_email;
        this.user_prefer_name = user_prefer_name;
    }

    public String getUser_id() {
        return user_id;
    }

    public String getUser_name() {
        return user_name;
    }

    public String getUser_email() {
        return user_email;
    }

    public String getUser_prefer_name() {
        return user_prefer_name;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public void setUser_email(String user_email) {
        this.user_email = user_email;
    }

    public void setUser_prefer_name(String user_prefer_name) {
        this.user_prefer_name = user_prefer_name;
    }
}
