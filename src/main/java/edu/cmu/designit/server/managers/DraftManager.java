package edu.cmu.designit.server.managers;

import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Sorts;
import edu.cmu.designit.server.exceptions.AppException;
import edu.cmu.designit.server.exceptions.AppInternalServerException;
import edu.cmu.designit.server.models.Draft;
import edu.cmu.designit.server.utils.MongoPool;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.sql.Array;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;

public class DraftManager extends Manager {
  public static DraftManager _self;
  private MongoCollection<Document> draftCollection;

  public DraftManager() {
    this.draftCollection = MongoPool.getInstance().getCollection("drafts");
  }

  public static DraftManager getInstance(){
    if (_self == null)
      _self = new DraftManager();
    return _self;
  }

  public void createDraft(Draft draft) throws AppException {
    try{
      Document newDoc = new Document()
              .append("title", draft.getTitle())
              .append("description", draft.getDescription())
              .append("imageUrl", draft.getImageUrl())
              .append("likedCount", 0)
              .append("viewNumber", 0)
              .append("userScore", 0.0)
              .append("createTime", new Date())
              .append("modifyTime", new Date());

      if (newDoc != null)
        draftCollection.insertOne(newDoc);
      else
        throw new AppInternalServerException(0, "Failed to create new draft");

    }catch(Exception e){
      throw handleException("Create Draft", e);
    }
  }



  public void updateDraft(Draft draft) throws AppException {
    try {
      Bson filter = new Document("_id", new ObjectId(draft.getId()));
      Bson newValue = new Document()
              .append("title", draft.getTitle())
              .append("description", draft.getDescription())
              .append("likedCount", draft.getLikedCount())
              .append("viewNumber", draft.getViewNumber())
              .append("userScore", draft.getUserScore())
              .append("createTime", draft.getCreateTime())
              .append("modifyTime", new Date());
      Bson updateOperationDocument = new Document("$set", newValue);

      if (newValue != null)
        draftCollection.updateOne(filter, updateOperationDocument);
      else
        throw new AppInternalServerException(0, "Failed to update draft details");

    } catch(Exception e) {
      throw handleException("Update Draft", e);
    }
  }

  public void deleteDraft(String draftId) throws AppException {
    try {
      Bson filter = new Document("_id", new ObjectId(draftId));
      draftCollection.deleteOne(filter);
    }catch (Exception e){
      throw handleException("Delete Draft", e);
    }
  }

  public ArrayList<Draft> getDraftList() throws AppException {
    try{
      ArrayList<Draft> draftList = new ArrayList<>();
      FindIterable<Document> draftDocs = draftCollection.find();
      for(Document draftDoc: draftDocs) {
        Draft draft = new Draft(
                draftDoc.getObjectId("_id").toString(),
                draftDoc.getString("title"),
                draftDoc.getString("description"),
                draftDoc.getString("imageUrl"),
                draftDoc.getInteger("likedCount"),
                draftDoc.getInteger("viewNumber"),
                draftDoc.getDouble("userScore"),
                draftDoc.getDate("createTime"),
                draftDoc.getDate("modifyTime")
        );
        draftList.add(draft);
      }
      return new ArrayList<>(draftList);
    } catch(Exception e){
      throw handleException("Get Draft List", e);
    }
  }

  public ArrayList<Draft> getDraftListSorted(String sortby, String direction) throws AppException {
    try {
      ArrayList<Draft> draftList = new ArrayList<>();
      BasicDBObject  sortParams = new BasicDBObject();
      int directionInt = direction.equals("asc") ? 1 : -1;
      sortParams.put(sortby, directionInt);

      FindIterable<Document> draftDocs = draftCollection.find().sort(sortParams);
      for(Document draftDoc: draftDocs) {
        Draft draft = new Draft(
                draftDoc.getObjectId("_id").toString(),
                draftDoc.getString("title"),
                draftDoc.getString("description"),
                draftDoc.getString("imageUrl"),
                draftDoc.getInteger("likedCount"),
                draftDoc.getInteger("viewNumber"),
                draftDoc.getDouble("userScore"),
                draftDoc.getDate("createTime"),
                draftDoc.getDate("modifyTime")
        );
        draftList.add(draft);
      }
      return draftList;
    } catch (Exception e) {
      throw handleException("Get Draft List Sorted", e);
    }
  }

  public ArrayList<Draft> getDraftListPaginated(Integer offset, Integer count) throws AppException {
    try {
      ArrayList<Draft> draftList = new ArrayList<>();
      BasicDBObject sortParams = new BasicDBObject();
      sortParams.put("title", -1);
      FindIterable<Document> draftDocs = draftCollection.find().sort(sortParams).skip(offset).limit(count);
      for(Document draftDoc: draftDocs) {
        Draft draft = new Draft(
                draftDoc.getObjectId("_id").toString(),
                draftDoc.getString("title"),
                draftDoc.getString("description"),
                draftDoc.getString("imageUrl"),
                draftDoc.getInteger("likedCount"),
                draftDoc.getInteger("viewNumber"),
                draftDoc.getDouble("userScore"),
                draftDoc.getDate("createTime"),
                draftDoc.getDate("modifyTime")
        );
        draftList.add(draft);
      }
      return draftList;
    } catch (Exception e) {
      throw handleException("Get Draft List Paginated", e);
    }
  }

  public ArrayList<Draft> getDraftListFiltered(String filter) throws AppException {
    try {
      ArrayList<Draft> draftList = new ArrayList<>();
      if(filter.equals("recent")) {

      }
      if(filter.equals("featured")) {

      }
      return draftList;
    } catch (Exception e) {
      throw handleException("Get Draft List Filtered", e);
    }
  }


  public ArrayList<Draft> getDraftById(String id) throws AppException {
    try{
      ArrayList<Draft> draftList = new ArrayList<>();
      FindIterable<Document> draftDocs = draftCollection.find();
      for(Document draftDoc: draftDocs) {
        if(draftDoc.getObjectId("_id").toString().equals(id)) {
          Draft draft = new Draft(
                  draftDoc.getObjectId("_id").toString(),
                  draftDoc.getString("title"),
                  draftDoc.getString("description"),
                  draftDoc.getString("imageUrl"),
                  draftDoc.getInteger("likedCount"),
                  draftDoc.getInteger("viewNumber"),
                  draftDoc.getDouble("userScore"),
                  draftDoc.getDate("createTime"),
                  draftDoc.getDate("modifyTime")
          );
          draftList.add(draft);
        }
      }
      return new ArrayList<>(draftList);
    } catch(Exception e){
      throw handleException("Get Draft List", e);
    }
  }


}
