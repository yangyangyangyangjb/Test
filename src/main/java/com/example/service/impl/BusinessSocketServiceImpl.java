package com.example.service.impl;

import com.example.service.IBusinessSocketService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import java.io.PrintWriter;

@Slf4j
@Service
public class BusinessSocketServiceImpl implements IBusinessSocketService {

    @Override
    public void executeBusinessCode(String requestInfo, String logId, PrintWriter writer) {
        String responseMsg;
        boolean isSuccess = Boolean.TRUE;
        try {
            if (StringUtils.isEmpty(requestInfo)) {
                return;
            }
            //执行你的业务操作
            responseMsg = "回填给客户端的信息,可以是任何格式的对象";
        } catch (Exception e) {
            isSuccess = Boolean.FALSE;
            e.printStackTrace();
            responseMsg = "回填给客户端的信息(业务处理错误的情况下)";
        }
        try {
            //将响应报文通过PrintWriter回写给客户端
            writer.println(responseMsg);
        } catch (Exception e) {
            log.error("Socket客户端数据返回异常!当前日志ID:[{}],异常信息:{}", logId, e);
        }
    }
}
