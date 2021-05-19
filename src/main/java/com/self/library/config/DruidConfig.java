package com.self.library.config;

import com.alibaba.druid.filter.Filter;
import com.alibaba.druid.filter.stat.StatFilter;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.support.http.StatViewServlet;
import com.alibaba.druid.support.http.WebStatFilter;
import com.alibaba.druid.wall.WallConfig;
import com.alibaba.druid.wall.WallFilter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.util.*;

/**
 * @Author Administrator
 * @Title: 数据源配置
 * @Description: 使用Druid数据源
 * @Date 2021-04-10 15:44
 * @Version: 1.0
 */
@Configuration
public class DruidConfig
{
    @ConfigurationProperties(prefix = "spring.datasource")
    @Bean
    public DataSource druid()
    {
        DruidDataSource dataSource = new DruidDataSource();
        List<Filter> list = new ArrayList<>();
        list.add(wallFilter());
        list.add(statFilter());
        dataSource.setProxyFilters(list);
        return dataSource;
    }

    @Bean
    public WallFilter wallFilter()
    {
        WallFilter wallFilter = new WallFilter();
        wallFilter.setConfig(wallConfig());

        return wallFilter;
    }

    @Bean
    public WallConfig wallConfig()
    {
        WallConfig config = new WallConfig();
        //允许一次执行多条语句
        config.setMultiStatementAllow(true);
        //允许非基本语句的其他语句
        config.setNoneBaseStatementAllow(true);

        return config;
    }

    @Bean
    public StatFilter statFilter()
    {
        StatFilter statFilter = new StatFilter();
        statFilter.setSlowSqlMillis(60000);
        statFilter.setLogSlowSql(true);
        statFilter.setMergeSql(true);

        return statFilter;
    }

    /**
     * 配置Druid的监控
     * 配置一个管理后台的Servlet
     * http://localhost:8085/druid/index.html
     *
     * @return
     */
    @Bean
    public ServletRegistrationBean statViewServlet()
    {
        ServletRegistrationBean<StatViewServlet> registrationBean = new ServletRegistrationBean<>(new StatViewServlet(), "/druid/*");
        Map<String, String> initParams = new HashMap<>(4);
        initParams.put("loginUsername", "self");
        initParams.put("loginPassword", "123456");
        initParams.put("allow", "");
        initParams.put("deny", "127.0.0.1");
        registrationBean.setInitParameters(initParams);

        return registrationBean;
    }

    //配置一个web监控的filter
    @Bean
    public FilterRegistrationBean webStatFilter()
    {
        FilterRegistrationBean<WebStatFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new WebStatFilter());
        Map<String, String> initParams = new HashMap<>(4);
        initParams.put("exclusions", "*.js,*.css,/druid/*");
        registrationBean.setInitParameters(initParams);
        registrationBean.setUrlPatterns(Arrays.asList("/*"));
        return registrationBean;
    }
}
