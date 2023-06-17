package tong.mywebbankbackend.mywebbankbackend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * <p>
 * 
 * </p>
 *
 * @author tong
 * @since 2023-06-17
 */
@TableName("t_danmu")
@Data
public class Danmu implements Serializable {

    private static final long serialVersionUID = 1L;
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long userId;

    private Long vedioId;

    private String text;

    private Long danmuTime;

    private LocalDateTime createTime;


}
