package perfect.book.keeping.model;

public class UserType {

    int user_type;

    String userTypeName;


    public UserType(int user_type, String userTypeName) {
        this.user_type = user_type;
        this.userTypeName = userTypeName;
    }


    public int getUser_type() {
        return user_type;
    }

    public String getUserTypeName() {
        return userTypeName;
    }
}
