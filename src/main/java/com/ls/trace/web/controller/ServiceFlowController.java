package com.ls.trace.web.controller;

import com.ls.trace.web.dto.DataBaseDto;
import com.ls.trace.web.dto.request.ServiceFlowRequestDto;
import com.ls.trace.web.service.FlowListService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/flow")
public class ServiceFlowController {

    @Autowired
    private FlowListService flowListService;

    /**
     * 接口流水
     * @date: 2018年12月14日
     * @author: leslie.zhang
     */
    @RequestMapping("/getServiceFlow")
    public Map<String,Object> getServiceFlow(ServiceFlowRequestDto requestDto){
        Map<String,Object> resultMap = new HashMap<>();
        if(StringUtils.isBlank(requestDto.getService_name())){
            resultMap.put("code","5001");
            resultMap.put("desc","service_name不能为空");
        }else if(StringUtils.isBlank(requestDto.getApp_name())){
            resultMap.put("code","5002");
            resultMap.put("desc","app_name不能为空");
        }else{
            List<DataBaseDto> flowList = flowListService.getFlowList(requestDto);
            if(flowList == null){
                resultMap.put("code","0001");
                resultMap.put("desc","最近两天没有日志输出");
            }else{
                resultMap.put("code","0000");
                resultMap.put("desc","成功");
                resultMap.put("body",flowList);
            }
        }
        return resultMap;
    }
}
