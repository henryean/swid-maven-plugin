package com.labs64.mojo.swid.configuration;

import java.util.List;

public class PayloadFile {
    private String name;
    private String size;
    private String version;
    private Boolean key;
    private String location;
    private String root;
    private List<Meta> extraKeys;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Boolean getKey() {
        return key;
    }

    public void setKey(Boolean key) {
        this.key = key;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getRoot() {
        return root;
    }

    public void setRoot(String root) {
        this.root = root;
    }

    public List<Meta> getExtraKeys() {
        return extraKeys;
    }

    public void setExtraKeys(List<Meta> extraKeys) {
        this.extraKeys = extraKeys;
    }
}
