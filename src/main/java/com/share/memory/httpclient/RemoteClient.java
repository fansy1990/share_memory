package com.share.memory.httpclient;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * author : fanzhe
 * email : fansy1990@foxmail.com
 * date : 2019/10/28 PM11:15.
 */
public class RemoteClient {
    static CloseableHttpClient httpclient = HttpClients.createDefault();

    private static final String GET = "http://192.168.128.149:8080/share_memory/api/check";
    private static final String SET = "http://192.168.128.148:8080/share_memory/api/set";
    private static final String REVOKE = "http://192.168.128.148:8080/share_memory/api/revoke";

    public static void main(String[] args) throws IOException, URISyntaxException, InterruptedException {
        Logger root = Logger.getLogger("");
        root.setLevel(Level.INFO);
        String key ;
        while (true) {

            for (int i = 0; i < 1000; i++) {
                key = set();
                System.out.println("key: " + key);
                check(key);
                revoke(key);
            }
            Thread.sleep(1000);
        }

    }

    public static String set() throws URISyntaxException, IOException {
        long t1 = System.currentTimeMillis();
        URIBuilder uriBuilder = new URIBuilder(SET);
        String key = UUID.randomUUID().toString();
        uriBuilder.addParameter("key", key);
        uriBuilder.addParameter("name",key+"_1");
        uriBuilder.addParameter("age","1000");
        HttpGet get = new HttpGet(uriBuilder.build());
        String result = EntityUtils.toString(httpclient.execute(get).getEntity());
        System.out.println("get : "+ (System.currentTimeMillis()-t1)/1000.0 +" seconds!");
        return result;
    }

    public static void check(String key) throws URISyntaxException, IOException, InterruptedException {
        long t1 = System.currentTimeMillis();
        URIBuilder uriBuilder = new URIBuilder(GET);

        uriBuilder.addParameter("key", key);

        HttpGet get = new HttpGet(uriBuilder.build());
        String flag = EntityUtils.toString(httpclient.execute(get).getEntity());
        while (!"true".equals(flag)){
            Thread.sleep(200);
            flag = EntityUtils.toString(httpclient.execute(get).getEntity());
        }
        System.out.println("check : "+ (System.currentTimeMillis()-t1)/1000.0 +" seconds!");
        return ;
    }
    public static void revoke(String key) throws URISyntaxException, IOException, InterruptedException {
        long t1 = System.currentTimeMillis();
        URIBuilder uriBuilder = new URIBuilder(REVOKE);

        uriBuilder.addParameter("key", key);

        HttpGet get = new HttpGet(uriBuilder.build());

        EntityUtils.toString(httpclient.execute(get).getEntity());

        System.out.println("revoke : "+ (System.currentTimeMillis()-t1)/1000.0 +" seconds!");
        return ;
    }

}
