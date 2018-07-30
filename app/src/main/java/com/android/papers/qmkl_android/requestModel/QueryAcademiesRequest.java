package com.android.papers.qmkl_android.requestModel;

public class QueryAcademiesRequest {
    private String collegeName,token;

    public QueryAcademiesRequest(String collegeName, String token) {
        this.collegeName = collegeName;
        this.token = token;
    }

    public String getCollegeName() {
        return collegeName;
    }

    public void setCollegeName(String collegeName) {
        this.collegeName = collegeName;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
