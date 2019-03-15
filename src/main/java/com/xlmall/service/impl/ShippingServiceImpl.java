package com.xlmall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xlmall.common.ServerResponse;
import com.xlmall.dao.ShippingMapper;
import com.xlmall.pojo.Shipping;
import com.xlmall.service.IShippingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("iShippingServiceImpl")
public class ShippingServiceImpl implements IShippingService{

    @Autowired
    private ShippingMapper shippingMapper;

    @Override
    public ServerResponse<Integer> add(Integer userId, Shipping shipping) {
        shipping.setUserId(userId);
        int rowCount = shippingMapper.insert(shipping);
        if(rowCount > 0){
            Integer shippingId = shipping.getId();
            return ServerResponse.createBySuccess("新建收货地址成功",shippingId);
        }
        return ServerResponse.createByErrorMessage("新建收货地址失败");
    }

    @Override
    public ServerResponse<String> delete(Integer userId, Integer shippingId) {
        int rowCount = shippingMapper.deleteByUserIdShippingId(userId,shippingId);
        if(rowCount>0){
            return ServerResponse.createBySuccess("删除地址成功");
        }
        return ServerResponse.createByErrorMessage("删除地址失败");
    }

    @Override
    public ServerResponse<String> update(Integer userId, Shipping shipping) {
        shipping.setUserId(userId);
        int rowCount = shippingMapper.updateShippingByUserId(shipping);
        if(rowCount>0){
            return ServerResponse.createBySuccess("更新地址成功");
        }
        return ServerResponse.createByErrorMessage("更新地址失败");
    }

    @Override
    public ServerResponse<Shipping> select(Integer userId, Integer shippingId) {
        Shipping shipping = shippingMapper.selectByUserIdShippingId(userId,shippingId);
        if(shipping!=null) {
            return ServerResponse.createBySuccess("查询地址成功", shipping);
        }
        return ServerResponse.createBySuccess("查询地址失败", shipping);
    }

    @Override
    public ServerResponse<PageInfo> list(Integer userId, Integer pageNum, Integer pageSize) {
        List<Shipping> shippingList = shippingMapper.selectByUserId(userId);
        PageHelper.startPage(pageNum,pageSize);
        PageInfo pageInfo = new PageInfo(shippingList);
        return ServerResponse.createBySuccess("获取地址列表成功",pageInfo);
    }
}
