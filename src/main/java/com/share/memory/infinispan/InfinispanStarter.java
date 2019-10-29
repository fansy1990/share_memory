package com.share.memory.infinispan;

import com.share.memory.infinispan.model.Cat;
import com.share.memory.infinispan.model.Person;
import org.infinispan.Cache;
import org.infinispan.configuration.cache.CacheMode;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.configuration.global.GlobalConfigurationBuilder;
import org.infinispan.manager.DefaultCacheManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static com.share.memory.infinispan.CacheType.PersonCat;
import static com.share.memory.infinispan.CacheType.StringPerson;

/**
 * author : fanzhe
 * email : fansy1990@foxmail.com
 * date : 2019/10/22 AM8:21.
 */

public class InfinispanStarter implements Callable<Boolean>{
    private static final Logger LOG = LoggerFactory.getLogger(InfinispanStarter.class);
    private static DefaultCacheManager cacheManager;

    private static Cache<String, Person> personCache;
    private static Cache<Person, Cat> personCatCache;


    public static void put(String key, Person p){
        put(CacheType.StringPerson,key,p,180);
    }

    public static boolean check(String key){
        return (boolean)check(CacheType.StringPerson,key);
    }
    public static Person revoke(String key){
        return personCache.remove(key);
    }

    public static void put(CacheType cacheType, Object key, Object value, long expireSeconds){
        switch (cacheType){
            case StringPerson:
                String realKey = (String) key;
                Person realValue = (Person) value;
                personCache.put(realKey,realValue,expireSeconds, TimeUnit.SECONDS);
                return ;

            case PersonCat:
                Person realPerson = (Person) key;
                Cat realCat = (Cat) value;
                personCatCache.put(realPerson,realCat,expireSeconds, TimeUnit.SECONDS);
                default:
                    LOG.error("Wrong cache type : {}", cacheType);
        }
    }

    public static DefaultCacheManager getCluster(){
        return cacheManager;
    }

    public static Object get(CacheType cacheType, Object key){
        switch (cacheType){
            case PersonCat:
                return personCatCache.get(key);
            case StringPerson:
                return personCache.get(key);
        }
        return null;
    }


    public static Object check(CacheType cacheType, Object key){
        switch (cacheType){
            case PersonCat:
                return personCatCache.containsKey(key);
            case StringPerson:
                return personCache.containsKey(key);
        }
        return null;
    }

    private void initial(){
        if(cacheManager == null){
            LOG.error("DefaultCacheManager is null");
            System.exit(1);
        }
        personCache = cacheManager.getCache(StringPerson.name());
        personCatCache = cacheManager.getCache(PersonCat.name());
        LOG.info("CacheManager initialed!");
    }


    public static void startServer() throws InterruptedException {
        InfinispanStarter starter = new InfinispanStarter();
        Future<Boolean> result = Executors.newFixedThreadPool(1).submit(starter);
        boolean flag = true ;
        while (flag){
            if(result.isDone()){
                flag = false;
            }
            Thread.sleep(1000);
        }
        LOG.info("Infinispan thread started!");
    }

    @Override
    public Boolean call() throws Exception {
        // Setup up a clustered cache manager
        GlobalConfigurationBuilder global = GlobalConfigurationBuilder.defaultClusteredBuilder();
        // Make the default cache a distributed synchronous one
        ConfigurationBuilder builder = new ConfigurationBuilder();
        builder.clustering().cacheMode(CacheMode.DIST_SYNC);
        // Initialize the cache manager
        cacheManager = new DefaultCacheManager(global.build(), builder.build());
        // Obtain the default cache
//        Cache<String, String> cache = cacheManager.getCache();
        initial();
        return true;
    }
}
