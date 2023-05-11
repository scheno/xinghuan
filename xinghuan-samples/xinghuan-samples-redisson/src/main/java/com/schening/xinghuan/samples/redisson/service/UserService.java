package com.schening.xinghuan.samples.redisson.service;

import com.schening.xinghuan.samples.redisson.po.UserPO;
import com.schening.xinghuan.samples.redisson.repository.UserRepository;
import com.schening.xinghuan.samples.redisson.util.SpringUtils;
import javax.jws.soap.SOAPBinding.Use;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.aop.framework.AopContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author shenchen
 * @version 1.0
 * @date 2023/5/11 16:45
 */
@Service
@RequiredArgsConstructor
public class UserService {

    private final RedissonClient redissonClient;

    private final UserRepository userRepository;

    private final String LOCK_PREFIX = "user-create";

    public String create() {
        RLock rLock = redissonClient.getLock(LOCK_PREFIX);
        try {
            boolean isLocked = rLock.tryLock();
            if (isLocked) {
                UserService proxy = getService();
                proxy.createUser();
            } else {
                return "正在创建";
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            rLock.unlock();
        }
        return "创建成功";
    }

    @Transactional(rollbackFor = Exception.class)
    public void createUser() {
        UserPO user1 = new UserPO();
        user1.setName("name1");
        UserPO user2 = new UserPO();
        user2.setName("user2");
        userRepository.insert(user1);
        int result = 1 / 0;
        userRepository.insert(user2);
    }

    private UserService getService() {
        return SpringUtils.getBean(this.getClass());
    }

}
