package com.ls.trace.web.base.support;

import com.ls.trace.web.base.register.CCmsRegister;
import com.howbuy.zkutils.CfClientBuilder;
import com.howbuy.zkutils.CfOperation;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;


/**
 * zk缓存数据处理
 * @date: 2018年12月06日
 * @author: leslie.zhang
 */
@Component
public class ZkCacheSupport implements CommandLineRunner {

    @Autowired
    private CCmsRegister ccmsRegister;

    private CfClientBuilder cfClientBuilder;

    private final String ZK_ROOT = "howbuy-trace-logger";

    public String get(String path){
        if(!StringUtils.isBlank(path)){
            try{
                return getCkClient().getString(path);
            }catch(Exception e){
                return null;
            }
        }
        return null;
    }


    public void addString(String path,String value){
        if(!StringUtils.isBlank(path)){
            getCkClient().addString(path, value == null ? "" : value);
        }
    }


    public void delete(String path){
        if(!StringUtils.isBlank(path)) {
            getCkClient().delete(path);
        }
    }

    public List<String> getChildren(String path){
        return getCkClient().getChildren(path);
    }


    @Override
    public void run(String... args) throws Exception {
        System.out.println("---ZkCacheSupport");
        if(StringUtils.isBlank(ccmsRegister.getZk_host())){
            return;
        }
        cfClientBuilder = new CfClientBuilder(ccmsRegister.getZk_host(),5000,"howbuy-trace-logger");
    }


    private CfOperation getCkClient(){
        if(cfClientBuilder == null){
            if(StringUtils.isBlank(ccmsRegister.getZk_host())){
                return null;
            }
            cfClientBuilder = new CfClientBuilder(ccmsRegister.getZk_host(),5000,"howbuy-trace-logger");
        }
        if(cfClientBuilder == null){
            return null;
        }else{
            return cfClientBuilder.newCfClient();
        }
    }
}
