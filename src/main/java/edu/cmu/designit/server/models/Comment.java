package edu.cmu.designit.server.models;

import java.util.Date;

public class Comment {
    private String id;
    private String draftId;
    private String userId;
    private String content;
    private Date createTime;
    private Date modifyTime;

    public Comment(String id, String draftId, String userId, String content, Date createTime, Date modifyTime) {
        this.id = id;
        this.draftId = draftId;
        this.userId = userId;
        this.content = content;
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
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
