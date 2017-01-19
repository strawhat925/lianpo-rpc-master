package com.lianpo.rpc.network.common;

/**
 * Created by Administrator on 2017/1/5.
 *
 * @author lianpo
 */
public enum Version {

    VERSION_1((byte) 1),
    VERSION_2((byte) 2);

    private byte version;
    Version(byte version) {
        this.version = version;
    }

    public byte getVersion() {
        return version;
    }
}
