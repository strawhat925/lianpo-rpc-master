package com.lianpo.rpc.bus;

import com.google.common.base.Function;
import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Ordering;
import com.google.common.primitives.Ints;

import org.springframework.core.annotation.Order;

/**
 * Created by liz on 2017/1/16.
 *
 * @auther liz
 */
public class OrderingExample {

    public static void main(String[] args) {
        //创建排序器，比较两个字符串长度
        Ordering<String> ordering = new Ordering<String>() {
            @Override
            public int compare(String s, String t1) {
                return Ints.compare(s.length(), t1.length());
            }
        };

        int result = ComparisonChain.start().compare("1313", "123", ordering).result();
        System.out.println(result);

        Ordering<Config> ordering1 = Ordering.natural().nullsFirst().onResultOf(new Function<Config, String>() {

            @Override
            public String apply(Config config) {
                return config.toString();
            }
        });

    }
}
