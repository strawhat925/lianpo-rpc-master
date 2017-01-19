package com.lianpo.rpc.bus;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

/**
 * Created by liz on 2017/1/16.
 *
 * @auther liz
 */
public class OptionalExample {

    @RequiredArgsConstructor
    class User{
        private String userName;
        private String sex;
    }

    public static void main(String[] args){
        //引用空对象NullPointerException
        //Optional<User> optional = Optional.of(null);

        //创建引用User实例缺失的Optional的对象
        Optional<User> optional = Optional.absent();
        //引用是否存在
        if(optional.isPresent()){
            System.out.println(optional.get());
        }

        //指定引用的Optional实例，若引用为null则表示缺失
        Optional<User> optional1 = Optional.fromNullable(null);

        //前置条件
        Preconditions.checkNotNull(optional);
        Preconditions.checkElementIndex(2,1);
    }

}
