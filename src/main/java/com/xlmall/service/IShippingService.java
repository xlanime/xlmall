package com.xlmall.service;

import com.github.pagehelper.PageInfo;
import com.xlmall.common.ServerResponse;
import com.xlmall.pojo.Shipping;

public interface IShippingService {

    ServerResponse<Integer> add(Integer userId, Shipping shipping);

    ServerResponse<String> delete(Integer userId,Integer shippingId);

    ServerResponse<String> update(Integer userId,Shipping shipping);

    ServerResponse<Shipping> select(Integer userId,Integer shippingId);

    ServerResponse<PageInfo> list(Integer userId,Integer pageNum,Integer pageSize);
}
