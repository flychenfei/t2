package com.britesnow.samplesocial.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpConnectionManager;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.params.HttpClientParams;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import org.apache.commons.httpclient.params.HttpMethodParams;

public class Client {
    public static String UTF8 = "UTF-8";
    
    public static String send(HttpMethod method) {
        String response = null;
        HttpMethodParams param = method.getParams();
        param.setContentCharset("UTF-8");

        int responseCode = -1;
        try {
            HttpClient client = createClient();
            method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler(3, false));
            client.executeMethod(method);
            responseCode = method.getStatusCode();
            response = method.getResponseBodyAsString();

            if (responseCode != 200) {
                throw new RuntimeException(responseCode + "    " + response);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            method.releaseConnection();
        }
        return response;
    }
    
    public static String encode(String str){
        String ret = "";
        try {
            ret = URLEncoder.encode(str,Client.UTF8);
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return ret;
    }

    private static HttpClient createClient() {
        HttpConnectionManager connectionManager = new MultiThreadedHttpConnectionManager();
        HttpConnectionManagerParams params = connectionManager.getParams();
        params.setDefaultMaxConnectionsPerHost(150);
        params.setConnectionTimeout(30000);
        params.setSoTimeout(30000);

        HttpClientParams clientParams = new HttpClientParams();
        clientParams.setCookiePolicy(CookiePolicy.IGNORE_COOKIES);
        HttpClient client = new HttpClient(clientParams, connectionManager);
        return client;
    }
}
