package com.lianpo.rpc.network.protocol;


import com.lianpo.rpc.exception.RpcException;
import com.lianpo.rpc.network.common.DefaultRpcResponse;
import com.lianpo.rpc.network.common.Request;
import com.lianpo.rpc.network.common.Response;
import com.lianpo.rpc.util.Environment;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

/**
 * Created by liz on 2017/1/7.
 *
 * @auther liz
 */
public class DefaultProvider<T> extends AbstractProvider<T> {
    private static final Logger logger = LoggerFactory.getLogger(DefaultProvider.class);

    public DefaultProvider(Class<?> interfaceClass) {
        super(interfaceClass);
    }

    @Override
    protected Response invoker(Request request) {
        DefaultRpcResponse response = new DefaultRpcResponse();
        response.setVersion(request.getVersion());
        response.setRequestId(request.getRequestId());

        String methodName = request.getMethodName();
        if (methodName == null) {
            RpcException exception = new RpcException(String.format("%s correspondence method does not exist : %s", interfaceClass.getSimpleName(), methodName));
            response.setException(exception);
            return response;
        }
        try {
            Class<?>[] parameterTypes = request.getParameterTypes();
            Object[] parameters = request.getParameters();
            Method method = interfaceClass.getMethod(methodName, parameterTypes);
            method.setAccessible(true);
            Object value = method.invoke(interfaceClass.newInstance(), parameters);
            response.setValue(value);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            RpcException exception = new RpcException(e.getMessage());
            response.setException(exception);
        } catch (Throwable t) {
            // 如果服务发生Error，将Error转化为Exception，防止拖垮调用方
            if (t.getCause() != null) {
                response.setException(new RpcException("provider has encountered a fatal error!", t.getCause()));
            } else {
                response.setException(new RpcException("provider has encountered a fatal error!", t));
            }
        }
        return response;
    }

    @Override
    public String desc() {
        return interfaceClass.getSimpleName();
    }

    @Override
    public String getServiceAddress() {
        Class<?>[] clazzs = this.interfaceClass.getInterfaces();
        return Environment.getLocalhostAddress() + "/" + clazzs[0].getName();
    }

}
