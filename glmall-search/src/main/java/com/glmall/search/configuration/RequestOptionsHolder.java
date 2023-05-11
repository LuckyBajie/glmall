package com.glmall.search.configuration;

import org.elasticsearch.client.RequestOptions;

public class RequestOptionsHolder {
    private static final RequestOptions COMMON_OPTIONS;

    static {
        RequestOptions.Builder builder = RequestOptions.DEFAULT.toBuilder();
//        builder.addHeader("Authorization", "Bearer" + TOKEN);
//        builder.setHttpAsyncResponseConsumerFactory(
//                new HttpAsyncResponseConsumerFactory.HeapBufferedResponseConsumerFactory(30 * 1024 * 1024 * 1024)
//        );
        COMMON_OPTIONS = builder.build();
    }

    private RequestOptionsHolder(){}

    public static RequestOptions getCommonOptions() {
        return COMMON_OPTIONS;
    }
}
