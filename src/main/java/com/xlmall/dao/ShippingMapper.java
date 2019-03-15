package com.xlmall.dao;

import com.xlmall.pojo.Shipping;
import com.xlmall.pojo.ShippingExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface ShippingMapper {
    int countByExample(ShippingExample example);

    int deleteByExample(ShippingExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(Shipping record);

    int insertSelective(Shipping record);

    List<Shipping> selectByExample(ShippingExample example);

    Shipping selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") Shipping record, @Param("example") ShippingExample example);

    int updateByExample(@Param("record") Shipping record, @Param("example") ShippingExample example);

    int updateByPrimaryKeySelective(Shipping record);

    int updateByPrimaryKey(Shipping record);

    int deleteByUserIdShippingId(@Param("userId") Integer userId,@Param("shippingId") Integer shippingId);

    int updateShippingByUserId(Shipping shipping);

    Shipping selectByUserIdShippingId(@Param("userId") Integer userId,@Param("shippingId") Integer shippingId);

    List<Shipping> selectByUserId(Integer userId);
}