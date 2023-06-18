package tong.mywebbankbackend.mywebbankbackend.mq;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.stereotype.Component;
import tong.mywebbankbackend.mywebbankbackend.controller.DanmuServerEndpoint;
import tong.mywebbankbackend.mywebbankbackend.dto.SendDanmuToMq;
import tong.mywebbankbackend.mywebbankbackend.entity.Danmu;
import tong.mywebbankbackend.mywebbankbackend.service.IDanmuService;

import java.io.IOException;


/**
 * @Author tong-exists
 * @Create 2023/06/17 59
 * @Version 1.0
 */
@Component
@Slf4j
@ConditionalOnProperty(name = "tong.my-web-bank.use-mq", havingValue = "true")
public class DanmuMqService {
    @Autowired
    private IDanmuService danmuService;

    @Bean
    public NewTopic topic() {
        return TopicBuilder.name("store-danmu-into-db")
                .partitions(10)
                .replicas(1)
                .build();
    }

    @KafkaListener(id = "store-danmu-into-db", topics = "store-danmu-into-db")
    public void listen(String in) {
        log.info("store-danmu-into-db");
        Danmu danmu = JSON.parseObject(in, Danmu.class);
        danmuService.save(danmu);
    }

    @Bean
    public NewTopic topic2() {
        return TopicBuilder.name("send-danmu-to-client")
                .partitions(10)
                .replicas(1)
                .build();
    }
    @KafkaListener(id = "send-danmu-to-client", topics = "send-danmu-to-client")
    public void listen2(String in) {
        log.info("send-danmu-to-client");
        SendDanmuToMq sendDanmuToMq = JSON.parseObject(in, SendDanmuToMq.class);
        for (String sessionId : sendDanmuToMq.getSessionIds()) {
            try {
                DanmuServerEndpoint.DANMU_SERVER_ENDPOINTS.get(sessionId).sendMessage(sendDanmuToMq.getMessage());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

    }


}
