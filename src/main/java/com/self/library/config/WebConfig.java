package com.self.library.config;

import com.google.common.collect.Lists;
import com.self.library.interceptor.LoginInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

import static com.self.library.constant.LibraryConstant.*;

/**
 * @Author Administrator
 * @Title: SpringBoot配置
 * @Description: 拦截器配置
 * @Date 2021-04-10 15:46
 * @Version: 1.0
 */
@Configuration
public class WebConfig implements WebMvcConfigurer
{
    //程序会先到拦截器，然后再加载IOC，我们这里注入IOC，则可以在拦截器中注入bean
    @Bean
    public LoginInterceptor loginInterceptor()
    {
        return new LoginInterceptor();
    }

    //设置拦截器拦截路径及放开路径
    @Override
    public void addInterceptors(InterceptorRegistry registry)
    {
        List<String> excludeList = Lists.newArrayList(ADMIN_REGISTER, ADMIN_LOGIN);
        registry.addInterceptor(loginInterceptor()).addPathPatterns(ADMIN_ALL).excludePathPatterns(excludeList);
    }
}
