package net.idt.psp.screenpop.service.actuator;

import lombok.RequiredArgsConstructor;
import net.idt.psp.screenpop.service.config.ServiceInfoProperties;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.boot.info.BuildProperties;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

@Component
@Endpoint(id = "about")
@RequiredArgsConstructor
public class AboutEndpoint {

    private final BuildProperties buildProperties;

    private final ServiceInfoProperties serviceInfoProperties;

    @ReadOperation
    public Map<String, String> about() {
        return new LinkedHashMap<String, String>() {{
            put("service", buildProperties.getName());
            put("version", buildProperties.getVersion());
            put("built date", buildProperties.getTime().toString());
            if (serviceInfoProperties != null) {
                serviceInfoProperties.getAbout().forEach(this::put);
            }
        }};
    }
}
