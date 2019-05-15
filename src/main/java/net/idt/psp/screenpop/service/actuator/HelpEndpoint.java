package net.idt.psp.screenpop.service.actuator;

import lombok.RequiredArgsConstructor;
import net.idt.psp.screenpop.service.config.ServiceInfoProperties;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.HashMap;
import java.util.Map;

@Component
@Endpoint(id = "help")
@RequiredArgsConstructor
public class HelpEndpoint {

    private final ServiceInfoProperties serviceInfoProperties;

    @ReadOperation
    public Map<String, String> help() {
        return new HashMap<String, String>() {{
            if(serviceInfoProperties.getEnableSwagger()){
                put("swagger", ServletUriComponentsBuilder.fromCurrentContextPath().path("/swagger-ui.html").toUriString());
            }
            serviceInfoProperties.getHelp().forEach(this::put);
        }};
    }
}
