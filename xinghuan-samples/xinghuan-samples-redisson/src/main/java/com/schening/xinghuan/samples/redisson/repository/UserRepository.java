package com.schening.xinghuan.samples.redisson.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.schening.xinghuan.samples.redisson.po.UserPO;
import org.springframework.stereotype.Repository;

/**
 * @author shenchen
 * @version 1.0
 * @date 2023/5/11 19:38
 */
@Repository
public interface UserRepository extends BaseMapper<UserPO> {

}
