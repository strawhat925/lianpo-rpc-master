package com.lianpo.rpc.network;

import com.lianpo.rpc.annotation.RpcServer;
import com.lianpo.rpc.enums.NetCommEnum;
import com.lianpo.rpc.enums.SerializeEnum;
import com.lianpo.rpc.exception.RpcException;
import com.lianpo.rpc.network.codec.Codec;
import com.lianpo.rpc.network.common.DefaultRpcReqeust;
import com.lianpo.rpc.network.common.DefaultRpcResponse;
import com.lianpo.rpc.network.common.Request;
import com.lianpo.rpc.network.common.Response;
import com.lianpo.rpc.network.netty.server.Server;
import com.lianpo.rpc.network.protocol.SimpleConfigHandler;
import com.lianpo.rpc.registry.ZookeeperRegistry;
import com.lianpo.rpc.serialize.Serialization;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * spring init communcaiton factory
 * Created by Administrator on 2017/1/5.
 *
 * @author lianpo
 */
public class NetServerFactory implements ApplicationContextAware, InitializingBean, DisposableBean, ApplicationListener<ContextRefreshedEvent> {
    private static final Logger logger = LoggerFactory.getLogger(NetServerFactory.class);
    private int port;
    private Serialization serialization;
    private Codec codec;
    private Server server;
    private int nThreads;
    private AtomicBoolean exported = new AtomicBoolean(false);
    //zk 开关
    private boolean zookeeper_switch = false;

    @Override
    public void destroy() throws Exception {
        server.destroy();
        if (zookeeper_switch) {
            ZookeeperRegistry.destroy();
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        //start netty
        NetCommEnum netCommEnum = NetCommEnum.NETTY;
        Class<?>[] classTypes = new Class<?>[]{int.class, Serialization.class, int.class};
        Constructor<?> constructor = netCommEnum.serverClass.getConstructor(classTypes);
        Object[] params = new Object[]{port, serialization, nThreads};
        server = (Server) constructor.newInstance(params);
        //server.start();

        if (zookeeper_switch) {
            ZookeeperRegistry.registerService(port, serviceMap.keySet());
        }
    }

    //cache bean
    private static Map<String, Object> serviceMap = new HashMap<String, Object>();

    //init server bean
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Map<String, Object> serviceBeanMap = applicationContext.getBeansWithAnnotation(RpcServer.class);
        if (serviceBeanMap != null && serviceBeanMap.size() > 0) {
            for (Object serverBean : serviceBeanMap.values()) {
                String interfaceName = serverBean.getClass().getAnnotation(RpcServer.class).value().getName();
                serviceMap.put(interfaceName, serverBean);
            }
        }
    }

    //invoker server
    public static Response invokerServer(Request request, Object serviceBean) throws RpcException {
        if (request instanceof DefaultRpcReqeust) {
            if (serviceBean == null) {
                serviceBean = serviceMap.get(request.getInterfaceName());
            }
            if (serviceBean == null) {
                throw new RpcException("service bean not exists");
            }

            DefaultRpcResponse response = new DefaultRpcResponse();
            response.setRequestId(request.getRequestId());
            response.setVersion(request.getVersion());
            try {
                Class<?> serviceClass = serviceBean.getClass();
                String methodName = request.getMethodName();
                Class<?>[] parameterTypes = request.getParameterTypes();
                Object[] parameters = request.getParameters();

                Method method = serviceClass.getMethod(methodName, parameterTypes);
                method.setAccessible(true);
                Object value = method.invoke(serviceBean, parameters);
                response.setValue(value);
            } catch (Exception e) {
                logger.error("reflect invoker service error", e.getMessage());
                response.setException(new RpcException("reflect invoker service error:" + e.getMessage()));
            } catch (Throwable t) {//
                response.setException(new RpcException(t.getMessage()));
            }
            return response;
        } else {
            logger.error("reflect invoker service error:", request);
            throw new RpcException("reflect invoker server error");
        }
    }


    public void setPort(int port) {
        this.port = port;
    }

    public void setSerialization(String serialization) {
        this.serialization = SerializeEnum.match(serialization, SerializeEnum.HESSIAN).serialization;
    }

    public void setCodec(Codec codec) {
        this.codec = codec;
    }

    public void setnThreads(int nThreads) {
        this.nThreads = nThreads;
    }

    public void setZookeeper_switch(boolean zookeeper_switch) {
        this.zookeeper_switch = zookeeper_switch;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        if(serviceMap != null && serviceMap.size() > 0){
            for(Object bean : serviceMap.values()){
                SimpleConfigHandler.getInstance().export(server, bean.getClass());
            }
        }
    }
}
