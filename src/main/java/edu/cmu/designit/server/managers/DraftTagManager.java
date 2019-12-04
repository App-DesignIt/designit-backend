package edu.cmu.designit.server.managers;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import edu.cmu.designit.server.exceptions.AppException;
import edu.cmu.designit.server.exceptions.AppInternalServerException;
import edu.cmu.designit.server.exceptions.AppNotFoundException;
import edu.cmu.designit.server.models.DraftTag;
import edu.cmu.designit.server.models.Tag;
import edu.cmu.designit.server.utils.MongoPool;
import org.bson.Document;
import org.bson.conversions.Bson;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DraftTagManager extends Manager {
  public static DraftTagManager _self;
  private MongoCollection<Document> draftTagCollection;

  public DraftTagManager() {
    this.draftTagCollection = MongoPool.getInstance().getCollection("draft_tag");
  }

  public static DraftTagManager getInstance(){
    if (_self == null)
      _self = new DraftTagManager();
    return _self;
  }

  public void createDraftTagRelation(String draftId, String tagId) throws AppException {
    try {
      if(DraftManager.getInstance().getDraftById(draftId).size() == 0) {
        throw new AppNotFoundException(404, "The draft doesn't exist");
      }
      if(TagManager.getInstance().getTagById(tagId).size() == 0) {
        throw new AppNotFoundException(404, "The tag doesn't exist");
      }
      if(getDraftTagRelationByDraftIdAndTagId(draftId, tagId).size() != 0) {
        throw new AppInternalServerException(500, "The relation already exists");
      }
      Document newDoc = new Document()
              .append("draftId", draftId)
              .append("tagId", tagId);
      draftTagCollection.insertOne(newDoc);
    } catch (Exception e) {
      throw handleException("Create draft tag relation", e);
    }
  }

  public ArrayList<DraftTag> getDraftTagRelationByDraftIdAndTagId(String draftId, String tagId) {
    Map<String, Object> filterMap = new HashMap<>();
    filterMap.put("draftId", draftId);
    filterMap.put("tagId", tagId);
    Bson filter = new Document(filterMap);
    ArrayList<DraftTag> draftTags = convertDocsToArrayList(draftTagCollection.find(filter));
    return draftTags;
  }

  public ArrayList<Tag> getTagsByDraftId(String draftId) throws AppException {
    try {
      Bson filter = new Document("draftId", draftId);
      ArrayList<DraftTag> draftTagArrayList = convertDocsToArrayList(draftTagCollection.find(filter));
      ArrayList<Tag> tags = new ArrayList<>();
      if(draftTagArrayList.size() == 0) return tags;
      for(DraftTag draftTag : draftTagArrayList) {
        String tagId = draftTag.getTagId();
        Tag tag = TagManager.getInstance().getTagById(tagId).get(0);
        tags.add(tag);
      }
      return tags;
    } catch (Exception e) {
      throw handleException("Get tags by draft id", e);
    }
  }

  public void deleteDraftTagRelation(String draftId, String tagId) throws AppException {
    try {
      if(getDraftTagRelationByDraftIdAndTagId(draftId, tagId).size() == 0) {
        throw new AppInternalServerException(0, "The relation doesn't exist");
      }
      Map<String, Object> filterMap = new HashMap<>();
      filterMap.put("draftId", draftId);
      filterMap.put("tagId", tagId);
      Bson filter = new Document(filterMap);
      draftTagCollection.deleteOne(filter);
    } catch (Exception e) {
      throw handleException("Delete draft tag relation", e);
    }
  }

  public ArrayList<DraftTag> convertDocsToArrayList(FindIterable<Document> draftTagDocs) {
    ArrayList<DraftTag> draftTagArrayList = new ArrayList<>();
    for(Document draftTagDoc: draftTagDocs) {
      DraftTag draftTag = new DraftTag (
              draftTagDoc.getObjectId("_id").toString(),
              draftTagDoc.getString("draftId"),
              draftTagDoc.getString("tagId")
      );
      draftTagArrayList.add(draftTag);
    }
    return draftTagArrayList;
  }

}
