package com.example.entity;

import lombok.SneakyThrows;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

/**
 * <Description> ******
 *
 * @author yuSen.zhang
 * @version 1.0
 * @date 2023/04/17
 */
public class StringUtil {

    @SneakyThrows
    public static String readToStreamToString(InputStream is) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] buffer = new byte[2048];
        int bytesRead;
        /*
        这一步可能会卡住,因为客户端如果传输完数据以后,
        如果没有调用socket.shutdownOutput()方法,会导致服务端不知道流是否已传输完毕,
        等待我们之前设置的10S流读取时间后,连接就会被自动关掉
        如果出现这种情况,服务端可以通过其它方式判断。例如换行符或者特殊字符等,只需要在
        while条件中加一个&&判断即可.例如我这里的业务结束标记是字符: ">",那么判断逻辑如下
        若客户端调用了shutdownOutput()方法,则不需要这个判断
        */
        while (!bos.toString().contains(">") && (bytesRead = is.read(buffer)) != -1) {
            bos.write(buffer, 0, bytesRead);
        }
        return bos.toString();
    }
}
