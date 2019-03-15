package com.xlmall.controller.portal;

import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.demo.trade.config.Configs;
import com.google.common.collect.Maps;
import com.xlmall.common.Const;
import com.xlmall.common.ResponseCode;
import com.xlmall.common.ServerResponse;
import com.xlmall.pojo.User;
import com.xlmall.service.IOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Iterator;
import java.util.Map;

@Controller
@RequestMapping("/order/")
@Slf4j
public class OrderController {

    @Autowired
    private IOrderService iOrderService;

    /**
     * 支付方法
     * @param session
     * @param orderNo
     * @param request
     * @return
     */
    @RequestMapping("pay.do")
    @ResponseBody
    public ServerResponse pay(HttpSession session, Long orderNo, HttpServletRequest request){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        String path = request.getSession().getServletContext().getRealPath("upload");

        return iOrderService.pay(user.getId(),orderNo,path);
    }

    /**
     * 支付宝回调函数，由支付宝调用
     * @param request
     * @return
     */
    @RequestMapping("alipay_callback.do")
    @ResponseBody
    public Object alipayCallback(HttpServletRequest request){
        Map<String,String> params = Maps.newHashMap();
        //从request中获取map。
        Map requestParams = request.getParameterMap();

        //使用迭代器遍历requestParams
        for(Iterator iterator = requestParams.keySet().iterator();iterator.hasNext();){
            String name = (String)iterator.next();
            String[] values = (String[])requestParams.get(name);
            String valueStr = "";
            for(int i=0;i<values.length;i++){
                valueStr = (i == values.length - 1)?valueStr + values[i]:valueStr+",";
            }
            params.put(name,valueStr);
        }
        log.info("支付宝回调,sign:{},trade_status:{},参数:{}",params.get("sign"),params.get("trade_status"),params.toString());
        //验证回调的正确性，看是不是支付宝发的，并且要避免重复通知。
        params.remove("sign_type");//文档已说明这个字段必须去掉。
        //四个参数：params参数，阿里云公钥，编码方式，加密方式
        try {
            boolean alipayRSACheck = AlipaySignature.rsaCheckV2(params, Configs.getAlipayPublicKey(),
                    "utf-8",Configs.getSignType());
            if(!alipayRSACheck) {
                return ServerResponse.createByErrorMessage("非法请求，验证不通过。");
            }
        } catch (AlipayApiException e) {
            log.error("支付宝回调验证异常",e);
        }

        //Todo:验证各种数据。

        ServerResponse serverResponse = iOrderService.alipayCallback(params);
        if(serverResponse.isSuccess()){
            return Const.AlipayCallback.RESPONSE_SUCCESS;
        }
        return Const.AlipayCallback.RESPONSE_FAILED;
    }

    /**
     * 查询订单状态接口。状态大于已支付则认为支付成功，否则支付失败。
     * @param session
     * @param orderNo
     * @param request
     * @return
     */
    @RequestMapping("query_order_pay_status.do")
    @ResponseBody
    public ServerResponse<Boolean> queryOrderPayStatus(HttpSession session, Long orderNo, HttpServletRequest request){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        ServerResponse serverResponse = iOrderService.queryOrderPayStatus(user.getId(),orderNo);
        if(serverResponse.isSuccess()){
            return ServerResponse.createBySuccess(true);
        }
        return ServerResponse.createBySuccess(false);
    }
}
