package edu.cmu.designit.server.models;

import org.bson.types.ObjectId;

public class DraftImage {
  private String id; //object id of the relation
  private String draftId;
  private String imageUrl;

  public DraftImage(String id, String draftId, String imageUrl) {
    this.id = id;
    this.draftId = draftId;
    this.imageUrl = imageUrl;
  }

  public DraftImage(String draftId, String imageUrl) {
    this.draftId = draftId;
    this.imageUrl = imageUrl;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getDraftId() {
    return draftId;
  }

  public void setDraftId(String draftId) {
    this.draftId = draftId;
  }

  public String getImageUrl() {
    return imageUrl;
  }

  public void setImageUrl(String imageUrl) {
    this.imageUrl = imageUrl;
  }
}
