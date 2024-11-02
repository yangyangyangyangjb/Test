package com.example.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.example.entity.JtgkTmckcp;
import com.example.entity.Result;
import com.example.service.DepositProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
public class DepositProductServiceImpl implements DepositProductService {
    @Override
    public Result sendDepositProductToCs(JSONObject jsonObject) {

        List<JtgkTmckcp> jtgkTmckcpList = new ArrayList<>();
        JtgkTmckcp jtgkTmckcp1 = new JtgkTmckcp();
        jtgkTmckcp1.setVer("1");
        jtgkTmckcp1.setId(UUID.randomUUID().toString());
        jtgkTmckcpList.add(jtgkTmckcp1);

        JtgkTmckcp jtgkTmckcp2 = new JtgkTmckcp();
        jtgkTmckcp2.setVer("2");
        jtgkTmckcp2.setId(UUID.randomUUID().toString());
        jtgkTmckcpList.add(jtgkTmckcp2);

        JtgkTmckcp jtgkTmckcp3 = new JtgkTmckcp();
        jtgkTmckcp3.setVer("3");
        jtgkTmckcp3.setId(UUID.randomUUID().toString());
        jtgkTmckcpList.add(jtgkTmckcp3);

        JtgkTmckcp jtgkTmckcp4 = new JtgkTmckcp();
        jtgkTmckcp4.setVer("5");
        jtgkTmckcp4.setId(UUID.randomUUID().toString());
        jtgkTmckcpList.add(jtgkTmckcp4);

        JtgkTmckcp jtgkTmckcp5 = new JtgkTmckcp();
        jtgkTmckcp5.setVer("4");
        jtgkTmckcp5.setId(UUID.randomUUID().toString());
        jtgkTmckcpList.add(jtgkTmckcp5);

        jtgkTmckcpList.sort((o1, o2) -> o2.getVer().compareTo(o1.getVer()));
        Collections.sort(jtgkTmckcpList, (o1, o2) -> o2.getVer().compareTo(o1.getVer()));

        for (JtgkTmckcp jtgkTmckcp : jtgkTmckcpList){
            log.info(jtgkTmckcp.toString());
        }

        return Result.ok();
    }
}
