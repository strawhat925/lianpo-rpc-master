package com.lianpo.rpc;

import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;

/**
 * Created by lianpo on 2017/1/16.
 *
 * @auther lianpo
 */
public class MyElasticJob implements SimpleJob {

    @Override
    public void execute(ShardingContext shardingContext) {

        System.out.println("taskId:" + shardingContext.getTaskId());
        System.out.println("getShardingItem:" + shardingContext.getShardingItem());

        switch (shardingContext.getShardingItem()) {
            case 0:
                // do something by sharding item 0
                System.out.println("执行第一个分页的业务逻辑.");
                break;
            case 1:
                System.out.println("执行第二个分页的业务逻辑.");
                // do something by sharding item 1
                break;
            case 2:
                System.out.println("执行第三个分页的业务逻辑.");
                // do something by sharding item 2
                break;
            // case n: ...
        }
    }
}
