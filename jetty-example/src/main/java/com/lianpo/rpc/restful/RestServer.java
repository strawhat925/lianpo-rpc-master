package com.lianpo.rpc.restful;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.util.thread.ExecutorThreadPool;

/**
 * Created by liz on 2017/1/19.
 *
 * @auther liz
 */
public class RestServer {
    private static Server server;
    private static int port;

    public RestServer(int port) {
        this.port = port;
        this.server = new Server();
    }


    public void start() throws Exception {

        new Thread(new Runnable() {
            @Override
            public void run() {
                //pool
                server.setThreadPool(new ExecutorThreadPool(200, 200, 30000));//非阻塞

                //connector
                SelectChannelConnector connector = new SelectChannelConnector();
                connector.setPort(RestServer.port);
                connector.setMaxIdleTime(30000);
                server.setConnectors(new Connector[]{connector});

                //handler
                HandlerCollection handlerCollection = new HandlerCollection();
                handlerCollection.setHandlers(new Handler[]{new CallHandler()});
                server.setHandler(handlerCollection);

                try {
                    server.start();
                    //线程阻塞，保证server的启动
                    server.join();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }


    public void stop() throws Exception {
        if (server != null) {
            server.stop();
        }
    }

}
