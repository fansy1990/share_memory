package com.share.memory.infinispan.model;

import java.io.Serializable;
import java.util.Arrays;

/**
 * author : fanzhe
 * email : fansy1990@foxmail.com
 * date : 2019/10/22 AM8:33.
 */
public class Person implements Serializable{
    public Person(String name, int age){
        this.name = name;
        setAge(age);
    }
    private String name;
    private int age;

    private double[] arr= new double[10000];

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        Arrays.fill(arr,1000.0);
        this.age = age;
    }
}
