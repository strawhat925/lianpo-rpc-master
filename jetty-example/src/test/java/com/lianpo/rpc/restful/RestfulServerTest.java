package com.lianpo.rpc.restful;


import com.google.common.base.Joiner;
import com.google.common.base.Optional;

import com.lianpo.rpc.restful.fix.Caller;
import com.lianpo.rpc.restful.fix.TestRestApi;

import org.eclipse.jetty.client.ContentExchange;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.io.ByteArrayBuffer;
import org.hamcrest.core.Is;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import javax.ws.rs.core.MediaType;

/**
 * Created by liz on 2017/1/19.
 *
 * @auther liz
 */
@RunWith(MockitoJUnitRunner.class)
public class RestfulServerTest {

    private static RestfulServer restfulServer;

    private static Caller caller;

    private static final String URL = "http://127.0.0.1:8000";

    private static final int port = 8000;

    @BeforeClass
    public static void setUpClass() throws Exception {
        restfulServer = new RestfulServer(port);
        restfulServer.start(TestRestApi.class.getPackage().getName(), Optional.<String>absent());
    }

    @AfterClass
    public static void setDown() throws Exception {
        restfulServer.stop();
    }

    @Before
    public void setUp(){
        caller = Mockito.mock(Caller.class);
        TestRestApi.setCaller(caller);
    }


    @Test
    public void assertCallSuccessOrFail() throws Exception {
        ContentExchange actual = sendRequest("{\"key\":\"test\",\"value\":1}");
        Assert.assertThat(actual.getResponseStatus(), Is.is(200));
        Assert.assertThat(actual.getResponseContent(), Is.is("{\"key\":\"test_processed\",\"value\":\"1_processed\"}"));
        Mockito.verify(caller).call("test");
        Mockito.verify(caller).call(1);

        //fail
        Mockito.verify(caller).call(2);
    }

    @Test
    public void test(){
        String packages = Joiner.on(",").join(RestfulServer.class.getPackage().getName(), TestRestApi.class.getPackage().getName());
        System.out.println(packages);
    }


    public static ContentExchange sendRequest(final String content) throws Exception {
        HttpClient client = new HttpClient();

        try {
            client.start();
            ContentExchange contentExchange = new ContentExchange();
            contentExchange.setMethod("POST");
            contentExchange.setRequestContentType(MediaType.APPLICATION_JSON);
            contentExchange.setRequestContent(new ByteArrayBuffer(content.getBytes("UTF-8")));
            client.setConnectorType(HttpClient.CONNECTOR_SELECT_CHANNEL);
            contentExchange.setURL(URL);
            client.send(contentExchange);
            contentExchange.waitForDone();

            return contentExchange;
        } finally {
            client.stop();
        }
    }
}
