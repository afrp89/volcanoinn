package com.around.volcanoinn.springboot.util;

import org.springframework.stereotype.Component;

@Component
public class CustomErrorType {

    private String errorMessage;

    public CustomErrorType(String errorMessage){
        this.errorMessage = errorMessage;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

}