package com.xlmall.dao;

import com.xlmall.pojo.Cart;
import com.xlmall.pojo.CartExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface CartMapper {
    int countByExample(CartExample example);

    int deleteByExample(CartExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(Cart record);

    int insertSelective(Cart record);

    List<Cart> selectByExample(CartExample example);

    Cart selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") Cart record, @Param("example") CartExample example);

    int updateByExample(@Param("record") Cart record, @Param("example") CartExample example);

    int updateByPrimaryKeySelective(Cart record);

    int updateByPrimaryKey(Cart record);

    Cart selectCartByUserIdAndProductId(@Param("userId") Integer userId,@Param("productId") Integer productId);

    List<Cart> selectCartByUserId(Integer userId);

    int selectAllCheckedStatusByUserId(Integer userId);

    int deleteByUserIdAndProductIds(@Param("userId") Integer userId,@Param("productIds") List<String> productIds);

    int selectOrUnSelect(@Param("userId") Integer userId,@Param("productId") Integer productId,@Param("checked") Integer checked);

    Integer getCartProductCount(Integer userId);
}