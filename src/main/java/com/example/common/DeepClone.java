package com.example.common;

import lombok.extern.slf4j.Slf4j;

import java.io.*;

@Slf4j
public class DeepClone {
    /**
     * 深克隆
     * @param t
     * @return
     * @param <T>
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public static  <T> Object deepClone(T t) throws IOException {
        ByteArrayOutputStream bos = null;
        ByteArrayInputStream bis = null;
        ObjectOutputStream oos = null;
        Object oldObject = null;
        try{
            //第一阶段，把对象信息输入到oos
            bos = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(bos);
            oos.writeObject(t);//写数据到流中
            //第二阶段，把oos中的数据再读出来
            bis = new ByteArrayInputStream(bos.toByteArray());
            ObjectInputStream ois=new ObjectInputStream(bis);
            oldObject = ois.readObject();//从流中把数据读出来
        }catch (Throwable e){
            log.error("克隆出错：{}",e);
        }finally {
            bos.close();
            oos.close();
            bis.close();//把流关闭
        }
        return oldObject;
    }
}
