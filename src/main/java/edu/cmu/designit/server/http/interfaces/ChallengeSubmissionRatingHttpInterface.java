package edu.cmu.designit.server.http.interfaces;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import edu.cmu.designit.server.exceptions.AppInternalServerException;
import edu.cmu.designit.server.http.exceptions.HttpBadRequestException;
import edu.cmu.designit.server.http.responses.AppResponse;
import edu.cmu.designit.server.http.utils.PATCH;
import edu.cmu.designit.server.managers.ChallengeSubmissionRatingManager;
import edu.cmu.designit.server.managers.RatingManager;
import edu.cmu.designit.server.models.ChallengeSubmissionRating;
import edu.cmu.designit.server.models.Rating;
import edu.cmu.designit.server.utils.AppLogger;
import org.json.JSONObject;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.Date;

@Path("/challenge_submission_ratings")
public class ChallengeSubmissionRatingHttpInterface extends HttpInterface {
  private ObjectWriter ow;

  public ChallengeSubmissionRatingHttpInterface() {
    ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
  }

  @GET
  @Produces({MediaType.APPLICATION_JSON})
  public AppResponse getChallengeSubmissionRatings(@Context HttpHeaders headers){
    try {
      ArrayList<ChallengeSubmissionRating> ratings = ChallengeSubmissionRatingManager.getInstance().getChallengeSubmissionRatingList();
      if(ratings != null)
        return new AppResponse(ratings);
      else
        throw new HttpBadRequestException(0, "Problem with getting rating list");
    } catch (Exception e) {
      throw handleException("GET /ratings", e);
    }
  }

  @POST
  @Consumes({MediaType.APPLICATION_JSON})
  @Produces({MediaType.APPLICATION_JSON})
  public AppResponse postRating(Object request){
    try {
      JSONObject json =  new JSONObject(ow.writeValueAsString(request));
      String recruiterId = json.getString("recruiterId");
      String challengeSubmissionId = json.getString("challengeSubmissionId");
      ArrayList<ChallengeSubmissionRating> ratings = ChallengeSubmissionRatingManager.getInstance().getRatingByRecruiterIdAndSubmissionId(recruiterId, challengeSubmissionId);
      if(ratings.size() != 0) {
        throw new AppInternalServerException(500, "The rating from this recruiter to this submission already exists.");
      }
      ChallengeSubmissionRating rating = new ChallengeSubmissionRating (
              null,
              challengeSubmissionId,
              recruiterId,
              json.getDouble("score"),
              new Date(),
              new Date());
      ChallengeSubmissionRatingManager.getInstance().createChallengeSubmissionRating(rating);
      return new AppResponse("Insert Successful");
    } catch (Exception e) {
      throw handleException("POST challenge submission rating", e);
    }
  }

  @PATCH
  @Consumes({ MediaType.APPLICATION_JSON})
  @Produces({ MediaType.APPLICATION_JSON})
  public AppResponse changeScore(Object request,
                                 @QueryParam("recruiterId") String recruiterId,
                                 @QueryParam("challengeSubmissionId") String challengeSubmissionId){
    try {
      ArrayList<ChallengeSubmissionRating> ratingList = ChallengeSubmissionRatingManager.getInstance().
              getRatingByRecruiterIdAndSubmissionId(recruiterId, challengeSubmissionId);
      if(ratingList.isEmpty()) {
        return new AppResponse("No such rating with given recruiter: "
                + recruiterId + " with given challenge submission: " + challengeSubmissionId);
      }
      JSONObject json = new JSONObject(ow.writeValueAsString(request));
      ChallengeSubmissionRating newRating = new ChallengeSubmissionRating (
              ratingList.get(0).getId(),
              challengeSubmissionId,
              recruiterId,
              json.getDouble("score"),
              null,
              new Date()
      );
      ChallengeSubmissionRatingManager.getInstance().updateRating(newRating);
    } catch (Exception e){
      throw handleException("PATCH ratings", e);
    }
    return new AppResponse("Update Challenge Submission Rating Score Successful");
  }
}
