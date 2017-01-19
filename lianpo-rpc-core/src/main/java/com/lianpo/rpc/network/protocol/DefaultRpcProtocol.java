package com.lianpo.rpc.network.protocol;

import com.lianpo.rpc.network.netty.server.Server;
import com.lianpo.rpc.network.netty.transport.ProviderMessageRouter;
import com.lianpo.rpc.network.netty.transport.ProviderProtectedMessageRouter;
import com.lianpo.rpc.util.Environment;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by lianpo on 2017/1/7.
 *
 * @auther lianpo
 */
public class DefaultRpcProtocol extends AbstractProtocol{

    private DefaultRpcProtocol(){

    }

    private static class SingletonHolder{
        public static DefaultRpcProtocol instance = new DefaultRpcProtocol();
    }

    public static DefaultRpcProtocol getInstance(){
        return SingletonHolder.instance;
    }


    private Map<String, ProviderMessageRouter> ipPort2RequestRouter = new HashMap<String, ProviderMessageRouter>();

    public <T> Exporter<T> createExporter(Server server, Provider<T> provider) {

        return new DefaultRpcExporter<T>(server, provider);
    }

    class DefaultRpcExporter<T> extends AbstractExporter<T> {

        public DefaultRpcExporter(Server server, Provider<T> provider) {
            super(provider, server);
            ProviderMessageRouter router = initRequestRouter();
            server.setMessageHanlder(router);
        }

        @Override
        public void destroy() {
            provider.destroy();
            server.close();
        }

        @Override
        public String getServiceAddress() {
            return provider.getServiceAddress();
        }

        @Override
        public void unexport() {
            String serviceKey = provider.getServiceAddress();
            Exporter<T> exporter = (Exporter<T>) exporterMap.remove(serviceKey);
            if(exporter != null){
                exporter.destroy();
            }

            String key = Environment.getLocalhostAddress() + server.getPort();
            synchronized (ipPort2RequestRouter){
                ProviderMessageRouter router = ipPort2RequestRouter.get(key);
                if(router != null){
                    router.removeProvider(provider);
                }
            }
        }

        @Override
        protected boolean doInit() {
            boolean result = server.open();
            return result;
        }


        protected ProviderMessageRouter initRequestRouter() {
            ProviderMessageRouter router = null;
            String key = Environment.getLocalhostAddress() + server.getPort();
            synchronized (ipPort2RequestRouter) {
                router = ipPort2RequestRouter.get(key);
                if (router == null) {
                    router = new ProviderProtectedMessageRouter(provider);
                    ipPort2RequestRouter.put(key, router);
                } else {
                    router.addProvider(provider);
                }
            }
            return router;
        }
    }
}
