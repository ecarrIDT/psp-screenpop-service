package net.idt.psp.screenpop.service;
import net.idt.psp.screenpop.service.config.ServiceInfoProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;


@SpringBootApplication(scanBasePackages = {"net.idt.psp.screenpop.service","org.springframework.data.redis.core"})
@EnableConfigurationProperties ( {ServiceInfoProperties.class, RedisProperties.class} )
@EnableSwagger2
public class PspScreenpopServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(PspScreenpopServiceApplication.class, args);
    }

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.any())
                .build();
    }
}
