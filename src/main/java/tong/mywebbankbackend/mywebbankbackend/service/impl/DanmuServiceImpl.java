package tong.mywebbankbackend.mywebbankbackend.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import tong.mywebbankbackend.mywebbankbackend.config.MyWebBankProperties;
import tong.mywebbankbackend.mywebbankbackend.config.RedisConfig;
import tong.mywebbankbackend.mywebbankbackend.entity.Danmu;
import tong.mywebbankbackend.mywebbankbackend.mapper.DanmuMapper;
import tong.mywebbankbackend.mywebbankbackend.service.IDanmuService;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author tong
 * @since 2023-06-17
 */
@Service
@Slf4j
public class DanmuServiceImpl extends ServiceImpl<DanmuMapper, Danmu> implements IDanmuService {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private MyWebBankProperties myWebBankProperties;
    @Override
    public boolean saveToRedis(Danmu danmu) {
        if (!myWebBankProperties.isUseRedis()) {
            return false;
        }
        log.info("saveToRedis: {}", danmu);
        ListOperations<String, String> lOps = stringRedisTemplate.opsForList();
        lOps.rightPush(RedisConfig.danmuKey(danmu.getVedioId()), JSON.toJSONString(danmu));
        return true;
    }

    @Override
    public List<Danmu> listByVideoIdFromRedis(Long videoId) {
        if (!myWebBankProperties.isUseRedis()) {
            return null;
        }
        log.info("listByVideoIdFromRedis: {}",videoId);
        ListOperations<String, String> lOps = stringRedisTemplate.opsForList();
        String key = RedisConfig.danmuKey(videoId);
        if (stringRedisTemplate.hasKey(key)) {
            List<String> list = lOps.range(key, 0, lOps.size(key));
            return list.stream().map(item -> JSON.parseObject(item, Danmu.class)).collect(Collectors.toList());
        } else {
            return null;
        }
    }


}
