package edu.cmu.designit.server.models;

public class Job {
  private String id;
  private String name;
  private String recruiterId;
  private String companyName;

  public Job(String name, String recruiterId, String companyName, String description, String address) {
    this.name = name;
    this.recruiterId = recruiterId;
    this.companyName = companyName;
    this.description = description;
    this.address = address;
  }

  public Job(String id, String name, String recruiterId, String companyName, String description, String address) {
    this.id = id;
    this.name = name;
    this.recruiterId = recruiterId;
    this.companyName = companyName;
    this.description = description;
    this.address = address;
  }

  private String description;
  private String address;

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

  public String getRecruiterId() {
    return recruiterId;
  }

  public void setRecruiterId(String recruiterId) {
    this.recruiterId = recruiterId;
  }

  public String getCompanyName() {
    return companyName;
  }

  public void setCompanyName(String companyName) {
    this.companyName = companyName;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }
}
