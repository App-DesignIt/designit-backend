package edu.cmu.designit.server.http.interfaces;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.mongodb.client.MongoCollection;
import edu.cmu.designit.server.http.exceptions.HttpBadRequestException;
import edu.cmu.designit.server.http.responses.AppResponse;
import edu.cmu.designit.server.http.utils.PATCH;
import edu.cmu.designit.server.managers.CommentManager;
import edu.cmu.designit.server.managers.DraftManager;
import edu.cmu.designit.server.models.Comment;
import edu.cmu.designit.server.models.Draft;
import edu.cmu.designit.server.utils.AppLogger;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.json.JSONObject;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.Date;

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
      JSONObject json = new JSONObject(ow.writeValueAsString(request));

      Draft newDraft = new Draft(
              json.getString("userId"),
              json.getString("title"),
              json.getString("description"),
              json.getString("imageUrl")
      );

      DraftManager.getInstance().createDraft(newDraft);
      return new AppResponse("Insert Successful");

    }catch (Exception e){
      throw handleException("POST drafts", e);
    }
  }

  //Sorting: http://localhost:8080/api/drafts?sortby=title&sortdirection=asc
  //Pagination: http://localhost:8080/api/drafts?offset=1&count=2
  //Filter: http://localhost:8080/api/drafts?filter=
  @GET
  @Produces({MediaType.APPLICATION_JSON})
  public AppResponse getDrafts(@Context HttpHeaders headers, @QueryParam("sortby") String sortby,
                               @QueryParam("sortdirection") String sortdirection,
                               @QueryParam("offset") Integer offset,
                               @QueryParam("count") Integer count,
                               @QueryParam("filter") String filter){

    try{
      ArrayList<Draft> drafts = null;
      if(sortby != null) {
        if(sortdirection == null)  {
          drafts = DraftManager.getInstance().getDraftListSorted(sortby, "desc");
        } else {
          drafts = DraftManager.getInstance().getDraftListSorted(sortby, "asc");
        }
      } else if (offset != null && count != null ) {
        drafts = DraftManager.getInstance().getDraftListPaginated(offset, count);
      } else if (filter != null) {
        drafts = DraftManager.getInstance().getDraftListFiltered(filter);
      } else{
        drafts = DraftManager.getInstance().getDraftList();
      }

      return new AppResponse(drafts);
    } catch (Exception e){
      throw handleException("GET /drafts", e);
    }
  }

  //Sorting: http://localhost:8080/api/drafts?sortby=title&sortdirection=asc
  //Pagination: http://localhost:8080/api/drafts?offset=1&count=2
  //Filter: http://localhost:8080/api/drafts?filter=
  @GET
  @Path("/search")
  @Produces({MediaType.APPLICATION_JSON})
  public AppResponse getDrafts(@Context HttpHeaders headers, @QueryParam("q") String query){
    try{
      ArrayList<Draft> drafts =  DraftManager.getInstance().searchDraftByName(query);
      return new AppResponse(drafts);
    } catch (Exception e){
      throw handleException("GET /drafts/search", e);
    }
  }

  @GET
  @Path("/{draftId}")
  @Produces({MediaType.APPLICATION_JSON})
  public AppResponse getSingleDraft(@Context HttpHeaders headers, @PathParam("draftId") String draftId){
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

  @GET
  @Path("/{draftId}/comments")
  @Produces({MediaType.APPLICATION_JSON})
  public AppResponse getDraftComment(@Context HttpHeaders headers, @PathParam("draftId") String draftId){
    try {
      AppLogger.info("Got an API call");
      ArrayList<Comment> comments = CommentManager.getInstance().getCommentByDraftId(draftId);

      if(comments != null)
        return new AppResponse(comments);
      else
        throw new HttpBadRequestException(0, "Problem with getting all comments from one draft");
    } catch (Exception e){
      throw handleException("GET /{draftId}/comments", e);
    }
  }


  @PATCH
  @Path("/{draftId}")
  @Consumes({ MediaType.APPLICATION_JSON})
  @Produces({ MediaType.APPLICATION_JSON})
  public AppResponse patchDrafts(Object request, @PathParam("draftId") String draftId){
    try{
      JSONObject json = new JSONObject(ow.writeValueAsString(request));
      Draft draft = new Draft(
              draftId,
              json.getString("userId"),
              json.getString("title"),
              json.getString("description"),
              json.getString("imageUrl"),
              json.getInt("likedCount"),
              json.getInt("rateNumber"),
              json.getInt("viewNumber"),
              json.getDouble("userScore"),
              new Date(json.getLong("createTime")),
              new Date(json.getLong("modifyTime"))
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

  @DELETE
  @Path("/reset")
  @Produces({ MediaType.APPLICATION_JSON })
  public AppResponse resetDrafts() {
    try {
      DraftManager.getInstance().resetDrafts();
      return new AppResponse("Reset Successful");
    } catch (Exception e) {
      throw handleException("Reset drafts", e);
    }
  }




}
