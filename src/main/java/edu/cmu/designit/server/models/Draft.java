package edu.cmu.designit.server.models;
import java.util.Date;

public class Draft {
  private String id;
  private String userId;
  private String title;
  private String description;
  private String imageUrl;
  private int likedCount;
  private int rateNumber;
  private int viewNumber;
  private double userScore;
  private Date createTime;
  private Date modifyTime;

  //for creating new draft
  public Draft(String userId, String title, String description, String imageUrl) {
    this.userId = userId;
    this.title = title;
    this.description = description;
    this.imageUrl = imageUrl;
  }

  //for patch
  public Draft(String id, String userId, String title, String description, String imageUrl, int likedCount, int rateNumber, int viewNumber, double userScore, Date createTime, Date modifyTime) {
    this.id = id;
    this.userId = userId;
    this.title = title;
    this.description = description;
    this.imageUrl = imageUrl;
    this.likedCount = likedCount;
    this.rateNumber = rateNumber;
    this.viewNumber = viewNumber;
    this.userScore = userScore;
    this.createTime = createTime;
    this.modifyTime = modifyTime;
  }

  public String getId() {
    return id;
  }

  public String getUserId() {
    return userId;
  }

  public void setUserId(String userId) {
    this.userId = userId;
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

  public String getImageUrl() {
    return imageUrl;
  }

  public void setImageUrl(String imageUrl) {
    this.imageUrl = imageUrl;
  }

  public int getLikedCount() {
    return likedCount;
  }

  public void setLikedCount(int likedCount) {
    this.likedCount = likedCount;
  }

  public int getRateNumber() {
    return rateNumber;
  }

  public void setRateNumber(int rateNumber) {
    this.rateNumber = rateNumber;
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
