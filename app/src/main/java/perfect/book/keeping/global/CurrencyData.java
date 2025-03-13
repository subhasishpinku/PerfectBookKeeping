package perfect.book.keeping.global;

public class CurrencyData {


    private static CurrencyData instance;
    private String currencySymbol;

    private String currencyName;

    private CurrencyData() {
        // Private constructor to prevent instantiation
    }

    public static synchronized CurrencyData getInstance() {
        if (instance == null) {
            instance = new CurrencyData();
        }
        return instance;
    }


    public String getCurrency() {
        return currencySymbol;
    }

    public void setCurrency(String currencySymbol) {
        currencySymbol = currencySymbol;
    }

    public String getCurrencyName() {
        return currencyName;
    }

    public void setCurrencyName(String currencyName) {
        this.currencyName = currencyName;
    }
}
