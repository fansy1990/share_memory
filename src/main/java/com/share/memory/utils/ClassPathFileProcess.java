package com.share.memory.utils;

import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * author : fanzhe
 * email : fansy1990@foxmail.com
 * date : 2019/10/19 AM11:01.
 */
public class ClassPathFileProcess {


    private static InputStream readClasspathFile2InputStream(String classpathFile) throws IOException {
        return new ClassPathResource(classpathFile).getInputStream();
    }

    public static Properties readClasspathFile2Properties(String classpathFile) throws IOException {
        Properties cfg = new Properties();
        InputStream in =null;
        try{
            in =readClasspathFile2InputStream(classpathFile);
            cfg.load(in);
        }finally {
            in.close();
        }
        return cfg;
    }
}
