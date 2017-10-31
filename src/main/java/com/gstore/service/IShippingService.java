package com.gstore.service;

import com.github.pagehelper.PageInfo;
import com.gstore.common.ServerResponse;
import com.gstore.pojo.Shipping;

/**
 * Created by Ocean .
 */
public interface IShippingService {
    ServerResponse add(Integer userId, Shipping shipping);

    ServerResponse<String> del(Integer userId,Integer shippingId);

    ServerResponse<String> update(Integer userId,Shipping shipping);

    ServerResponse<Shipping> select(Integer userId,Integer shippingId);

    ServerResponse<PageInfo> list(Integer userId, int pageNum, int pageSize);
}
