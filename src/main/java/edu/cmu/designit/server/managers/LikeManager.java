package edu.cmu.designit.server.managers;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import edu.cmu.designit.server.exceptions.AppException;
import edu.cmu.designit.server.exceptions.AppInternalServerException;
import edu.cmu.designit.server.models.Like;
import edu.cmu.designit.server.utils.MongoPool;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.*;

public class LikeManager extends Manager {
    private static LikeManager _self;
    private MongoCollection<Document> likeCollection;


    private LikeManager() {
        this.likeCollection = MongoPool.getInstance().getCollection("likes");
    }

    public static LikeManager getInstance(){
        if (_self == null)
            _self = new LikeManager();
        return _self;
    }

    public void createLike(Like like) throws AppException {
        try {
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
            FindIterable<Document> likeDocs = likeCollection.find();
            return convertDocsToArrayList(likeDocs);
        } catch(Exception e){
            throw handleException("Get User List", e);
        }
    }

    public ArrayList<Like> getLikeByUser(String likerId) throws AppException {
        try {
            Bson filter = new Document("likerId", likerId);
            FindIterable<Document> likeDocs = likeCollection.find(filter);
            return convertDocsToArrayList(likeDocs);
        } catch (Exception e){
            throw handleException("Get LikeByLiker List", e);
        }
    }

    public ArrayList<Like> getLikeByDraft(String draftId) throws AppException {
        try {
            Bson filter = new Document("draftId", draftId);
            FindIterable<Document> likeDocs = likeCollection.find(filter);
            return convertDocsToArrayList(likeDocs);
        } catch (Exception e){
            throw handleException("Get Like List", e);
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

    private ArrayList<Like> convertDocsToArrayList(FindIterable<Document> likeDocs) {
        ArrayList<Like> likeList = new ArrayList<>();
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
        return likeList;
    }
}
