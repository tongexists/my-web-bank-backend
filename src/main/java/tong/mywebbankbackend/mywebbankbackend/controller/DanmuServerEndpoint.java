package tong.mywebbankbackend.mywebbankbackend.controller;

import com.alibaba.fastjson.JSON;
import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import tong.mywebbankbackend.mywebbankbackend.config.MyWebBankProperties;
import tong.mywebbankbackend.mywebbankbackend.dto.SendDanmuToMq;
import tong.mywebbankbackend.mywebbankbackend.entity.Danmu;
import tong.mywebbankbackend.mywebbankbackend.service.IDanmuService;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;


/**
 * 1. test whether messaging by websocket is useful. finished
 * 2. add watching number counter. finished
 * 3. sync store data into mysql db. finished
 * 4. use MQ to deal high usage
 * 5. use redis to speed up reading
 * @Author tong-exists
 * @Create 2023/06/17 25
 * @Version 1.0
 */
@ServerEndpoint("/danmu/{videoId}")
@Component
@Slf4j
public class DanmuServerEndpoint implements ApplicationContextAware {

    private Session session;

    private String sessionId;

    /**
     * deal with the problem that can not use @autowired in multiple bean
     */
    private static ApplicationContext APPLICATION_CONTEXT;

    /**
     * record the watching number
     * videoId -> count
     */
    private static final ConcurrentHashMap<Long, AtomicLong> ONLINE_NUMBER = new ConcurrentHashMap<>(16);

    /**
     * store DanmuServerEndpoints which connected with clients
     * sessionId -> DanmuServerEndpoint
     */
    public static final ConcurrentHashMap<String, DanmuServerEndpoint> DANMU_SERVER_ENDPOINTS = new ConcurrentHashMap<>(16);

    private KafkaTemplate<String, String> kafkaTemplate;

    private MyWebBankProperties myWebBankProperties;

    @OnOpen
    public void openConnection(Session session, @PathParam("videoId") Long videoId) {
        log.info("open connection");
        this.session = session;
        this.sessionId = this.session.getId();
        this.kafkaTemplate = (KafkaTemplate<String, String>) APPLICATION_CONTEXT.getBean("kafkaTemplate");
        this.myWebBankProperties = APPLICATION_CONTEXT.getBean(MyWebBankProperties.class);
        // user watch video, increase watching number
        if (ONLINE_NUMBER.containsKey(videoId)) {
            AtomicLong counter = ONLINE_NUMBER.get(videoId);
            counter.getAndIncrement();
        } else {
            AtomicLong counter = new AtomicLong(1L);
            ONLINE_NUMBER.put(videoId, counter);
        }

        DANMU_SERVER_ENDPOINTS.put(this.sessionId, this);
    }

    @OnClose
    public void closeConnection(@PathParam("videoId") Long videoId) {
        log.info("close connection");
        if (ONLINE_NUMBER.containsKey(videoId)) {
            AtomicLong counter = ONLINE_NUMBER.get(videoId);
            counter.getAndDecrement();
        }

        DANMU_SERVER_ENDPOINTS.remove(this.sessionId);
    }

    @OnError
    public void onError(Throwable error) {
        log.error(String.valueOf(error));
    }


    @OnMessage
    public void onMessage(String json) {
        Danmu input = JSON.parseObject(json, Danmu.class);
        IDanmuService danmuService = APPLICATION_CONTEXT.getBean(IDanmuService.class);
        input.setId(null);
        input.setCreateTime(LocalDateTime.now());

        String nJson = JSON.toJSONString(input);
        try {
            if (myWebBankProperties.isUseMq()) {
                this.kafkaTemplate.send("store-danmu-into-db",nJson).whenComplete(new BiConsumer<SendResult<String, String>, Throwable>() {
                    @Override
                    public void accept(SendResult<String, String> stringStringSendResult, Throwable throwable) {
                        log.info("send to topic[store-danmu-into-db] ok, result: {}", stringStringSendResult);
                    }
                });
                danmuService.saveToRedis(input);
                sendDanmuToMq(nJson);

            } else {
                danmuService.save(input);
                danmuService.saveToRedis(input);
                broadcast(nJson);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void sendDanmuToMq(String nJson) {
        // send danmu to mq
        List<Map.Entry<String, DanmuServerEndpoint>> list = DANMU_SERVER_ENDPOINTS.entrySet().stream().collect(Collectors.toList());
        for (int i = 0; i < list.size(); i+=10) {
            List<Map.Entry<String, DanmuServerEndpoint>> subList = null;
            if (i+10 > list.size()) {
                subList = list.subList(i, list.size());
            } else {
                subList = list.subList(i, i + 10);
            }
            SendDanmuToMq sendDanmuToMq = new SendDanmuToMq();
            sendDanmuToMq.setSessionIds(subList.stream().map(item -> item.getKey()).collect(Collectors.toList()));
            sendDanmuToMq.setMessage(nJson);
            this.kafkaTemplate.send("send-danmu-to-client", JSON.toJSONString(sendDanmuToMq));
        }
    }

    private void broadcast(String message) throws IOException {
        for (Map.Entry<String, DanmuServerEndpoint> entry : DANMU_SERVER_ENDPOINTS.entrySet()) {
            DanmuServerEndpoint endpoint = entry.getValue();
            endpoint.session.getBasicRemote().sendText(message);
        }
    }

    public void sendMessage(String message) throws IOException {
        this.session.getBasicRemote().sendText(message);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        APPLICATION_CONTEXT = applicationContext;
    }


}
