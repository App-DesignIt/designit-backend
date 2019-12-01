package edu.cmu.designit.server.http.interfaces;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.mongodb.client.MongoCollection;
import edu.cmu.designit.server.http.exceptions.HttpBadRequestException;
import edu.cmu.designit.server.http.responses.AppResponse;
import edu.cmu.designit.server.http.utils.PATCH;
import edu.cmu.designit.server.managers.CommentManager;
import edu.cmu.designit.server.models.Comment;
import edu.cmu.designit.server.utils.AppLogger;
import org.bson.Document;
import org.json.JSONObject;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.Date;

@Path("/comments")
public class CommentHttpInterface extends HttpInterface{
    private ObjectWriter ow;
    private MongoCollection<Document> commentCollection = null;

    public CommentHttpInterface() {
        ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
    }

    @POST
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public AppResponse postComments(Object request){
        try {
            JSONObject json = null;
            json = new JSONObject(ow.writeValueAsString(request));

            Date date = new Date();

            Comment comment = new Comment (
                    null,
                    json.getString("draftId"),
                    json.getString("userId"),
                    json.getString("content"),
                    date,
                    date
            );

            CommentManager.getInstance().createComment(comment);
            return new AppResponse("Insert Successful");
        } catch (Exception e) {
            throw handleException("POST comments", e);
        }
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public AppResponse getComments(@Context HttpHeaders headers){
        try {
            AppLogger.info("Got an API call");
            ArrayList<Comment> comments = CommentManager.getInstance().getCommentList();
            if(comments != null)
                return new AppResponse(comments);
            else
                throw new HttpBadRequestException(0, "Problem with getting likes list");
        } catch (Exception e) {
            throw handleException("GET /likes", e);
        }
    }

    @GET
    @Path("/{commentId}")
    @Produces({MediaType.APPLICATION_JSON})
    public AppResponse getSingleComment(@Context HttpHeaders headers, @PathParam("commentId") String commentId){
        try {
            AppLogger.info("Got an API call");
            ArrayList<Comment> comments = CommentManager.getInstance().getCommentById(commentId);

            if(comments != null)
                return new AppResponse(comments);
            else
                throw new HttpBadRequestException(0, "Problem with getting single comment");
        } catch (Exception e){
            throw handleException("GET /comments/{commentId}", e);
        }
    }

    @GET
    @Path("/draft/{draftId}")
    @Produces({MediaType.APPLICATION_JSON})
    public AppResponse getCommentsByDraft(@Context HttpHeaders headers, @PathParam("draftId") String draftId){
        try {
            AppLogger.info("Got an API call");
            ArrayList<Comment> comments = CommentManager.getInstance().getCommentByDraftId(draftId);

            if(comments != null)
                return new AppResponse(comments);
            else
                throw new HttpBadRequestException(0, "Problem with getting single comment");
        } catch (Exception e){
            throw handleException("GET /comments/draft/{draftId}", e);
        }
    }

    @PATCH
    @Path("/{commentId}")
    @Consumes({ MediaType.APPLICATION_JSON})
    @Produces({ MediaType.APPLICATION_JSON})
    public AppResponse patchComment(Object request, @PathParam("commentId") String commentId){
        JSONObject json = null;
        try {
            ArrayList<Comment> commentList = CommentManager.getInstance().getCommentById(commentId);
            if(commentList.isEmpty()) {
                return new AppResponse("No such comment with given Id: " + commentId);
            }
            json = new JSONObject(ow.writeValueAsString(request));
            Date date = new Date();
            Comment comment = new Comment (
                    commentId,
                    null,
                    null,
                    json.getString("content"),
                    null,
                    date
            );
            CommentManager.getInstance().updateComment(comment);
        } catch (Exception e){
            throw handleException("PATCH comment/{commentId}", e);
        }
        return new AppResponse("Update Successful");
    }

    @DELETE
    @Path("/{commentId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public AppResponse deleteComment(@PathParam("commentId") String commentId){
        try {
            CommentManager.getInstance().deleteComment(commentId);
            return new AppResponse("Delete Successful");
        } catch (Exception e){
            throw handleException("DELETE comment/{commentId}", e);
        }
    }


}
