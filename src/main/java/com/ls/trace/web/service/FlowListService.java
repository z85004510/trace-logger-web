package com.ls.trace.web.service;

import com.ls.trace.web.dto.DataBaseDto;
import com.ls.trace.web.dto.request.ServiceFlowRequestDto;

import java.util.List;

public interface FlowListService {

    List<DataBaseDto> getFlowList(ServiceFlowRequestDto dto);
}
