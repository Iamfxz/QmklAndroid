package com.android.papers.qmkl_android.requestModel;

public class UMengLoginRequest {
    private String platformId,platform;

    public UMengLoginRequest(String platformId, String platform) {
        this.platformId = platformId;
        this.platform = platform;
    }

    public String getPlatformId() {
        return platformId;
    }

    public void setPlatformId(String platformId) {
        this.platformId = platformId;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }


}
