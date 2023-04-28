package com.glmall.common.interceptor;

import com.glmall.common.constant.Constant;
import com.glmall.common.utils.TraceIdUtil;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;

@Slf4j
public class FeignBasicAuthRequestInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate requestTemplate) {
        // 请求上下文
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder
                .getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();

        // 对消息头进行配置
        Enumeration<String> headerNames = request.getHeaderNames();
        if (headerNames != null) {
            while (headerNames.hasMoreElements()) {
                String name = headerNames.nextElement();
                String values = request.getHeader(name);
                requestTemplate.header(name, values);
            }
        }
        // 将traceId给添加到请求头中
        requestTemplate.header(Constant.TRACE_ID, TraceIdUtil.getTraceId());

        // todo 可以将服务之间的认证信息添加到请求头中，
        //  在服务提供者方进行权限验证，保证服务的安全

        // 对请求体进行配置
        Enumeration<String> bodyNames = request.getParameterNames();
        StringBuilder body = new StringBuilder();
        if (bodyNames != null) {
            while (bodyNames.hasMoreElements()) {
                String name = bodyNames.nextElement();
                String values = request.getParameter(name);
                body.append(name).append("=").append(values).append("&");
            }
        }
        if (body.length() != 0) {
            body.deleteCharAt(body.length() - 1);
            requestTemplate.body(body.toString());
            log.info("feign interceptor body:{}", body.toString());
        }

    }
}
