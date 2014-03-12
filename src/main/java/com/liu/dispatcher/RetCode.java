package com.liu.dispatcher;


public class RetCode {
    public static final int EMPTY_MESSAGE_FAILURE = 1;
    public static final int GATEWAY_FAILURE = 2;
    public static final int POST_FAILURE = 3;
    public static final int TEMPLATE_DB_FAILURE = 4;
    public static final int SUCC = 0;
    public static final int BLACKLISTED = 5;
    public static final int MSGID_IN_CACHE = 6;
}
