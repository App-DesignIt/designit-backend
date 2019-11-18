package edu.cmu.designit.server.models;

import java.util.Date;

public class Rating {

    private String id;
    private String draftId;
    private String userId;
    private Double score;
    private Date createTime;
    private Date modifyTime;

    public Rating(String id, String draftId, String userId,
                  Double score, Date createTime, Date modifyTime) {
        this.id = id;
        this.draftId = draftId;
        this.userId = userId;
        this.score = score;
        this.createTime = createTime;
        this.modifyTime = modifyTime;
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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(Date modifyTime) {
        this.modifyTime = modifyTime;
    }
}
