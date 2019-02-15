package com.ls.trace.web.dto;

import java.util.List;

public class TraceLogTreeDto extends DataBaseDto{
    private List<TraceLogTreeDto> node;

    public List<TraceLogTreeDto> getNode() {
        return node;
    }

    public void setNode(List<TraceLogTreeDto> node) {
        this.node = node;
    }
}
