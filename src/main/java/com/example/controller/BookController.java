package com.example.controller;

import com.alibaba.fastjson.JSONObject;
import com.example.entity.BankTransDetail;
import com.example.entity.JtgkDqckCsFhSjEntity;
import com.example.entity.JtgkTmckcpEntity;
import com.example.entity.Result;
import com.example.service.ClientTest;
import com.example.service.SpgtmBankTransDetailService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.annotation.Resource;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.Base64;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/jtgk/spgtm/settlement/v1.0")
@Slf4j
public class BookController {

    @Resource
    private SpgtmBankTransDetailService spgtmBankTransDetailService;
    @Resource
    private ClientTest clientTest;
    @Resource
    private RedisTemplate redisTemplate;
    private static final String LOCK_KEY = "EM_WHYQTQJGSQ_REDIS";
    private static final long EXPIRE_TIME = 30000; // 锁的过期时间，单位为毫秒
    private static final long WAIT_TIME = 1000; // 获取锁时的等待时间，单位为毫秒

    @PostMapping("/getBankTransDetail")
    public Result getBankTransDetail(@RequestBody BankTransDetail bankTransDetail){
        log.info("银行流水通用查询接口参数controller传入:{}",bankTransDetail.toString());
        return spgtmBankTransDetailService.getBankTransDetail(bankTransDetail);
    }


    @PostMapping("/redisTest")
    public Result redisTest(@RequestBody JSONObject jsonObject) throws ParseException {

        String repData = "<Bill><BillNo>9e332dc8-1bb6-c492-e9a6-de58af0384b9</BillNo><ReqSeqNo>37d61f58d6584277a1a09ff7bff68095</ReqSeqNo><ReqDate>20241129</ReqDate><Result>2</Result><Descript>已提交;00:财司接收未处理{\"JSSJ\":\"2024-11-29 09:36:53\",\"JBSJ\":\"\",\"SPSJ\":\"\",\"FSSJ\":\"\"}</Descript><ExpFlag/><ExpInfo/><CreateTime>2024-11-29T09:48:22.330087</CreateTime></Bill>";
        if(StringUtils.isNotBlank(repData)) {
            //现在返回的Descript信息是从财务公司的BillInfo节点获取的，我们把新加的TimeInfo节点值也放到Descript节点。放的格式是X|{"TimeInfo":"TimeInfo的返回值"}。X为原来Descript信息。老师您看这样行吗？
            String Descript = getTagValue(repData,"Descript");
            log.error("Descript：{}",Descript);

            if(StringUtils.isNotBlank(Descript)){
                String[] Descripts = Descript.split("\\{");
                log.error("Descripts：{}",Descripts);
                log.error("Descripts.size：{}",Descripts.length);

                //{"JSSJ":"2024-09-13 10:11:12","JBSJ":"2024-09-13 10:21:12","SPSJ":"2024-09-13 10:31:12","FSSJ":"2024-09-13 10:41:12"}
                //字段依次为：
                //财司接收时间、营业部经办完成时间、营业部审批完成时间、财司发送银行时间
                //对于不落地单据，营业部经办完成时间、营业部审批完成时间 为空
                for(int i=0; i<Descripts.length ;i++){
                    if(Descripts[i].contains("JSSJ")){
                        Descripts[i] = "{" + Descripts[i];
                        JSONObject jsonDescript = JSONObject.parseObject(Descripts[i]);
                        log.error("jsonDescript：{}",jsonDescript);

                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        String JSSJ = jsonDescript.getString("JSSJ");//财司接收时间 --作为营业部经办开始时间
                        String JBSJ = jsonDescript.getString("JBSJ");//营业部经办完成时间 -- 营业部审批开始时间
                        String SPSJ = jsonDescript.getString("SPSJ");//营业部审批完成时间
                        String FSSJ = jsonDescript.getString("FSSJ");//财司发送银行时间 --财司发送银行开始时间

                        String taskType = "";
                        String date = "";
                        //根据id更新 JSSJ JBSJ SPSJ FSSJ
                        if(StringUtils.isNotBlank(JSSJ)){
                            taskType = "JBSJ";
                            date = JSSJ;
                        } else if (StringUtils.isNotBlank(JBSJ)) {
                            taskType = "SPSJ";
                            date = JBSJ;
                        } else if (StringUtils.isNotBlank(FSSJ)) {
                            taskType = "FSSJ";
                            date = FSSJ;
                        }
                        log.error("date:{}",date);

                        // 计算两个日期的时间差
                        if(StringUtils.isNotBlank(date)){
                            Date date1 = simpleDateFormat.parse(date);
                            Date date2 = new Date();
                            log.error("date1:{},date2:{}",date1,date2);

                            long diffInMillies = Math.abs(date2.getTime() - date1.getTime());
                            Duration duration = Duration.of(diffInMillies, ChronoUnit.MILLIS);

                            long days = duration.toDays();           // 获取天数
                            duration = duration.minusDays(days);

                            long hours = duration.toHours();         // 获取小时数
                            duration = duration.minusHours(hours);

                            long minutes = duration.toMinutes();     // 获取分钟数
                            duration = duration.minusMinutes(minutes);

                            long seconds = duration.getSeconds();    // 获取秒数

                            String REMAINING_TIME1 = String.valueOf(diffInMillies);
                            String REMAINING_TIME = days + "天" + hours + "时" + minutes + "分" + seconds + "秒";
                            log.error("REMAINING_TIME1:{},REMAINING_TIME:{}",REMAINING_TIME1,REMAINING_TIME);


                        }
                    }
                }
            }
        }




        return Result.ok();
    }

    private Boolean isChange(JtgkTmckcpEntity jtgkTmckcpOld,JtgkTmckcpEntity jtgkTmckcpNew){
        log.error("判断实体类（JtgkTmckcpEntity）是否变化 jtgkTmckcpOld:{},jtgkTmckcpNew:{}", JSONObject.toJSONString(jtgkTmckcpOld) + "===="+JSONObject.toJSONString(jtgkTmckcpNew));
        log.error("判断实体类（JtgkTmckcpEntity）是否变化 jtgkTmckcpOld:{},jtgkTmckcpNew:{}",
                "!Objects.equals(jtgkTmckcpNew.getZl(),jtgkTmckcpOld.getZl())："+!Objects.equals(jtgkTmckcpNew.getZl(),jtgkTmckcpOld.getZl())+
                        "!Objects.equals(jtgkTmckcpNew.getTimestamps_createdby(),jtgkTmckcpOld.getTimestamps_createdby())："+!Objects.equals(jtgkTmckcpNew.getTimestamps_createdby(),jtgkTmckcpOld.getTimestamps_createdby())+
                        "!Objects.equals(jtgkTmckcpNew.getCode(),jtgkTmckcpOld.getCode())："+!Objects.equals(jtgkTmckcpNew.getCode(),jtgkTmckcpOld.getCode())+
                        "!Objects.equals(jtgkTmckcpNew.getName_chs(),jtgkTmckcpOld.getName_chs())："+!Objects.equals(jtgkTmckcpNew.getName_chs(),jtgkTmckcpOld.getName_chs())+
                        "!Objects.equals(jtgkTmckcpNew.getLx(),jtgkTmckcpOld.getLx())："+!Objects.equals(jtgkTmckcpNew.getLx(),jtgkTmckcpOld.getLx())+
                        "!Objects.equals(jtgkTmckcpNew.getCurrency(),jtgkTmckcpOld.getCurrency())："+!Objects.equals(jtgkTmckcpNew.getCurrency(),jtgkTmckcpOld.getCurrency())+
                        "!Objects.equals(jtgkTmckcpNew.getZdqx(),jtgkTmckcpOld.getZdqx())："+!Objects.equals(jtgkTmckcpNew.getZdqx(),jtgkTmckcpOld.getZdqx())+
                        "!Objects.equals(jtgkTmckcpNew.getQxdw(),jtgkTmckcpOld.getQxdw())："+!Objects.equals(jtgkTmckcpNew.getQxdw(),jtgkTmckcpOld.getQxdw())+
                        "!Objects.equals(jtgkTmckcpNew.getStartdate(),jtgkTmckcpOld.getStartdate())："+!Objects.equals(jtgkTmckcpNew.getStartdate(),jtgkTmckcpOld.getStartdate())+
                        "!Objects.equals(jtgkTmckcpNew.getEnddate(),jtgkTmckcpOld.getEnddate())："+!Objects.equals(jtgkTmckcpNew.getEnddate(),jtgkTmckcpOld.getEnddate())+
                        "!Objects.equals(jtgkTmckcpNew.getInterestratetype(),jtgkTmckcpOld.getInterestratetype())："+!Objects.equals(jtgkTmckcpNew.getInterestratetype(),jtgkTmckcpOld.getInterestratetype())+
                        "!Objects.equals(jtgkTmckcpNew.getFdxx(),jtgkTmckcpOld.getFdxx())："+!Objects.equals(jtgkTmckcpNew.getFdxx(),jtgkTmckcpOld.getFdxx())+
                        "!Objects.equals(jtgkTmckcpNew.getFdsx(),jtgkTmckcpOld.getFdsx())："+!Objects.equals(jtgkTmckcpNew.getFdsx(),jtgkTmckcpOld.getFdsx())+
                        "!Objects.equals(jtgkTmckcpNew.getInterestrate(),jtgkTmckcpOld.getInterestrate())："+!Objects.equals(jtgkTmckcpNew.getInterestrate(),jtgkTmckcpOld.getInterestrate())+
                        "!Objects.equals(jtgkTmckcpNew.getJzlv(),jtgkTmckcpOld.getJzlv())："+!Objects.equals(jtgkTmckcpNew.getJzlv(),jtgkTmckcpOld.getJzlv())+
                        "!Objects.equals(jtgkTmckcpNew.getLastmodifiedtime(),jtgkTmckcpOld.getLastmodifiedtime())："+!Objects.equals(jtgkTmckcpNew.getLastmodifiedtime(),jtgkTmckcpOld.getLastmodifiedtime())+
                        "!Objects.equals(jtgkTmckcpNew.getAmount(),jtgkTmckcpOld.getAmount())："+!Objects.equals(jtgkTmckcpNew.getAmount(),jtgkTmckcpOld.getAmount())+
                        "!Objects.equals(jtgkTmckcpNew.getState(),jtgkTmckcpOld.getState())："+!Objects.equals(jtgkTmckcpNew.getState(),jtgkTmckcpOld.getState())+
                        "!Objects.equals(jtgkTmckcpNew.getTimestamps_lastchangedby(),jtgkTmckcpOld.getTimestamps_lastchangedby())："+!Objects.equals(jtgkTmckcpNew.getTimestamps_lastchangedby(),jtgkTmckcpOld.getTimestamps_lastchangedby())
        );
        if(!Objects.equals(jtgkTmckcpNew.getZl(),jtgkTmckcpOld.getZl()) ||
                !Objects.equals(jtgkTmckcpNew.getTimestamps_createdby(),jtgkTmckcpOld.getTimestamps_createdby()) ||
                !Objects.equals(jtgkTmckcpNew.getCode(),jtgkTmckcpOld.getCode()) ||
                !Objects.equals(jtgkTmckcpNew.getName_chs(),jtgkTmckcpOld.getName_chs()) ||
                !Objects.equals(jtgkTmckcpNew.getLx(),jtgkTmckcpOld.getLx()) ||
                !Objects.equals(jtgkTmckcpNew.getCurrency(),jtgkTmckcpOld.getCurrency()) ||
                !Objects.equals(jtgkTmckcpNew.getZdqx(),jtgkTmckcpOld.getZdqx()) ||
                !Objects.equals(jtgkTmckcpNew.getQxdw(),jtgkTmckcpOld.getQxdw()) ||
                !Objects.equals(jtgkTmckcpNew.getStartdate(),jtgkTmckcpOld.getStartdate()) ||
                !Objects.equals(jtgkTmckcpNew.getEnddate(),jtgkTmckcpOld.getEnddate()) ||
                !Objects.equals(jtgkTmckcpNew.getInterestratetype(),jtgkTmckcpOld.getInterestratetype()) ||
                !Objects.equals(jtgkTmckcpNew.getFdxx(),jtgkTmckcpOld.getFdxx()) ||
                !Objects.equals(jtgkTmckcpNew.getFdsx(),jtgkTmckcpOld.getFdsx()) ||
                !Objects.equals(jtgkTmckcpNew.getInterestrate(),jtgkTmckcpOld.getInterestrate())||
                !Objects.equals(jtgkTmckcpNew.getJzlv(),jtgkTmckcpOld.getJzlv()) ||
                !Objects.equals(jtgkTmckcpNew.getLastmodifiedtime(),jtgkTmckcpOld.getLastmodifiedtime()) ||
                !Objects.equals(jtgkTmckcpNew.getAmount(),jtgkTmckcpOld.getAmount()) ||
                !Objects.equals(jtgkTmckcpNew.getState(),jtgkTmckcpOld.getState()) ||
                !Objects.equals(jtgkTmckcpNew.getTimestamps_lastchangedby(),jtgkTmckcpOld.getTimestamps_lastchangedby())
        ){
            return true;
        }
        return false;
    }


    @PostMapping("/redisTest1")
    public Result redisTest1(@RequestBody JSONObject jsonObject) throws ParseException {

        try{


            String FilePath = "20181029\\010399999\\123401.pdf";

            String[] FilePaths = FilePath.split("\\\\");
            String FilePath2 = FilePaths[FilePaths.length-1];


            InputStream inputStream = returnBitMap("192.168.92.1",FilePath);
            String Base64String = inputStreamToBase64(inputStream);


            log.info("redisTest1", "redisTest1=：{}",Base64String);
        }catch (Exception e){

            log.error("redisTest1", "redisTest1=：{}",e);
        }
        return Result.ok();
    }

    @PostMapping("/redisTest2")
    public Result redisTest2(@RequestBody JSONObject jsonObject) throws ParseException {

        try{


            String filePath = "C:\\Users\\hp\\Pictures\\Camera Roll\\asd.jpg"; // 替换为您的文件路径
            String base64String = convertFileToBase64(new File(filePath));
            log.error(base64String);
        }catch (Exception e){

            log.error("redisTest1", "redisTest1=：{}",e);


        }
        return Result.ok();
    }

    @PostMapping("/redisTest3")
    public Result redisTest3(@RequestBody JSONObject jsonObject) throws ParseException {

        try{


            String repData = "<?xml version='1.0' encoding='GBK'?>\n" +                     "<FTMS>\n" +                     "    <head>\n" +                     "        <CustIP>10.50.225.1</CustIP>\n" +                     "        <CustOpr>101026198003021328</CustOpr>\n" +                     "        <TransID>202409251039065936thchh7t8e8jm</TransID>\n" +                     "        <TransCode>DEP011</TransCode>\n" +                     "        <RespCode>F001</RespCode>\n" +                     "        <RespInfo>交易成功</RespInfo>\n" +                     "    </head>\n" +                     "    <body>\n" +                     "        <Bill>\n" +                     "            <BusiStatus>1</BusiStatus>\n" +                     "            <ProtocolId>XY_2024092513065</ProtocolId>\n" +                     "            <ProtocolCode>DC20240117000064</ProtocolCode>\n" +                     "            <ProtocolAccCode>99011400104000000003</ProtocolAccCode>\n" +                     "            <CurrType>CNY</CurrType>\n" +                     "            <StartDate>20240118</StartDate>\n" +                     "            <ExpireDate>20240119</ExpireDate>\n" +                     "            <IsTranDeposit>1</IsTranDeposit>\n" +                     "            <Amt>7000249.5</Amt>\n" +                     "            <Rate>1.3</Rate>\n" +                     "            <FloatType>1</FloatType>\n" +                     "            <ZjlvdcNlv>1.1</ZjlvdcNlv>\n" +                     "            <FloatMethod>1</FloatMethod>\n" +                     "            <FloatValue>20</FloatValue>\n" +                     "            <ProtocolType>01</ProtocolType>\n" +                     "            <ProdFactId>CP20240924000001</ProdFactId>\n" +                     "            <IsTranTake>0</IsTranTake>\n" +                     "            <TranDepositType>1</TranDepositType>\n" +                     "            <InterestAccCode>99011131104000001001</InterestAccCode>\n" +                     "            <StartAccualDate>20240118</StartAccualDate>\n" +                     "        </Bill>\n" +                     "        <Bill>\n" +                     "            <BusiStatus>1</BusiStatus>\n" +                     "            <ProtocolId>DC20240116000060</ProtocolId>\n" +                     "            <ProtocolCode>DC20240116000060</ProtocolCode>\n" +                     "            <ProtocolAccCode>99011400104000000002</ProtocolAccCode>\n" +                     "            <CurrType>CNY</CurrType>\n" +                     "            <StartDate>20240116</StartDate>\n" +                     "            <ExpireDate>20240716</ExpireDate>\n" +                     "            <IsTranDeposit>0</IsTranDeposit>\n" +                     "            <Amt>700000</Amt>\n" +                     "            <Rate>1.82</Rate>\n" +                     "            <FloatType>1</FloatType>\n" +                     "            <ZjlvdcNlv>1.3</ZjlvdcNlv>\n" +                     "            <FloatMethod>0</FloatMethod>\n" +                     "            <FloatValue>40</FloatValue>\n" +                     "            <ProtocolType>01</ProtocolType>\n" +                     "            <ProdFactId>CP20200603000001</ProdFactId>\n" +                     "            <IsTranTake>1</IsTranTake>\n" +                     "            <TranDepositType></TranDepositType>\n" +                     "            <InterestAccCode>99011131104000001001</InterestAccCode>\n" +                     "            <StartAccualDate>20240116</StartAccualDate>\n" +                     "        </Bill>\n" +                     "        <Bill>\n" +                     "            <BusiStatus>2</BusiStatus>\n" +                     "            <ProtocolId>DC20240116000061</ProtocolId>\n" +                     "            <ProtocolCode>DC20240116000061</ProtocolCode>\n" +                     "            <ProtocolAccCode>99011400101005000001</ProtocolAccCode>\n" +                     "            <CurrType>CNY</CurrType>\n" +                     "            <StartDate>20240116</StartDate>\n" +                     "            <ExpireDate>20240117</ExpireDate>\n" +                     "            <IsTranDeposit>1</IsTranDeposit>\n" +                     "            <Amt>100000</Amt>\n" +                     "            <Rate>1.3</Rate>\n" +                     "            <FloatType>1</FloatType>\n" +                     "            <ZjlvdcNlv>1.1</ZjlvdcNlv>\n" +                     "            <FloatMethod>1</FloatMethod>\n" +                     "            <FloatValue>20</FloatValue>\n" +                     "            <ProtocolType>01</ProtocolType>\n" +                     "            <ProdFactId>CP20240924000001</ProdFactId>\n" +                     "            <IsTranTake>0</IsTranTake>\n" +                     "            <TranDepositType>1</TranDepositType>\n" +                     "            <InterestAccCode>99011131104000001001</InterestAccCode>\n" +                     "            <StartAccualDate>20240116</StartAccualDate>\n" +                     "        </Bill>\n" +                     "        <Bill>\n" +                     "            <BusiStatus>2</BusiStatus>\n" +                     "            <ProtocolId>XY_2024092413056</ProtocolId>\n" +                     "            <ProtocolCode>DC20240116000061</ProtocolCode>\n" +                     "            <ProtocolAccCode>99011400101005000001</ProtocolAccCode>\n" +                     "            <CurrType>CNY</CurrType>\n" +                     "            <StartDate>20240117</StartDate>\n" +                     "            <ExpireDate>20240118</ExpireDate>\n" +                     "            <IsTranDeposit>1</IsTranDeposit>\n" +                     "            <Amt>100003.56</Amt>\n" +                     "            <Rate>1.3</Rate>\n" +                     "            <FloatType>1</FloatType>\n" +                     "            <ZjlvdcNlv>1.1</ZjlvdcNlv>\n" +                     "            <FloatMethod>1</FloatMethod>\n" +                     "            <FloatValue>20</FloatValue>\n" +                     "            <ProtocolType>01</ProtocolType>\n" +                     "            <ProdFactId>CP20240924000001</ProdFactId>\n" +                     "            <IsTranTake>0</IsTranTake>\n" +                     "            <TranDepositType>1</TranDepositType>\n" +                     "            <InterestAccCode>99011131104000001001</InterestAccCode>\n" +                     "            <StartAccualDate>20240117</StartAccualDate>\n" +                     "        </Bill>\n" +                     "        <Bill>\n" +                     "            <BusiStatus>2</BusiStatus>\n" +                     "            <ProtocolId>DC20240117000064</ProtocolId>\n" +                     "            <ProtocolCode>DC20240117000064</ProtocolCode>\n" +                     "            <ProtocolAccCode>99011400104000000003</ProtocolAccCode>\n" +                     "            <CurrType>CNY</CurrType>\n" +                     "            <StartDate>20240117</StartDate>\n" +                     "            <ExpireDate>20240118</ExpireDate>\n" +                     "            <IsTranDeposit>1</IsTranDeposit>\n" +                     "            <Amt>7000000</Amt>\n" +                     "            <Rate>1.3</Rate>\n" +                     "            <FloatType>1</FloatType>\n" +                     "            <ZjlvdcNlv>1.1</ZjlvdcNlv>\n" +                     "            <FloatMethod>1</FloatMethod>\n" +                     "            <FloatValue>20</FloatValue>\n" +                     "            <ProtocolType>01</ProtocolType>\n" +                     "            <ProdFactId>CP20240924000001</ProdFactId>\n" +                     "            <IsTranTake>0</IsTranTake>\n" +                     "            <TranDepositType>1</TranDepositType>\n" +                     "            <InterestAccCode>99011131104000001001</InterestAccCode>\n" +                     "            <StartAccualDate>20240117</StartAccualDate>\n" +                     "        </Bill>\n" +                     "    </body>\n" +                     "</FTMS>";

            Document document = DocumentHelper.parseText(repData);
            Element reRoot = document.getRootElement();
            Element head = reRoot.element("head");
            String respCode = head.element("RespCode").getTextTrim();
            if ("F001".equals(respCode)) {
                List<Element> bills = reRoot.element("body").elements("Bill");//获取响应报文信息的BILL节点
                bills.get(0).element("StartAccualDate").getTextTrim();

                List<JtgkDqckCsFhSjEntity> jtgkDqckCsFhSjEntities= new ArrayList<>();
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
                for(Element bill : bills){
                    JtgkDqckCsFhSjEntity jtgkDqckCsFhSjEntity = new JtgkDqckCsFhSjEntity();
                    //财司返回参数
                    jtgkDqckCsFhSjEntity.setProtocolId(bill.element("ProtocolId") == null ? "" : bill.element("ProtocolId").getTextTrim());//协议主键ID
                    jtgkDqckCsFhSjEntity.setBusiStatus(bill.element("BusiStatus") == null ? "" : bill.element("BusiStatus").getTextTrim());//协议状态 1、生效  2、终止
                    jtgkDqckCsFhSjEntity.setProtocolCode(bill.element("ProtocolCode") == null ? "" : bill.element("ProtocolCode").getTextTrim());//协议号
                    jtgkDqckCsFhSjEntity.setProtocolAccCode(bill.element("ProtocolAccCode") == null ? "" : bill.element("ProtocolAccCode").getTextTrim());//协议账户
                    String CurrType = bill.element("CurrType") == null ? "" : bill.element("CurrType").getTextTrim();//币种 货币码的国际标准（3个大写的英文字母）CNY-人民币
                    if(!StringUtils.isEmpty(CurrType) && CurrType.equals("CNY")){
                        jtgkDqckCsFhSjEntity.setCurrType("RMB");//币种转换，只有人民币需要转换
                    }
                    jtgkDqckCsFhSjEntity.setStartDate(bill.element("StartDate") == null ? "" : bill.element("StartDate").getTextTrim());//协议开始日期
                    jtgkDqckCsFhSjEntity.setExpireDate(bill.element("ExpireDate") == null ? "" : bill.element("ExpireDate").getTextTrim());//协议到期日期
                    jtgkDqckCsFhSjEntity.setIsTranDeposit(bill.element("IsTranDeposit") == null ? "" : bill.element("IsTranDeposit").getTextTrim());//到期自动顺延 “1”是“0”否
                    jtgkDqckCsFhSjEntity.setRate(bill.element("Rate") == null ? new BigDecimal(0) : new BigDecimal(bill.element("Rate").getTextTrim().equals("") ? "0" :bill.element("Rate").getTextTrim()));//存款利率 百分比，10%填10
                    jtgkDqckCsFhSjEntity.setAmt(bill.element("Amt") == null ? new BigDecimal(0) : new BigDecimal(bill.element("Amt").getTextTrim().equals("") ? "0" :bill.element("Amt").getTextTrim()));//协议金额
                    jtgkDqckCsFhSjEntity.setFloatType(bill.element("FloatType") == null ? "" : bill.element("FloatType").getTextTrim());//是否固定利率（浮动类型） 1浮动，2不浮动
                    jtgkDqckCsFhSjEntity.setZjlvdcNlv(bill.element("ZjlvdcNlv") == null ? new BigDecimal(0) : new BigDecimal(bill.element("ZjlvdcNlv").getTextTrim().equals("") ? "0" : bill.element("ZjlvdcNlv").getTextTrim()));//基准利率 百分比，10%填10
                    jtgkDqckCsFhSjEntity.setFloatMethod(bill.element("FloatMethod") == null ? "" : bill.element("FloatMethod").getTextTrim());//浮动方式 0按比例，1按加减点
                    jtgkDqckCsFhSjEntity.setFloatValue(bill.element("FloatValue") == null ? new BigDecimal(0) : new BigDecimal(bill.element("FloatValue").getTextTrim().equals("") ? "0" :bill.element("FloatValue").getTextTrim()));//浮动值 百分比，10%填10

                    //2024/09/19修改
                    jtgkDqckCsFhSjEntity.setProtocolType(bill.element("ProtocolType") == null ? "" : bill.element("ProtocolType").getTextTrim());//协议类型 01-定期存款，03-协定存款
                    jtgkDqckCsFhSjEntity.setProdFactId(bill.element("ProdFactId") == null ? "" : bill.element("ProdFactId").getTextTrim());//存款产品id 对应存款产品同步DEP001接口中的 产品ID 字段
                    jtgkDqckCsFhSjEntity.setIsTranTake(bill.element("IsTranTake") == null ? "" : bill.element("IsTranTake").getTextTrim());//到期是否自动取款 “1”是“0”否。
                    jtgkDqckCsFhSjEntity.setTranDepositType(bill.element("TranDepositType") == null ? "" : bill.element("TranDepositType").getTextTrim());//续存方式 “1”本息“0”本金，到期是否续存为是时必传。
                    jtgkDqckCsFhSjEntity.setInterestAccCode(bill.element("InterestAccCode") == null ? "" : bill.element("InterestAccCode").getTextTrim());//收息账号 财司客户账户，9901开头的内部户

                    String StartAccualDate= bill.element("StartAccualDate") == null ? "" : bill.element("StartAccualDate").getTextTrim();//起息日期
                    jtgkDqckCsFhSjEntity.setStartAccualDate(StringUtils.isEmpty(StartAccualDate) ? null : simpleDateFormat.parse(StartAccualDate));
                    jtgkDqckCsFhSjEntities.add(jtgkDqckCsFhSjEntity);
                }

                jtgkDqckCsFhSjEntities = jtgkDqckCsFhSjEntities.stream()
                        .sorted(Comparator.comparing(JtgkDqckCsFhSjEntity::getStartAccualDate))
                        .collect(Collectors.toList());

                for(JtgkDqckCsFhSjEntity jtgkDqckCsFhSjEntity : jtgkDqckCsFhSjEntities){
                    log.error(jtgkDqckCsFhSjEntity.getProtocolId());
                }
            }

        }catch (Exception e){

            log.error("redisTest1", "redisTest1=：{}",e);
        }
        return Result.ok();
    }

    @PostMapping("/redisTest4")
    public Result redisTest4(@RequestBody JSONObject jsonObject) throws ParseException {
        try{

            String FilePath = "/20241017/TZ20241016599933.pdf";
            //FilePath 全地址 例/20241017/TZ20241016599933.pdf
            String[] FilePaths = FilePath.split("/");
            log.error("文件路径FilePaths：{}",FilePaths);

            //获取文件路径 例/20241017
            String path= FilePath.substring(0,FilePath.indexOf(FilePaths[FilePaths.length-1])-1);
            log.error("文件路径path：{}",path);

            //获取文件名 例TZ20241016599933.pdf
            String file = FilePath.substring(FilePath.indexOf(FilePaths[FilePaths.length-1]),FilePath.length());
            log.error("文件路径file：{}",file);


        }catch (Exception e){

            log.error("redisTest1", "redisTest1=：{}",e);
        }
        return Result.ok();
    }

    @PostMapping("/redisTest5")
    public Result redisTest5(@RequestBody JSONObject jsonObject) throws ParseException {
        try{

            String repData = "<?xml version=\\\"1.0\\\" encoding=\\\"GBK\\\"?> <lcbank> <SysID>GSCloud</SysID> <TuxName>MQReg</TuxName> <ReqDate>20241021</ReqDate> <ReqTime>185305</ReqTime> <List> <Bill> <BillNo>d9174dad-6476-28d3-503d-e4576c01d913</BillNo> <ReqSeqNo>db2b4ef45bc644ce94455004085ebae6</ReqSeqNo><ReqDate>20241021</ReqDate><Result>2</Result><Descript>消息已到达前置</Descript><ExpFlag/><ExpInfo/><CreateTime>2024-10-21T18:53:05.481414</CreateTime></Bill></List></lcbank>";
            Document document = DocumentHelper.parseText(repData);
            Element reroot = document.getRootElement();
            Element head = reroot.element("head");


        }catch (Exception e){

            log.error("redisTest1", "redisTest1=：{}",e);
        }
        return Result.ok();
    }

    /**
     * 获取财司文件
     * @param urlStr ip
     * @param FilePath 文件路径
     * @return
     */
    private InputStream returnBitMap(String urlStr, String FilePath) {
        //拼接文件地址
        String path = "";
        if(FilePath.charAt(0) == '\\'){
            path = urlStr + FilePath;
        }else {
            path = urlStr + "\\" + FilePath;
        }

        URL url = null;
        InputStream is = null;
        try {
            url = new URL(path);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        try {
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();//利用HttpURLConnection对象,我们可以从网络中获取网页数据.
            conn.setDoInput(true);
            conn.connect();
            is = conn.getInputStream();    //得到网络返回的输入流

        } catch (IOException e) {
            e.printStackTrace();
            log.error("getDepCerDownToCs", "存款证书下载文件错误=：{}",e);
        }

        return is;
    }

    /**
     * InputStream转换为Base64编码的字符串
     * @param inputStream
     * @return
     * @throws IOException
     */
    private  String inputStreamToBase64(InputStream inputStream) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int bytesRead;

        while ((bytesRead = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
        }

        byte[] bytes = outputStream.toByteArray();
//        return Base64.encodeBase64String(bytes);
        return null;
    }

    public static String convertFileToBase64(File file) {
        if (file == null || !file.exists()) {
            return null;
        }

        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            byte[] fileBytes = new byte[(int) file.length()];
            fileInputStream.read(fileBytes);
            return Base64.getEncoder().encodeToString(fileBytes);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取xml节点信息
     * @param xml
     * @param tagName
     * @return
     */
    public static String getTagValue(String xml, String tagName) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            org.w3c.dom.Document doc = builder.parse(new java.io.ByteArrayInputStream(xml.getBytes()));
            doc.getDocumentElement().normalize();

            NodeList nodeList = doc.getElementsByTagName(tagName);
            if (nodeList.getLength() > 0) {
                Node node = nodeList.item(0);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    org.w3c.dom.Element element = (org.w3c.dom.Element) node;
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
