package tong.mywebbankbackend.mywebbankbackend.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Param;
import tong.mywebbankbackend.mywebbankbackend.entity.CommonText;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author tong
 * @since 2023-06-10
 */
public interface ICommonTextService extends IService<CommonText> {

    IPage<CommonText> listByBusinessTypePage(int businessType, int pageNum, int pageSize);

}
