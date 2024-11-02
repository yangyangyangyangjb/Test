package com.example.common;

import com.example.entity.SnowFlakeUtil;
import com.example.service.IBusinessSocketService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

@Slf4j
public class ClientHandler implements Runnable {

    private final Socket clientSocket;

    private final IBusinessSocketService socketService;

    public ClientHandler(Socket clientSocket, IBusinessSocketService socketService) {
        this.clientSocket = clientSocket;
        this.socketService = socketService;
    }

    @Override
    @SneakyThrows
    public void run() {
        //SnowFlakeUtil 雪花ID生成工具类,后面会统一给出
        String logId = SnowFlakeUtil.getId();
        String hostIp = clientSocket.getInetAddress().getHostAddress();
        String port = String.valueOf(clientSocket.getPort());
        try (PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), Boolean.TRUE)) {
            //这里的StringUtil是自己写的工具类,后面会统一给出
/*            String requestInfo = StringUtil.readIoStreamToString(clientSocket.getInputStream());
            if (StringUtil.isNotEmpty(requestInfo)) {
                log.info("监听到客户端消息:{},监听日志ID为:{}", requestInfo, logId);
                socketService.executeBusinessCode(requestInfo, logId, out);
                clientSocket.shutdownOutput();
                TimeUnit.SECONDS.sleep(1L);
            }*/
        } catch (IOException e) {
            log.error("与客户端:[{}:{}]通信异常!错误信息:{}", hostIp, port, e.getMessage());
        } finally {
            log.info("客户端:[{}:{}]Socket连接已关闭,日志ID为:{}========>", hostIp, port, logId);
        }
    }
}
