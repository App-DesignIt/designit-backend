package edu.cmu.designit.server.models;

import java.util.Date;

public class ChallengeSubmissionRating {
  private String id;
  private String challengeSubmissionId;
  private String recruiterId;
  private Double score;
  private Date createTime;
  private Date modifyTime;

  public ChallengeSubmissionRating(String id, String challengeSubmissionId, String recruiterId, double score, Date createTime, Date modifyTime) {
    this.id = id;
    this.challengeSubmissionId = challengeSubmissionId;
    this.recruiterId = recruiterId;
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

  public String getChallengeSubmissionId() {
    return challengeSubmissionId;
  }

  public void setChallengeSubmissionId(String challengeSubmissionId) {
    this.challengeSubmissionId = challengeSubmissionId;
  }

  public String getRecruiterId() {
    return recruiterId;
  }

  public void setRecruiterId(String recruiterId) {
    this.recruiterId = recruiterId;
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
