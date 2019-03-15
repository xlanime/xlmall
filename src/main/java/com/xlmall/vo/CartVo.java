package com.xlmall.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * CartProductVo的集合
 */
@Data
public class CartVo {

    private List<CartProductVo> cartProductVoList;

    private BigDecimal cartTotalPrice;//购物车选中商品的总价

    private boolean allchecked;//是否全部勾选

    private String imageHost;
}
