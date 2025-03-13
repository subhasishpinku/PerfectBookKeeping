package perfect.book.keeping.model;

public class CardList {
    String card_number;
    String card_holder_name;

    int card_id;

    int cardType;

    String expiry_date;

    public CardList(String card_number, String card_holder_name, int card_id, int cardType, String expiry_date) {
        this.card_number = card_number;
        this.card_holder_name = card_holder_name;
        this.card_id = card_id;
        this.cardType = cardType;
        this.expiry_date = expiry_date;
    }


    public String getCard_number() {
        return card_number;
    }

    public String getCard_holder_name() {
        return card_holder_name;
    }

    public int getCard_id() {
        return card_id;
    }

    public int getCardType() {
        return cardType;
    }

    public String getExpiry_date() {
        return expiry_date;
    }
}
