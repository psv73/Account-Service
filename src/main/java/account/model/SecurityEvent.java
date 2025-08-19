package account.model;

import account.constant.EventEnum;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class SecurityEvent {

    @Id
    @GeneratedValue
    private Integer id;

    private LocalDateTime date;

    @Enumerated(EnumType.STRING)
    private EventEnum action;

    private String subject;

    private String object;

    private String path;

    public SecurityEvent() {
    }

    public SecurityEvent(LocalDateTime date, EventEnum action, String subject, String object, String path) {
        this.date = date;
        this.action = action;
        this.subject = subject;
        this.object = object;
        this.path = path;
    }

    public Integer getId() {
        return id;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public EventEnum getAction() {
        return action;
    }

    public String getSubject() {
        return subject;
    }

    public String getObject() {
        return object;
    }

    public String getPath() {
        return path;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public void setAction(EventEnum action) {
        this.action = action;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void setObject(String object) {
        this.object = object;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
