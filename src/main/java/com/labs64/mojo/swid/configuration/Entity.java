package com.labs64.mojo.swid.configuration;

import java.util.List;

public class Entity {
    private String name;
    private String regid;
    private List<String> roles;

    public Entity() {

    }

    public Entity(String name, String regid, List<String> roles) {
        this.name = name;
        this.regid = regid;
        this.roles = roles;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRegid() {
        return regid;
    }

    public void setRegid(String regid) {
        this.regid = regid;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }
}
