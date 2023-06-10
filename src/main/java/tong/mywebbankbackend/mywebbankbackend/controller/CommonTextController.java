package tong.mywebbankbackend.mywebbankbackend.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;
import tong.mywebbankbackend.mywebbankbackend.entity.CommonText;
import tong.mywebbankbackend.mywebbankbackend.service.ICommonTextService;
import tong.mywebbankbackend.mywebbankbackend.vo.PageVo;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author tong
 * @since 2023-06-10
 */
@RestController
@RequestMapping("/commonText")
public class CommonTextController {

    @Autowired
    private ICommonTextService commonTextService;

    @GetMapping("/common-text/{businessType}/{pageNum}/{pageSize}")
    public PageVo<String> commonText(@PathVariable("businessType") int businessType,
                                         @PathVariable("pageNum")int pageNum,
                                         @PathVariable("pageSize")int pageSize) {
        IPage<CommonText> page = commonTextService.listByBusinessTypePage(businessType, pageNum, pageSize);
        PageVo<String> pageVo = new PageVo<>();
        pageVo.setPageNum((int) page.getCurrent());
        pageVo.setPageSize((int) page.getSize());
        pageVo.setRecordsSize(page.getRecords().size());
        pageVo.setRecords(page.getRecords().stream().map(ct -> ct.getText()).collect(Collectors.toList()));
        pageVo.setTotalPages((int) page.getPages());
        pageVo.setTotalRecords((int) page.getTotal());
        return pageVo;
    }
}
