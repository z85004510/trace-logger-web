package com.ls.trace.web;

import com.alibaba.fastjson.JSON;
import com.howbuy.es.client.EsSearchClient;
import com.ls.trace.web.base.enums.EsIndexEnum;
import com.ls.trace.web.base.support.ZkCacheSupport;
import com.ls.trace.web.dto.DataBaseDto;
import com.ls.trace.web.dto.TraceLogTreeDto;
import com.howbuy.zkutils.CfClientBuilder;
import org.apache.commons.lang.StringUtils;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.BucketOrder;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.avg.Avg;
import org.elasticsearch.search.aggregations.metrics.avg.AvgAggregationBuilder;
import org.elasticsearch.search.aggregations.support.ValueType;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class HowbuyTraceLoggerWebApplicationTests {

    @Autowired
    private EsSearchClient esSearchClient;
    @Autowired
    private ZkCacheSupport zkCacheSupport;

    @Test
    public void testDate() throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");


        long now = System.currentTimeMillis();
        System.out.println("--now--" + dateFormat.format(new Date(now)) + "----" + now);

        String time1 = "2018-12-23 15:44";
        System.out.println("--time1--" + time1 + "----" + dateFormat.parse(time1).getTime());

        String time2 = "2018-12-20 11:52";
        System.out.println("--time2--" + time2 + "----" + dateFormat.parse(time2).getTime());

        String time3 = "2018-10-25 16:21";
        System.out.println("--time2--" + time3 + "----" + dateFormat.parse(time3).getTime());
//        1545148800000  1545277920000

        System.out.println("---" + 2 * 24 * 60 * 60 * 1000);

        SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd");
        long startTime = dateFormat.parse(time3).getTime(),
            endTime = dateFormat.parse(time2).getTime();
        //开始时间不能等于当前时间
        if(startTime >= now){
            //默认取当前时间前15分钟
            startTime = now - 900000;
            endTime = now;
        }else{
            //最多查询距离最近7天的数据
            int day = (int) ((now - startTime)/86400000 + ((now - startTime)%86400000 == 0 ? 0 : 1));
            if(day > 7){
                startTime = dateFormat1.parse(dateFormat1.format(new Date(now - 518400000))).getTime();
            }
            if(endTime <= startTime){ //显示当前时间
                endTime = now;
            }
        }

        List<String> timeStrList = new ArrayList<>();
        //判断开始时间与结束时间的差值是否大于1天
        if(endTime - startTime <= 86400000){
            //判断开始时间与结束时间的字符串值是否相同
            if(!dateFormat1.format(new Date(endTime)).equals(dateFormat1.format(new Date(startTime)))){
                timeStrList.add(dateFormat1.format(new Date(startTime)));
            }
            timeStrList.add(dateFormat1.format(new Date(endTime)));
        }else{
            long flag = startTime;
            while(true){
                String str = dateFormat1.format(new Date(flag));
                timeStrList.add(str);
                if(dateFormat1.format(new Date(endTime)).equals(str)){
                    break;
                }
                flag+=86400000;
            }
        }




    }

    @Test
    public void testTraceTree(){
        List<TraceLogTreeDto> list = new ArrayList<>();
        TraceLogTreeDto dto1 = new TraceLogTreeDto();
        dto1.setService_name("JjyjService.getNoEndHbWithHbsale");
        dto1.setTree_id("d86be83d-b66d-4809-a050-e48b93b131c5");
        dto1.setNode_id("[1]");
        TraceLogTreeDto dto2 = new TraceLogTreeDto();
        dto2.setService_name("service1.getService1");
        dto2.setTree_id("d86be83d-b66d-4809-a050-e48b93b131c5");
        dto2.setNode_id("[1.1]");
        TraceLogTreeDto dto3 = new TraceLogTreeDto();
        dto3.setService_name("service3.getService3");
        dto3.setTree_id("d86be83d-b66d-4809-a050-e48b93b131c5");
        dto3.setNode_id("[1.1.1]");
        TraceLogTreeDto dto4 = new TraceLogTreeDto();
        dto4.setService_name("dao1.getDao1");
        dto4.setTree_id("d86be83d-b66d-4809-a050-e48b93b131c5");
        dto4.setNode_id("[1.1.2]");
        TraceLogTreeDto dto5 = new TraceLogTreeDto();
        dto5.setService_name("service2.getService2");
        dto5.setTree_id("d86be83d-b66d-4809-a050-e48b93b131c5");
        dto5.setNode_id("[1.2]");

        list.add(dto4);
        list.add(dto1);
        list.add(dto2);
        list.add(dto3);
        list.add(dto5);
        System.out.println("--- " + JSON.toJSONString(treeList(list)));
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
                        String[] ns = traceLogTreeDto.getNode().get(j).getNode_id().substring(1, dto.getNode_id().length() - 1).split("\\.");
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

    @Test
    public void ZkTest(){
        String appName = "fund-remote";
        String serviceName = "com.howbuy.fund.service.business.product.JjyjService.getNoEndHbWithHbsale(java.lang.String[],com.howbuy.common.sort.OrderField[],int)";
        String serviceName2 = "com.howbuy.fund.service.business.product.JjyjService.getNoEndHbWithHbsale2(java.lang.String[],com.howbuy.common.sort.OrderField[],int)";
        String serviceName3 = "com.howbuy.fund.service.business.product.JjyjService.getNoEndHbWithHbsale3(java.lang.String[],com.howbuy.common.sort.OrderField[],int)";

        String value = zkCacheSupport.get(appName +"/service_name/" + serviceName2);
        System.out.println("------" + value);
//        zkCacheSupport.addString(appName +"/service_name/" + serviceName,"on");
//        zkCacheSupport.addString(appName +"/service_name/" + serviceName2,"on");
//        zkCacheSupport.addString(appName +"/service_name/" + serviceName3,"on");
        zkCacheSupport.delete(appName +"/service_name/" + serviceName2);
        List<String> children = zkCacheSupport.getChildren(appName + "/service_name");
        System.out.println("--" + JSON.toJSONString(children));

    }

    @Test
    public void contextLoads() throws ParseException {
        String str = "2018-12-03 08:47:30";
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd H:m:s");
        Date date = dateFormat.parse(str);
        System.out.println("----" + date.getTime());
    }

    @Test
    public void RequestCostTop50(){

        EsIndexEnum indexEnum = EsIndexEnum.ec_fund_remote_log;

        SearchSourceBuilder sourceBuilder = SearchSourceBuilder.searchSource();
        sourceBuilder.size(0);
        sourceBuilder.aggregation(new TermsAggregationBuilder("2", ValueType.STRING)
                .field("service_name.keyword")
                .size(50)
                .order(BucketOrder.aggregation("1", false))
                .subAggregation(new AvgAggregationBuilder("1").field("cost"))
        );
//        long now = System.currentTimeMillis();
        long now = 1543826850941l;
        //默认取最近30分钟
        sourceBuilder.query(new BoolQueryBuilder()
                .must(new RangeQueryBuilder("time").gte(now - 1800000).lte(now).format("epoch_millis"))
        );
        SearchRequest searchRequest = new SearchRequest(indexEnum.getIndex());
        searchRequest.source(sourceBuilder);

        Aggregations resultAgg = esSearchClient.searchAggs(searchRequest);
        Terms term = resultAgg.get("2");
        List<DataBaseDto> list = new ArrayList<>();
        for(Terms.Bucket bucket : term.getBuckets()){
            String service_name = bucket.getKeyAsString();
            Avg avg = bucket.getAggregations().get("1");
            DataBaseDto dto = new DataBaseDto();
            dto.setCost(avg.getValue());
            dto.setService_name(service_name);
            dto.setApp_name(indexEnum.getApp_name());
            list.add(dto);
        }
        System.out.println("====" + JSON.toJSONString(list));
    }


    public static void main(String[] args) {
        CfClientBuilder cfClientBuilder = new CfClientBuilder("192.168.220.108:2181", 5000, "howbuy-trace-logger");

        cfClientBuilder.newCfClient().addString("howbuy-fund-remote/1/2","2");

        String string = cfClientBuilder.newCfClient().getString("howbuy-fund-remote/1/2");
        List<String> children = cfClientBuilder.newCfClient().getChildren("howbuy-fund-remote/1/2");
        System.out.println("--value-" + string);

        System.out.println(JSON.toJSONString(children));
    }

}
