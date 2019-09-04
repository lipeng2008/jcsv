package com.github.jcsv;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.Arrays;
import java.util.List;

public class SpringContext implements ApplicationContextAware {
    private static SpringContext singleton;
    private ApplicationContext context;

    public SpringContext() {
        initSingleton(this);
    }

    public static SpringContext getSingleton() {
        return singleton;
    }

    private static void initSingleton(SpringContext springContext) {
        singleton = springContext;
    }

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
    }

    public List<String> getBeanDefinitionNames() {
        return Arrays.asList(this.context.getBeanDefinitionNames());
    }

    public Object getBean(String name) {
        return this.context.getBean(name);
    }
}