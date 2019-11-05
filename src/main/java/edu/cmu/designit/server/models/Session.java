package edu.cmu.designit.server.models;

import java.util.UUID;

public class Session {
  private String token = null;

  private String userId = null;

  private String userName = null;

  public Session(User user) throws Exception{
    this.userId = user.getId();
    this.token = UUID.randomUUID().toString();
    this.userName = user.getFullName();
  }

  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }

  public String getUserId() {
    return userId;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }

  public String getUserName() {
    return userName;
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }




}
