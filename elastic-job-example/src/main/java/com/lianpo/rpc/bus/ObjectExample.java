package com.lianpo.rpc.bus;

import com.google.common.base.Objects;
import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Ordering;

/**
 * Created by liz on 2017/1/16.
 *
 * @auther liz
 */
public class ObjectExample {


    public static void main(String[] args) {
        //equal
        Objects.equal("a", "b");//return false;

        //hashcode
        int code = Objects.hashCode(new Config());
        System.out.println(code);


        //比较器，相当于java的Comparable
        int value = ComparisonChain.start().compare("a","a", Ordering.natural().nullsLast()).result();
        System.out.println(value);

    }
}
