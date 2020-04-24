package com.labs64.mojo.swid.configuration;

import java.util.List;

public class Directory {
    private Boolean key;
    private String location;
    private String root;
    private List<Meta> extraKeys;

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
