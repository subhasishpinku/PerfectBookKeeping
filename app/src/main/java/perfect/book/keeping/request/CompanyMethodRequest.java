package perfect.book.keeping.request;

public class CompanyMethodRequest {
    Card card;
    Billing_Address billing_address;
    public CompanyMethodRequest(Card card, Billing_Address billing_address) {
        this.card = card;
        this.billing_address = billing_address;
    }

    public Card getCard() {
        return card;
    }

    public void setCard(Card card) {
        this.card = card;
    }

    public Billing_Address getBilling_address() {
        return billing_address;
    }

    public void setBilling_address(Billing_Address billing_address) {
        this.billing_address = billing_address;
    }
}
