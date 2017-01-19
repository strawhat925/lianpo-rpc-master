package com.lianpo.rpc.restful;

import com.google.common.base.Joiner;
import com.google.common.base.Optional;

import com.sun.jersey.api.core.PackagesResourceConfig;
import com.sun.jersey.spi.container.servlet.ServletContainer;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.resource.Resource;

/**
 * Created by liz on 2017/1/19.
 *
 * @auther liz
 */
public class RestfulServer {
    //jetty server
    private final Server server;

    public RestfulServer(int port) {
        this.server = new Server(port);
    }


    /**
     * 启动jetty
     */
    public void start(final String packages, final Optional<String> resourcePath) throws Exception {
        //
        HandlerList handlers = new HandlerList();
        if (resourcePath.isPresent()) {
            handlers.addHandler(buildResourceHandler(resourcePath.get()));
        }
        handlers.addHandler(buildServletContextHandler(packages));

        server.setHandler(handlers);
        server.start();
    }


    /**
     * 使用ResourceHanlder来处理用户对静态资源的请求
     * ResourceHandler可以处理的包括 html htm 等浏览器可以解释的 文件类型也可以处理txt 和图片  只要是能显示的内容 都可以在浏览器里显示
     * 如果文件类型不能解释 也不能在浏览器里显示的话 则会提示是否下载
     * ResourceHandler不能处理jsp页面 等动态页面
     */
    private ResourceHandler buildResourceHandler(final String resourcePath) {
        ResourceHandler resourceHandler = new ResourceHandler();
        resourceHandler.setBaseResource(Resource.newClassPathResource(resourcePath));
        return resourceHandler;
    }

    /**
     * 创建一个handler
     */
    private ServletContextHandler buildServletContextHandler(final String packages) {
        ServletContextHandler servletContextHandler = new ServletContextHandler(ServletContextHandler.SESSIONS);
        //上下文path
        servletContextHandler.setContextPath("/");
        //add servlet
        servletContextHandler.addServlet(getServletHolder(packages), "/*");
        return servletContextHandler;
    }


    /**
     * 创建一个JerseyServlet
     */
    private ServletHolder getServletHolder(final String packages) {
        ServletHolder servletHolder = new ServletHolder(ServletContainer.class);
        //系统启动时扫描的包的服务端的路径
        servletHolder.setInitParameter(PackagesResourceConfig.PROPERTY_PACKAGES, Joiner.on(",").join(RestfulServer.class.getPackage().getName(), packages));
        servletHolder.setInitParameter("com.sun.jersey.config.property.resourceConfigClass", PackagesResourceConfig.class.getName());
        //是否打开Json POJO
        servletHolder.setInitParameter("com.sun.jersey.api.json.POJOMappingFeature", Boolean.TRUE.toString());
        //扫描@Provider注解并注册
        //servletHolder.setInitParameter("resteasy.scan.providers", Boolean.TRUE.toString());
        //是否注册resteasy默认值，内置@Provider classes.
        //servletHolder.setInitParameter("resteasy.use.builtin.providers", Boolean.FALSE.toString());
        return servletHolder;
    }


    /**
     * 关闭jetty
     */
    public void stop() throws Exception {
        if (server != null) {
            server.stop();
        }
    }
}
