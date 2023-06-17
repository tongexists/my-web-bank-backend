package tong.mywebbankbackend.mywebbankbackend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

/**
 * @Author tong-exists
 * @Create 2023/06/17 37
 * @Version 1.0
 */
@Configuration
public class WebSocketConfig {

    /**
     * 发现server endpoint
     * @return
     */
    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }
}
