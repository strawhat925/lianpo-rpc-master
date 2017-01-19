package com.lianpo.rpc.network.netty.transport;

import com.lianpo.rpc.exception.RpcException;
import com.lianpo.rpc.network.common.DefaultRpcResponse;
import com.lianpo.rpc.network.common.Request;
import com.lianpo.rpc.network.common.Response;
import com.lianpo.rpc.network.protocol.Provider;
import com.lianpo.rpc.util.RpcUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by lianpo on 2017/1/6.
 *
 * @auther lianpo
 * <pre>
 *     控制方法路由
 *     1、接口只有一个方法，直接返回true
 *     2、接口有多个方法，如果单个method超过maxThread / 2 && totalCount > (maxThread * 3 /4)，返回false
 *     3、接口有多个方法（4），同事请求数超过maxThread * 3 / 4，同时该method请求超过maxThread * 1 / 4，返回false
 *     4、其它场景返回true
 * </pre>
 */
public class ProviderProtectedMessageRouter extends ProviderMessageRouter {
    private static final Logger logger = LoggerFactory.getLogger(ProviderProtectedMessageRouter.class);
    //total count
    protected AtomicInteger totalCounter = new AtomicInteger(0);
    //request count
    protected ConcurrentMap<String, AtomicInteger> requestCounter = new ConcurrentHashMap<String, AtomicInteger>();


    public ProviderProtectedMessageRouter(Provider<?> provider) {
        super(provider);
    }


    @Override
    protected Response call(Request request, Provider<?> provider) {
        String requestKey = RpcUtils.getFullMethodString(request);
        int maxThread = 1000;
        try {

            int requestCount = incrRequestCounter(requestKey);
            int totalCount = incrTotalCounter();

            if (isAllowRequest(totalCount, requestCount, maxThread, request)) {
                return super.call(request, provider);
            } else {
                return reject(requestKey, requestCount, totalCount, maxThread);
            }
        } finally {
            decrRequestCounter(requestKey);
            decrTotalCounter();
        }
    }

    protected Response reject(String method, int requestCount, int totalCount, int maxThread) {
        DefaultRpcResponse response = new DefaultRpcResponse();
        RpcException exception = new RpcException("ThreadProtectedRequestRouter reject request: request_counter=" + requestCounter
                + " total_counter=" + totalCounter + " max_thread=" + maxThread + " servie reject");
        exception.setStackTrace(new StackTraceElement[0]);
        response.setException(exception);
        logger.info("ThreadProtectedRequestRouter reject request: request_method=" + method + " request_counter=" + requestCounter
                + " =" + totalCounter + " max_thread=" + maxThread);
        return response;
    }


    protected int incrTotalCounter() {
        return totalCounter.incrementAndGet();
    }

    protected void decrTotalCounter() {
        totalCounter.decrementAndGet();
    }

    protected int incrRequestCounter(String requestKey) {
        AtomicInteger atomicInteger = requestCounter.get(requestKey);
        if (atomicInteger == null) {
            atomicInteger = new AtomicInteger(0);
            requestCounter.putIfAbsent(requestKey, atomicInteger);
            atomicInteger = requestCounter.get(requestKey);
        }
        return atomicInteger.incrementAndGet();
    }

    protected void decrRequestCounter(String requestKey) {
        if (requestCounter.containsKey(requestKey)) {
            requestCounter.get(requestKey).decrementAndGet();
        }
    }


    protected boolean isAllowRequest(int totalCount, int requestCount, int maxThread, Request request) {
        if (methodCounter.get() == 1) {
            return true;
        }

        // 该方法第一次请求，直接return true
        if (requestCount == 1) {
            return true;
        }

        // 不简单判断 requsetCount > (maxThread / 2) ，因为假如有2或者3个method对外提供，
        // 但是只有一个接口很大调用量，而其他接口很空闲，那么这个时候允许单个method的极限到 maxThread * 3 / 4
        if (requestCount > (maxThread / 2) && totalCount > (maxThread * 3 / 4)) {
            return false;
        }

        // 如果总体线程数超过 maxThread * 3 / 4个，并且对外的method比较多，那么意味着这个时候整体压力比较大，
        // 那么这个时候如果单method超过 maxThread * 1 / 4，那么reject
        return !(methodCounter.get() >= 4 && totalCount > (maxThread * 3 / 4) && requestCount > (maxThread * 1 / 4));
    }
}
