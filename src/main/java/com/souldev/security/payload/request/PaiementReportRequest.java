package com.souldev.security.payload.request;

import java.util.List;

public class PaiementReportRequest {
    private String startDate;
    private String endDate;
    private List json;

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public List getJson() {
        return json;
    }

    public void setJson(List json) {
        this.json = json;
    }

    @Override
    public String toString() {
        return "PaiementReportRequest{" +
                "startDate='" + startDate + '\'' +
                ", endDate='" + endDate + '\'' +
                ", json=" + json +
                '}';
    }
}
