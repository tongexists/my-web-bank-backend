package tong.mywebbankbackend.mywebbankbackend.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

/**
 * @Author tong-exists
 * @Create 2023/06/18 20
 * @Version 1.0
 */
@Configuration
@Slf4j
public class RedisConfig {
    public static String danmuKey(Long videoId) {
        return "mywebbank-danmu-" + videoId;
    }

}
