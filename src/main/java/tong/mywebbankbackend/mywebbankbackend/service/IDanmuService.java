package tong.mywebbankbackend.mywebbankbackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import tong.mywebbankbackend.mywebbankbackend.entity.Danmu;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author tong
 * @since 2023-06-17
 */
public interface IDanmuService extends IService<Danmu> {

    boolean saveToRedis(Danmu danmu);

    List<Danmu> listByVideoIdFromRedis(Long videoId);

}
