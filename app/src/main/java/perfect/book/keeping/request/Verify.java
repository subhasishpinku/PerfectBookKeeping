package perfect.book.keeping.request;

public class Verify {

    String email;

    String mac_address;

    String device_type;


    public Verify(String email, String mac_address, String device_type) {
        this.email = email;
        this.mac_address = mac_address;
        this.device_type = device_type;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMac_address() {
        return mac_address;
    }

    public void setMac_address(String mac_address) {
        this.mac_address = mac_address;
    }

    public String getDevice_type() {
        return device_type;
    }

    public void setDevice_type(String device_type) {
        this.device_type = device_type;
    }

}
