package org.apache.shardingsphere.elasticjob.error.handler.general;

import java.util.Properties;
import org.apache.shardingsphere.elasticjob.error.handler.JobErrorHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author shenchen
 * @version 1.0
 * @date 2023/2/10 14:48
 */
public final class EnhanceLogJobErrorHandler implements JobErrorHandler {

    private static final Logger log = LoggerFactory.getLogger(
            org.apache.shardingsphere.elasticjob.error.handler.general.EnhanceLogJobErrorHandler.class);

    public EnhanceLogJobErrorHandler() {
    }

    @Override
    public void init(Properties props) {
    }

    @Override
    public void handleException(String jobName, Throwable cause) {
        log.error(String.format("ENHANCE Job '%s' exception occur in job processing", jobName), cause);
    }

    @Override
    public String getType() {
        return "ENHANCELOG";
    }
}
