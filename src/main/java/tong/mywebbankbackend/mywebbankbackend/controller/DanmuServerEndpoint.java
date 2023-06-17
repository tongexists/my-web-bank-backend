package tong.mywebbankbackend.mywebbankbackend.controller;

import com.alibaba.fastjson.JSON;
import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import tong.mywebbankbackend.mywebbankbackend.entity.Danmu;
import tong.mywebbankbackend.mywebbankbackend.service.IDanmuService;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;


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
    private static final ConcurrentHashMap<String, DanmuServerEndpoint> DANMU_SERVER_ENDPOINTS = new ConcurrentHashMap<>(16);

    private boolean useMq = true;

    private RocketMQTemplate rocketMQTemplate;


    @OnOpen
    public void openConnection(Session session, @PathParam("videoId") Long videoId) {
        log.info("open connection");
        this.session = session;
        this.sessionId = this.session.getId();
        this.rocketMQTemplate = (RocketMQTemplate) APPLICATION_CONTEXT.getBean("rocketMQTemplate");
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

        try {
            if (useMq) {
                this.rocketMQTemplate.asyncSend("danmu-store-into-db", input, new SendCallback() {
                    @Override
                    public void onSuccess(SendResult sendResult) {
                        log.info(sendResult.toString());
                    }

                    @Override
                    public void onException(Throwable throwable) {
                        log.error(String.valueOf(throwable));
                    }
                });
                broadcast(json);
            } else {
                danmuService.save(input);
                broadcast(json);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void broadcast(String message) throws IOException {
        for (Map.Entry<String, DanmuServerEndpoint> entry : DANMU_SERVER_ENDPOINTS.entrySet()) {
            DanmuServerEndpoint endpoint = entry.getValue();
            endpoint.session.getBasicRemote().sendText(message);
        }
    }

    private void sendMessage(String message) throws IOException {
        this.session.getBasicRemote().sendText(message);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        APPLICATION_CONTEXT = applicationContext;
    }
}
