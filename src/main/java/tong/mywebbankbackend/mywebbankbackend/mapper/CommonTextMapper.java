package tong.mywebbankbackend.mywebbankbackend.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Param;
import tong.mywebbankbackend.mywebbankbackend.entity.CommonText;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author tong
 * @since 2023-06-10
 */
public interface CommonTextMapper extends BaseMapper<CommonText> {

    IPage<CommonText> listByBusinessType(IPage<?> page, @Param("businessType") int businessType);

}
