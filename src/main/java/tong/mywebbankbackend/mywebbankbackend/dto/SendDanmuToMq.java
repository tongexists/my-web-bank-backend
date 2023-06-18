package tong.mywebbankbackend.mywebbankbackend.dto;

import lombok.Data;

import java.util.List;

/**
 * @Author tong-exists
 * @Create 2023/06/18 40
 * @Version 1.0
 */
@Data
public class SendDanmuToMq {
    private List<String> sessionIds;
    private String message;
}
