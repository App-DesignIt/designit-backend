package edu.cmu.designit.server.managers;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import edu.cmu.designit.server.exceptions.AppException;
import edu.cmu.designit.server.exceptions.AppInternalServerException;
import edu.cmu.designit.server.models.ChallengeSubmission;
import edu.cmu.designit.server.models.ChallengeSubmissionRating;
import edu.cmu.designit.server.models.Draft;
import edu.cmu.designit.server.models.Rating;
import edu.cmu.designit.server.utils.MongoPool;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.glassfish.jersey.model.internal.RankedComparator;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ChallengeSubmissionRatingManager extends Manager {
  private static ChallengeSubmissionRatingManager _self;
  private MongoCollection<Document> challengeSubmissionRatingCollection;
  private MongoCollection<Document> challengeSubmissionCollection;


  private ChallengeSubmissionRatingManager() {
    this.challengeSubmissionRatingCollection = MongoPool.getInstance().getCollection("challenge_submission_ratings");
    this.challengeSubmissionCollection = MongoPool.getInstance().getCollection("challenge_submissions");
  }

  public static ChallengeSubmissionRatingManager getInstance(){
    if (_self == null)
      _self = new ChallengeSubmissionRatingManager();
    return _self;
  }

  public ArrayList<ChallengeSubmissionRating> getChallengeSubmissionRatingList() throws AppException {
    try {
      return convertDocsToArrayList(challengeSubmissionRatingCollection.find());
    } catch(Exception e) {
      throw handleException("Get Challenge Submission Rating List", e);
    }
  }

  public void createChallengeSubmissionRating(ChallengeSubmissionRating rating) throws AppException {
    try {
      String challengeSubmissionId = rating.getChallengeSubmissionId();
      String recruiterId = rating.getRecruiterId();
      double score = rating.getScore();
      Document newDoc = new Document()
              .append("challengeSubmissionId", challengeSubmissionId)
              .append("recruiterId", recruiterId)
              .append("score", score)
              .append("createTime", rating.getCreateTime())
              .append("modifyTime", rating.getModifyTime());
      if (newDoc != null) {
        challengeSubmissionRatingCollection.insertOne(newDoc);
        Bson filter = new Document("_id", new ObjectId(challengeSubmissionId));
        Bson newValue = new Document().append("recruiterScore", score);
        challengeSubmissionCollection.updateOne(filter,  new Document("$set", newValue));
      } else {
        throw new AppInternalServerException(0, "Failed to create new challenge submission rating");
      }
    } catch (Exception e) {
      throw handleException("Create challenge submission rating", e);
    }
  }

  public ArrayList<ChallengeSubmissionRating> getRatingByRecruiterIdAndSubmissionId(String recruiterId, String challengeSubmissionId) throws AppException {
    try {
      Map<String, Object> filterMap = new HashMap<>();
      filterMap.put("recruiterId", recruiterId);
      filterMap.put("challengeSubmissionId", challengeSubmissionId);
      Bson filter = new Document(filterMap);
      return convertDocsToArrayList(challengeSubmissionRatingCollection.find(filter));
    } catch (Exception e){
      throw handleException("Get Rating List by Recruiter Id and Challenge Submission Id", e);
    }
  }

  public void updateRating(ChallengeSubmissionRating rating) throws AppException {
    try {
      Bson filter = new Document("_id", new ObjectId(rating.getId()));
      double newScore= rating.getScore();
      Date modifyTime = rating.getModifyTime();
      String challengeSubmissionId = rating.getChallengeSubmissionId();
      Bson newValue = new Document().append("score", newScore).append("modifyTime", modifyTime);

      if (newValue != null) {
        challengeSubmissionRatingCollection.updateOne(filter, new Document("$set", newValue));
        Bson challengeSubmissionFilter = new Document("_id", new ObjectId(challengeSubmissionId));
        Bson challengeSubmissionNewValue = new Document().append("recruiterScore", newScore);
        challengeSubmissionCollection.updateOne(challengeSubmissionFilter, new Document("$set", challengeSubmissionNewValue));
      }
      else {
        throw new AppInternalServerException(0, "Failed to update challenge submission rating score");
      }
    } catch (Exception e) {
      throw handleException("Update challenge submission rating", e);
    }
  }

  private ArrayList<ChallengeSubmissionRating> convertDocsToArrayList(FindIterable<Document> ratingDocs) {
    ArrayList<ChallengeSubmissionRating> ratingList = new ArrayList<>();
    for(Document ratingDoc: ratingDocs) {
      ChallengeSubmissionRating rating = new ChallengeSubmissionRating (
              ratingDoc.getObjectId("_id").toString(),
              ratingDoc.getString("challengeSubmissionId"),
              ratingDoc.getString("recruiterId"),
              ratingDoc.getDouble("score"),
              (Date) ratingDoc.get("createTime"),
              (Date) ratingDoc.get("modifyTime")
      );
      ratingList.add(rating);
    }
    return ratingList;
  }
}
