package tong.mywebbankbackend.mywebbankbackend.controller;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.*;
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


    private String BASE_PATH = "D:\\projects\\springboot-projects\\my-web-bank-backend\\test-resources";

    @PostConstruct
    public void begin() {

    }

    @GetMapping("/getOrganizationTalkNodes")
    public String getOrganizationTalkNodes() throws FileNotFoundException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(BASE_PATH + File.separator + "organizationTalkNodes.html")));
        return reader.lines().collect(Collectors.joining("\n"));
    }

}
