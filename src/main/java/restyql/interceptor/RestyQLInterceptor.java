package restyql.interceptor;


import org.json.JSONObject;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import restyql.annotation.RestyQL;
import restyql.model.RestySchema;
import restyql.util.RestyQLBuilder;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;

public class RestyQLInterceptor implements HandlerInterceptor {


    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object handler) throws Exception {
        HandlerMethod hm = (HandlerMethod) handler;
        Method method = hm.getMethod();

        Type returnType = method.getGenericReturnType();
        Class<?> schemaModel;
        if (returnType instanceof ParameterizedType) {
            schemaModel = (Class<?>) ((ParameterizedType) returnType).getActualTypeArguments()[0];
        } else {
            schemaModel = (Class<?>) returnType;
        }

        RestyQL annotation = method.getAnnotation(RestyQL.class);
        RestySchema restySchema = new RestySchema();
        String paramQuery = httpServletRequest.getParameter("query");
        String paramIgnore = httpServletRequest.getParameter("ignore");

        restySchema.setRestyMap((HashMap) RestyQLBuilder.buildSchema(schemaModel, annotation, paramQuery, paramIgnore));


        //if parameter contains schema as a key return empty schema as a description
        if (httpServletRequest.getParameter("schema") != null) {
            HashMap restyout = restySchema.getRestyMap();
            JSONObject jo = new JSONObject(restyout);
            httpServletResponse.getWriter().write(jo.toString());
            return false;
        } else {
            httpServletRequest.setAttribute(RestyQLBuilder.RESTY_SCHEMA_ATTRIBUTE, restySchema);
            return true;
        }
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {

    }

}
