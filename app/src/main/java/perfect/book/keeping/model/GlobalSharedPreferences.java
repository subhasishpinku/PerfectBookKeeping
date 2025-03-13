package perfect.book.keeping.model;

public class GlobalSharedPreferences {
    private int companyId;
    private int permission;
    private int fileId;
    private String flag;
    private int file_status;
    private String url;
    public GlobalSharedPreferences(int companyId, int permission, int fileId, String flag,int file_status,String url) {
        this.companyId = companyId;
        this.permission = permission;
        this.fileId = fileId;
        this.flag = flag;
        this.file_status=file_status;
        this.url=url;
    }

    public int getCompanyId() {
        return companyId;
    }

    public void setCompanyId(int companyId) {
        this.companyId = companyId;
    }

    public int getPermission() {
        return permission;
    }

    public void setPermission(int permission) {
        this.permission = permission;
    }

    public int getFileId() {
        return fileId;
    }

    public void setFileId(int fileId) {
        this.fileId = fileId;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public int getFile_status() {
        return file_status;
    }

    public void setFile_status(int file_status) {
        this.file_status = file_status;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
