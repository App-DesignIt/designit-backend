package edu.cmu.designit.server.http.interfaces;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.mongodb.client.MongoCollection;
import edu.cmu.designit.server.http.exceptions.HttpBadRequestException;
import edu.cmu.designit.server.http.responses.AppResponse;
import edu.cmu.designit.server.managers.DraftManager;
import edu.cmu.designit.server.managers.LikeManager;
import edu.cmu.designit.server.models.Like;
import edu.cmu.designit.server.utils.AppLogger;
import org.bson.Document;
import org.json.JSONObject;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.Date;

@Path("/likes")
public class LikeHttpInterface extends HttpInterface {

    private ObjectWriter ow;
    private MongoCollection<Document> likeCollection = null;

    public LikeHttpInterface() {
        ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
    }

    @POST
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public AppResponse postLikes(Object request){
        try {
            JSONObject json = null;
            json = new JSONObject(ow.writeValueAsString(request));

            Date date = new Date();

            Like newLike = new Like (
                    null,
                    json.getString("draftId"),
                    json.getString("ownerId"),
                    json.getString("likerId"),
                    date
            );
            LikeManager.getInstance().createLike(newLike);
            DraftManager.getInstance().updateDraftLikeCount(newLike, 1);
            return new AppResponse("Insert Successful");
        } catch (Exception e) {
            throw handleException("POST likes", e);
        }
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public AppResponse getLikes(@Context HttpHeaders headers){
        try {
            AppLogger.info("Got an API call");
            ArrayList<Like> likes = LikeManager.getInstance().getLikeList();
            if(likes != null)
                return new AppResponse(likes);
            else
                throw new HttpBadRequestException(0, "Problem with getting likes list");
        } catch (Exception e) {
            throw handleException("GET /likes", e);
        }
    }

    @GET
    @Path("/liker/{likerId}")
    @Produces({MediaType.APPLICATION_JSON})
    public AppResponse getLikeByLiker(@Context HttpHeaders headers, @PathParam("likerId") String likerId){
        try {
            AppLogger.info("Got an API call");
            ArrayList<Like> likes = LikeManager.getInstance().getLikebyUser(likerId);

            if(likes != null)
                return new AppResponse(likes);
            else
                throw new HttpBadRequestException(0, "Problem with getting like by likerId");
        } catch (Exception e){
            throw handleException("GET /likes/{likerId}", e);
        }
    }

    @GET
    @Path("/draft/{draftId}")
    @Produces({MediaType.APPLICATION_JSON})
    public AppResponse getLikerByDraft(@Context HttpHeaders headers, @PathParam("draftId") String draftId){
        try {
            AppLogger.info("Got an API call");
            ArrayList<Like> likes = LikeManager.getInstance().getLikebyDraft(draftId);

            if(likes != null)
                return new AppResponse(likes);
            else
                throw new HttpBadRequestException(0, "Problem with getting like by draftId");
        } catch (Exception e){
            throw handleException("GET /draft/{draftId}", e);
        }
    }

    @DELETE
    @Produces({ MediaType.APPLICATION_JSON })
    public AppResponse deleteLikes(@QueryParam("draftId") String draftId,
                                   @QueryParam("likerId") String likerId){
        try {
            Like newLike = new Like (
                    null,
                    draftId,
                    null,
                    likerId,
                    null
            );
            LikeManager.getInstance().deleteLike(draftId, likerId);
            DraftManager.getInstance().updateDraftLikeCount(newLike, -1);
            return new AppResponse("Delete Successful");
        } catch (Exception e){
            throw handleException("DELETE likes/", e);
        }
    }

}
