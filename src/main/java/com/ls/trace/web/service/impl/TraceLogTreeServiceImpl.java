package com.ls.trace.web.service.impl;

import com.howbuy.es.client.EsSearchClient;
import com.ls.trace.web.base.enums.EsIndexEnum;
import com.ls.trace.web.dto.TraceLogTreeDto;
import com.ls.trace.web.service.TraceLogTreeService;
import org.apache.commons.lang.StringUtils;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Service
public class TraceLogTreeServiceImpl implements TraceLogTreeService {

    @Autowired
    private EsSearchClient esSearchClient;

    @Override
    public List<TraceLogTreeDto> getTraceLogTree(String tree_id,String app_name) {

        //先验证当天是否有日志输出
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd");
        String nowDateStr = dateFormat.format(new Date());

        EsIndexEnum indexEnum = EsIndexEnum.getEsIndexEnumByAppName(app_name);
        String indexName = indexEnum.getIndex() + nowDateStr;
        if(!esSearchClient.isExistsIndex(indexName)){
            return null;
        }

        SearchSourceBuilder sourceBuilder = SearchSourceBuilder.searchSource();
        //最多展示50条
        sourceBuilder.size(50);
        sourceBuilder.query(new BoolQueryBuilder()
                .must(new MatchQueryBuilder("tree_id.keyword",tree_id))
        );

        SearchRequest searchRequest = new SearchRequest(indexName);
        searchRequest.source(sourceBuilder);

        List<TraceLogTreeDto> list = esSearchClient.search(searchRequest, TraceLogTreeDto.class);
        return treeList(list);
    }

    private List<TraceLogTreeDto> treeList(List<TraceLogTreeDto> list){
        TraceLogTreeDto traceLogTreeDto = new TraceLogTreeDto();
        for(TraceLogTreeDto dto : list){
            String[] node_ids = dto.getNode_id().substring(1,dto.getNode_id().length() - 1).split("\\.");
            if(node_ids.length == 1){
                List<TraceLogTreeDto> node = traceLogTreeDto.getNode();
                traceLogTreeDto = dto;
                traceLogTreeDto.setNode(node);
            }else{
                put(traceLogTreeDto,Arrays.asList(node_ids), dto);
            }
        }
        List<TraceLogTreeDto> l = new ArrayList<>();
        l.add(traceLogTreeDto);
        return l;
    }
    // [1]   [1.1.1]
    private TraceLogTreeDto put(TraceLogTreeDto traceLogTreeDto, List<String> node_ids, TraceLogTreeDto dto){
        if(node_ids.size() > 1){
            if(traceLogTreeDto.getNode() == null){
                traceLogTreeDto.setNode(new ArrayList<>());
                traceLogTreeDto.getNode().add(new TraceLogTreeDto());
            }
            int i = 0;
            for(; i< traceLogTreeDto.getNode().size(); i++){
                TraceLogTreeDto temp = traceLogTreeDto.getNode().get(i);
                if(StringUtils.isBlank(temp.getTree_id())){
                    if(traceLogTreeDto.getNode().size() == 1 || i == (traceLogTreeDto.getNode().size()-1)){
                        break;
                    }
                }else{
                    String[] ns = temp.getNode_id().substring(1, temp.getNode_id().length() - 1).split("\\.");
                    String n = ns[ns.length-1];
                    if(Integer.parseInt(n) == Integer.parseInt(node_ids.get(0))){
                        break;
                    }
                }
            }
            if(node_ids.size() == 2){
                if(i >= traceLogTreeDto.getNode().size()){
                    int j = 0;
                    for(; j < traceLogTreeDto.getNode().size(); j++){
                        String[] ns = traceLogTreeDto.getNode().get(j).getNode_id().substring(1, traceLogTreeDto.getNode().get(j).getNode_id().length() - 1).split("\\.");
                        if(Integer.parseInt(node_ids.get(node_ids.size() - 1)) <= Integer.parseInt(ns[ns.length - 1] )){
                            break;
                        }
                    }
                    traceLogTreeDto.getNode().add(j,dto);
                }else{
                    if(StringUtils.isBlank(traceLogTreeDto.getNode().get(i).getTree_id())){
                        dto.setNode(traceLogTreeDto.getNode().get(i).getNode());
                        traceLogTreeDto.getNode().set(i,dto);
                    }else{
                        traceLogTreeDto.getNode().add(dto);
                    }
                }
            }else{
                if(StringUtils.isBlank(traceLogTreeDto.getNode().get(i).getTree_id())){
                    TraceLogTreeDto d = put(traceLogTreeDto.getNode().get(i),node_ids.subList(1,node_ids.size()),dto);
                    d.setNode(traceLogTreeDto.getNode().get(i).getNode());
                    traceLogTreeDto.getNode().set(i, d);
                }else{
                    put(traceLogTreeDto.getNode().get(i),node_ids.subList(1,node_ids.size()),dto);
                }
            }
            return traceLogTreeDto;
        }else{
            return dto;
        }
    }



}
