package edu.cmu.designit.server.models;

import java.util.Date;

public class ChallengeSubmission {
  private String id;
  private int ranking; //-1 for normal user, 0 for not judged, 1 for 1st winner, 2 for 2nd winner, 3 for 3rd winner
  private String draftId;
  private String userId;
  private String challengeId;
  private Date submissionTime;
  private double recruiterScore;
  private double finalScore; //40% user scores + 60% recruiter scores

  public ChallengeSubmission(String id, int ranking, String draftId, String userId, String challengeId, Date submissionTime, double recruiterScore, double finalScore) {
    this.id = id;
    this.ranking = ranking;
    this.draftId = draftId;
    this.userId = userId;
    this.challengeId = challengeId;
    this.submissionTime = submissionTime;
    this.recruiterScore = recruiterScore;
    this.finalScore = finalScore;
  }

  //for creating a new challenge submission
  public ChallengeSubmission(String draftId, String userId, String challengeId) {
    this.draftId = draftId;
    this.userId = userId;
    this.challengeId = challengeId;
  }

  public ChallengeSubmission(int ranking, String draftId, String userId, String challengeId, Date submissionTime, double recruiterScore, double finalScore) {
    this.ranking = ranking;
    this.draftId = draftId;
    this.userId = userId;
    this.challengeId = challengeId;
    this.submissionTime = submissionTime;
    this.recruiterScore = recruiterScore;
    this.finalScore = finalScore;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public int getRanking() {
    return ranking;
  }

  public void setRanking(int ranking) {
    this.ranking = ranking;
  }

  public String getDraftId() {
    return draftId;
  }

  public void setDraftId(String draftId) {
    this.draftId = draftId;
  }

  public String getUserId() { return userId; }

  public void setUserId(String userId) { this.userId = userId; }

  public String getChallengeId() {
    return challengeId;
  }

  public void setChallengeId(String challengeId) {
    this.challengeId = challengeId;
  }

  public Date getSubmissionTime() {
    return submissionTime;
  }

  public void setSubmissionTime(Date submissionTime) {
    this.submissionTime = submissionTime;
  }

  public double getRecruiterScore() {
    return recruiterScore;
  }

  public void setRecruiterScore(double recruiterScore) {
    this.recruiterScore = recruiterScore;
  }

  public double getFinalScore() {
    return finalScore;
  }

  public void setFinalScore(double finalScore) {
    this.finalScore = finalScore;
  }
}
