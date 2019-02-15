package com.ls.trace.web.service;

import com.ls.trace.web.dto.TraceLogTreeDto;

import java.util.List;

public interface TraceLogTreeService {

    List<TraceLogTreeDto> getTraceLogTree(String tree_id, String app_name);


}
