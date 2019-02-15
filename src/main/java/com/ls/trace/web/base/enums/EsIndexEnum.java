package com.ls.trace.web.base.enums;

public enum EsIndexEnum {

    ec_fund_remote_log("log-ec-fund-remote-","","fund-remote"),
    ec_simu_remote_log("log-ec-simu-remote-","","simu-remote"),
    ec_content_remote_log("log-ec-content-remote-","","content-remote"),
    ec_cms_remote_log("log-ec-cms-remote-","","cms-remote");

    private String index;
    private String type;
    private String app_name;

    EsIndexEnum(String index, String type,String app_name) {
        this.index = index;
        this.type = type;
        this.app_name = app_name;
    }

    public static EsIndexEnum getEsIndexEnumByAppName(String app_name){
        EsIndexEnum[] values = EsIndexEnum.values();
        for(EsIndexEnum indexEnum : values){
            if(indexEnum.getApp_name().equals(app_name)){
                return indexEnum;
            }
        }
        return null;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getApp_name() {
        return app_name;
    }

    public void setApp_name(String app_name) {
        this.app_name = app_name;
    }
}
