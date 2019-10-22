package edu.cmu.designit.server.models;

import java.util.Calendar;
import java.util.Date;

public class Like {
    private String id;
    private String draftId;
    private String ownerId;
    private String likerId;
    private Date date;

    public Like(String id, String draftId, String ownerId, String likerId, Date date) {
        this.id = id;
        this.draftId = draftId;
        this.ownerId = ownerId;
        this.likerId = likerId;
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDraftId() {
        return draftId;
    }

    public void setDraftId(String draftId) {
        this.draftId = draftId;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getLikerId() {
        return likerId;
    }

    public void setLikerId(String likerId) {
        this.likerId = likerId;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
