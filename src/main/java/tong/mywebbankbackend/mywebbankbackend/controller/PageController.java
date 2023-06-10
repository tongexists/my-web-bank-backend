package tong.mywebbankbackend.mywebbankbackend.controller;

import jakarta.annotation.PostConstruct;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

/**
 * @Author tong-exists
 * @Create 2023/06/09 21
 * @Version 1.0
 */
@RestController
@RequestMapping("/page")
public class PageController {

    private String homePage;

    @PostConstruct
    public void begin() {
        BufferedReader reader = new BufferedReader(new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream("home.vue")));
        homePage = reader.lines().collect(Collectors.joining("\n"));
    }

    @GetMapping("/getPage")
    public String getPage(@RequestParam("name") String name) {
        if ("home".equals(name)) {
            return homePage;
        }
        return null;
    }

}
