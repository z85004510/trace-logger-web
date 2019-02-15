package com.ls.trace.web.controller;

import com.ls.trace.web.dto.TraceLogTreeDto;
import com.ls.trace.web.dto.request.TraceLogTreeRequestDto;
import com.ls.trace.web.service.TraceLogTreeService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/traceLogTree")
public class TreceLogTreeController {

    @Autowired
    private TraceLogTreeService traceLogTreeService;

    /**
     * 获取接口的链路树
     * @date: 2018年12月14日
     * @author: leslie.zhang
     */
    @RequestMapping("/getTraceLogTree")
    public Map<String,Object> getTraceLogTree(TraceLogTreeRequestDto requestDto){
        long time1 = System.currentTimeMillis();
        Map<String,Object> resultMap = new HashMap<>();
        if(StringUtils.isBlank(requestDto.getTree_id())){
            resultMap.put("code","5001");
            resultMap.put("desc","tree_id不能为空");
        }else if(StringUtils.isBlank(requestDto.getApp_name())){
            resultMap.put("code","5002");
            resultMap.put("desc","app_name不能为空");
        }else {
            List<TraceLogTreeDto> traceLogTree = traceLogTreeService.getTraceLogTree(requestDto.getTree_id(), requestDto.getApp_name());
            System.out.println("--getTraceLogTree---" + (System.currentTimeMillis() - time1));
            resultMap.put("code", "0000");
            resultMap.put("desc", "成功");
            resultMap.put("body", traceLogTree);
        }
        return resultMap;
    }
}
