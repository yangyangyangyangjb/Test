package com.example.common;

import com.example.service.SpgtmBankTransDetailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@Component
public class SocketServers{

    //注入被开放的端口
    @Value("${socket.port}")
    private Integer port;

    //这个是业务处理的接口
    @Autowired
    private SpgtmBankTransDetailService socketService;

    @PostConstruct
    public void socketStart(){
        //直接另起一个线程挂起Socket服务
        new Thread(this::socketServer).start();
    }

    private void socketServer() {
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            log.info("socket端口在:{}中开启并持续监听=====>", port);
            while (Boolean.TRUE) {
                Socket clientSocket = serverSocket.accept();
                //设置流读取的超时时间,这里设置为10s。超时后自动断开连接
                clientSocket.setSoTimeout(10000);
                //是否与客户端保持持续连接,这行代码在本示例中,并没有作用,因为后面的逻辑处理完成后,会自动断开连接.
                clientSocket.setKeepAlive(Boolean.TRUE);
                log.info("发现客户端连接Socket:{}:{}===========>", clientSocket.getInetAddress().getHostAddress(),
                        clientSocket.getPort());
                //这里通过线程池启动ClientHandler方法中的run方法.
//                executorService.execute(new ClientHandler(clientSocket, socketService));
            }
        } catch (Exception e) {
            log.error("Socket服务启动异常!", e);
        }
    }
}