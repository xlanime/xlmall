package com.xlmall.service;

import com.xlmall.common.ServerResponse;
import com.xlmall.vo.CartVo;

public interface ICartService {

    ServerResponse<CartVo> add(Integer userId, Integer count, Integer productId);

    ServerResponse<CartVo> update(Integer userId, Integer count, Integer productId);

    ServerResponse<CartVo> delete(Integer userId, String productIds);

    ServerResponse<CartVo> list(Integer userId);

    ServerResponse<CartVo> selectOrUnSelect(Integer userId,Integer productId,Integer isChecked);

    ServerResponse<Integer> getCartProductCount(Integer userId);

}
