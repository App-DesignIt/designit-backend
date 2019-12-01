package edu.cmu.designit.server.managers;

import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import edu.cmu.designit.server.exceptions.AppException;
import edu.cmu.designit.server.exceptions.AppInternalServerException;
import edu.cmu.designit.server.models.Challenge;
import edu.cmu.designit.server.models.ChallengeSubmission;
import edu.cmu.designit.server.utils.MongoPool;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ChallengeManager extends Manager {
  public static ChallengeManager _self;
  private MongoCollection<Document> challengeCollection;
  private MongoCollection<Document> challengeSubmissionCollection;

  public ChallengeManager() {

    this.challengeCollection = MongoPool.getInstance().getCollection("challenges");
    this.challengeSubmissionCollection = MongoPool.getInstance().getCollection("challenge_submissions");
  }

  public static ChallengeManager getInstance() {
    if (_self == null)
      _self = new ChallengeManager();
    return _self;
  }

  public void createChallenge(Challenge challenge) throws AppException {
    try{
      Document newDoc = new Document()
              .append("name", challenge.getName())
              .append("description", challenge.getDescription())
              .append("recruiterId", challenge.getRecruiterId())
              .append("startTime", challenge.getStartTime())
              .append("endTime", challenge.getEndTime())
              .append("winnerPrize", challenge.getWinnerPrize())
              .append("createTime", new Date())
              .append("modifyTime", new Date());

      if (newDoc != null)
        challengeCollection.insertOne(newDoc);
      else
        throw new AppInternalServerException(0, "Failed to create new challenge");

    }catch(Exception e){
      throw handleException("Create Challenge", e);
    }
  }

  public ArrayList<Challenge> getChallengeById(String id) throws AppException {
    try{
      Bson filter = new Document("_id", new ObjectId(id));
      FindIterable<Document> challengeDocs = challengeCollection.find(filter);
      return convertDocsToArrayList(challengeDocs);
    } catch(Exception e){
      throw handleException("Get Challenge List", e);
    }
  }

  public void updateChallenge(Challenge challenge) throws AppException {
    try {
      Bson filter = new Document("_id", new ObjectId(challenge.getId()));
      Bson newValue = new Document()
              .append("name", challenge.getName())
              .append("description", challenge.getDescription())
              .append("recruiterId", challenge.getRecruiterId())
              .append("startTime", challenge.getStartTime())
              .append("endTime", challenge.getEndTime())
              .append("winnerPrize", challenge.getWinnerPrize())
              .append("modifyTime", new Date());
      Bson updateOperationDocument = new Document("$set", newValue);

      if (newValue != null)
        challengeCollection.updateOne(filter, updateOperationDocument);
      else
        throw new AppInternalServerException(0, "Failed to update challenge details");

    } catch(Exception e) {
      throw handleException("Update Challenge", e);
    }
  }

  public void deleteChallenge(String challengeId) throws AppException {
    try {
      Bson filter = new Document("_id", new ObjectId(challengeId));
      challengeCollection.deleteOne(filter);
    }catch (Exception e){
      throw handleException("Delete Challenge", e);
    }
  }


  public ArrayList<Challenge> getChallengeList() throws AppException {
    try{
      FindIterable<Document> challengeDocs = challengeCollection.find();
      return convertDocsToArrayList(challengeDocs);
    } catch(Exception e){
      throw handleException("Get Draft List", e);
    }
  }

  public ArrayList<Challenge> getChallengeListSorted(String sortby, String direction) throws AppException {
    try {
      BasicDBObject sortParams = new BasicDBObject();
      int directionInt = direction.equals("asc") ? 1 : -1;
      sortParams.put(sortby, directionInt);

      FindIterable<Document> challengeDocs = challengeCollection.find().sort(sortParams);
      return convertDocsToArrayList(challengeDocs);
    } catch (Exception e) {
      throw handleException("Get Challenge List Sorted", e);
    }
  }

  public ArrayList<Challenge> getChallengeListPaginated(Integer offset, Integer count) throws AppException {
    try {
      FindIterable<Document> challengeDocs = challengeCollection.find().skip(offset).limit(count);
      return convertDocsToArrayList(challengeDocs);
    } catch (Exception e) {
      throw handleException("Get Challenge List Paginated", e);
    }
  }

  public ArrayList<Challenge> getChallengeListFiltered(String filter) throws AppException {
    try {
      ArrayList<Challenge> challengeList = new ArrayList<>();
      if(filter.equals("recent")) {
        BasicDBObject sortParams = new BasicDBObject();
        sortParams.put("createTime", -1);
        FindIterable<Document> challengeDocs = challengeCollection.find().sort(sortParams).limit(100);
        challengeList = convertDocsToArrayList(challengeDocs);
      } else {
        FindIterable<Document> challengeDocs = challengeCollection.find();
        challengeList = convertDocsToArrayList(challengeDocs);
      }
      return challengeList;
    } catch (Exception e) {
      throw handleException("Get Challenge List Filtered", e);
    }
  }

  public ArrayList<ChallengeSubmission> calculateAllSubmissionScore(String challengeId) throws AppException {
    try {
      ArrayList<ChallengeSubmission> submissions = ChallengeSubmissionManager.getInstance().getChallengeSubmissionsByChallengeId(challengeId);
      for(ChallengeSubmission submission : submissions) {
        ChallengeSubmissionManager.getInstance().calculateChallengeSubmissionScore(submission.getId());
      }
      return ChallengeSubmissionManager.getInstance().getChallengeSubmissionsByChallengeId(challengeId);
    } catch (Exception e) {
      throw handleException("Calculate all submission score", e);
    }
  }

  public ArrayList<ChallengeSubmission> getWinners(String challengeId) throws AppException {
    try {
      Bson filter = new Document("challengeId", challengeId);
      BasicDBObject sortParams = new BasicDBObject();
      sortParams.put("finalScore", -1);
      FindIterable<Document> challengeSubmissionDocs = challengeSubmissionCollection.find(filter).sort(sortParams);
      ArrayList<ChallengeSubmission> winners = ChallengeSubmissionManager.getInstance().convertDocsToArrayList(challengeSubmissionDocs);
      int winnerNumber = winners.size() >= 3 ? 3 : winners.size();
      for(int i = 1; i <= winnerNumber; i++) {
        winners.get(i - 1).setRanking(i);
        String winnerId = winners.get(i - 1).getId();
        Bson winnerFilter = new Document("_id", new ObjectId(winnerId));
        Bson newValue = new Document().append("ranking", i);
        Bson updateOperationDocument = new Document("$set", newValue);
        challengeSubmissionCollection.updateOne(winnerFilter, updateOperationDocument);
      }
      return winners;
    } catch (Exception e) {
      throw handleException("Get challenge winners", e);
    }
  }

  public ArrayList<Challenge> convertDocsToArrayList(FindIterable<Document> challengeDocs) {
    ArrayList<Challenge> challengeList = new ArrayList<>();
    for(Document challengeDoc: challengeDocs) {
      Challenge challenge = new Challenge(
              challengeDoc.getObjectId("_id").toString(),
              challengeDoc.getString("name"),
              challengeDoc.getString("description"),
              challengeDoc.getString("recruiterId"),
              challengeDoc.getDate("startTime"),
              challengeDoc.getDate("endTime"),
              challengeDoc.getDouble("winnerPrize"),
              challengeDoc.getDate("createTime"),
              challengeDoc.getDate("modifyTime")
      );
      challengeList.add(challenge);
    }
    return challengeList;
  }




}
