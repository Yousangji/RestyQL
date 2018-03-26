package restyql.interceptor;


import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;
import restyql.annotation.RestyQL;
import restyql.model.RestySchema;
import restyql.util.RestyQLBuilder;

import javax.servlet.http.HttpServletRequest;


@RestControllerAdvice
public class RestyResAdvice implements ResponseBodyAdvice<Object> {


    @Override
    public boolean supports(MethodParameter methodParameter, Class aClass) {
        return methodParameter.hasMethodAnnotation(RestyQL.class);
    }


    @Override
    public Object beforeBodyWrite(Object o, MethodParameter methodParameter, MediaType mediaType, Class<? extends HttpMessageConverter<?>> aClass, ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse) {
        //catch http response to intersect response model with schema
        HttpServletRequest httpServletRequest = ((ServletServerHttpRequest) serverHttpRequest).getServletRequest();
        RestySchema restySchema = (RestySchema) httpServletRequest.getAttribute(RestyQLBuilder.RESTY_SCHEMA_ATTRIBUTE); // restSchema MD5 Hash
        return RestyQLBuilder.buildDataFromSchemaMap(restySchema, o);
    }

}
