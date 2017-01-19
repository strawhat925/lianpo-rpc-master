package com.lianpo.rpc.network.netty.transport;

import com.lianpo.rpc.exception.RpcException;
import com.lianpo.rpc.network.common.DefaultRpcResponse;
import com.lianpo.rpc.network.common.Request;
import com.lianpo.rpc.network.common.Response;
import com.lianpo.rpc.network.netty.server.Channel;
import com.lianpo.rpc.network.protocol.Provider;
import com.lianpo.rpc.util.RpcUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * Created by lianpo on 2017/1/6.
 *
 * @auther lianpo
 */
public class ProviderMessageRouter implements MessageHanlder {
    private static final Logger logger = LoggerFactory.getLogger(ProviderMessageRouter.class);
    private Map<String, Provider<?>> providers = new HashMap<String, Provider<?>>();
    protected AtomicInteger methodCounter = new AtomicInteger(0);

    public ProviderMessageRouter(Provider<?> provider) {
        addProvider(provider);
    }

    @Override
    public Object handle(Channel channel, Object message) {
        if (channel == null || message == null) {
            throw new RpcException("RequestRouter handler(channel, message) params is null");
        }
        if (!(message instanceof Request)) {
            throw new RpcException("RequestRouter message type not support:" + message.getClass());
        }

        Request request = (Request) message;
        String serviceKey = RpcUtils.getServiceKey(request);
        Provider<?> provider = providers.get(serviceKey);
        if (provider == null) {
            logger.error(this.getClass().getSimpleName() + " handler Error: provider not exist serviceKey=" + serviceKey + " "
                    + RpcUtils.toString(request));
            RpcException exception =
                    new RpcException(this.getClass().getSimpleName() + " handler Error: provider not exist serviceKey="
                            + serviceKey + " " + RpcUtils.toString(request));

            DefaultRpcResponse response = new DefaultRpcResponse();
            response.setException(exception);
            return response;
        }
        return call(request, provider);
    }


    protected Response call(Request request, Provider<?> provider) {
        try {
            return provider.call(request);
        } catch (Exception e) {
            DefaultRpcResponse response = new DefaultRpcResponse();
            response.setException(new RpcException("provider call error", e));
            return response;
        }
    }


    public synchronized void addProvider(Provider<?> provider) {
        String serviceAddress = provider.getServiceAddress();
        if (providers.containsKey(serviceAddress)) {
            throw new RpcException("provider alread exists:" + serviceAddress);
        }

        providers.put(serviceAddress, provider);

        List<Method> methodList = RpcUtils.getPublicMethod(provider.getInterface());
        int methodSize = methodList.size();
        methodCounter.addAndGet(methodSize);

        logger.info("RequestRouter add provider success.");
    }


    public synchronized void removeProvider(Provider<?> provider){
        String serviceAddress = provider.getServiceAddress();

        providers.remove(serviceAddress);
        List<Method> methodList = RpcUtils.getPublicMethod(provider.getInterface());
        int methodSize = methodList.size();

        methodCounter.getAndAdd(methodCounter.get() - methodSize);
    }
}
