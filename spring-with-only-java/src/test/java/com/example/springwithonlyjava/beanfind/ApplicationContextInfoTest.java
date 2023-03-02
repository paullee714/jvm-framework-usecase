package com.example.springwithonlyjava.beanfind;

import com.example.springwithonlyjava.AppConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class ApplicationContextInfoTest {

    AnnotationConfigApplicationContext ac = new AnnotationConfigApplicationContext(AppConfig.class);

    @Test
    @DisplayName("모든 빈 출력하기")
    void findAllBean(){
        String[] beanDefinition = ac.getBeanDefinitionNames();
        for (String s : beanDefinition) {
            Object bean = ac.getBean(s);
            System.out.println("name = " + s + " Object = " + bean);
        }
    }


    @Test
    @DisplayName("Application 빈 출력하기")
    void findAApplicationBean(){
        String[] beanDefinition = ac.getBeanDefinitionNames();
        for (String s : beanDefinition) {
            BeanDefinition beanDefinition1 = ac.getBeanDefinition(s);

            if(beanDefinition1.getRole() == BeanDefinition.ROLE_APPLICATION) {
                Object bean = ac.getBean(s);
                System.out.println("name = " + s + " Object = " + bean);
            }
        }


    }
}
