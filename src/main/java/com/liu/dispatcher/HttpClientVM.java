package com.liu.dispatcher;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

public class HttpClientVM {
    private static Logger logger = Logger.getLogger(HttpClientVM.class);

    private final static String USER_AGENT = "Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; "
            + "Trident/5.0; SLCC2; .NET CLR 2.0.50727; .NET CLR 3.5.30729; .NET CLR 3.0.30729;"
            + " Media Center PC 6.0; InfoPath.3; CIBA)";

    private static DefaultHttpClient client;

    public static void init(int maxTotalConnection, int maxPerRouteConnection) {
        BasicHttpParams params = new BasicHttpParams();
        HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
        HttpProtocolParams.setContentCharset(params, "utf-8");
        HttpProtocolParams.setUseExpectContinue(params, false);
        HttpConnectionParams.setStaleCheckingEnabled(params, true);
        HttpConnectionParams.setConnectionTimeout(params, 30000);
        params.setParameter(ClientPNames.ALLOW_CIRCULAR_REDIRECTS, true);

        SchemeRegistry schemeRegistry = new SchemeRegistry();
        schemeRegistry.register(new Scheme("http", 80,
                PlainSocketFactory.getSocketFactory()));
        schemeRegistry.register(new Scheme("https", 443,
                SSLSocketFactory.getSocketFactory()));
        PoolingClientConnectionManager manager =
                new PoolingClientConnectionManager(schemeRegistry);
        manager.setMaxTotal(maxTotalConnection);
        manager.setDefaultMaxPerRoute(maxPerRouteConnection);

        client = new DefaultHttpClient(manager, params);
        logger.info("HttpClient is started");
    }

    public static String get(String url) {
        return get(url, "utf-8", null);
    }

    public static String get(String url, String encoding) {
        return get(url, encoding, null);
    }

    public static String getWithReferrer(String url, String referrer) {
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Referer", referrer);
        return get(url, "utf-8", headers);
    }

    public static String get(String url, String encoding,
                      Map<String, String> headerMap) {
        HttpResponse response = getResponse(url, encoding, headerMap, null);
        try {
            return EntityUtils.toString(response.getEntity(), encoding);
        } catch (IOException ioe) {
            logger.error("Failed to access " + url + " by ", ioe);
        }
        return null;
    }

    public static HttpResponse getResponse(String url, String encoding,
                                           Map<String, String> headerMap,
                                           HttpContext httpContext) {
        HttpResponse response = null;

        HttpGet httpGet = new HttpGet(url);
        httpGet.setHeader("User-Agent", USER_AGENT);
        if (headerMap != null) {
            for (Entry<String, String> entry : headerMap.entrySet()) {
                httpGet.setHeader(entry.getKey(), entry.getValue());
            }
        }

        try {
            if (httpContext == null)
                response = client.execute(httpGet);
            else
                response = client.execute(httpGet, httpContext);
        } catch (IOException ioe) {
            logger.error("Failed to access " + url + " by ", ioe);
        } finally {
            httpGet.releaseConnection();
        }

        return response;
    }

    public static String post(String url, Map<String, String> keyValue,
                              String encoding) {
        HttpPost httpPost = new HttpPost(url);
        httpPost.setHeader("User-Agent", USER_AGENT);

        List<NameValuePair> params = new ArrayList<NameValuePair>();
        for (String key : keyValue.keySet()) {
            params.add(new BasicNameValuePair(key, keyValue.get(key)));
        }

        try {
            httpPost.setEntity(new UrlEncodedFormEntity(params, encoding));
            HttpResponse response = client.execute(httpPost);
            return EntityUtils.toString(response.getEntity(), encoding);
        } catch (IOException ioe) {
            logger.error("Failed to access " + url + " by ", ioe);
            return null;
        } finally {
            httpPost.releaseConnection();
        }
    }

    public static String post(String url, String jsonString, String encoding) {
        HttpPost httpPost = new HttpPost(url);
        httpPost.setHeader("User-Agent", USER_AGENT);
        httpPost.setHeader("Content-Type", "application/json");

        try {
            httpPost.setEntity(new StringEntity(jsonString, encoding));
            HttpResponse response = client.execute(httpPost);
            return EntityUtils.toString(response.getEntity(), encoding);
        } catch (IOException ioe) {
            logger.error("Failed to access " + url + " by ", ioe);
            return null;
        } finally {
            httpPost.releaseConnection();
        }
    }

    public static void changeIP(InetAddress ip) {
        logger.info("IP used by HttpClient is set to " + ip.getHostAddress());
        client.getParams().setParameter(ConnRoutePNames.LOCAL_ADDRESS,
                ip);
    }

    public static void shutdown() {
        if (client != null) {
            client.getConnectionManager().shutdown();
            logger.info("HttpClient is terminated");
        }
    }
}
