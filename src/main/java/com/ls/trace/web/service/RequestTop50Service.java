package com.ls.trace.web.service;

import com.ls.trace.web.base.enums.EsIndexEnum;
import com.ls.trace.web.dto.DataBaseDto;
import com.ls.trace.web.dto.request.TopDataRequestDto;

import java.util.List;

/**
 * 请求top50
 * @date: 2018年12月06日
 * @author: leslie.zhang
 */
public interface RequestTop50Service {

    /**
     * 请求耗时前50数据
     * @date: 2018年12月18日
     * @author: leslie.zhang
     */
    List<DataBaseDto> getRequestCostTop50List(EsIndexEnum indexEnum, TopDataRequestDto requestDto);

    /**
     * 请求总数前50数据
     * @date: 2018年12月18日
     * @author: leslie.zhang
     */
    List<DataBaseDto> getRequestCountTop50List(EsIndexEnum indexEnum, TopDataRequestDto requestDto);
}
