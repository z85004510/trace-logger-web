package com.ls.trace.web.service.impl;

import com.howbuy.es.client.EsSearchClient;
import com.ls.trace.web.base.enums.EsIndexEnum;
import com.ls.trace.web.dto.DataBaseDto;
import com.ls.trace.web.dto.request.ServiceFlowRequestDto;
import com.ls.trace.web.service.FlowListService;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class FlowListServiceImpl implements FlowListService {

    @Autowired
    private EsSearchClient esSearchClient;

    @Override
    public List<DataBaseDto> getFlowList(ServiceFlowRequestDto dto) {
        //先验证当天是否有日志输出
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd");
        //固定查询2天
        long endTime = System.currentTimeMillis();
        long startTime = endTime - 86400000;
        String endTimeStr = dateFormat.format(new Date(endTime));
        String startTimeStr = dateFormat.format(new Date(startTime));
        EsIndexEnum indexEnum = EsIndexEnum.getEsIndexEnumByAppName(dto.getApp_name());
        List<String> indexList = new ArrayList<>();
        if(esSearchClient.isExistsIndex(indexEnum.getIndex() + endTimeStr)){
            indexList.add(indexEnum.getIndex() + endTimeStr);
        }
        if(esSearchClient.isExistsIndex(indexEnum.getIndex() + startTimeStr)){
            indexList.add(indexEnum.getIndex() + startTimeStr);
        }

        if(indexList.size() == 0){
            return null;
        }


        SearchSourceBuilder sourceBuilder = SearchSourceBuilder.searchSource();
        //最多展示50条
        sourceBuilder.size(10);
        sourceBuilder.query(new BoolQueryBuilder()
                .must(new RangeQueryBuilder("time").gte(startTime).lte(endTime))
                .must(new MatchQueryBuilder("app_name.keyword",dto.getApp_name()))
                .must(new MatchQueryBuilder("service_name.keyword",dto.getService_name()))
        );

        SearchRequest searchRequest = new SearchRequest(indexList.toArray(new String[0]));
        sourceBuilder.sort("time",SortOrder.DESC);
        searchRequest.source(sourceBuilder);

        List<DataBaseDto> list = esSearchClient.search(searchRequest, DataBaseDto.class);
        for(DataBaseDto d : list){
            d.setService_name(d.getService_name());
        }
        return list;
    }


}
