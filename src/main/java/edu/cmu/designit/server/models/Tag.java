package edu.cmu.designit.server.models;

public class Tag {
    private String id;
    private String tagName;
    private int tagCount;

    public Tag(String id, String tagName, int tagCount) {
        this.id = id;
        this.tagName = tagName;
        this.tagCount = tagCount;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public int getTagCount() {
        return tagCount;
    }

    public void setTagCount(int tagCount) {
        this.tagCount = tagCount;
    }
}
