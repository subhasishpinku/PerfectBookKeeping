package perfect.book.keeping.model;

public class Receipt {
    String  transaction_id;
    String  amount;
    String  createdAt;
    int id;
    int compId;
    String compName;
    int permission;



    public Receipt(String transactionId, String amount, String createdAt, int id, int compId, String compName, int permission) {
        this.transaction_id=transactionId;
        this.amount=amount;
        this.createdAt=createdAt;
        this.id = id;
        this.compId = compId;
        this.compName = compName;
        this.permission  = permission;

    }

    public String getTransaction_id() {
        return transaction_id;
    }

    public String getAmount() {
        return amount;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public int getId() {
        return id;
    }

    public int getCompId() {
        return compId;
    }

    public String getCompName() {
        return compName;
    }

    public int getPermission() {
        return permission;
    }
}
