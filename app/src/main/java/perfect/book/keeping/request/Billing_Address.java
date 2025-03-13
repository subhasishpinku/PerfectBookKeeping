package perfect.book.keeping.request;

public class Billing_Address {
    String address1;
    String address2;
    String city;
    String country;
    String state;
    String zipcode;
    public Billing_Address(String address1, String address2, String city, String country, String state, String zipcode) {
        this.address1 = address1;
        this.address2 = address2;
        this.city = city;
        this.country = country;
        this.state = state;
        this.zipcode = zipcode;
    }
    public String getAddress1() {
        return address1;
    }

    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    public String getAddress2() {
        return address2;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getZipcode() {
        return zipcode;
    }

    public void setZipcode(String zipcode) {
        this.zipcode = zipcode;
    }
}
