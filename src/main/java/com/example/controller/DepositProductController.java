package com.example.controller;

import com.alibaba.fastjson.JSONObject;
import com.example.entity.Result;
import com.example.service.DepositProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

@RestController
@RequestMapping("/jtgk")
@Slf4j
public class DepositProductController {
    @Resource
    private DepositProductService depositProductService;

    @PostMapping("/ckcptb")
    public Result DepositProductService(@RequestBody JSONObject jsonObject) throws ParseException {

        return depositProductService.sendDepositProductToCs(jsonObject);
    }

    @PostMapping("/redisTest5")
    public Result redisTest5(@RequestBody JSONObject jsonObject) throws ParseException {
        try{

            log.error("redisTest1\nredisTest1");
            String repData = "<?xml version=\"1.0\" encoding=\"UTF-8\"?> <lcbank> <SysID>GSCloud</SysID> <TuxName>MQReg</TuxName> <ReqDate>20241021</ReqDate> <ReqTime>185305</ReqTime> <List> <Bill> <BillNo>d9174dad-6476-28d3-503d-e4576c01d913</BillNo> <ReqSeqNo>db2b4ef45bc644ce94455004085ebae6</ReqSeqNo><ReqDate>20241021</ReqDate><Result>2</Result><Descript>消息已到达前置|{\"TimeInfo\":\"TimeInfo的返回值\"}</Descript><ExpFlag/><ExpInfo/><CreateTime>2024-10-21T18:53:05.481414</CreateTime></Bill></List></lcbank>";

            String Descript= getTagValue(repData,"Descript");
            String[] Descripts = Descript.split("|");
            log.error("Descript:{}",Descript);

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String JSSJ = "2024-10-23 10:11:12";
            Date date1 = simpleDateFormat.parse(JSSJ);
            Date date2 = new Date();

            // 计算两个日期的时间差
            long diffInMillies = Math.abs(date2.getTime() - date1.getTime());
            log.error("diffInMillies:{}",diffInMillies);

            Duration duration = Duration.of(diffInMillies, ChronoUnit.MILLIS);

            long days = duration.toDays();           // 获取天数
            duration = duration.minusDays(days);

            long hours = duration.toHours();         // 获取小时数
            duration = duration.minusHours(hours);

            long minutes = duration.toMinutes();     // 获取分钟数
            duration = duration.minusMinutes(minutes);

            long seconds = duration.getSeconds();    // 获取秒数
            log.error("years:{}",seconds);


        }catch (Exception e){

            log.error("redisTest1", "redisTest1=：{}",e);
        }
        return Result.ok();
    }


    public static String getTagValue(String xml, String tagName) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new java.io.ByteArrayInputStream(xml.getBytes()));
            doc.getDocumentElement().normalize();

            NodeList nodeList = doc.getElementsByTagName(tagName);
            if (nodeList.getLength() > 0) {
                Node node = nodeList.item(0);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;
                    return element.getTextContent();
                }
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
