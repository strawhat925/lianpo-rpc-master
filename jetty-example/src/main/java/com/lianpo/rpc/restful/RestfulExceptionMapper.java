package com.lianpo.rpc.restful;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * Created by liz on 2017/1/19.
 *
 * @auther liz
 */
@Provider
public final class RestfulExceptionMapper implements ExceptionMapper<Throwable> {

    @Override
    public Response toResponse(Throwable throwable) {
        return Response.ok(throwable.getMessage(), MediaType.TEXT_PLAIN).status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }
}
