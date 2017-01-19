package com.lianpo.rpc.restful;

import org.eclipse.jetty.client.ContentExchange;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.io.ByteArrayBuffer;
import org.hamcrest.core.Is;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import javax.ws.rs.core.MediaType;

/**
 * Created by liz on 2017/1/19.
 *
 * @auther liz
 */
@RunWith(MockitoJUnitRunner.class)
public class RestServerTest {

    private static RestServer server;

    private static String URL = "http://127.0.0.1:8888/test/call";

    @BeforeClass
    public static void setUpClass() throws Exception {
        server = new RestServer(8888);
        server.start();
    }

    @AfterClass
    public static void setDown() throws Exception {
        server.stop();
    }


    @Test
    public void assertCallSuccessOrFail() throws Exception {
        ContentExchange actual = sendRequest("{\"key\":\"test\",\"value\":1}");
        Assert.assertThat(actual.getResponseStatus(), Is.is(200));
        Assert.assertThat(actual.getResponseContent(), Is.is("成功"));

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
