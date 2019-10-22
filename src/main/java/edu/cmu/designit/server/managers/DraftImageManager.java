package edu.cmu.designit.server.managers;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import edu.cmu.designit.server.exceptions.AppException;
import edu.cmu.designit.server.exceptions.AppInternalServerException;
import edu.cmu.designit.server.models.Draft;
import edu.cmu.designit.server.models.DraftImage;
import edu.cmu.designit.server.utils.MongoPool;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.json.JSONObject;

import java.util.ArrayList;

public class DraftImageManager extends Manager {
  public static DraftImageManager _self;
  private MongoCollection<Document> draftImageCollection;

  public DraftImageManager() {
    this.draftImageCollection = MongoPool.getInstance().getCollection("draftImages");
  }

  public static DraftImageManager getInstance(){
    if (_self == null)
      _self = new DraftImageManager();
    return _self;
  }

  public void createDraftImage(DraftImage draftImage) throws AppException {
    try{
      JSONObject json = new JSONObject(draftImage);

      Document newDoc = new Document()
              .append("draftId", draftImage.getDraftId())
              .append("imageUrl", draftImage.getImageUrl());

      if (newDoc != null)
        draftImageCollection.insertOne(newDoc);
      else
        throw new AppInternalServerException(0, "Failed to create draft image relation");

    }catch(Exception e){
      throw handleException("Create Draft Image Relation", e);
    }
  }

  public ArrayList<DraftImage> getDraftImageByDraftId(String draftId) throws AppException {
    try{
      ArrayList<DraftImage> draftImageList = new ArrayList<>();
      FindIterable<Document> draftImageDocs = draftImageCollection.find(Filters.eq("draftId", draftId));

      for(Document draftImageDoc: draftImageDocs) {
          DraftImage draftImage = new DraftImage(
                  draftImageDoc.getObjectId("_id").toString(),
                  draftImageDoc.getString("draftId"),
                  draftImageDoc.getString("imageUrl")
          );
          draftImageList.add(draftImage);
        }
        return new ArrayList<>(draftImageList);
    } catch(Exception e){
      throw handleException("Get Draft Image List By Draft Id", e);
    }
  }


}
