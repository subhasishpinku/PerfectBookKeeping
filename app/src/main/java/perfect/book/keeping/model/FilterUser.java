package perfect.book.keeping.model;

public class FilterUser {
    int id;
    String name;
    boolean isSelected;
    String subUser_name;

    public FilterUser(int id, String name, boolean isSelected, String subUser_name) {
        this.id = id;
        this.name = name;
        this.isSelected = isSelected;
        this.subUser_name = subUser_name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public String getSubUser_name() {
        return subUser_name;
    }
}
