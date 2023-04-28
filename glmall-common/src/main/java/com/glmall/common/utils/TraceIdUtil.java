package com.glmall.common.utils;

import com.glmall.common.constant.Constant;
import org.apache.commons.lang.StringUtils;
import org.slf4j.MDC;

import java.util.UUID;

public class TraceIdUtil {

    public static void setTraceId(String traceId) {
        MDC.put(Constant.TRACE_ID, traceId);
    }


    public static String getTraceId() {
        String traceId = MDC.get(Constant.TRACE_ID);
        if (StringUtils.isEmpty(traceId)) {
            traceId = UUID.randomUUID().toString();
        }
        return traceId;
    }

    public static void clear() {
        MDC.clear();
    }
}
