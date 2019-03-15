package com.xlmall.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductListVo {

    private Integer id;

    private Integer categoryId;

    private String name;

    private String subtitle;

    private String mainImage;

    private BigDecimal price;

    private Integer status;

    //图片地址，从配置文件获取
    private String imageHost;
}
