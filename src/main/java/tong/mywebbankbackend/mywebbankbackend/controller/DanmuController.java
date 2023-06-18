package tong.mywebbankbackend.mywebbankbackend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tong.mywebbankbackend.mywebbankbackend.entity.Danmu;
import tong.mywebbankbackend.mywebbankbackend.service.IDanmuService;

import java.util.List;

/**
 * @Author tong-exists
 * @Create 2023/06/18 16
 * @Version 1.0
 */
@RestController
@RequestMapping("/danmu")
public class DanmuController {

    @Autowired
    private IDanmuService danmuService;


    @GetMapping("/danmuList/{videoId}")
    public List<Danmu> danmuList(@PathVariable("videoId") Long videoId) {
        List<Danmu> danmus = null;
        danmus = danmuService.listByVideoIdFromRedis(videoId);
        if (danmus == null || danmus.size() == 0) {
            danmus = danmuService.lambdaQuery().eq(Danmu::getVedioId, videoId).list();
        }
        return danmus;
    }

}
