package com.labs64.mojo.swid.configuration;

import java.util.List;

public class Process {
    private String name;
    private String pid;
    private List<Meta> extraKeys;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public List<Meta> getExtraKeys() {
        return extraKeys;
    }

    public void setExtraKeys(List<Meta> extraKeys) {
        this.extraKeys = extraKeys;
    }
}
