package com.schening.xinghuan.samples.elasticjob;

import com.schening.xinghuan.samples.elasticjob.job.MyJob;
import java.util.ServiceLoader;
import org.apache.shardingsphere.elasticjob.api.JobConfiguration;
import org.apache.shardingsphere.elasticjob.error.handler.JobErrorHandler;
import org.apache.shardingsphere.elasticjob.infra.spi.TypedSPI;
import org.apache.shardingsphere.elasticjob.lite.api.bootstrap.impl.OneOffJobBootstrap;
import org.apache.shardingsphere.elasticjob.lite.api.bootstrap.impl.ScheduleJobBootstrap;
import org.apache.shardingsphere.elasticjob.reg.base.CoordinatorRegistryCenter;
import org.apache.shardingsphere.elasticjob.reg.zookeeper.ZookeeperConfiguration;
import org.apache.shardingsphere.elasticjob.reg.zookeeper.ZookeeperRegistryCenter;

/**
 * @author shenchen
 * @version 1.0
 * @date 2023/2/10 11:27
 */
public class ElasticJobApplication {

    public static void main(String[] args) {
        ServiceLoader.load(JobErrorHandler.class).forEach(instance -> {
            System.out.println(instance.getClass());
        });
        //  定时调度作业
        new ScheduleJobBootstrap(createRegistryCenter(), new MyJob(), createScheduleJobConfiguration()).schedule();
        // 一次性调度作业
//        new OneOffJobBootstrap(createRegistryCenter(), new MyJob(), createOneOffJobConfiguration()).execute();
    }

    private static CoordinatorRegistryCenter createRegistryCenter() {
        CoordinatorRegistryCenter regCenter = new ZookeeperRegistryCenter(
                new ZookeeperConfiguration("localhost:2181", "my-job"));
        regCenter.init();
        return regCenter;
    }

    private static JobConfiguration createJobConfiguration() {
        // 创建作业配置
        JobConfiguration jobConfig = JobConfiguration.newBuilder("MyJob", 3).cron("0/5 * * * * ?")
                .build();
        return jobConfig;
    }

    private static JobConfiguration createScheduleJobConfiguration() {
        // 创建定时作业配置， 并且使用记录日志策略
        return JobConfiguration.newBuilder("myScheduleJob", 3).cron("0/5 * * * * ?").jobErrorHandlerType("LOG").build();
    }

    private static JobConfiguration createOneOffJobConfiguration() {
        // 创建一次性作业配置， 并且使用记录日志策略
        return JobConfiguration.newBuilder("myOneOffJob", 3).jobErrorHandlerType("123").build();
//        return JobConfiguration.newBuilder("myOneOffJob", 3).jobErrorHandlerType("ENHANCELOG").build();
//        return JobConfiguration.newBuilder("myOneOffJob", 3).build();
    }
}

