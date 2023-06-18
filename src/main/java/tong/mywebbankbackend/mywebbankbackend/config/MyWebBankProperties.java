package tong.mywebbankbackend.mywebbankbackend.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @Author tong-exists
 * @Create 2023/06/18 9
 * @Version 1.0
 */
@Component
@ConfigurationProperties(prefix = "tong.my-web-bank")
@Data
public class MyWebBankProperties {

    private boolean useMq;
    private boolean useRedis;

}
