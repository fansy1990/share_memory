package com.share.memory.controller;

import com.share.memory.infinispan.CacheType;
import com.share.memory.infinispan.InfinispanStarter;
import com.share.memory.infinispan.model.Person;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

/**
 * author : fanzhe
 * email : fansy1990@foxmail.com
 * date : 2019/10/12 AM9:54.
 */
@RestController
@RequestMapping("/api")
public class InfinispanGetAndSet {
    @RequestMapping(value = "/getPerson",method = RequestMethod.GET,produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public Object getPerson(@RequestParam(name = "cacheType",required = false) String cacheType,
                      @RequestParam(name = "key")String key){
        return InfinispanStarter.get(CacheType.StringPerson,key);
    }

    @RequestMapping(value = "/setPerson",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public Object setPerson(@RequestParam(name = "cacheType") String cacheType,
                            @RequestParam(name = "key")String key,
                            @RequestBody()Person person){
        InfinispanStarter.put(CacheType.StringPerson,key,person,30);

        return InfinispanStarter.getCluster();
    }
}
