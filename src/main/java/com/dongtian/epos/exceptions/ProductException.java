package com.dongtian.epos.exceptions;

import com.dongtian.epos.enums.ResultEnum;

public class ProductException extends RuntimeException {
    private Integer code;

    public ProductException(ResultEnum resultEnum) {
        super(resultEnum.getMsg());
        this.code = resultEnum.getCode();
    }

    public ProductException(Integer code, String message) {
        super(message);
        this.code = code;
    }
}
