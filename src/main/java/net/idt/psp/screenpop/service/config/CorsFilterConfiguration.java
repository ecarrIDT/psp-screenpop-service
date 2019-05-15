package net.idt.psp.screenpop.service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.Ordered;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;

@Profile("!default")
@Configuration
public class CorsFilterConfiguration {

    @Value("${cors.allowed-origins}")
    private String allowedOrigins;

    @Value("${cors.allowed-methods}")
    private String allowedMethods;

    @Value("${cors.allowed-headers}")
    private String allowedHeaders;

    @Value("${cors.allow-credentials}")
    private Boolean allowCredentials;

    @Bean
    public FilterRegistrationBean corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(allowCredentials);
        config.setAllowedMethods(Arrays.asList(allowedMethods.split(", ")));
        config.setAllowedOrigins(Arrays.asList(allowedOrigins.split(", ")));
        config.addAllowedHeader(allowedHeaders);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        FilterRegistrationBean registrationBean = new FilterRegistrationBean(new CorsFilter(source));
        registrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return registrationBean;
    }
}
