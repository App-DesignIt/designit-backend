package edu.cmu.designit.server.managers;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import edu.cmu.designit.server.exceptions.AppException;
import edu.cmu.designit.server.exceptions.AppInternalServerException;
import edu.cmu.designit.server.models.Like;
import edu.cmu.designit.server.utils.MongoPool;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.json.JSONObject;

import java.util.*;

public class LikeManager extends Manager {
    public static LikeManager _self;
    private MongoCollection<Document> likeCollection;


    public LikeManager() {
        this.likeCollection = MongoPool.getInstance().getCollection("likes");
    }

    public static LikeManager getInstance(){
        if (_self == null)
            _self = new LikeManager();
        return _self;
    }

    public void createLike(Like like) throws AppException {
        try {
            JSONObject json = new JSONObject(like);

            Document newDoc = new Document()
                    .append("draftId", like.getDraftId())
                    .append("ownerId", like.getOwnerId())
                    .append("likerId", like.getLikerId())
                    .append("date", like.getDate());
            if (newDoc != null) {
                likeCollection.insertOne(newDoc);
            }
            else {
                throw new AppInternalServerException(0, "Failed to create new like relation");
            }
        } catch (Exception e) {
            throw handleException("Create Like", e);
        }
    }

    public ArrayList<Like> getLikeList() throws AppException {
        try{
            ArrayList<Like> likeList = new ArrayList<>();
            FindIterable<Document> likeDocs = likeCollection.find();
            for(Document likeDoc: likeDocs) {
                Like like = new Like(
                        likeDoc.getObjectId("_id").toString(),
                        likeDoc.getString("draftId"),
                        likeDoc.getString("ownerId"),
                        likeDoc.getString("likerId"),
                        (Date) likeDoc.get("date")
                );
                likeList.add(like);
            }
            return new ArrayList<>(likeList);
        } catch(Exception e){
            throw handleException("Get User List", e);
        }
    }

    public ArrayList<Like> getLikebyLiker(String likerId) throws AppException {
        try {
            ArrayList<Like> likeList = new ArrayList<>();
            FindIterable<Document> likeDocs = likeCollection.find();
            for(Document likeDoc: likeDocs) {
                if(likeDoc.getString("likerId").equals(likerId)) {
                    Like like = new Like(
                            likeDoc.getObjectId("_id").toString(),
                            likeDoc.getString("draftId"),
                            likeDoc.getString("ownerId"),
                            likeDoc.getString("likerId"),
                            (Date) likeDoc.get("date")
                    );
                    likeList.add(like);
                }
            }
            return new ArrayList<>(likeList);
        } catch (Exception e){
            throw handleException("Get User List", e);
        }
    }

    public ArrayList<Like> getLikebyDraft(String draftId) throws AppException {
        try {
            ArrayList<Like> likeList = new ArrayList<>();
            FindIterable<Document> likeDocs = likeCollection.find();
            for(Document likeDoc: likeDocs) {
                if(likeDoc.getString("draftId").equals(draftId)) {
                    Like like = new Like(
                            likeDoc.getObjectId("_id").toString(),
                            likeDoc.getString("draftId"),
                            likeDoc.getString("ownerId"),
                            likeDoc.getString("likerId"),
                            (Date) likeDoc.get("date")
                    );
                    likeList.add(like);
                }
            }
            return new ArrayList<>(likeList);
        } catch (Exception e){
            throw handleException("Get User List", e);
        }
    }

    public void deleteLike(String draftId, String likerId) throws AppException {
        try {
            Map<String, Object> filterMap = new HashMap<>();
            filterMap.put("draftId", draftId);
            filterMap.put("likerId", likerId);
            Bson filter = new Document(filterMap);
            likeCollection.deleteOne(filter);
        }catch (Exception e){
            throw handleException("Delete Like", e);
        }
    }
}