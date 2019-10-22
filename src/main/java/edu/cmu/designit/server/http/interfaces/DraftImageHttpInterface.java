package edu.cmu.designit.server.http.interfaces;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.mongodb.client.MongoCollection;
import edu.cmu.designit.server.http.responses.AppResponse;
import edu.cmu.designit.server.managers.DraftImageManager;
import edu.cmu.designit.server.managers.DraftManager;
import edu.cmu.designit.server.models.Draft;
import edu.cmu.designit.server.models.DraftImage;
import org.bson.Document;
import org.json.JSONObject;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

public class DraftImageHttpInterface extends HttpInterface {
  private ObjectWriter ow;
  private MongoCollection<Document> draftCollection = null;

  public DraftImageHttpInterface() {
    ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
  }

  @POST
  @Consumes({MediaType.APPLICATION_JSON})
  @Produces({MediaType.APPLICATION_JSON})
  public AppResponse postDraftImage(Object request){
    try{
      JSONObject json = null;
      json = new JSONObject(ow.writeValueAsString(request));

      DraftImage newDraftImage = new DraftImage(
              json.getString("draftId"),
              json.getString("imageUrl")
      );
      DraftImageManager.getInstance().createDraftImage(newDraftImage);
      return new AppResponse("Insert Successful");

    }catch (Exception e){
      throw handleException("POST draft image", e);
    }

  }

}
