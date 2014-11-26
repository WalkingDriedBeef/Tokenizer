package com.pachira.spider.util;
/**
 * Some constants of Http protocal.
 * @author code4crafer@gmail.com
 * @since 0.5.0
 */
public abstract class HttpInfoConstant {

    public static abstract class Method {

        public static final String GET = "GET";

        public static final String HEAD = "HEAD";

        public static final String POST = "POST";

        public static final String PUT = "PUT";

        public static final String DELETE = "DELETE";

        public static final String TRACE = "TRACE";

        public static final String CONNECT = "CONNECT";

    }

    public static abstract class Header {

        public static final String REFERER = "Referer";

        public static final String USER_AGENT = "User-Agent";
    }

}
