package com.labs64.mojo.swid.configuration;

import java.util.List;

public class Resource {
    private String type;
    private List<Meta> extraKeys;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<Meta> getExtraKeys() {
        return extraKeys;
    }

    public void setExtraKeys(List<Meta> extraKeys) {
        this.extraKeys = extraKeys;
    }
}
