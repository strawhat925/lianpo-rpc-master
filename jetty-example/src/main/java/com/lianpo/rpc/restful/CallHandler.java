package com.lianpo.rpc.restful;

import com.google.common.base.Strings;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by liz on 2017/1/19.
 *
 * @auther liz
 */
public class CallHandler extends AbstractHandler {
    private static final String UTF_8 = "UTF-8";

    @Override
    public void handle(String s, Request request, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws IOException, ServletException {
        System.out.println("===================" + s);

        InputStreamReader streamReader = new InputStreamReader(httpServletRequest.getInputStream(), UTF_8);
        StringBuilder result = new StringBuilder();
        BufferedReader reader = new BufferedReader(streamReader);
        String line;
        while (!Strings.isNullOrEmpty(line = reader.readLine())){
            result.append(line);
        }
        streamReader.close();
        reader.close();
        System.out.println("---------------------------" + result);

        httpServletResponse.setContentType("text/html;charset=utf-8");
        httpServletResponse.setStatus(HttpServletResponse.SC_OK);
        request.setHandled(true);
        httpServletResponse.getWriter().print("成功");
    }
}
