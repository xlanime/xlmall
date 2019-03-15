package com.xlmall.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 结合购物车和商品信息的对象
 */
@Data
public class CartProductVo {

    private Integer id;

    private Integer userId;

    private Integer productId;

    private Integer quantity;

    private String productName;

    private String producSubtitle;

    private String producMainImage;

    private BigDecimal productPrice;

    private Integer productStatus;

    private BigDecimal productTotalPrice;//计算商品总价

    private Integer productStock;

    private Integer productChecked;//商品是否选中。用来计算和显示的时候使用

    private String limitQuantity;//限制数量的返回结果
}
