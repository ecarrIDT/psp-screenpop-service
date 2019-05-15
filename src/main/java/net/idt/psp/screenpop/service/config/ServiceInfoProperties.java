package net.idt.psp.screenpop.service.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.util.Map;

@Getter
@Setter
@Configuration
@PropertySource("classpath:service-info.yml")
@ConfigurationProperties(prefix = "info")
public class ServiceInfoProperties {

    private Map<String, String> help;

    private Map<String, String> about;

    private Boolean enableSwagger;
}
