package edu.cmu.designit.server.models;

public class DraftTag {
  private String id;
  private String draftId;
  private String tagId;
  private int tagCount;

  public DraftTag(String id, String draftId, String tagId) {
    this.id = id;
    this.draftId = draftId;
    this.tagId = tagId;
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

  public String getTagId() {
    return tagId;
  }

  public void setTagId(String tagId) {
    this.tagId = tagId;
  }
}
