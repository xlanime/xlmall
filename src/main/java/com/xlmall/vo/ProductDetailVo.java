package com.xlmall.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class ProductDetailVo {

    private Integer id;

    private Integer categoryId;

    private String name;

    private String subtitle;

    private String mainImage;

    private String subImages;

    private String detail;

    private BigDecimal price;

    private Integer stock;

    private Integer status;

    private String createTime;

    private String updateTime;

    //图片地址，从配置文件获取
    private String imageHost;

    //父商品类别id
    private Integer parentCategoryId;
}
