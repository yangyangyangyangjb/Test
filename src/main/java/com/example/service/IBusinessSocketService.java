package com.example.service;

import java.io.PrintWriter;

public interface IBusinessSocketService {

    /**
     * 从Socket中接受消息并处理
     *
     * @param requestInfo 请求报文
     * @param logId       日志ID
     * @param writer      回写给客户端消息的回写类(Socket自带)
     */
    void executeBusinessCode(String requestInfo, String logId, PrintWriter writer);
}