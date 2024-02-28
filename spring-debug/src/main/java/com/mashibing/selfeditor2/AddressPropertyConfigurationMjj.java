package com.mashibing.selfeditor2;

import org.springframework.beans.PropertyEditorRegistrar;
import org.springframework.beans.factory.config.CustomEditorConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author dyan
 * @data 2020/12/12
 */
@ComponentScan(basePackages = "com.mashibing.selfeditor2")
@Configuration
public class AddressPropertyConfigurationMjj {

//    @Bean
//    public Customer customer() {
//        return new Customer();
//    }

    @Bean
    public static CustomEditorConfigurer editorConfigurer() {
        CustomEditorConfigurer customEditorConfigurer = new CustomEditorConfigurer();
        customEditorConfigurer.setPropertyEditorRegistrars(new PropertyEditorRegistrar[]{new AddressPropertyEditorRegistrar()});
        return customEditorConfigurer;
    }

}
