package com.self.library.interceptor;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.self.library.utils.JWTUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static com.self.library.constant.InterceptorConstant.AFTER_COMPLETION;
import static com.self.library.constant.InterceptorConstant.POST_HANDLE;
import static com.self.library.constant.LibraryConstant.CHINESE_COLON;

/**
 * @Author Administrator
 * @Title: 拦截器
 * @Description: Admin拦截器
 * @Date 2021-04-11 13:54
 * @Version: 1.0
 */
@Slf4j
public class LoginInterceptor implements HandlerInterceptor
{
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception
    {
        //浏览器的试探请求，直接放过
        if ("OPTIONS".equalsIgnoreCase(request.getMethod()))
        {
            return true;
        }

        //获取请求头的Authorization位的token
        String token = request.getHeader("Authorization");
        //使用工具类校验
        Pair<Boolean, DecodedJWT> pair = JWTUtils.verify(token);
        return pair.getLeft();
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception
    {
        log.info(this.getClass().getSimpleName() + CHINESE_COLON + POST_HANDLE);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception
    {
        log.info(this.getClass().getSimpleName() + CHINESE_COLON + AFTER_COMPLETION);
    }
}
