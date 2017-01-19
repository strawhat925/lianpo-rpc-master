package com.lianpo.rpc.enums;

import com.lianpo.rpc.serialize.Serialization;
import com.lianpo.rpc.serialize.hessian.Hessian2Serializetion;
import com.lianpo.rpc.serialize.protostuff.ProtostuffSerialization;

/**
 * Created by Administrator on 2017/1/5.
 * @author lianpo
 */
public enum SerializeEnum {

    HESSIAN(new Hessian2Serializetion()),
    PROTOSTUFF(new ProtostuffSerialization());

    public final Serialization serialization;

    SerializeEnum(Serialization serialization) {
        this.serialization = serialization;
    }

    public static SerializeEnum match(String name, SerializeEnum defaultSerializeEnum){
        for(SerializeEnum item : SerializeEnum.values()){
            if(item.name().equalsIgnoreCase(name)){
                return item;
            }
        }
        return defaultSerializeEnum;
    }


    public static void main(String[] args){
    }
}
