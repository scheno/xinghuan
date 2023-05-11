package com.schening.xinghuan.samples.redisson.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author shenchen
 * @version 1.0
 * @date 2023/5/11 19:40
 */
@Data
@TableName("user")
public class UserPO {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;

    private Long age;

    private String address;

}

