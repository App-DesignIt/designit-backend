package edu.cmu.designit.server.managers;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import edu.cmu.designit.server.exceptions.AppException;
import edu.cmu.designit.server.exceptions.AppInternalServerException;
import edu.cmu.designit.server.models.Comment;
import edu.cmu.designit.server.utils.MongoPool;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import java.util.ArrayList;
import java.util.Date;

public class CommentManager extends Manager{

    private static CommentManager _self;
    private MongoCollection<Document> commentCollection;


    private CommentManager() {
        this.commentCollection = MongoPool.getInstance().getCollection("comments");
    }

    public static CommentManager getInstance(){
        if (_self == null)
            _self = new CommentManager();
        return _self;
    }

    public void createComment(Comment comment) throws AppException {
        try {
            Document newDoc = new Document()
                    .append("draftId", comment.getDraftId())
                    .append("userId", comment.getUserId())
                    .append("content", comment.getContent())
                    .append("createTime", comment.getCreateTime())
                    .append("modifyTime", comment.getModifyTime());
            if (newDoc != null) {
                commentCollection.insertOne(newDoc);
            } else {
                throw new AppInternalServerException(0, "Failed to create new comment relation");
            }
        } catch (Exception e) {
            throw handleException("Create comment", e);
        }
    }

    public ArrayList<Comment> getCommentList() throws AppException {
        try {
            return convertDocsToArrayList(commentCollection.find());
        } catch(Exception e) {
            throw handleException("Get Comment List", e);
        }
    }

    public ArrayList<Comment> getCommentById(String id) throws AppException {
        try {
            Bson filter = new Document("_id", new ObjectId(id));
            return convertDocsToArrayList(commentCollection.find(filter));
        } catch (Exception e){
            throw handleException("Get Comment List", e);
        }
    }

    public ArrayList<Comment> getCommentByDraftId(String draftId) throws AppException {
        try {
            Bson filter = new Document("draftId", draftId);
            return convertDocsToArrayList(commentCollection.find(filter));
        } catch (Exception e){
            throw handleException("Get Comment List", e);
        }
    }

    public ArrayList<Comment> getCommentByUserId(String userId) throws AppException {
        try {
            Bson filter = new Document("userId", userId);
            return convertDocsToArrayList(commentCollection.find(filter));
        } catch (Exception e){
            throw handleException("Get Comment List", e);
        }
    }

    public void updateComment(Comment comment) throws AppException {
        try {
            Bson filter = new Document("_id", new ObjectId(comment.getId()));
            Bson newValue = new Document()
                    .append("content", comment.getContent())
                    .append("modifyTime", comment.getModifyTime());
            Bson updateOperationDocument = new Document("$set", newValue);

            if (newValue != null)
                commentCollection.updateOne(filter, updateOperationDocument);
            else
                throw new AppInternalServerException(0, "Failed to update comment details");

        } catch (Exception e) {
            throw handleException("Update Comment", e);
        }
    }

    public void deleteComment(String id) throws AppException {
        try {
            Bson filter = new Document("_id", new ObjectId(id));
            commentCollection.deleteOne(filter);
        } catch (Exception e){
            throw handleException("Delete comment", e);
        }
    }

    private ArrayList<Comment> convertDocsToArrayList(FindIterable<Document> commentDocs) {
        ArrayList<Comment> commentList = new ArrayList<>();
        for(Document commentDoc: commentDocs) {
            Comment comment = new Comment(
                    commentDoc.getObjectId("_id").toString(),
                    commentDoc.getString("draftId"),
                    commentDoc.getString("userId"),
                    commentDoc.getString("content"),
                    (Date) commentDoc.get("createTime"),
                    (Date) commentDoc.get("modifyTime")
            );
            commentList.add(comment);
        }
        return commentList;
    }
}
