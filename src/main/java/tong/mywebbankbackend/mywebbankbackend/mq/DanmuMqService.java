package tong.mywebbankbackend.mywebbankbackend.mq;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import tong.mywebbankbackend.mywebbankbackend.entity.Danmu;
import tong.mywebbankbackend.mywebbankbackend.service.IDanmuService;


/**
 * @Author tong-exists
 * @Create 2023/06/17 59
 * @Version 1.0
 */
@Component
public class DanmuMqService {



    @Slf4j
    @Service
    @RocketMQMessageListener(topic = "danmu-store-into-db", consumerGroup = "my-consumer-group")
    public static class MyConsumer1 implements RocketMQListener<Danmu> {
        @Autowired
        private IDanmuService danmuService;

        @Override
        public void onMessage(Danmu message) {
            danmuService.save(message);
            log.info("received message: {}", message);
        }
    }
}
