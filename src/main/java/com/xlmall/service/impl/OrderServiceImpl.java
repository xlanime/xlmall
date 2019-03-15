package com.xlmall.service.impl;

import com.alipay.api.AlipayResponse;
import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.alipay.demo.trade.config.Configs;
import com.alipay.demo.trade.model.ExtendParams;
import com.alipay.demo.trade.model.GoodsDetail;
import com.alipay.demo.trade.model.builder.AlipayTradePrecreateRequestBuilder;
import com.alipay.demo.trade.model.result.AlipayF2FPrecreateResult;
import com.alipay.demo.trade.service.AlipayTradeService;
import com.alipay.demo.trade.service.impl.AlipayTradeServiceImpl;
import com.alipay.demo.trade.utils.ZxingUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.xlmall.common.Const;
import com.xlmall.common.ServerResponse;
import com.xlmall.dao.OrderItemMapper;
import com.xlmall.dao.OrderMapper;
import com.xlmall.dao.PayInfoMapper;
import com.xlmall.pojo.Order;
import com.xlmall.pojo.OrderItem;
import com.xlmall.pojo.PayInfo;
import com.xlmall.service.IOrderService;
import com.xlmall.util.BigDecimalUtil;
import com.xlmall.util.DateTimeUtil;
import com.xlmall.util.FTPUtil;
import com.xlmall.util.PropertiesUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service("iOrderService")
public class OrderServiceImpl implements IOrderService{

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Autowired
    private PayInfoMapper payInfoMapper;

    /**
     * 订单需要用户Id，订单号和上传路径
     * 创建好之后，会把二维码地址上传到ftp并返回给前端。
     * @param userId
     * @param orderNo
     * @param path
     * @return
     */
    public ServerResponse pay(Integer userId,Long orderNo,String path){

        Map<String,String> resultMap = Maps.newHashMap();
        //根据userId和orderNo查询该订单是否存在。
        Order order = orderMapper.selectByUserIdOrderNo(userId,String.valueOf(orderNo));
        if(order == null){
            return ServerResponse.createByErrorMessage("该订单不存在");
        }

        //若订单存在，则返回下单成功的信息
        // (必填) 商户网站订单系统中唯一订单号，64个字符以内，只能包含字母、数字、下划线，
        // 需保证商户系统端不能重复，建议通过数据库sequence生成，
        String outTradeNo = order.getOrderNo().toString();

        // (必填) 订单标题，粗略描述用户的支付目的。如“xxx品牌xxx门店当面付扫码消费”
        String subject = new StringBuilder().append("轩辚商城扫码支付，订单号").append(order.getOrderNo().toString()).toString();

        // (必填) 订单总金额，单位为元，不能超过1亿元
        // 如果同时传入了【打折金额】,【不可打折金额】,【订单总金额】三者,则必须满足如下条件:【订单总金额】=【打折金额】+【不可打折金额】
        String totalAmount = order.getPayment().toString();

        // (可选) 订单不可打折金额，可以配合商家平台配置折扣活动，如果酒水不参与打折，则将对应金额填写至此字段
        // 如果该值未传入,但传入了【订单总金额】,【打折金额】,则该值默认为【订单总金额】-【打折金额】
        String undiscountableAmount = "0";

        // 卖家支付宝账号ID，用于支持一个签约账号下支持打款到不同的收款账号，(打款到sellerId对应的支付宝账号)
        // 如果该字段为空，则默认为与支付宝签约的商户的PID，也就是appid对应的PID
        String sellerId = "";

        // 订单描述，可以对交易或商品进行一个详细地描述，比如填写"购买商品2件共15.00元"
        String body = new StringBuilder().append("订单").append(outTradeNo).append("购买商品共").append(totalAmount).append("元").toString();

        // 商户操作员编号，添加此参数可以为商户操作员做销售统计
        String operatorId = "test_operator_id";

        // (必填) 商户门店编号，通过门店号和商家后台可以配置精准到门店的折扣信息，详询支付宝技术支持
        String storeId = "test_store_id";

        // 业务扩展参数，目前可添加由支付宝分配的系统商编号(通过setSysServiceProviderId方法)，详情请咨询支付宝技术支持
        ExtendParams extendParams = new ExtendParams();
        extendParams.setSysServiceProviderId("2088100200300400500");//这里就用默认不做修改。

        // 支付超时，定义为120分钟
        String timeoutExpress = "120m";

        // 商品明细列表，需填写购买商品详细信息，
        List<GoodsDetail> goodsDetailList = new ArrayList<GoodsDetail>();
        //遍历获取订单商品信息并添加到商品明细列表中
        List<OrderItem> orderItems = orderItemMapper.selectByUserIdOrderNo(userId,orderNo.toString());
        for(OrderItem orderItem : orderItems){
            GoodsDetail goodsDetail = GoodsDetail.newInstance(orderItem.getProductId().toString(),orderItem.getProductName(),
                    BigDecimalUtil.multiply(orderItem.getCurrentUnitPrice().doubleValue(),new Double(100).doubleValue()).longValue(),
                    orderItem.getQuantity());
            goodsDetailList.add(goodsDetail);
        }

        // 创建扫码支付请求builder，设置请求参数
        AlipayTradePrecreateRequestBuilder builder = new AlipayTradePrecreateRequestBuilder()
                .setSubject(subject).setTotalAmount(totalAmount).setOutTradeNo(outTradeNo)
                .setUndiscountableAmount(undiscountableAmount).setSellerId(sellerId).setBody(body)
                .setOperatorId(operatorId).setStoreId(storeId).setExtendParams(extendParams)
                .setTimeoutExpress(timeoutExpress)
                .setNotifyUrl(PropertiesUtil.getProperty("alipay.callback.url"))//支付宝服务器主动通知商户服务器里指定的页面http路径,根据需要设置（沙箱应用中设置）（回调地址）
                .setGoodsDetailList(goodsDetailList);

        Configs.init("zfbinfo.properties");
        String appid = Configs.getAppid();
        AlipayTradeService tradeService = new AlipayTradeServiceImpl.ClientBuilder().build();
//                .setGatewayUrl("http://mcloudmonitor.com/gateway.do").setCharset("UTF-8")
//                .setFormat("json").setAppid(Configs.getAppid()).build();

        AlipayF2FPrecreateResult result = tradeService.tradePrecreate(builder);
        switch (result.getTradeStatus()) {
            case SUCCESS:
                log.info("支付宝预下单成功: )");

                AlipayTradePrecreateResponse response = result.getResponse();
                dumpResponse(response);

                /**
                 * 集成的关键步骤，生成二维码上传的ftp服务器。
                 */
                File folder = new File(path);
                if(!folder.exists()){
                    //如果文件路径不存在，则创建文件路径并设置可写权限。
                    folder.setWritable(true);
                    folder.mkdirs();
                }
                // 需要修改为运行机器上的路径
                //注意这里path后面是没有/的
                //上传二维码的地址
                String qrPath = String.format(path+"/qr-%s.png",response.getOutTradeNo());
                //上传二维码的文件名
                String qrFileName = String.format("qr-%s.png",response.getOutTradeNo());
                //工具类生成二维码图片
                ZxingUtils.getQRCodeImge(response.getQrCode(),256,qrPath);

                File targetFile = new File(path,qrFileName);

                //上传文件
                try {
                    FTPUtil.uploadFile(Lists.newArrayList(targetFile));
                } catch (IOException e) {
                    log.error("上传二维码异常",e);
                }
                log.info("qrPath:" + qrPath);

                //拼接二维码在ftp服务器的地址
                String qrFtpUrl = PropertiesUtil.getProperty("ftp.server.http.prefix")+targetFile.getName();
                resultMap.put("qrUrl",qrFtpUrl);

                //返回结果
                return ServerResponse.createBySuccess(resultMap);

            case FAILED:
                log.error("支付宝预下单失败!!!");
                return ServerResponse.createByErrorMessage("支付宝预下单失败!!!");

            case UNKNOWN:
                log.error("系统异常，预下单状态未知!!!");
                return ServerResponse.createByErrorMessage("系统异常，预下单状态未知!!!");

            default:
                log.error("不支持的交易状态，交易返回异常!!!");
                return ServerResponse.createByErrorMessage("不支持的交易状态，交易返回异常!!!");
        }
    }

    // 简单打印应答
    private void dumpResponse(AlipayResponse response) {
        if (response != null) {
            log.info(String.format("code:%s, msg:%s", response.getCode(), response.getMsg()));
            if (StringUtils.isNotEmpty(response.getSubCode())) {
                log.info(String.format("subCode:%s, subMsg:%s", response.getSubCode(),
                        response.getSubMsg()));
            }
            log.info("body:" + response.getBody());
        }
    }

    public ServerResponse alipayCallback(Map<String,String> params){
        //获取外部订单号：（支付宝提供）
        Long orderNo = Long.parseLong(params.get("out_trade_no"));
        //获取平台交易编号：
        String tradeNo = params.get("trade_no");
        //获取交易状态：
        String tradeStatus = params.get("trade_status");

        //验证订单是否存在
        Order order = orderMapper.selectByOrderNo(orderNo);
        if(order == null){
            return ServerResponse.createByErrorMessage("未找到该订单。非本项目订单。回调忽略。");
        }

        //判断订单状态是否成功。
        //如果状态code大于20，则不继续操作。认为是重复调用。
        if(order.getStatus() >= Const.OrderStatus.PAID.getCode()){
            return ServerResponse.createBySuccess("支付宝重复调用");
        }

        //如果交易状态为交易成功,更新订单状态为已支付
        if(Const.AlipayCallback.TRADE_STATUS_TRADE_SUCCESS.equals(tradeStatus)){
            //更新订单时间
            order.setPaymentTime(DateTimeUtil.strToDate(params.get("gmt_payment")));
            //更新订单状态
            order.setStatus(Const.OrderStatus.PAID.getCode());

            orderMapper.updateByPrimaryKeySelective(order);
        }

        PayInfo payInfo = new PayInfo();
        payInfo.setUserId(order.getUserId());
        payInfo.setOrderNo(order.getOrderNo());
        payInfo.setPayPlatform(Const.PayPlatform.ALIPAY.getCode());
        payInfo.setPlatformNumber(tradeNo);
        payInfo.setPlatformStatus(tradeStatus);

        payInfoMapper.insert(payInfo);
        return ServerResponse.createBySuccess();
    }

    public ServerResponse queryOrderPayStatus(Integer userId,Long orderNo){
        Order order = orderMapper.selectByUserIdOrderNo(userId,String.valueOf(orderNo));
        if(order == null){
            return ServerResponse.createByErrorMessage("该订单不存在");
        }

        if(order.getStatus() >= Const.OrderStatus.PAID.getCode()){
            return ServerResponse.createBySuccess();
        }
        return ServerResponse.createByError();
    }
}
