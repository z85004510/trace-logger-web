package com.ls.trace.web.service.impl;

import com.howbuy.es.client.EsSearchClient;
import com.ls.trace.web.base.enums.EsIndexEnum;
import com.ls.trace.web.dto.DataBaseDto;
import com.ls.trace.web.dto.request.TopDataRequestDto;
import com.ls.trace.web.service.RequestTop50Service;
import org.apache.commons.lang.StringUtils;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.index.query.QueryStringQueryBuilder;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.BucketOrder;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.avg.Avg;
import org.elasticsearch.search.aggregations.metrics.avg.AvgAggregationBuilder;
import org.elasticsearch.search.aggregations.support.ValueType;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class RequestTop50ServiceImpl implements RequestTop50Service {

    @Autowired
    private EsSearchClient esSearchClient;

    @Override
    public List<DataBaseDto> getRequestCostTop50List(EsIndexEnum indexEnum, TopDataRequestDto requestDto) {

        SearchSourceBuilder sourceBuilder = SearchSourceBuilder.searchSource();
        sourceBuilder.size(0);
        sourceBuilder.aggregation(new TermsAggregationBuilder("1", ValueType.STRING)
                .field("service_name.keyword")
                .size(50)
                .order(BucketOrder.aggregation("2", false))
                .subAggregation(new AvgAggregationBuilder("2").field("cost"))
        );
        return queryEs(indexEnum, sourceBuilder,requestDto.getStartTime(), requestDto.getEndTime(),requestDto.getContent());
    }

    @Override
    public List<DataBaseDto> getRequestCountTop50List(EsIndexEnum indexEnum, TopDataRequestDto requestDto) {

        SearchSourceBuilder sourceBuilder = SearchSourceBuilder.searchSource();
        sourceBuilder.size(0);
        sourceBuilder.aggregation(new TermsAggregationBuilder("1", ValueType.STRING)
                .field("service_name.keyword")
                .size(50)
                .order(BucketOrder.count(false))
                .subAggregation(new AvgAggregationBuilder("2").field("cost"))
        );
        return queryEs(indexEnum, sourceBuilder,requestDto.getStartTime(), requestDto.getEndTime(),requestDto.getContent());
    }

    /**
     * startTime,endTime格式为yyyy-MM-dd HH:mm
     * @date: 2018年12月25日
     * @author: leslie.zhang
     */
    private List<DataBaseDto> queryEs(EsIndexEnum indexEnum, SearchSourceBuilder sourceBuilder, long startTime, long endTime, String queryServiceName) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyy.MM.dd");
        long now = System.currentTimeMillis();
        //开始时间不能等于当前时间
        if(startTime >= now){
            //默认取当前时间前15分钟
            startTime = now - 900000;
            endTime = now;
        }else{
            //最多查询距离最近7天的数据
            int day = (int) ((now - startTime)/86400000 + ((now - startTime)%86400000 == 0 ? 0 : 1));
            if(day > 7){
                try {
                    startTime = dateFormat.parse(dateFormat.format(new Date(now - 518400000))).getTime();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            if(endTime <= startTime){ //显示当前时间
                endTime = now;
            }
        }

        List<String> indexList = new ArrayList<>();
        //判断开始时间与结束时间的差值是否大于1天
        if(endTime - startTime <= 86400000){
            //判断开始时间与结束时间的字符串值是否相同
            if(!dateFormat.format(new Date(endTime)).equals(dateFormat.format(new Date(startTime)))){
                String startTimeStr = dateFormat1.format(new Date(startTime));
                if(esSearchClient.isExistsIndex(indexEnum.getIndex() + startTimeStr)){
                    indexList.add(indexEnum.getIndex() + startTimeStr);
                }
            }
            String endTimeStr = dateFormat1.format(new Date(endTime));
            if(esSearchClient.isExistsIndex(indexEnum.getIndex() + endTimeStr)){
                indexList.add(indexEnum.getIndex() + endTimeStr);
            }
        }else{
            long flag = startTime;
            while(true){
                String str = dateFormat1.format(new Date(flag));
                if(esSearchClient.isExistsIndex(indexEnum.getIndex() + str)){
                    indexList.add(indexEnum.getIndex() + str);
                }
                if(dateFormat1.format(new Date(endTime)).equals(str)){
                    break;
                }
                flag+=86400000;
            }
        }



        if(indexList.size() == 0){
            return null;
        }


        if(!StringUtils.isBlank(queryServiceName)){
            sourceBuilder.query(new QueryStringQueryBuilder("*" + queryServiceName + "*").field("service_name"));
        }

        SearchRequest searchRequest = new SearchRequest(indexList.toArray(new String[0]));
        searchRequest.source(sourceBuilder);

        Aggregations resultAgg = esSearchClient.searchAggs(searchRequest);

        Terms term = resultAgg.get("1");
        List<DataBaseDto> list = new ArrayList<>();
        for(Terms.Bucket bucket : term.getBuckets()){
            String service_name = bucket.getKeyAsString();
            Avg avg = bucket.getAggregations().get("2");
            DataBaseDto dto = new DataBaseDto();
            dto.setCost((int)avg.getValue());
            dto.setDoc_count((int)bucket.getDocCount());
            dto.setService_name(service_name);
            dto.setApp_name(indexEnum.getApp_name());
            list.add(dto);
        }
        return list;
    }
}
