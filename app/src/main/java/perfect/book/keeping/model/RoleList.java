package perfect.book.keeping.model;

public class RoleList {
    int id;
    String name;

    String DescriptionId;
    public RoleList(int id, String name,String DescriptionId) {
        this.id = id;
        this.name = name;
        this.DescriptionId=DescriptionId;
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

    public String getDescriptionId() {
        return DescriptionId;
    }

    public void setDescriptionId(String descriptionId) {
        DescriptionId = descriptionId;
    }
}
