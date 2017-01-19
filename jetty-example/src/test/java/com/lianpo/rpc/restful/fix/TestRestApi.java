package com.lianpo.rpc.restful.fix;

import com.google.common.collect.Maps;
import java.util.Map;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

import lombok.Setter;

/**
 * Created by liz on 2017/1/19.
 *
 * @auther liz
 */
@Path("/test")
public final class TestRestApi {

    @Setter
    private static Caller caller;

    @POST
    @Path("/call")
    public final Map<String,String> call(Map<String,String> map){

        caller.call(map.get("key"));
        caller.call(Integer.valueOf(map.get("value")));
        System.out.println("==============" + map);
        return Maps.transformEntries(map, new Maps.EntryTransformer<String, String, String>() {

            @Override
            public String transformEntry(final String key, final String value) {
                return value + "_processed";
            }
        });
    }

}
