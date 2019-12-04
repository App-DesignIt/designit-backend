package edu.cmu.designit.server.managers;

import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import edu.cmu.designit.server.exceptions.AppException;
import edu.cmu.designit.server.exceptions.AppInternalServerException;
import edu.cmu.designit.server.exceptions.AppNotFoundException;
import edu.cmu.designit.server.models.ChallengeSubmission;
import edu.cmu.designit.server.utils.MongoPool;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.Date;

public class ChallengeSubmissionManager extends Manager {
  public static ChallengeSubmissionManager _self;
  private MongoCollection<Document> challengeSubmissionCollection;
  private MongoCollection<Document> draftCollection;

  public ChallengeSubmissionManager() {
    this.challengeSubmissionCollection = MongoPool.getInstance().getCollection("challenge_submissions");
    this.draftCollection = MongoPool.getInstance().getCollection("drafts");
  }

  public static ChallengeSubmissionManager getInstance() {
    if (_self == null)
      _self = new ChallengeSubmissionManager();
    return _self;
  }

  public ChallengeSubmission createChallengeSubmission(ChallengeSubmission challengeSubmission) throws AppException {
    try{
      Document newDoc = new Document()
              .append("ranking", 0)
              .append("draftId", challengeSubmission.getDraftId())
              .append("userId", challengeSubmission.getUserId())
              .append("challengeId", challengeSubmission.getChallengeId())
              .append("submissionTime", new Date())
              .append("recruiterScore", 0.0)
              .append("finalScore", 0.0);

      if (newDoc != null) {
        challengeSubmissionCollection.insertOne(newDoc);
        return convertDocToArrayList(newDoc);
      }
      else {
        throw new AppInternalServerException(0, "Failed to create new challenge submission");
      }
    }catch(Exception e){
      throw handleException("Create Challenge Submission", e);
    }
  }

  public void updateChallengeSubmission(ChallengeSubmission challengeSubmission) throws AppException {
    try {
      Bson filter = new Document("_id", new ObjectId(challengeSubmission.getId()));
      Bson newValue = new Document()
              .append("ranking", challengeSubmission.getRanking())
              .append("draftId", challengeSubmission.getDraftId())
              .append("submissionTime", challengeSubmission.getSubmissionTime())
              .append("recruiterScore", challengeSubmission.getRecruiterScore())
              .append("finalScore", challengeSubmission.getFinalScore());
      Bson updateOperationDocument = new Document("$set", newValue);

      if (newValue != null)
        challengeSubmissionCollection.updateOne(filter, updateOperationDocument);
      else
        throw new AppInternalServerException(0, "Failed to update challenge submission details");

    } catch(Exception e) {
      throw handleException("Update Challenge Submission", e);
    }
  }

  public void deleteChallengeSubmission(String challengeSubmissionId) throws AppException {
    try {
      if(getChallengeSubmissionById(challengeSubmissionId).size() == 0) {
        throw new AppNotFoundException(404, "There is no challengeSubmission with the given id");
      }
      Bson filter = new Document("_id", new ObjectId(challengeSubmissionId));
      challengeSubmissionCollection.deleteOne(filter);
    }catch (Exception e){
      throw handleException("Delete Challenge Submission", e);
    }
  }

  public ArrayList<ChallengeSubmission> getChallengeSubmissionById(String id) throws AppException {
    try{
      if(getChallengeSubmissionById(id).size() == 0) {
        throw new AppNotFoundException(404, "There is no challengeSubmission with the given id");
      }
      Bson filter = new Document("_id", new ObjectId(id));
      FindIterable<Document> challengeSubmissionDocs = challengeSubmissionCollection.find(filter);
      return convertDocsToArrayList(challengeSubmissionDocs);
    } catch(Exception e){
      throw handleException("Get Draft List", e);
    }
  }

  public ArrayList<ChallengeSubmission> getChallengeSubmissionsByChallengeId(String challengeId) throws AppException {
    try {
      if(ChallengeManager.getInstance().getChallengeById(challengeId).size() == 0) {
        throw new AppNotFoundException(404, "There is no challenge with the given id");
      }
      Bson filter = new Document("challengeId", challengeId);
      FindIterable<Document> challengeSubmissionDocs = challengeSubmissionCollection.find(filter);
      return convertDocsToArrayList(challengeSubmissionDocs);
    } catch (Exception e) {
      throw handleException("Get Challenge Submission List by Challenge Id", e);
    }
  }

  public ArrayList<ChallengeSubmission> getChallengeSubmissionsSorted(String sortby, String direction) throws AppException {
    try {
      BasicDBObject sortParams = new BasicDBObject();
      int directionInt = direction.equals("asc") ? 1 : -1;
      sortParams.put(sortby, directionInt);

      FindIterable<Document> challengeSubmissionDocs = challengeSubmissionCollection.find().sort(sortParams);
      return convertDocsToArrayList(challengeSubmissionDocs);
    } catch (Exception e) {
      throw handleException("Get Challenge Submission List Sorted", e);
    }
  }

  public ArrayList<ChallengeSubmission> getChallengeSubmissionListPaginated(Integer offset, Integer count) throws AppException {
    try {
      FindIterable<Document> challengeSubmissionDocs = challengeSubmissionCollection.find().skip(offset).limit(count);
      return convertDocsToArrayList(challengeSubmissionDocs);
    } catch (Exception e) {
      throw handleException("Get Challenge Submission List Paginated", e);
    }
  }

  public ArrayList<ChallengeSubmission> getChallengeSubmissionListFiltered(String filter) throws AppException {
    try {
      ArrayList<ChallengeSubmission> challengeSubmissionList = new ArrayList<>();
      if(filter.equals("recent")) {
        BasicDBObject sortParams = new BasicDBObject();
        sortParams.put("createTime", -1);
        FindIterable<Document> challengeSubmissionDocs = challengeSubmissionCollection.find().sort(sortParams).limit(100);
        challengeSubmissionList = convertDocsToArrayList(challengeSubmissionDocs);
      } else {
        FindIterable<Document> draftDocs = challengeSubmissionCollection.find();
        challengeSubmissionList = convertDocsToArrayList(draftDocs);
      }
      return challengeSubmissionList;
    } catch (Exception e) {
      throw handleException("Get Challenge Submission List Filtered", e);
    }
  }

  public ArrayList<ChallengeSubmission> getChallengeSubmissionList() throws AppException {
    try{
      FindIterable<Document> challengeSubmissionDocs = challengeSubmissionCollection.find();
      return convertDocsToArrayList(challengeSubmissionDocs);
    } catch(Exception e){
      throw handleException("Get Challenge Submission List", e);
    }
  }

  public void calculateChallengeSubmissionScore(String challengeSubmissionId) throws AppException {
    try {
      if(getChallengeSubmissionById(challengeSubmissionId).size() == 0) {
        throw new AppNotFoundException(404, "There is no challengeSubmission with the given id");
      }
      ChallengeSubmission challengeSubmission = getChallengeSubmissionById(challengeSubmissionId).get(0);
      String draftId = challengeSubmission.getDraftId();
      double userScore = DraftManager.getInstance().getDraftById(draftId).get(0).getUserScore();
      double score = 0.4 * userScore + 0.6 * challengeSubmission.getRecruiterScore();
      Bson filter = new Document("_id", new ObjectId(challengeSubmissionId));
      Bson newValue = new Document().append("finalScore", score);
      Bson updateOperationDocument = new Document("$set", newValue);
      if (newValue != null)
        challengeSubmissionCollection.updateOne(filter, updateOperationDocument);
      else
        throw new AppInternalServerException(0, "Failed to calculate challenge submission score");

    } catch (Exception e) {
      throw handleException("Get Challenge Submission Score", e);
    }
  }

  public ChallengeSubmission convertDocToArrayList(Document challengeSubmissionDoc) {
    ChallengeSubmission challengeSubmission = new ChallengeSubmission(
            challengeSubmissionDoc.getObjectId("_id").toString(),
            challengeSubmissionDoc.getInteger("ranking"),
            challengeSubmissionDoc.getString("draftId"),
            challengeSubmissionDoc.getString("userId"),
            challengeSubmissionDoc.getString("challengeId"),
            challengeSubmissionDoc.getDate("submissionTime"),
            challengeSubmissionDoc.getDouble("recruiterScore"),
            challengeSubmissionDoc.getDouble("finalScore")
    );
    return challengeSubmission;
  }


  public ArrayList<ChallengeSubmission> convertDocsToArrayList(FindIterable<Document> challengeSubmissionDocs) {
    ArrayList<ChallengeSubmission> challengeSubmissionList = new ArrayList<>();
    for(Document challengeSubmissionDoc: challengeSubmissionDocs) {
      ChallengeSubmission challengeSubmission = new ChallengeSubmission(
              challengeSubmissionDoc.getObjectId("_id").toString(),
              challengeSubmissionDoc.getInteger("ranking"),
              challengeSubmissionDoc.getString("draftId"),
              challengeSubmissionDoc.getString("userId"),
              challengeSubmissionDoc.getString("challengeId"),
              challengeSubmissionDoc.getDate("submissionTime"),
              challengeSubmissionDoc.getDouble("recruiterScore"),
              challengeSubmissionDoc.getDouble("finalScore")
      );
      challengeSubmissionList.add(challengeSubmission);
    }
    return challengeSubmissionList;
  }

}
