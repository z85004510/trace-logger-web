package com.ls.trace.web.dto.request;

/**
 * 打开/关闭要监控链路操作的接收实体类
 * @date: 2018年12月05日
 * @author: leslie.zhang
 */
public class SpliceInterfaceRequestDto {

    private String app_name;
    private String service_name;

    public String getApp_name() {
        return app_name;
    }

    public void setApp_name(String app_name) {
        this.app_name = app_name;
    }

    public String getService_name() {
        return service_name;
    }

    public void setService_name(String service_name) {
        this.service_name = service_name;
    }
}
