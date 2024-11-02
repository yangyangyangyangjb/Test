package com.example.entity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Result<T> {
    private String code;
    private String message;
    private T datas;

    public static <T> Result<T> ok() {
        return ok("查询成功");
    }

    public static <T> Result<T> ok(String message) {
        return ok(message, null);
    }

    public static <T> Result ok(T datas) {
        return ok("查询成功", datas);
    }

    public static <T> Result ok(String message, T datas) {
        return builder().code("S").message(message).datas(datas).build();
    }

    public static <T> Result fail(String message) {
        return Result.<T>builder().code("E").message(message).build();
    }

}

