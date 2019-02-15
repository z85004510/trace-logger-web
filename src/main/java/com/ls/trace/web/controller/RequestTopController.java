package com.ls.trace.web.controller;

import com.ls.trace.web.base.enums.EsIndexEnum;
import com.ls.trace.web.dto.DataBaseDto;
import com.ls.trace.web.dto.request.TopDataRequestDto;
import com.ls.trace.web.service.RequestTop50Service;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 请求top数据获取控制器
 * @date: 2018年12月06日
 * @author: leslie.zhang
 */
@RestController
@ResponseBody
@RequestMapping("/top50")
public class RequestTopController {

    @Autowired
    private RequestTop50Service top50Service;


    /**
     * 获取请求耗时最久的top50接口数据
     * @date: 2018年12月06日
     * @author: leslie.zhang
     */
    @RequestMapping(value = "/getTop50")
    public Map<String,Object> getRquestTop50(TopDataRequestDto requestDto){
        Map<String,Object> resultMap = new HashMap<>();
        if(StringUtils.isBlank(requestDto.getApp_name())){
            resultMap.put("code","4000");
            resultMap.put("desc","app_name错误");
        }else{
            EsIndexEnum indexEnum = EsIndexEnum.getEsIndexEnumByAppName(requestDto.getApp_name());
            if(indexEnum == null){
                resultMap.put("code","4000");
                resultMap.put("desc","app_name错误");
            }else{
                List<DataBaseDto> requestCostTop50List = null;
                if(requestDto.getType().equals("RequestCosttop50")){
                    requestCostTop50List = top50Service.getRequestCostTop50List(indexEnum,requestDto);
                } else if(requestDto.getType().equals("RequestCounttop50")){
                    requestCostTop50List = top50Service.getRequestCountTop50List(indexEnum,requestDto);
                }
                if(requestCostTop50List == null){
                    resultMap.put("code","0001");
                    resultMap.put("desc","没有找到日志文件");

                }else{
                    resultMap.put("code","0000");
                    resultMap.put("desc","成功");
                    resultMap.put("body",requestCostTop50List);
                }
            }
        }
        return resultMap;
    }


}
