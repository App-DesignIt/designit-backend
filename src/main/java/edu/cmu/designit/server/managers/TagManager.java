package edu.cmu.designit.server.managers;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import edu.cmu.designit.server.exceptions.AppException;
import edu.cmu.designit.server.exceptions.AppInternalServerException;
import edu.cmu.designit.server.models.Draft;
import edu.cmu.designit.server.models.DraftTag;
import edu.cmu.designit.server.models.Tag;
import edu.cmu.designit.server.models.User;
import edu.cmu.designit.server.utils.MongoPool;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class TagManager extends Manager{
    private static TagManager _self;
    private MongoCollection<Document> tagCollection;
    private MongoCollection<Document> draftTagCollection;

    private TagManager() {
        this.tagCollection = MongoPool.getInstance().getCollection("tags");
        this.draftTagCollection = MongoPool.getInstance().getCollection("draft_tag");
    }

    public static TagManager getInstance(){
        if (_self == null)
            _self = new TagManager();
        return _self;
    }

    public void createTag(Tag tag) throws AppException {
        try {
            Document newDoc = new Document()
                    .append("tagName", tag.getTagName())
                    .append("tagCount", tag.getTagCount());
            if (newDoc != null) {
                tagCollection.insertOne(newDoc);
            }
            else {
                throw new AppInternalServerException(0, "Failed to create new tag");
            }
        } catch (Exception e) {
            throw handleException("Create Tag", e);
        }
    }

    public void updateTag(Tag tag) throws AppException {
        try {
            Bson filter = new Document("tagName", tag.getTagName());
            Bson newValue = new Document()
                    .append("tagName", tag.getTagName())
                    .append("tagCount", tag.getTagCount());
            Bson updateOperationDocument = new Document("$set", newValue);

            if (newValue != null)
                tagCollection.updateOne(filter, updateOperationDocument);
            else
                throw new AppInternalServerException(0, "Failed to update tag details");

        } catch(Exception e) {
            throw handleException("Update Tag", e);
        }
    }

    public void increaseTag(Tag tag) throws AppException {
        try {
            String tagName = tag.getTagName();
            ArrayList<Tag> tagList = getTagByName(tagName);

            Bson filter = new Document("tagName", tag.getTagName());
            //Increase the tag count by 1, need to read from db first
            Bson newValue = new Document()
                    .append("tagName", tagName)
                    .append("tagCount", tagList.get(0).getTagCount() + 1);
            Bson updateOperationDocument = new Document("$set", newValue);

            if (newValue != null)
                tagCollection.updateOne(filter, updateOperationDocument);
            else
                throw new AppInternalServerException(0, "Failed to update tag details");

        } catch (Exception e) {
            throw handleException("Update Tag", e);
        }
    }

    public void deleteTag(String tagName) throws AppException {
        try {
            Bson filter = new Document("tagName", tagName);
            tagCollection.deleteOne(filter);
        } catch (Exception e){
            throw handleException("Delete tagName", e);
        }
    }

    public ArrayList<Tag> getTagList() throws AppException {
        try{
            ArrayList<Tag> userList = new ArrayList<>();
            FindIterable<Document> tagDocs = tagCollection.find();
            for(Document tagDoc: tagDocs) {
                Tag tag = new Tag(
                        tagDoc.getObjectId("_id").toString(),
                        tagDoc.getString("tagName"),
                        tagDoc.getInteger("tagCount")
                );
                userList.add(tag);
            }
            return new ArrayList<>(userList);
        } catch(Exception e){
            throw handleException("Get Tag List", e);
        }
    }

    public ArrayList<Tag> getTagByName(String tagName) throws AppException {
        try{
            ArrayList<Tag> tagList = new ArrayList<>();
            FindIterable<Document> tagDocs = tagCollection.find();
            for(Document tagDoc: tagDocs) {
                if(tagDoc.getString("tagName").equals(tagName)) {
                    Tag tag = new Tag(
                            tagDoc.getObjectId("_id").toString(),
                            tagDoc.getString("tagName"),
                            tagDoc.getInteger("tagCount")
                    );
                    tagList.add(tag);
                }
            }
            return new ArrayList<>(tagList);
        } catch(Exception e){
            throw handleException("Get Tag List", e);
        }
    }

    public ArrayList<Tag> getTagById(String tagId) throws AppException {
        try{
            Bson filter = new Document("_id", new ObjectId(tagId));
            FindIterable<Document> tagDocs = tagCollection.find(filter);
            return convertDocsToArrayList(tagDocs);
        } catch(Exception e){
            throw handleException("Get User List", e);
        }
    }

    public ArrayList<Draft> getDraftsByTagId(String tagId) throws AppException {
        try{
            Bson filter = new Document("tagId", tagId);
            FindIterable<Document> draftTagDocs = draftTagCollection.find(filter);
            ArrayList<DraftTag> draftTags = DraftTagManager.getInstance().convertDocsToArrayList(draftTagDocs);
            ArrayList<Draft> drafts = new ArrayList<>();
            for(DraftTag draftTag : draftTags ) {
                String draftId = draftTag.getDraftId();
                Draft draft = DraftManager.getInstance().getDraftById(draftId).get(0);
                drafts.add(draft);
            }
            return drafts;
        } catch (Exception e) {
            throw handleException("Get Drafts By Tag Id", e);
        }
    }

    private ArrayList<Tag> convertDocsToArrayList(FindIterable<Document> tagDocs) {
        ArrayList<Tag> tagArrayList = new ArrayList<>();
        for(Document tagDoc: tagDocs) {
            Tag tag = new Tag(
                    tagDoc.getObjectId("_id").toString(),
                    tagDoc.getString("tagName"),
                    tagDoc.getInteger("tagCount")
            );
            tagArrayList.add(tag);
        }
        return tagArrayList;
    }


}
