package com.guzhandong.springframework.boot.elasticsearch.exception.impl;


/**
 * 远程执行脚本异常返回
 *
 * @author guzhandong
 * @CREATE 2017-05-17 10:56 PM
 */
public class RemoteExecException extends RuntimeException {


    protected int defReturnCode = 500;

    public int getDefReturnCode() {
        return this.defReturnCode;
    }

    public void setDefReturnCode(int defReturnCode) {
        this.defReturnCode = defReturnCode;
    }

    public RemoteExecException() {
        super();
    }

    public RemoteExecException(String message) {
        super(message);
    }

    public RemoteExecException(String message, Throwable cause) {
        super(message, cause);
    }

    public RemoteExecException(Throwable cause) {
        super(cause);
    }

    protected RemoteExecException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
