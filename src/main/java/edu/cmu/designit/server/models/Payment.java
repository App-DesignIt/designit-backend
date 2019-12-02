package edu.cmu.designit.server.models;

import java.util.Date;

public class Payment {
    private String id;
    private String userId;
    private Double amount;
    private int status; // 0 for not paid, 1 for paid
    private String recruiterId;
    private String challengeId;
    private String submissionId;
    private Date createTime;
    private Date modifyTime;


    public Payment(String id, String userId, Double amount, int status, String recruiterId, String challengeId,
                   String submissionId, Date createTime, Date modifyTime) {
        this.id = id;
        this.userId = userId;
        this.amount = amount;
        this.status = status;
        this.recruiterId = recruiterId;
        this.challengeId = challengeId;
        this.submissionId = submissionId;
        this.createTime = createTime;
        this.modifyTime = modifyTime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRecruiterId() {
        return recruiterId;
    }

    public void setRecruiterId(String recruiterId) {
        this.recruiterId = recruiterId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getChallengeId() {
        return challengeId;
    }

    public void setChallengeId(String challengeId) {
        this.challengeId = challengeId;
    }

    public String getSubmissionId() {
        return submissionId;
    }

    public void setSubmissionId(String submissionId) {
        this.submissionId = submissionId;
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


