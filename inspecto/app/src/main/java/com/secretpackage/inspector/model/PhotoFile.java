package com.secretpackage.inspector.model;

/**
 * Created by ali on 7/7/19.
 */

public class PhotoFile {

    private int propertyId;
    private String id;
    private String name;
    private String url;

    public PhotoFile(String id, String name, String url) {
        this.id = id;
        this.name = name;
        this.url = url;
    }

    public int getPropertyId() {
        return propertyId;
    }

    public void setPropertyId(int propertyId) {
        this.propertyId = propertyId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
