package com.schening.xinghuan.samples.elasticjob.job;

import org.apache.shardingsphere.elasticjob.api.ShardingContext;
import org.apache.shardingsphere.elasticjob.simple.job.SimpleJob;

/**
 * @author shenchen
 * @version 1.0
 * @date 2023/2/10 11:31
 */
public class MyJob implements SimpleJob {

    @Override
    public void execute(ShardingContext shardingContext) {
        System.out.println("定时任务开始执行");
        int result = 1 / 0;
        System.out.println("Hello ElasticJob:" + result);
    }
}
