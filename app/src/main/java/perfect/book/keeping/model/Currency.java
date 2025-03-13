package perfect.book.keeping.model;

public class Currency {

    String symbol;
    String currency_name;

    public Currency(String symbol, String currency_name) {
        this.symbol = symbol;
        this.currency_name = currency_name;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getCurrency_name() {
        return currency_name;
    }
}
