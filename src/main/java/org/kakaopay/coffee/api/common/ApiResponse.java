package org.kakaopay.coffee.api.common;

import org.springframework.http.HttpStatus;

public class ApiResponse<T> {

    private int code;
    private HttpStatus status;
    private String msg;
    private T result;

    public ApiResponse(HttpStatus status, String msg, T result){
        this.code = status.value();
        this.status = status;
        this.msg = msg;
        this.result = result;
    }

    public static <T> ApiResponse<T> of(HttpStatus httpStatus, String msg, T result){
        return new ApiResponse<T>(httpStatus, msg, result);
    }

    public static <T> ApiResponse<T> of(HttpStatus httpStatus, T result) {
        return of(httpStatus, httpStatus.name(), result);
    }

    public static <T> ApiResponse<T> ok(T result) {
        return of(HttpStatus.OK, result);
    }

}
