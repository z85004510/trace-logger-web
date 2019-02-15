package com.ls.trace.web.controller;

import com.ls.trace.web.base.support.ZkCacheSupport;
import com.ls.trace.web.dto.request.SpliceInterfaceRequestDto;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 打开/关闭要监控的链路地址
 * @date: 2018年12月05日
 * @author: leslie.zhang
 */
@RestController
@RequestMapping("/spliceInterface")
public class SpliceInterfaceController {

    @Autowired
    private ZkCacheSupport zkCacheSupport;

    /**
     * 获取所有已经打开链路的服务
     * @date: 2018年12月14日
     * @author: leslie.zhang
     */
    @RequestMapping("/getList")
    public Map<String,Object> getList(SpliceInterfaceRequestDto request){
        long time1 = System.currentTimeMillis();
        Map<String,Object> resultMap = new HashMap<>();
        if(StringUtils.isBlank(request.getApp_name())){
            resultMap.put("code","5001");
            resultMap.put("desc","app_name不能为空");
        }else{
            List<String> children = zkCacheSupport.getChildren(request.getApp_name() + "/service_name");
            System.out.println("--spliceInterface--getList--" + (System.currentTimeMillis() - time1));
            resultMap.put("code","0000");
            resultMap.put("desc","设置成功");
            resultMap.put("body",children);
        }
        return resultMap;
    }
    /**
     * 验证接口是否已经打开链路
     * @date: 2018年12月14日
     * @author: leslie.zhang
     */
    @RequestMapping("/get")
    public Map<String,Object> get(SpliceInterfaceRequestDto request){
        long time1 = System.currentTimeMillis();
        Map<String,Object> resultMap = new HashMap<>();
        if(StringUtils.isBlank(request.getApp_name())){
            resultMap.put("code","5000");
            resultMap.put("desc","app_name不能都为空");
        }else if(StringUtils.isBlank(request.getService_name())){
            resultMap.put("code","5002");
            resultMap.put("desc","service_name不能为空");
        }else{
            //获取列表
            String value = zkCacheSupport.get(request.getApp_name() + "/service_name/" + request.getService_name());
            System.out.println("--spliceInterface--get--" + (System.currentTimeMillis() - time1));
            resultMap.put("code","0000");
            resultMap.put("desc","成功");
            resultMap.put("body",StringUtils.isBlank(value) ? "off" : "on");
        }
        return resultMap;
    }


    /**
     * 设置接口链路打开和关闭状态
     * @date: 2018年12月14日
     * @author: leslie.zhang
     */
    @RequestMapping("/set")
    public Map<String,Object> set(SpliceInterfaceRequestDto request){
        long time1 = System.currentTimeMillis();
        Map<String,Object> resultMap = new HashMap<>();
        if(StringUtils.isBlank(request.getApp_name())){
            resultMap.put("code","5001");
            resultMap.put("desc","app_name不能为空");
        } else if(StringUtils.isBlank(request.getService_name())){
            resultMap.put("code","5002");
            resultMap.put("desc","service_name不能为空");
        }else{
            String value = zkCacheSupport.get(request.getApp_name() + "/service_name/" + request.getService_name());
            if(StringUtils.isBlank(value)){
                zkCacheSupport.addString(request.getApp_name()+"/service_name/"+request.getService_name(),"on");
                resultMap.put("desc","链路日志已经打开");
                resultMap.put("code","0000");
            }else{
                zkCacheSupport.delete(request.getApp_name()+"/service_name/"+request.getService_name());
                resultMap.put("desc","链路日志已经关闭");
                resultMap.put("code","0001");
            }
        }
        System.out.println("--spliceInterface--set--" + (System.currentTimeMillis() - time1));
        return resultMap;
    }


}
