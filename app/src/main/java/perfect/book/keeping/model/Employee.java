package perfect.book.keeping.model;


public class Employee {

    String employeeName;

    String employeeEmail;

    int employeeId;
    String image;
    String phone_no;

    public Employee(String employeeName, String employeeEmail, int employeeId, String image, String phone_no) {
        this.employeeName = employeeName;
        this.employeeEmail = employeeEmail;
        this.employeeId = employeeId;
        this.image = image;
        this.phone_no = phone_no;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public String getEmployeeEmail() {
        return employeeEmail;
    }

    public int getEmployeeId() {
        return employeeId;
    }

    public String getImage() {
        return image;
    }

    public String getPhone_no() {
        return phone_no;
    }
}
