package com.lianpo.rpc.network;

import com.lianpo.rpc.enums.NetCommEnum;
import com.lianpo.rpc.enums.SerializeEnum;
import com.lianpo.rpc.exception.RpcException;
import com.lianpo.rpc.network.common.DefaultRpcReqeust;
import com.lianpo.rpc.network.common.Response;
import com.lianpo.rpc.network.common.Version;
import com.lianpo.rpc.network.netty.client.Client;
import com.lianpo.rpc.serialize.Serialization;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.UUID;


/**
 * Created by Administrator on 2017/1/5.
 */
public class NetClientFactory<T> implements FactoryBean<Object>, InitializingBean {
    private static final Logger logger = LoggerFactory.getLogger(NetClientFactory.class);
    private String serverAddress;
    private Class<?> interfacleClass;
    private Serialization serialization;
    private long timeoutMillis = 5000;
    private Client client;

    @Override
    public Object getObject() throws Exception {
        return Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class[]{interfacleClass}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                DefaultRpcReqeust reqeust = new DefaultRpcReqeust();
                reqeust.setRequestId(UUID.randomUUID().toString().replace("-", ""));
                reqeust.setVersion(Version.VERSION_1.getVersion());
                reqeust.setInterfaceName(method.getDeclaringClass().getName());
                reqeust.setMethodName(method.getName());
                reqeust.setParameterTypes(method.getParameterTypes());
                reqeust.setParameters(args);
                Response response = client.sendRequest(reqeust);
                if (response == null) {
                    logger.error("lianpo-rpc response not found");
                    throw new RpcException("lianpo-rpc response not found");
                }
                if (response.getException() != null) {
                    return response.getException();
                }

                return response.getValue();
            }
        });
    }

    @Override
    public Class<?> getObjectType() {
        return this.interfacleClass;
    }

    @Override
    public boolean isSingleton() {
        return false;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        //connect netty
        NetCommEnum netCommEnum = NetCommEnum.NETTY;
        client = netCommEnum.clientClass.newInstance();
        client.init(serverAddress, serialization, timeoutMillis);
    }

    public void setServerAddress(String serverAddress) {
        this.serverAddress = serverAddress;
    }

    public void setInterfacleClass(Class<?> interfacleClass) {
        this.interfacleClass = interfacleClass;
    }

    public void setSerialization(String name) {
        this.serialization = SerializeEnum.match(name, SerializeEnum.HESSIAN).serialization;
    }

    public void setTimeoutMillis(long timeoutMillis) {
        this.timeoutMillis = timeoutMillis;
    }


}
