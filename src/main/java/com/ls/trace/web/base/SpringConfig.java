package com.ls.trace.web.base;

import com.howbuy.ccms.independent.CcmsIndependent;
import com.howbuy.ccms.independent.web.CcmsIndependentListener;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.IOException;

@Configuration
@ImportResource(locations = {"classpath*:es-connection.xml"})
    public class SpringConfig implements WebMvcConfigurer{

    static{
        /**
         * 此处一定要显式的加载ccms.properties， 否则CcmsIndependentListener内部加载ccms.properties的顺序要比CCmsRegister晚。
         */
        try {
            CcmsIndependent.loadConfiguration("ccms.properties");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**").allowedOrigins("*");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        System.out.println("-----配置静态目录--------");
        if(!registry.hasMappingForPattern("/static/**")){
            registry.addResourceHandler("/static/**").addResourceLocations("classpath:/static/");
        }
    }

    @Bean
    public ServletListenerRegistrationBean listenerRegister(){
        ServletListenerRegistrationBean listenerBean = new ServletListenerRegistrationBean();
        listenerBean.setListener(new CcmsIndependentListener());
        System.out.println("---注册ccms监听器---");
        return listenerBean;
    }

}
