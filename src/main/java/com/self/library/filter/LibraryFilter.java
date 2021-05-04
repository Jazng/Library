package com.self.library.filter;

import lombok.extern.slf4j.Slf4j;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.self.library.constant.FilterConstant.FILTER_DESTROY;
import static com.self.library.constant.FilterConstant.FILTER_INIT;
import static com.self.library.constant.LibraryConstant.CHINESE_COLON;

/**
 * @Author Administrator
 * @Title: 过滤器
 * @Description: 主要解决一些跨域问题等，这里设置为对所有请求都进行过滤
 * @Date 2021-04-17 13:53
 * @Version: 1.0
 */
@WebFilter("/*")
@Slf4j
public class LibraryFilter implements Filter
{
    @Override
    public void init(FilterConfig filterConfig)
    {
        log.info(this.getClass().getName() + CHINESE_COLON + FILTER_INIT);
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException
    {
        HttpServletRequest req = (HttpServletRequest) servletRequest;
        HttpServletResponse resp = (HttpServletResponse) servletResponse;

        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json;charset=utf-8");

        /* 允许跨域的主机地址 */
        resp.setHeader("Access-Control-Allow-Origin", req.getHeader("Origin"));
        /* 允许跨域的请求方法GET, POST, HEAD 等 */
        resp.setHeader("Access-Control-Allow-Methods", "*");
        /* 重新预检验跨域的缓存时间 (s) */
        resp.setHeader("Access-Control-Max-Age", "3600");
        /* 允许跨域的请求头 */
        resp.setHeader("Access-Control-Allow-Headers", "*");
        /* 是否携带cookie */
        resp.setHeader("Access-Control-Allow-Credentials", "true");
        resp.setHeader("XDomainRequestAllowed", "1");

        filterChain.doFilter(req, resp);
    }

    @Override
    public void destroy()
    {
        log.info(this.getClass().getName() + CHINESE_COLON + FILTER_DESTROY);
    }
}
