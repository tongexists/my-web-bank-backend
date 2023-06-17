package tong.mywebbankbackend.mywebbankbackend.controller;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.*;
import java.nio.file.FileSystem;
import java.util.stream.Collectors;

/**
 * @Author tong-exists
 * @Create 2023/06/09 21
 * @Version 1.0
 */
@RestController
@RequestMapping("/page")
@Slf4j
public class PageController {

    @Resource
    private RocketMQTemplate rocketMQTemplate;
    private String BASE_PATH = "D:\\projects\\springboot-projects\\my-web-bank-backend\\test-resources";

    @PostConstruct
    public void begin() {
        log.info("====================rocketMQTemplate: {}", rocketMQTemplate);
    }

    @GetMapping("/getOrganizationTalkNodes")
    public String getOrganizationTalkNodes() throws FileNotFoundException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(BASE_PATH + File.separator + "organizationTalkNodes.html")));
        return reader.lines().collect(Collectors.joining("\n"));
    }

}
