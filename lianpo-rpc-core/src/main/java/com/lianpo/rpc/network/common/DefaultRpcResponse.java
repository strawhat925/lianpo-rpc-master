package com.lianpo.rpc.network.common;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/1/5.
 */
public class DefaultRpcResponse implements Response,Serializable {

    private static final long serialVersionUID = -3044099275110117944L;
    //
    private String requestId;
    private byte version;
    private Exception exception;
    private Object value;
    private long processTime;

    public DefaultRpcResponse(Object value){
        this.value = value;
    }

    public DefaultRpcResponse(){

    }

    @Override
    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public byte getVersion() {
        return version;
    }

    public void setVersion(byte version) {
        this.version = version;
    }

    public Exception getException() {
        return exception;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public long getProcessTime() {
        return processTime;
    }

    public void setProcessTime(long processTime) {
        this.processTime = processTime;
    }

    @Override
    public String toString() {
        return "DefaultRpcResponse{" +
                "requestId='" + requestId + '\'' +
                ", version=" + version +
                ", exception=" + exception +
                ", value=" + value +
                ", processTime=" + processTime +
                '}';
    }
}
