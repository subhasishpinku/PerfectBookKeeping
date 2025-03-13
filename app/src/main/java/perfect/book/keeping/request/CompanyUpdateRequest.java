package perfect.book.keeping.request;

public class CompanyUpdateRequest {
    ModifyCompanies company;

    Address address;
    Image image;

    public CompanyUpdateRequest(ModifyCompanies company, Address address, Image image) {
        this.company = company;
        this.address = address;
        this.image = image;
    }

    public ModifyCompanies getCompany() {
        return company;
    }

    public void setCompany(ModifyCompanies company) {
        this.company = company;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

}
