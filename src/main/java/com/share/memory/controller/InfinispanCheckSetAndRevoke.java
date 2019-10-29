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
public class InfinispanCheckSetAndRevoke {
    @RequestMapping(value = "/check",method = RequestMethod.GET,produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public Object getPerson(@RequestParam(name = "cacheType",required = false) String cacheType,
                      @RequestParam(name = "key")String key){
        return InfinispanStarter.check(key);
    }

    @RequestMapping(value = "/set",method = RequestMethod.GET,produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public String setPerson(
                            @RequestParam(name = "key")String key,
                            @RequestParam(name = "name")String name,
                            @RequestParam(name = "age")int age
                            ){
        InfinispanStarter.put(key,new Person(name,age));
        return key;
    }

    @RequestMapping(value = "/revoke",method = RequestMethod.GET,produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public Person revokePerson(
            @RequestParam(name = "key")String key

    ){
        return InfinispanStarter.revoke(key);
    }
}
