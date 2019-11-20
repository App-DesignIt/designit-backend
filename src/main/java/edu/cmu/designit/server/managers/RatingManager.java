package edu.cmu.designit.server.managers;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import edu.cmu.designit.server.exceptions.AppException;
import edu.cmu.designit.server.exceptions.AppInternalServerException;
import edu.cmu.designit.server.models.Draft;
import edu.cmu.designit.server.models.Rating;
import edu.cmu.designit.server.utils.MongoPool;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class RatingManager extends Manager {
    private static RatingManager _self;
    private MongoCollection<Document> ratingCollection;
    private MongoCollection<Document> draftCollection;


    private RatingManager() {
        this.ratingCollection = MongoPool.getInstance().getCollection("ratings");
        this.draftCollection = MongoPool.getInstance().getCollection("drafts");
    }

    public static RatingManager getInstance(){
        if (_self == null)
            _self = new RatingManager();
        return _self;
    }

    public void createRating(Rating rating) throws AppException {
        try {
            String draftId = rating.getDraftId();
            String userId = rating.getUserId();
            double score = rating.getScore();
            Document newDoc = new Document()
                    .append("draftId", draftId)
                    .append("userId", userId)
                    .append("score", score)
                    .append("createTime", rating.getCreateTime())
                    .append("modifyTime", rating.getModifyTime());
            if (newDoc != null) {
                Bson filter = new Document("_id", new ObjectId(draftId));
                Draft draft = DraftManager.getInstance().getDraftById(draftId).get(0);
                int rateNumber = draft.getRateNumber();
                double userScore = draft.getUserScore();
                Bson newValue = new Document().append("userScore", (userScore * rateNumber + score) / (rateNumber + 1)).append("rateNumber", rateNumber + 1);
                draftCollection.updateOne(filter, new Document("$set", newValue));
                ratingCollection.insertOne(newDoc);
            } else {
                throw new AppInternalServerException(0, "Failed to create new rating relation");
            }
        } catch (Exception e) {
            throw handleException("Create rating", e);
        }
    }

    public ArrayList<Rating> getRatingList() throws AppException {
        try {
            return convertDocsToArrayList(ratingCollection.find());
        } catch(Exception e) {
            throw handleException("Get Rating List", e);
        }
    }

    public ArrayList<Rating> getRatingById(String id) throws AppException {
        try {
            Bson filter = new Document("_id", new ObjectId(id));
            return convertDocsToArrayList(ratingCollection.find(filter));
        } catch (Exception e){
            throw handleException("Get Rating List by rating Id", e);
        }
    }

    public ArrayList<Rating> getRatingByDraft(String draftId) throws AppException {
        try {
            Bson filter = new Document("draftId", draftId);
            return convertDocsToArrayList(ratingCollection.find(filter));
        } catch (Exception e){
            throw handleException("Get Rating List by draftId", e);
        }
    }

    public ArrayList<Rating> getRatingByUser(String userId) throws AppException {
        try {
            Bson filter = new Document("userId", userId);
            return convertDocsToArrayList(ratingCollection.find(filter));
        } catch (Exception e){
            throw handleException("Get Rating List by userId", e);
        }
    }

    public ArrayList<Rating> getRatingByUserAndDraft(String userId, String draftId) throws AppException {
        try {
            Map<String, Object> filterMap = new HashMap<>();
            filterMap.put("draftId", draftId);
            filterMap.put("userId", userId);
            Bson filter = new Document(filterMap);
            return convertDocsToArrayList(ratingCollection.find(filter));
        } catch (Exception e){
            throw handleException("Get Rating List by userId", e);
        }
    }

    public void updateRating(double originalScore, Rating rating, String draftId) throws AppException {
        try {
            Bson filter = new Document("_id", new ObjectId(rating.getId()));
            double newScore= rating.getScore();
            Bson newValue = new Document()
                    .append("score", newScore)
                    .append("modifyTime", rating.getModifyTime());
            Bson updateOperationDocument = new Document("$set", newValue);

            if (newValue != null) {
                ratingCollection.updateOne(filter, updateOperationDocument);
                Bson draftFilter = new Document("_id", new ObjectId(draftId));
                Draft draft = DraftManager.getInstance().getDraftById(draftId).get(0);
                int rateNumber = draft.getRateNumber();
                double userScore = draft.getUserScore();
                Bson newDraftValue = new Document().append("userScore", (userScore * rateNumber - originalScore + newScore) / rateNumber );
                draftCollection.updateOne(draftFilter, new Document("$set", newDraftValue));
            }
            else {
                throw new AppInternalServerException(0, "Failed to update rating score");
            }
        } catch (Exception e) {
            throw handleException("Update rating", e);
        }
    }


    public void deleteRating(String userId, String draftId) throws AppException {
        try {
            Map<String, Object> filterMap = new HashMap<>();
            filterMap.put("draftId", draftId);
            filterMap.put("userId", userId);
            Bson filter = new Document(filterMap);
            ratingCollection.deleteOne(filter);
        }catch (Exception e){
            throw handleException("Delete rating", e);
        }
    }

    private ArrayList<Rating> convertDocsToArrayList(FindIterable<Document> ratingDocs) {
        ArrayList<Rating> ratingList = new ArrayList<>();
        for(Document ratingDoc: ratingDocs) {
            Rating rating = new Rating (
                    ratingDoc.getObjectId("_id").toString(),
                    ratingDoc.getString("draftId"),
                    ratingDoc.getString("userId"),
                    ratingDoc.getDouble("score"),
                    (Date) ratingDoc.get("createTime"),
                    (Date) ratingDoc.get("modifyTime")
            );
            ratingList.add(rating);
        }
        return ratingList;
    }
}
