package tong.mywebbankbackend.mywebbankbackend.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import tong.mywebbankbackend.mywebbankbackend.entity.CommonText;
import tong.mywebbankbackend.mywebbankbackend.mapper.CommonTextMapper;
import tong.mywebbankbackend.mywebbankbackend.service.ICommonTextService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author tong
 * @since 2023-06-10
 */
@Service
public class CommonTextServiceImpl extends ServiceImpl<CommonTextMapper, CommonText> implements ICommonTextService {

    @Autowired
    private CommonTextMapper commonTextMapper;

    @Override
    public IPage<CommonText> listByBusinessTypePage(int businessType, int pageNum, int pageSize) {
        IPage p = new Page(pageNum, pageSize);
        IPage<CommonText> commonTextIPage = commonTextMapper.listByBusinessType(p, businessType);
        return commonTextIPage;
    }
}
