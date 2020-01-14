package me.yukino.reminderapp.record.vo;

import java.util.Date;

/**
 * @author Yukino Yukinoshita
 */

public class RecordVO {

    private Date id;
    private String name;
    private String subject;
    private String details;
    private Date time;

    public RecordVO() {
    }

    public RecordVO(Date id, String name, String subject, String details, Date time) {
        this.id = id;
        this.name = name;
        this.subject = subject;
        this.details = details;
        this.time = time;
    }

    public Date getId() {
        return id;
    }

    public void setId(Date id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "RecordVO{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", subject='" + subject + '\'' +
                ", details='" + details + '\'' +
                ", time='" + time + '\'' +
                '}';
    }
}
