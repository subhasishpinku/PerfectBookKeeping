package perfect.book.keeping.request;

public class UpdateUserRequest {
    private User user;
    private Address address;

    private Boolean isExisting;

//    public UpdateUserRequest(User user, Address address, Boolean isExisting) {
//        this.user = user;
//        this.address = address;
//        this.isExisting = isExisting;
//    }

    public UpdateUserRequest(User user, Boolean isExisting) {
        this.user = user;
        this.isExisting = isExisting;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

}
