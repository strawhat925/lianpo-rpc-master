package com.lianpo.rpc.network.protocol;

import com.lianpo.rpc.exception.RpcException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by lianpo on 2017/1/6.
 *
 * @auther lianpo
 */
public abstract class AbstractNode<T> implements Node<T> {
    private static final Logger logger = LoggerFactory.getLogger(AbstractNode.class);
    private boolean init = false;
    private boolean available = false;

    @Override
    public void init() {
        if (init) {
            logger.warn(this.getClass().getSimpleName() + " node already exists init:" + desc());
            return;
        }
        boolean result = doInit();
        if (!result) {
            logger.error(this.getClass().getSimpleName() + " node init Error: " + desc());
            throw new RpcException(this.getClass().getSimpleName() + " node init Error: " + desc());
        } else {
            logger.info(this.getClass().getSimpleName() + " node init Success: " + desc());

            init = true;
            available = true;
        }
    }

    protected abstract boolean doInit();

    @Override
    public boolean isAvailable() {
        return this.available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }
}
