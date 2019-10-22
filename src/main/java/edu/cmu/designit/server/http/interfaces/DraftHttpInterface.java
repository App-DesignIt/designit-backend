package edu.cmu.designit.server.http.interfaces;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.mongodb.client.MongoCollection;
import edu.cmu.designit.server.http.exceptions.HttpBadRequestException;
import edu.cmu.designit.server.http.responses.AppResponse;
import edu.cmu.designit.server.http.utils.PATCH;
import edu.cmu.designit.server.managers.DraftManager;
import edu.cmu.designit.server.models.Draft;
import edu.cmu.designit.server.utils.AppLogger;
import org.bson.Document;
import org.json.JSONObject;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;

@Path("/drafts")

public class DraftHttpInterface extends HttpInterface{

  private ObjectWriter ow;
  private MongoCollection<Document> draftCollection = null;

  public DraftHttpInterface() {
    ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
  }

  @POST
  @Consumes({MediaType.APPLICATION_JSON, MediaType.MULTIPART_FORM_DATA})
  @Produces({MediaType.APPLICATION_JSON})
  public AppResponse postDrafts(Object request){

    try{
      JSONObject json = null;
      json = new JSONObject(ow.writeValueAsString(request));

      Draft newDraft = new Draft(
              json.getString("title"),
              json.getString("description")
      );
      DraftManager.getInstance().createDraft(newDraft);
      return new AppResponse("Insert Successful");

    }catch (Exception e){
      throw handleException("POST drafts", e);
    }

  }


  @GET
  @Produces({MediaType.APPLICATION_JSON})
  public AppResponse getDrafts(@Context HttpHeaders headers){

    try{
      AppLogger.info("Got an API call");
      ArrayList<Draft> drafts = DraftManager.getInstance().getDraftList();

      if(drafts != null)
        return new AppResponse(drafts);
      else
        throw new HttpBadRequestException(0, "Problem with getting users");
    }catch (Exception e){
      throw handleException("GET /drafts", e);
    }

  }

  @GET
  @Path("/{draftId}")
  @Produces({MediaType.APPLICATION_JSON})
  public AppResponse getSingleUser(@Context HttpHeaders headers, @PathParam("draftId") String draftId){

    try{
      AppLogger.info("Got an API call");
      ArrayList<Draft> drafts = DraftManager.getInstance().getDraftById(draftId);


      if(drafts != null)
        return new AppResponse(drafts);
      else
        throw new HttpBadRequestException(0, "Problem with getting drafts");
    }catch (Exception e){
      throw handleException("GET /drafts/{draftId}", e);
    }


  }


  @PATCH
  @Path("/{draftId}")
  @Consumes({ MediaType.APPLICATION_JSON})
  @Produces({ MediaType.APPLICATION_JSON})
  public AppResponse patchUsers(Object request, @PathParam("draftId") String draftId){

    JSONObject json = null;

    try{
      json = new JSONObject(ow.writeValueAsString(request));
      Draft draft = new Draft(
              draftId,
              json.getString("title"),
              json.getString("description")
      );

      DraftManager.getInstance().updateDraft(draft);

    }catch (Exception e){
      throw handleException("PATCH drafts/{draftId}", e);
    }

    return new AppResponse("Update Successful");
  }




  @DELETE
  @Path("/{draftId}")
  @Consumes({ MediaType.APPLICATION_JSON })
  @Produces({ MediaType.APPLICATION_JSON })
  public AppResponse deleteDrafts(@PathParam("draftId") String draftId){

    try{
      DraftManager.getInstance().deleteDraft(draftId);
      return new AppResponse("Delete Successful");
    }catch (Exception e){
      throw handleException("DELETE drafts/{draftId}", e);
    }

  }

}
