package com.dongtian.epos.vo;

import lombok.Data;

@Data
public class ResultVO<T> {
    //状态码
    private Integer code;
    //消息
    private String msg;
    //数据域
    private T data;
}
