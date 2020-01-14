package me.yukino.reminderapp.record.vo;

/**
 * @author Yukino Yukinoshita
 */

public class AddRecordVO {

    private String subject;
    private String details;
    private String time;

    public AddRecordVO() {
    }

    public AddRecordVO(String subject, String details, String time) {
        this.subject = subject;
        this.details = details;
        this.time = time;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "AddRecordVO{" +
                "subject='" + subject + '\'' +
                ", details='" + details + '\'' +
                ", time='" + time + '\'' +
                '}';
    }
}
