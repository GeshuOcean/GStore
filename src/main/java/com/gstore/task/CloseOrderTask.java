package com.gstore.task;

import com.gstore.service.IOrderService;
import com.gstore.util.PropertiesUtil;
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
        iOrderService.closeOrder(hour);
        log.info("关闭订单任务结束");
    }
}
