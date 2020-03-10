package com.zeal.softwareengineeringprojectmanage.listener;

import org.springframework.boot.web.servlet.ServletContextInitializer;

import javax.servlet.*;

public class MyListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("contextInitialized....web应用启动");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        System.out.println("contextDestroyed.....服务器关闭");
    }
}
