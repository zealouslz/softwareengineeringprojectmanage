package com.zeal.softwareengineeringprojectmanage.config;

import com.zeal.softwareengineeringprojectmanage.component.LoginHandlerInterceptor;
import com.zeal.softwareengineeringprojectmanage.component.MylocaleResolver;
import org.springframework.boot.autoconfigure.web.embedded.EmbeddedWebServerFactoryCustomizerAutoConfiguration;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.ErrorPage;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.*;

import java.util.concurrent.TimeUnit;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {




    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/static/**").addResourceLocations("classpath:/static/");
        registry.addResourceHandler("/templates/**").addResourceLocations("classpath:/templates/");
        registry.addResourceHandler("/uploadfile/**").addResourceLocations("classpath:/uploadfile/");

    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("login");
        registry.addViewController("/login.html").setViewName("login");
        registry.addViewController("/main.html").setViewName("dashboard");
        registry.addViewController("/studentmain.html").setViewName("student/studentmain");
        registry.addViewController("/teachermain.html").setViewName("teacher/teachermain");
        registry.addViewController("/adminmain.html").setViewName("admin/adminmain");
        registry.addViewController("/location").setViewName("location");
        //registry.addViewController("/studentImport.html").setViewName("admin/studentImport");

    }

    @Bean
    public LocaleResolver localeResolver(){
        return new MylocaleResolver();
    }
//注册拦截器
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoginHandlerInterceptor()).addPathPatterns("/**").
                excludePathPatterns("/login.html","/","/user/login**","/webjars/**","/asserts/**","/getCode");
    }
}
