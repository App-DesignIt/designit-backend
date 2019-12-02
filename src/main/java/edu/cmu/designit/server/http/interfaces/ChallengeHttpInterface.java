package edu.cmu.designit.server.http.interfaces;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.mongodb.client.MongoCollection;
import edu.cmu.designit.server.http.exceptions.HttpBadRequestException;
import edu.cmu.designit.server.http.responses.AppResponse;
import edu.cmu.designit.server.http.utils.PATCH;
import edu.cmu.designit.server.managers.ChallengeManager;
import edu.cmu.designit.server.managers.ChallengeSubmissionManager;
import edu.cmu.designit.server.models.Challenge;
import edu.cmu.designit.server.models.ChallengeSubmission;
import edu.cmu.designit.server.models.Payment;
import org.bson.Document;
import org.json.JSONObject;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Date;

@Path("/challenges")
public class ChallengeHttpInterface extends HttpInterface  {
  private ObjectWriter ow;
  private MongoCollection<Document> challengeCollection = null;

  public ChallengeHttpInterface() {
    ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
  }

  @POST
  @Consumes({MediaType.APPLICATION_JSON})
  @Produces({MediaType.APPLICATION_JSON})
  public AppResponse postChallenges(Object request){
    try{
      JSONObject json = new JSONObject(ow.writeValueAsString(request));

      Challenge newChallenge = new Challenge(
              json.getString("name"),
              json.getString("description"),
              json.getString("recruiterId"),
              new Date(json.getLong("startTime")),
              new Date(json.getLong("endTime")),
              json.getDouble("winnerPrize")
      );

      Challenge challenge = ChallengeManager.getInstance().createChallenge(newChallenge);
      return new AppResponse(challenge);

    }catch (Exception e){
      throw handleException("POST challenges", e);
    }
  }

  @GET
  @Produces({ MediaType.APPLICATION_JSON })
  public AppResponse getChallenges(@Context HttpHeaders headers, @QueryParam("sortby") String sortby,
                                   @QueryParam("sortdirection") String sortdirection,
                                   @QueryParam("offset") Integer offset,
                                   @QueryParam("count") Integer count,
                                   @QueryParam("filter") String filter) {
    try{
      ArrayList<Challenge> challenges = null;
      if(sortby != null) {
        if(sortdirection == null)  {
          challenges = ChallengeManager.getInstance().getChallengeListSorted(sortby, "desc");
        } else {
          challenges = ChallengeManager.getInstance().getChallengeListSorted(sortby, "asc");
        }
      } else if (offset != null && count != null ) {
        challenges = ChallengeManager.getInstance().getChallengeListPaginated(offset, count);
      } else if (filter != null) {
        challenges = ChallengeManager.getInstance().getChallengeListFiltered(filter);
      } else{
        challenges = ChallengeManager.getInstance().getChallengeList();
      }

      return new AppResponse(challenges);
    } catch (Exception e){
      throw handleException("GET /challenges", e);
    }
  }

  @GET
  @Path("/{challengeId}")
  @Produces({MediaType.APPLICATION_JSON})
  public AppResponse getSingleChallenge(@Context HttpHeaders headers, @PathParam("challengeId") String challengeId){
    try{
      ArrayList<Challenge> challenges = ChallengeManager.getInstance().getChallengeById(challengeId);
      if(challenges != null)
        return new AppResponse(challenges);
      else
        throw new HttpBadRequestException(0, "Problem with getting challenges");
    }catch (Exception e){
      throw handleException("GET /challenges/{challengeId}", e);
    }
  }

  @GET
  @Path("/{challengeId}/challenge_submissions")
  @Produces({MediaType.APPLICATION_JSON})
  public AppResponse getSubmissions(@Context HttpHeaders headers, @PathParam("challengeId") String challengeId) {
    try {
      ArrayList<ChallengeSubmission> challengeSubmissions = ChallengeSubmissionManager.getInstance().getChallengeSubmissionsByChallengeId(challengeId);
      if(challengeSubmissions != null)
        return new AppResponse(challengeSubmissions);
      else
        throw new HttpBadRequestException(0, "Problem with getting challenges submissions by challenge id");
    } catch (Exception e) {
      throw handleException("GET /challenges/{challengeId}/challenge_submissions", e);
    }
  }

  @GET
  @Path("/{challengeId}/winners")
  @Produces({MediaType.APPLICATION_JSON})
  public AppResponse getChallengeWinners(@Context HttpHeaders headers, @PathParam("challengeId") String challengeId) {
    try {
      ArrayList<ChallengeSubmission> challengeSubmissions = ChallengeSubmissionManager.getInstance().getChallengeSubmissionsByChallengeId(challengeId);
      if(challengeSubmissions != null)
        return new AppResponse(challengeSubmissions);
      else
        throw new HttpBadRequestException(0, "Problem with getting challenges submissions by challenge id");
    } catch (Exception e) {
      throw handleException("GET /challenges/{challengeId}/winners", e);
    }
  }


  @PATCH
  @Path("/{challengeId}")
  @Consumes({ MediaType.APPLICATION_JSON})
  @Produces({ MediaType.APPLICATION_JSON})
  public AppResponse patchChallenge(Object request, @PathParam("challengeId") String challengeId){
    try{
      JSONObject json = new JSONObject(ow.writeValueAsString(request));
      Challenge challenge = new Challenge(
              challengeId,
              json.getString("name"),
              json.getString("description"),
              json.getString("recruiterId"),
              new Date(json.getLong("startTime")),
              new Date(json.getLong("endTime")),
              json.getDouble("winnerPrize"),
              null,
              new Date()
      );

      ChallengeManager.getInstance().updateChallenge(challenge);

    }catch (Exception e){
      throw handleException("PATCH challenges/{challengeId}", e);
    }

    return new AppResponse("Update Successful");
  }

  @DELETE
  @Path("/{challengeId}")
  @Consumes({ MediaType.APPLICATION_JSON })
  @Produces({ MediaType.APPLICATION_JSON })
  public AppResponse deleteDrafts(@PathParam("challengeId") String challengeId){
    try{
      ChallengeManager.getInstance().deleteChallenge(challengeId);
      return new AppResponse("Delete Successful");
    }catch (Exception e){
      throw handleException("DELETE challenges/{challengeId}", e);
    }
  }

  @GET
  @Path("/{challengeId}/calculateScore")
  @Consumes({ MediaType.APPLICATION_JSON })
  @Produces({ MediaType.APPLICATION_JSON })
  public AppResponse calculateScore(@PathParam("challengeId") String challengeId) {
    try{
      ArrayList<ChallengeSubmission> submissions = ChallengeManager.getInstance().calculateAllSubmissionScore(challengeId);
      if(submissions != null)
        return new AppResponse(submissions);
      else
        throw new HttpBadRequestException(0, "Problem with calculating score by challenge id");
    } catch(Exception e) {
      throw handleException("GET challenges/{challengeId}/calculateScore", e);
    }
  }

  @GET
  @Path("/{challengeId}/winners")
  @Consumes({ MediaType.APPLICATION_JSON })
  @Produces({ MediaType.APPLICATION_JSON })
  public AppResponse getWinners(@PathParam("challengeId") String challengeId) {
    try{
      ArrayList<ChallengeSubmission> winners = ChallengeManager.getInstance().getWinners(challengeId);
      if(winners != null)
        return new AppResponse(winners);
      else
        throw new HttpBadRequestException(0, "Problem with getting winners by challenge id");
    } catch(Exception e) {
      throw handleException("GET challenges/{challengeId}/winners", e);
    }
  }

  @GET
  @Path("/{challengeId}/payments")
  @Consumes({ MediaType.APPLICATION_JSON })
  @Produces({ MediaType.APPLICATION_JSON })
  public AppResponse getPayments(@PathParam("challengeId") String challengeId) {
    try {
      ArrayList<ChallengeSubmission> winners = ChallengeManager.getInstance().getWinners(challengeId);
      ArrayList<Payment> payments = ChallengeManager.getInstance().createPaymentsByChallengeIdAndChallengeWinners(challengeId, winners);
      return new AppResponse(payments);
    } catch (Exception e) {
      throw handleException("GET challenges/{challengeId}/payments", e);
    }
  }

}
