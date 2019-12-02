package edu.cmu.designit.server.models;


public class User {
    private String id;
    private String fullName;
    private String userAvatar;
    private String email;
    private int roleId;
    private String phoneNumber;
    private String password;
    private String salt;
    private String companyName;
    private String preferredJob;
    private String bankAccount;

    public User(String id, String fullName,
                String email, int roleId, String password, String salt, String bankAccount) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.roleId = roleId;
        this.password = password;
        this.salt = salt;
        this.bankAccount = bankAccount;
    }

    public User(String id, String fullName, String email, int roleId) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.roleId = roleId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getUserAvatar() {
        return userAvatar;
    }

    public void setUserAvatar(String userAvatar) {
        this.userAvatar = userAvatar;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getRoleId() {
        return roleId;
    }

    public void setRoleId(int roleId) {
        this.roleId = roleId;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public String getBankAccount() {
        return bankAccount;
    }

    public void setBankAccount(String bankAccount) {
        this.bankAccount = bankAccount;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getPreferredJob() {
        return preferredJob;
    }

    public void setPreferredJob(String preferredJob) {
        this.preferredJob = preferredJob;
    }
}
