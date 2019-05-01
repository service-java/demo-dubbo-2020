package com.zksite.web.common;

import java.util.Arrays;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ImportResource;
import org.springframework.core.env.Environment;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@SpringBootApplication
@ImportResource(locations = {"classpath:spring-context-all.xml"})
@ServletComponentScan
public class Application implements EmbeddedServletContainerCustomizer {

    @Autowired
    private Environment environment;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Override
    public void customize(ConfigurableEmbeddedServletContainer container) {
        String _port = System.getProperty("tomcat.server.port");
        if (StringUtils.isBlank(_port)) {
            _port = environment.getProperty("tomcat.server.port");
        }
        if (StringUtils.isNotBlank(_port)) {
            container.setPort(Integer.valueOf(_port));
        }
    }


    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.addAllowedOrigin(environment.getProperty("cors.origin"));
        corsConfiguration.addAllowedHeader(environment.getProperty("cors.header"));
        String methods = environment.getProperty("cors.method");
        String[] methodList = StringUtils.split(methods, ",");
        corsConfiguration.setAllowedMethods(Arrays.asList(methodList));
        source.registerCorsConfiguration("/**", corsConfiguration);
        return new CorsFilter(source);
    }

}
