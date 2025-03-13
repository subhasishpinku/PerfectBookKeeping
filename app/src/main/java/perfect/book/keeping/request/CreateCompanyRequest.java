package perfect.book.keeping.request;

public class CreateCompanyRequest {

   Company company;

   Address address;
   Billing_Address billing_address;

   Card card;
   Image image;

    public CreateCompanyRequest(Company company, Address address, Card card, Billing_Address billing_address, Image image) {
        this.company = company;
        this.image = image;
        this.address  =address;
        this.card = card;
        this.billing_address = billing_address;
    }


    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public Billing_Address getBillingAddress() {
        return billing_address;
    }

    public void setBillingAddress(Billing_Address billing_address) {
        this.billing_address = billing_address;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public Card getCard() {
        return card;
    }

    public void setCard(Card card) {
        this.card = card;
    }
}
