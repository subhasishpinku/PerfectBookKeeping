package perfect.book.keeping.global;

public class PermissionData {

    private static PermissionData instance;
    private int globalValue;

    private PermissionData() {
        // Private constructor to prevent instantiation
    }

    public static synchronized PermissionData getInstance() {
        if (instance == null) {
            instance = new PermissionData();
        }
        return instance;
    }


    public int getPermission() {
        return globalValue;
    }

    public void setPermission(int value) {
        globalValue = value;
    }

}
