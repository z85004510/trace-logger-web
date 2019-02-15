package com.ls.trace.web.dto.request;

public class TopDataRequestDto {

    private String type; //类型 RequestCosttop50:请求耗时top50；RequestCounttop50：请求总数top50

    private long startTime; //时间跨度，开始时间,毫秒值
    private long endTime; //时间跨度，结束时间，毫秒值

    private String content; //模糊搜索接口名

    private String app_name; //要搜索的app_name


    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getApp_name() {
        return app_name;
    }

    public void setApp_name(String app_name) {
        this.app_name = app_name;
    }
}

