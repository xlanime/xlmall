package com.xlmall.pojo;

import lombok.Data;

import java.util.Date;

/**
 * 订单信息
 */
@Data
public class PayInfo {
    private Integer id;

    private Integer userId;

    private Long orderNo;

    private Integer payPlatform;

    private String platformNumber;

    private String platformStatus;

    private Date createTime;

    private Date updateTime;

    public PayInfo(Integer id, Integer userId, Long orderNo, Integer payPlatform, String platformNumber, String platformStatus, Date createTime, Date updateTime) {
        this.id = id;
        this.userId = userId;
        this.orderNo = orderNo;
        this.payPlatform = payPlatform;
        this.platformNumber = platformNumber;
        this.platformStatus = platformStatus;
        this.createTime = createTime;
        this.updateTime = updateTime;
    }

    public PayInfo() {
        super();
    }

}