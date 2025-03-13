package perfect.book.keeping.request;

public class Card {
    String card_number;
    String expiry;
    String cvv;
    String card_holder_name;
    int type;

    public Card(String card_number, String expiry, String cvv, String card_holder_name, int type) {
        this.card_number = card_number;
        this.expiry = expiry;
        this.cvv = cvv;
        this.card_holder_name = card_holder_name;
        this.type = type;
    }

    public String getCard_number() {
        return card_number;
    }

    public void setCard_number(String card_number) {
        this.card_number = card_number;
    }

    public String getExpiry() {
        return expiry;
    }

    public void setExpiry(String expiry) {
        this.expiry = expiry;
    }

    public String getCvv() {
        return cvv;
    }

    public void setCvv(String cvv) {
        this.cvv = cvv;
    }

    public String getCard_holder_name() {
        return card_holder_name;
    }

    public void setCard_holder_name(String card_holder_name) {
        this.card_holder_name = card_holder_name;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
