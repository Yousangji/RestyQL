package restyql.interceptor;


import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import restyql.annotation.RestyQL;
import restyql.annotation.RestyQLSchema;
import restyql.model.RestySchema;
import restyql.util.RestyQLBuilder;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;

public class RestyQLHandlerArgResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter methodParameter) {
        return methodParameter.hasParameterAnnotation(RestyQLSchema.class);
    }

    @Override
    public Object resolveArgument(MethodParameter methodParameter, ModelAndViewContainer modelAndViewContainer, NativeWebRequest nativeWebRequest, WebDataBinderFactory webDataBinderFactory) throws Exception {
        //if RestyQLSchema annotation cling to parameter resolved bind parameter with resolved Resty Schema of returning model type
        RestySchema restySchema = new RestySchema();
        if (nativeWebRequest.getAttribute(RestyQLBuilder.RESTY_SCHEMA_ATTRIBUTE, 0) == null) {
            Type returnType = methodParameter.getMethod().getGenericReturnType();
            Class<?> elementType;
            if (returnType instanceof ParameterizedType) {
                //ParameterizedType returnType = (ParameterizedType) methodParameter.getMethod().getGenericReturnType();
                elementType = (Class<?>) ((ParameterizedType) returnType).getActualTypeArguments()[0];
            } else {
                elementType = (Class<?>) returnType;
            }

            RestyQL annotation = methodParameter.getMethod().getAnnotation(RestyQL.class);
            restySchema.setRestyMap((HashMap) RestyQLBuilder.buildSchema(elementType, annotation, nativeWebRequest.getParameter("query"), nativeWebRequest.getParameter("ignore")));
            nativeWebRequest.setAttribute(RestyQLBuilder.RESTY_SCHEMA_ATTRIBUTE, restySchema, 0);
        } else {
            restySchema = (RestySchema) nativeWebRequest.getAttribute(RestyQLBuilder.RESTY_SCHEMA_ATTRIBUTE, 0);
        }

        return restySchema;
    }
}
