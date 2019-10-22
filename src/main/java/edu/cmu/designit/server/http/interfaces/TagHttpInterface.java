package edu.cmu.designit.server.http.interfaces;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.mongodb.client.MongoCollection;
import edu.cmu.designit.server.http.exceptions.HttpBadRequestException;
import edu.cmu.designit.server.http.responses.AppResponse;
import edu.cmu.designit.server.http.utils.PATCH;
import edu.cmu.designit.server.managers.TagManager;
import edu.cmu.designit.server.models.Tag;
import edu.cmu.designit.server.utils.AppLogger;
import org.bson.Document;
import org.json.JSONObject;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;

@Path("/tags")
public class TagHttpInterface extends HttpInterface{

    private ObjectWriter ow;
    private MongoCollection<Document> tagCollection = null;

    public TagHttpInterface() {
        ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
    }

    @POST
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public AppResponse postTags(Object request){
        try {
            JSONObject json = null;
            json = new JSONObject(ow.writeValueAsString(request));

            Tag newTag = new Tag(
                    null,
                    json.getString("tagName"),
                    0
            );
            TagManager.getInstance().createTag(newTag);
            return new AppResponse("Insert Successful");
        } catch (Exception e){
            throw handleException("POST tags", e);
        }
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public AppResponse getTags(@Context HttpHeaders headers){
        try {
            AppLogger.info("Got an API call");
            ArrayList<Tag> tags = TagManager.getInstance().getTagList();

            if(tags != null)
                return new AppResponse(tags);
            else
                throw new HttpBadRequestException(0, "Problem with getting tags list");
        } catch (Exception e){
            throw handleException("GET /tags", e);
        }
    }

    @GET
    @Path("/{tagName}")
    @Produces({MediaType.APPLICATION_JSON})
    public AppResponse getSingleTag(@Context HttpHeaders headers, @PathParam("tagName") String tagName){
        try {
            AppLogger.info("Got an API call");
            ArrayList<Tag> tags = TagManager.getInstance().getTagByName(tagName);

            if(tags != null)
                return new AppResponse(tags);
            else
                throw new HttpBadRequestException(0, "Problem with getting single tag");
        } catch (Exception e){
            throw handleException("GET /tags/{tagName}", e);
        }
    }

    @PATCH
    @Path("/{tagName}")
    @Consumes({ MediaType.APPLICATION_JSON})
    @Produces({ MediaType.APPLICATION_JSON})
    public AppResponse patchTag(Object request, @PathParam("tagName") String tagName){
        JSONObject json = null;
        try {
            ArrayList<Tag> tagList = TagManager.getInstance().getTagByName(tagName);
            if(tagList.isEmpty()) {
                return new AppResponse("No such tag with tagName: " + tagName);
            }
            json = new JSONObject(ow.writeValueAsString(request));
            Tag tag = new Tag (
                    null,
                    tagName,
                    json.getInt("tagCount")
            );
            TagManager.getInstance().updateTag(tag);
        } catch (Exception e){
            throw handleException("PATCH tags/{tagName}", e);
        }

        return new AppResponse("Update Successful");
    }

    @PATCH
    @Path("/increase/{tagName}")
    @Consumes({ MediaType.APPLICATION_JSON})
    @Produces({ MediaType.APPLICATION_JSON})
    public AppResponse increaseTag(Object request, @PathParam("tagName") String tagName){
        JSONObject json = null;
        try {
            ArrayList<Tag> tagList = TagManager.getInstance().getTagByName(tagName);
            if(tagList.isEmpty()) {
                return new AppResponse("No such tag with tagName: " + tagName);
            }
            json = new JSONObject(ow.writeValueAsString(request));
            Tag tag = new Tag (
                    null,
                    tagName,
                    0
            );
            TagManager.getInstance().increaseTag(tag);
        } catch (Exception e) {
            throw handleException("PATCH tags/{tagName}", e);
        }

        return new AppResponse("Update Successful");
    }

    @DELETE
    @Path("/{tagName}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public AppResponse deleteTags(@PathParam("tagName") String tagName){
        try {
            TagManager.getInstance().deleteTag(tagName);
            return new AppResponse("Delete Successful");
        } catch (Exception e){
            throw handleException("DELETE tags/{tagName}", e);
        }
    }

}
