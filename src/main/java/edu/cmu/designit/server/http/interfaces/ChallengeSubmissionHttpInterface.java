package edu.cmu.designit.server.http.interfaces;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import edu.cmu.designit.server.http.exceptions.HttpBadRequestException;
import edu.cmu.designit.server.http.responses.AppResponse;
import edu.cmu.designit.server.http.utils.PATCH;
import edu.cmu.designit.server.managers.ChallengeSubmissionManager;
import edu.cmu.designit.server.models.ChallengeSubmission;
import org.json.JSONObject;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.Date;

@Path("/challenge_submissions")
public class ChallengeSubmissionHttpInterface extends HttpInterface {
  private ObjectWriter ow;

  public ChallengeSubmissionHttpInterface() {
    ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
  }

  @POST
  @Consumes({MediaType.APPLICATION_JSON})
  @Produces({MediaType.APPLICATION_JSON})
  public AppResponse postChallengeSubmissions(Object request){
    try{
      JSONObject json = new JSONObject(ow.writeValueAsString(request));

      ChallengeSubmission newChallengeSubmission = new ChallengeSubmission(
              json.getString("draftId"),
              json.getString("userId"),
              json.getString("challengeId")
      );

      ChallengeSubmission challengeSubmission = ChallengeSubmissionManager.getInstance().createChallengeSubmission(newChallengeSubmission);
      return new AppResponse(challengeSubmission);
    }catch (Exception e){
      throw handleException("POST challenge submissons", e);
    }
  }

  @GET
  @Consumes({MediaType.APPLICATION_JSON})
  @Produces({ MediaType.APPLICATION_JSON })
  public AppResponse getChallengeSubmissons(@Context HttpHeaders headers, @QueryParam("sortby") String sortby,
                                   @QueryParam("sortdirection") String sortdirection,
                                   @QueryParam("offset") Integer offset,
                                   @QueryParam("count") Integer count,
                                   @QueryParam("filter") String filter) {
    try {
      ArrayList<ChallengeSubmission> challengeSubmissions = null;
      if(sortby != null) {
        if(sortdirection == null)  {
          challengeSubmissions = ChallengeSubmissionManager.getInstance().getChallengeSubmissionsSorted(sortby, "desc");
        } else {
          challengeSubmissions = ChallengeSubmissionManager.getInstance().getChallengeSubmissionsSorted(sortby, "asc");
        }
      } else if (offset != null && count != null ) {
        challengeSubmissions = ChallengeSubmissionManager.getInstance().getChallengeSubmissionListPaginated(offset, count);
      } else if (filter != null) {
        challengeSubmissions =ChallengeSubmissionManager.getInstance().getChallengeSubmissionListFiltered(filter);
      } else {
        challengeSubmissions = ChallengeSubmissionManager.getInstance().getChallengeSubmissionList();
      }
      return new AppResponse(challengeSubmissions);
    } catch (Exception e) {
      throw handleException("GET /challenge_submissions", e);
    }
  }

  @GET
  @Path("/{challengeSubmissionId}")
  @Produces({MediaType.APPLICATION_JSON})
  public AppResponse getSingleChallenge(@Context HttpHeaders headers, @PathParam("challengeSubmissionId") String challengeSubmissionId){
    try{
      ArrayList<ChallengeSubmission> challengeSubmissions = ChallengeSubmissionManager.getInstance().getChallengeSubmissionById(challengeSubmissionId);
      if(challengeSubmissions != null)
        return new AppResponse(challengeSubmissions);
      else
        throw new HttpBadRequestException(0, "Problem with getting challenges submissions");
    }catch (Exception e){
      throw handleException("GET /challenge_submissions/{challengeSubmissions}", e);
    }
  }

  @PATCH
  @Path("/{challengeSubmissionId}")
  @Consumes({ MediaType.APPLICATION_JSON})
  @Produces({ MediaType.APPLICATION_JSON})
  public AppResponse patchChallengeSubmission(Object request, @PathParam("challengeSubmissionId") String challengeSubmissionId){
    try{
      JSONObject json = new JSONObject(ow.writeValueAsString(request));
      ChallengeSubmission challengeSubmission = new ChallengeSubmission(
              challengeSubmissionId,
              json.getInt("ranking"),
              json.getString("draftId"),
              null,
              null,
              new Date(),
              json.getDouble("recruiterScore"),
              json.getDouble("finalScore")
      );
      ChallengeSubmissionManager.getInstance().updateChallengeSubmission(challengeSubmission);
    }catch (Exception e){
      throw handleException("PATCH challenge_submissions/{challengeSubmissionId}", e);
    }
    return new AppResponse("Update Successful");
  }

  @GET
  @Produces({ MediaType.APPLICATION_JSON })
  public AppResponse getChallengeSubmissions(@Context HttpHeaders headers, @QueryParam("sortby") String sortby,
                                   @QueryParam("sortdirection") String sortdirection,
                                   @QueryParam("offset") Integer offset,
                                   @QueryParam("count") Integer count,
                                   @QueryParam("filter") String filter) {
    try{
      ArrayList<ChallengeSubmission> challengeSubmissions = null;
      if(sortby != null) {
        if(sortdirection == null)  {
          challengeSubmissions = ChallengeSubmissionManager.getInstance().getChallengeSubmissionsSorted(sortby, "desc");
        } else {
          challengeSubmissions = ChallengeSubmissionManager.getInstance().getChallengeSubmissionsSorted(sortby, "asc");
        }
      } else if (offset != null && count != null ) {
        challengeSubmissions = ChallengeSubmissionManager.getInstance().getChallengeSubmissionListPaginated(offset, count);
      } else if (filter != null) {
        challengeSubmissions = ChallengeSubmissionManager.getInstance().getChallengeSubmissionListFiltered(filter);
      } else{
        challengeSubmissions = ChallengeSubmissionManager.getInstance().getChallengeSubmissionList();
      }
      return new AppResponse(challengeSubmissions);
    } catch (Exception e){
      throw handleException("GET /challenge_submissions", e);
    }
  }

  @DELETE
  @Path("/{challengeSubmissionId}")
  @Consumes({ MediaType.APPLICATION_JSON })
  @Produces({ MediaType.APPLICATION_JSON })
  public AppResponse deleteChallengeSubmissions(@PathParam("challengeSubmissionId") String challengeSubmissionId){
    try{
      ChallengeSubmissionManager.getInstance().deleteChallengeSubmission(challengeSubmissionId);
      return new AppResponse("Delete Successful");
    }catch (Exception e){
      throw handleException("DELETE challenge_submissions/{challengeSubmissionId}", e);
    }
  }



}
