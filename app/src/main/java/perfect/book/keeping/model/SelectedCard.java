package perfect.book.keeping.model;

public class SelectedCard {
    int cardType;
    String card_number;
    String card_holder_name;
    String expiry_date;

    public SelectedCard(int cardType, String card_number, String card_holder_name, String expiry_date) {
        this.cardType = cardType;
        this.card_number = card_number;
        this.card_holder_name = card_holder_name;
        this.expiry_date = expiry_date;
    }

    public int getCardType() {
        return cardType;
    }

    public String getCard_number() {
        return card_number;
    }

    public String getCard_holder_name() {
        return card_holder_name;
    }

    public String getExpiry_date() {
        return expiry_date;
    }
}
