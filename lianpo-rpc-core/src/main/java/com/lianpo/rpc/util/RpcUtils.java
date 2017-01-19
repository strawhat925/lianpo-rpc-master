package com.lianpo.rpc.util;

import com.lianpo.rpc.network.common.Request;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lianpo on 2017/1/6.
 *
 * @auther lianpo
 */
public class RpcUtils {


    /**
     * 获取接口对应的public方法
     * @param interfaceClass
     * @return
     */
    public static List<Method> getPublicMethod(Class<?> interfaceClass){
        List<Method> ret = new ArrayList<Method>();
        Method[] methods = interfaceClass.getMethods();
        for(Method method : methods){
            boolean isPublic = Modifier.isPublic(method.getModifiers());
            boolean isNotObjectClass = method.getDeclaringClass() != Object.class;

            if(isPublic && isNotObjectClass){
                ret.add(method);
            }
        }
        return ret;
    }


    public static String getFullMethodString(Request request){
        StringBuffer sb = new StringBuffer();
        sb.append(request.getInterfaceName());
        sb.append(".");
        sb.append(request.getMethodName());
        sb.append("(");
        if(request.getParameterTypes() != null && request.getParameterTypes().length > 0){
            for(Class<?> clazz : request.getParameterTypes()){
                sb.append(clazz.getName()).append(",");
            }
            sb.substring(0, sb.length() - 1);
        }
        sb.append(")");
        return sb.toString();
    }


    public static String getServiceKey(Request request){
        return Environment.getLocalhostAddress() + "/" + request.getInterfaceName();
    }


    public static String toString(Request request){
        StringBuffer sb = new StringBuffer();
        sb.append(" requestId=").append(request.getRequestId());
        sb.append(" interfaceName=").append(request.getInterfaceName());
        sb.append(" methodName=").append(request.getMethodName());
        sb.append("(");
        if(request.getParameterTypes() != null && request.getParameterTypes().length > 0){
            for(Class<?> clazz : request.getParameterTypes()){
                sb.append(clazz.getName()).append(",");
            }
            sb.substring(0, sb.length() - 1);
        }
        sb.append(")");
        return sb.toString();
    }
}
