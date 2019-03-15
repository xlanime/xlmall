package com.xlmall.common;

import com.google.common.collect.Sets;

import java.util.Set;

//定义常量类
public class Const {
    //字符串常量：当前用户
    public static final String CURRENT_USER = "currentUser";

    public static final String USERNAME ="username";
    public static final String EMAIL = "email";

    public static final Integer PARENT_ID = 0;

    public interface Role{
        int ROLE_CUSTOMER = 0;//普通用户
        int ROLE_ADMIN = 1;//管理员
    }

    public interface Checked{
        int CHECKED = 1; //选中
        int UN_CHECKED = 0; //未选中
    }

    public interface Cart{
        String LIMIT_NUM_SUCCESS = "LIMIT_NUM_SUCCESS"; //库存充足
        String LIMIT_NUM_FAIL = "LIMIT_NUM_SUCCESS"; //库存不足
    }

    //设置根据价格升序还是降序
    public interface ProductListOrdeyBy{
        Set<String> PRODUCT_ASC_DESC = Sets.newHashSet("price_desc","price_asc");
    }

    //商品状态枚举
    public enum ProductStatus{
        ON_SALE(1,"在线");

        private int code;
        private String status;

        ProductStatus(int code,String status){
            this.code = code;
            this.status = status;
        }

        public int getCode() {
            return code;
        }

        public String getStatus() {
            return status;
        }
    }

    //订单状态枚举
    public enum OrderStatus{
        //当订单状态code大于等于20的时候，就不应该更新回调了。
        CANCELED(0,"已取消"),
        NO_PAY(10,"未支付"),
        PAID(20,"已付款"),
        SHIPPED(40,"已发货"),
        ORDER_SUCCESS(50,"订单完成"),
        ORDER_CLOSE(60,"订单关闭");

        private int code;
        private String status;

        OrderStatus(int code,String status){
            this.code = code;
            this.status = status;
        }

        public int getCode() {
            return code;
        }

        public String getStatus() {
            return status;
        }
    }

    public interface AlipayCallback{

        String TRADE_STATUS_TRADE_SUCCESS = "TRADE_SUCCESS";//支付成功
        String TRADE_STATUS_WAIT_BUYER_PAY = "WAIT_BUYER_PAY";//交易创建

        String RESPONSE_SUCCESS = "success";
        String RESPONSE_FAILED = "failed";

//        String TRADE_CLOSED = "TRADE_CLOSED";//交易关闭
//        String TRADE_FINISHED = "TRADE_FINISHED";//交易完成
    }

    /**
     * 支付平台枚举类
     */
    public enum PayPlatform{
        ALIPAY(1,"支付宝"),
        WEIXIN(2,"微信");

        private int code;
        private String status;

        PayPlatform(int code,String status){
            this.code = code;
            this.status = status;
        }

        public int getCode() {
            return code;
        }

        public String getStatus() {
            return status;
        }
    }
}
