package com.lianpo.rpc.restful;

/**
 * Created by liz on 2017/1/19.
 *
 * @auther liz
 */
public final class RestfulException extends RuntimeException {


    private static final long serialVersionUID = -7424564317548550506L;

    public RestfulException(final Throwable throwable){
        super(throwable);
    }
}
