package perfect.book.keeping.request;

public class FileUpdateRequest {
    int approval_status;
    int file_id;
    String reason;
    public FileUpdateRequest(int approval_status, int file_id,String reason) {
        this.approval_status = approval_status;
        this.file_id = file_id;
        this.reason=reason;
    }

    public int getApproval_status() {
        return approval_status;
    }

    public void setApproval_status(int approval_status) {
        this.approval_status = approval_status;
    }

    public int getFile_id() {
        return file_id;
    }

    public void setFile_id(int file_id) {
        this.file_id = file_id;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
