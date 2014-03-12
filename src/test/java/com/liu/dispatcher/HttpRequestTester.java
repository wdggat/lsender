package com.liu.dispatcher;

import org.junit.Before;
import org.junit.Test;

import com.liu.dispatcher.HttpClientVM;

import static org.junit.Assert.assertEquals;

public class HttpRequestTester {

    @Test
    public void testHttpRequestGet() {
        HttpClientVM.init(200, 50);
        String response = HttpClientVM.get("http://localhost:8000");
        response = HttpClientVM.getWithReferrer("http://localhost:8000", "http://baidu.com");

        assertEquals("", response);
    }
}
