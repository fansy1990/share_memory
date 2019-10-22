package com.share.memory;

import com.share.memory.infinispan.InfinispanStarter;
import com.share.memory.zk.ZkStart;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * author : fanzhe
 * email : fansy1990@foxmail.com
 * date : 2019/10/12 AM9:44.
 */
@SpringBootApplication
@EnableSwagger2
public class Application {
    public static void main(String[] args) {
//        ZkStart.start();
        SpringApplication.run(Application.class, args);
        InfinispanStarter.startServer();
    }

}
