package edu.cmu.designit.server.http.interfaces;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.mongodb.client.MongoCollection;
import edu.cmu.designit.server.http.exceptions.HttpBadRequestException;
import edu.cmu.designit.server.http.responses.AppResponse;
import edu.cmu.designit.server.http.utils.PATCH;
import edu.cmu.designit.server.managers.RatingManager;
import edu.cmu.designit.server.models.Rating;
import edu.cmu.designit.server.utils.AppLogger;
import org.bson.Document;
import org.json.JSONObject;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.Date;

@Path("/ratings")
public class RatingHttpInterface extends HttpInterface {
    private ObjectWriter ow;
    private MongoCollection<Document> ratingCollection = null;

    public RatingHttpInterface() {
        ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
    }

    @POST
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public AppResponse postRating(Object request){
        try {
            JSONObject json = null;
            json = new JSONObject(ow.writeValueAsString(request));

            Date date = new Date();
            Rating rating = new Rating (
                    null,
                    json.getString("draftId"),
                    json.getString("userId"),
                    json.getDouble("score"),
                    date,
                    date
            );

            RatingManager.getInstance().createRating(rating);
            return new AppResponse("Insert Successful");
        } catch (Exception e) {
            throw handleException("POST rating", e);
        }
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public AppResponse getRatings(@Context HttpHeaders headers){
        try {
            AppLogger.info("Got an API call");
            ArrayList<Rating> ratings = RatingManager.getInstance().getRatingList();
            if(ratings != null)
                return new AppResponse(ratings);
            else
                throw new HttpBadRequestException(0, "Problem with getting rating list");
        } catch (Exception e) {
            throw handleException("GET /ratings", e);
        }
    }

    @GET
    @Path("/{ratingId}")
    @Produces({MediaType.APPLICATION_JSON})
    public AppResponse getSingleRating(@Context HttpHeaders headers, @PathParam("ratingId") String ratingId){
        try {
            AppLogger.info("Got an API call");
            ArrayList<Rating> ratings = RatingManager.getInstance().getRatingById(ratingId);

            if(ratings != null)
                return new AppResponse(ratings);
            else
                throw new HttpBadRequestException(0, "Problem with getting single rating");
        } catch (Exception e){
            throw handleException("GET /rating/{ratingId}", e);
        }
    }

    @GET
    @Path("/draft/{draftId}")
    @Produces({MediaType.APPLICATION_JSON})
    public AppResponse getRatingsByDraft(@Context HttpHeaders headers, @PathParam("draftId") String draftId){
        try {
            AppLogger.info("Got an API call");
            ArrayList<Rating> ratings = RatingManager.getInstance().getRatingByDraft(draftId);

            if(ratings != null)
                return new AppResponse(ratings);
            else
                throw new HttpBadRequestException(0, "Problem with getting rating by draft");
        } catch (Exception e){
            throw handleException("GET /ratings/draft/{draftId}", e);
        }
    }

    @GET
    @Path("/user/{userId}")
    @Produces({MediaType.APPLICATION_JSON})
    public AppResponse getRatingsByUser(@Context HttpHeaders headers, @PathParam("userId") String userId){
        try {
            AppLogger.info("Got an API call");
            ArrayList<Rating> ratings = RatingManager.getInstance().getRatingByUser(userId);

            if(ratings != null)
                return new AppResponse(ratings);
            else
                throw new HttpBadRequestException(0, "Problem with getting rating by draft");
        } catch (Exception e){
            throw handleException("GET /ratings/draft/{draftId}", e);
        }
    }

    @PATCH
    @Consumes({ MediaType.APPLICATION_JSON})
    @Produces({ MediaType.APPLICATION_JSON})
    public AppResponse changeScore(Object request,
                                   @QueryParam("userId") String userId,
                                   @QueryParam("draftId") String draftId){
        JSONObject json = null;
        try {
            ArrayList<Rating> ratingList = RatingManager.getInstance().getRatingByUserAndDraft(userId, draftId);
            if(ratingList.isEmpty()) {
                return new AppResponse("No such rating with given user: "
                        + userId + " with given draft: " + draftId);
            }
            json = new JSONObject(ow.writeValueAsString(request));
            Date date = new Date();
            Rating rating = new Rating (
                    ratingList.get(0).getId(),
                    null,
                    null,
                    json.getDouble("score"),
                    null,
                    date
            );
            RatingManager.getInstance().updateRating(rating);
        } catch (Exception e){
            throw handleException("PATCH ratings", e);
        }
        return new AppResponse("Update Rating Score Successful");
    }

    @DELETE
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public AppResponse deleteRating(@QueryParam("userId") String userId,
                                     @QueryParam("draftId") String draftId){
        try {
            ArrayList<Rating> ratingList = RatingManager.getInstance().getRatingByUserAndDraft(userId, draftId);
            if(ratingList.isEmpty()) {
                return new AppResponse("No such rating with given user: "
                        + userId + " with given draft: " + draftId);
            }
            RatingManager.getInstance().deleteRating(userId, draftId);
            return new AppResponse("Delete Rating Successful");
        } catch (Exception e){
            throw handleException("DELETE rating", e);
        }
    }

}
