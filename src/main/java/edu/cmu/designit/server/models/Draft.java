package edu.cmu.designit.server.models;

import org.bson.types.ObjectId;

import java.sql.Timestamp;

public class Draft {
  private String id;
  private String title;
  private String description;
  private int likedCount;
  private int viewNumber;
  private double userScore;
  private Timestamp createTime;
  private Timestamp modifyTime;

  public Draft(String title, String description) {
    this.title = title;
    this.description = description;
  }

  public Draft(String id, String title, String description) {
    this.id = id;
    this.title = title;
    this.description = description;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public int getLikedCount() {
    return likedCount;
  }

  public void setLikedCount(int likedCount) {
    this.likedCount = likedCount;
  }

  public int getViewNumber() {
    return viewNumber;
  }

  public void setViewNumber(int viewNumber) {
    this.viewNumber = viewNumber;
  }

  public double getUserScore() {
    return userScore;
  }

  public void setUserScore(double userScore) {
    this.userScore = userScore;
  }

  public Timestamp getCreateTime() {
    return createTime;
  }

  public void setCreateTime(Timestamp createTime) {
    this.createTime = createTime;
  }

  public Timestamp getModifyTime() {
    return modifyTime;
  }

  public void setModifyTime(Timestamp modifiedTime) {
    this.modifyTime = modifiedTime;
  }
}
