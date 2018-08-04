package com.android.papers.qmkl_android.requestModel;

public class QueryAcademiesRequest {
    private String collegeName;

    public QueryAcademiesRequest(String collegeName) {
        this.collegeName = collegeName;
    }

    public String getCollegeName() {
        return collegeName;
    }

    public void setCollegeName(String collegeName) {
        this.collegeName = collegeName;
    }

}
