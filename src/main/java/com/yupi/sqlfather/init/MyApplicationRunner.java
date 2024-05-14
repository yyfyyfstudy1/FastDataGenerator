package com.rongda.ih.thirdparty.init;

import com.rongda.system.sign.util.RedisUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * addId和key做redis缓存(暂时)
 */
@Component
public class MyApplicationRunner implements ApplicationRunner {

    @Autowired
    private RedisUtil redisUtil;
    private Logger logger = LoggerFactory.getLogger(MyApplicationRunner.class);

    @Override
    public void run(ApplicationArguments args) throws Exception {
        initAppId();
    }

    public void initAppId() {
        // 暂时appId和key缓存到redis
        logger.info("Initialization the appId and appSecret...");
        redisUtil.set("APP_ID_TEST", "123123");
    }
}
