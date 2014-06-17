package com.liu.dispatcher;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.liu.helper.HttpClientVM;

public class HttpRequestTester {

    @Test
    public void testHttpRequestGet() {
        HttpClientVM.init(200, 50);
        String response = HttpClientVM.get("http://localhost:8000");
        response = HttpClientVM.getWithReferrer("http://localhost:8000", "http://baidu.com");

        assertEquals("", response);
    }
}
