package com.self.library.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

import javax.servlet.http.HttpServletRequest;

/**
 * @Author Administrator
 * @Title: Swagger配置
 * @Description: Swagger配置
 * @Date 2021-05-02 15:26
 * @Version: 1.0
 */
@Configuration
public class SwaggerConfig
{
    @Bean
    public Docket docket(Environment environment)
    {
        Profiles profiles = Profiles.of("dev", "pro");
        boolean b = environment.acceptsProfiles(profiles);
        return new Docket(DocumentationType.OAS_30).apiInfo(apiInfo())
                .ignoredParameterTypes(HttpServletRequest.class)
                .enable(b)
                .groupName("library")
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.self.library.controller"))
                .paths(PathSelectors.any())
                .build();
    }

    private ApiInfo apiInfo()
    {
        Contact contact = new Contact("self", "https://github.com/Jazng", "zjj_955@yeah.net");
        return new ApiInfoBuilder()
                .contact(contact)
                .title("Library Management System")
                .description("图书管理系统")
                .version("0.0.1-RELEASE")
                .build();
    }
}
