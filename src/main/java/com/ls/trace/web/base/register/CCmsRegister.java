package com.ls.trace.web.base.register;

import com.howbuy.ccms.independent.CcmsIndependent;
import com.howbuy.ccms.independent.CcmsMapper;
import org.springframework.stereotype.Component;

@Component
public class CCmsRegister {

    public CCmsRegister(){
        System.out.println("-----------ccmsRegister");
        CcmsIndependent.add(this);
    }

    @CcmsMapper(group="TRACE_LOG_ZK",name="zk_host")
    private String zk_host;

    public String getZk_host() {
        return zk_host;
    }

    public void setZk_host(String zk_host) {
        this.zk_host = zk_host;
    }
}
