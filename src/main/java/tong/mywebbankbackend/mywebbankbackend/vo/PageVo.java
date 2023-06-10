package tong.mywebbankbackend.mywebbankbackend.vo;

import lombok.Data;

import java.util.List;

/**
 * @Author tong-exists
 * @Create 2023/06/10 40
 * @Version 1.0
 */
@Data
public class PageVo<T> {
    private int pageNum;
    private int pageSize;
    private int recordsSize;
    private List<T> records;
    private int totalRecords;
    private int totalPages;
}
