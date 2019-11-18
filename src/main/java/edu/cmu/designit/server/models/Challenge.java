package edu.cmu.designit.server.models;

import java.util.Date;

public class Challenge {
  private String id;
  private String name;
  private String description;
  private String recruiterId;
  private Date startTime;
  private Date endTime;
  private double winnerPrize;
  private Date createTime;
  private Date modifyTime;

  public Challenge(String name, String description, String recruiterId, Date startTime, Date endTime, double winnerPrize) {
    this.name = name;
    this.description = description;
    this.recruiterId = recruiterId;
    this.startTime = startTime;
    this.endTime = endTime;
    this.winnerPrize = winnerPrize;
  }

  public Challenge(String name, String description, String recruiterId, Date startTime, Date endTime, double winnerPrize, Date createTime, Date modifyTime) {
    this.name = name;
    this.description = description;
    this.recruiterId = recruiterId;
    this.startTime = startTime;
    this.endTime = endTime;
    this.winnerPrize = winnerPrize;
    this.createTime = createTime;
    this.modifyTime = modifyTime;
  }

  public Challenge(String id, String name, String description, String recruiterId, Date startTime, Date endTime, double winnerPrize, Date createTime, Date modifyTime) {
    this.id = id;
    this.name = name;
    this.description = description;
    this.recruiterId = recruiterId;
    this.startTime = startTime;
    this.endTime = endTime;
    this.winnerPrize = winnerPrize;
    this.createTime = createTime;
    this.modifyTime = modifyTime;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getRecruiterId() {
    return recruiterId;
  }

  public void setRecruiterId(String recruiterId) {
    this.recruiterId = recruiterId;
  }

  public Date getStartTime() {
    return startTime;
  }

  public void setStartTime(Date startTime) {
    this.startTime = startTime;
  }

  public Date getEndTime() {
    return endTime;
  }

  public void setEndTime(Date endTime) {
    this.endTime = endTime;
  }

  public double getWinnerPrize() {
    return winnerPrize;
  }

  public void setWinnerPrize(double winnerPrize) {
    this.winnerPrize = winnerPrize;
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
