package com.gstore.task;

import com.gstore.common.Const;
import com.gstore.service.IOrderService;
import com.gstore.util.PropertiesUtil;
import com.gstore.util.RedisShardedPoolUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Created by Ocean .
 */
@Component
@Slf4j
public class CloseOrderTask {

    @Autowired
    private IOrderService iOrderService;

    @Scheduled(cron = "0 */1 * * * ?")//每隔一分钟的整数倍
    public void closeOrderTaskV1(){
        log.info("关闭订单任务启动");
        int hour = Integer.parseInt(PropertiesUtil.getProperty("close.order.task.hour","2"));
//        iOrderService.closeOrder(hour);
        log.info("关闭订单任务结束");
    }

    @Scheduled(cron = "0 */1 * * * ?")//每隔一分钟的整数倍
    public void closeOrderTaskV2(){
        log.info("关闭订单任务启动");
        long lockTimeout = Long.parseLong(PropertiesUtil.getProperty("lock.timeout","50000"));

        Long setnxResult = RedisShardedPoolUtil.setnx(Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK,String.valueOf(System.currentTimeMillis()+lockTimeout));
        if(setnxResult != null && setnxResult.intValue() == 1){
            //如果返回值是1则表示设置成功，获取锁


        }else{
            log.info("没有获得分布式锁:{}",Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK);
        }

        log.info("关闭订单任务结束");
    }

    private void closeOrder(String lockName){
        RedisShardedPoolUtil.expire(lockName,5);//有效期50秒，防止死锁
        log.info("获取{},ThreadName:{}",Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK,Thread.currentThread().getName());
        int hour = Integer.parseInt(PropertiesUtil.getProperty("close.order.task.time.hour","2"));
        iOrderService.closeOrder(hour);
        RedisShardedPoolUtil.del(Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK);
        log.info("释放{},ThreadName:{}",Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK,Thread.currentThread().getName());
        log.info("===============================");
    }
}
