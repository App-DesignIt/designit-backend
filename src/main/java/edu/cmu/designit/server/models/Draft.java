package edu.cmu.designit.server.models;

import java.sql.Timestamp;
import java.util.Date;

public class Draft {
  private String id;
  private String title;
  private String description;
  private String imageUrl;
  private int likedCount;
  private int viewNumber;
  private double userScore;
  private Date createTime;
  private Date modifyTime;

  public String getImageUrl() {
    return imageUrl;
  }

  public void setImageUrl(String imageUrl) {
    this.imageUrl = imageUrl;
  }

  //for creating new draft
  public Draft(String title, String description, String imageUrl) {
    this.title = title;
    this.description = description;
    this.imageUrl = imageUrl;
  }

  //for patch
  public Draft(String id, String title, String description, String imageUrl, int likedCount, int viewNumber, double userScore, Date createTime, Date modifyTime) {
    this.id = id;
    this.title = title;
    this.description = description;
    this.imageUrl = imageUrl;
    this.likedCount = likedCount;
    this.viewNumber = viewNumber;
    this.userScore = userScore;
    this.createTime = createTime;
    this.modifyTime = modifyTime;
  }

  public Draft(String id, String title, String description, String imageUrl) {
    this.id = id;
    this.title = title;
    this.description = description;
    this.imageUrl = imageUrl;
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

  public Date getCreateTime() {
    return createTime;
  }

  public void setCreateTime(Date createTime) {
    this.createTime = createTime;
  }

  public Date getModifyTime() {
    return modifyTime;
  }

  public void setModifyTime(Date modifiedTime) {
    this.modifyTime = modifiedTime;
  }
}
