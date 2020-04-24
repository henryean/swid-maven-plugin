package com.labs64.mojo.swid.configuration;

import javax.xml.datatype.XMLGregorianCalendar;

public class Evidence {
    private XMLGregorianCalendar date;
    private String deviceId;

    public Evidence() {
    }

    public Evidence(XMLGregorianCalendar date, String deviceId) {
        this.date = date;
        this.deviceId = deviceId;
    }

    public XMLGregorianCalendar getDate() {
        return date;
    }

    public void setDate(XMLGregorianCalendar date) {
        this.date = date;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }
}
