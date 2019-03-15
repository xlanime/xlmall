package com.xlmall.controller.portal;

import com.xlmall.common.Const;
import com.xlmall.common.ResponseCode;
import com.xlmall.common.ServerResponse;
import com.xlmall.pojo.User;
import com.xlmall.service.ICartService;
import com.xlmall.vo.CartVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/cart/")
public class CartController {

    @Autowired
    private ICartService iCartService;

    /**
     * 新增商品到购物车
     * @param count
     * @param productId
     * @param session
     * @return
     */
    @RequestMapping(value = "add.do")
    public ServerResponse<CartVo> add(Integer count, Integer productId, HttpSession session){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }

        return iCartService.add(user.getId(),count,productId);
    }

    /**
     * 修改购物车
     * @param count
     * @param productId
     * @param session
     * @return
     */
    @RequestMapping(value = "update.do")
    public ServerResponse<CartVo> update(Integer count, Integer productId, HttpSession session){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }

        return iCartService.update(user.getId(),count,productId);
    }

    /**
     * 删除购物车中商品
     * @param productIds
     * @param session
     * @return
     */
    @RequestMapping(value = "delete.do")
    public ServerResponse<CartVo> delete(String productIds, HttpSession session){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iCartService.delete(user.getId(),productIds);
    }

    /**
     * 获取购物车列表
     * @param session
     * @return
     */
    @RequestMapping(value = "list.do")
    public ServerResponse<CartVo> list(HttpSession session){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iCartService.list(user.getId());
    }

    /**
     * 全选
     * @param session
     * @return
     */
    @RequestMapping(value = "select_all.do")
    public ServerResponse<CartVo> selectAll(HttpSession session){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iCartService.selectOrUnSelect(user.getId(),null,Const.Checked.CHECKED);
    }

    /**
     * 全反选
     * @param session
     * @return
     */
    @RequestMapping(value = "un_select_all.do")
    public ServerResponse<CartVo> unSelectAll(HttpSession session){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iCartService.selectOrUnSelect(user.getId(),null,Const.Checked.UN_CHECKED);
    }

    /**
     * 单独选
     * @param productId
     * @param session
     * @return
     */
    @RequestMapping(value = "select.do")
    public ServerResponse<CartVo> select(Integer productId,HttpSession session){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iCartService.selectOrUnSelect(user.getId(),productId,Const.Checked.CHECKED);
    }

    /**
     * 单反选
     * @param productId
     * @param session
     * @return
     */
    @RequestMapping(value = "un_select.do")
    public ServerResponse<CartVo> unSelect(Integer productId,HttpSession session){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iCartService.selectOrUnSelect(user.getId(),productId,Const.Checked.UN_CHECKED);
    }

    /**
     * 获取商品数量
     * @param session
     * @return
     */
    @RequestMapping(value = "get_cart_product_count.do")
    public ServerResponse<Integer> getCartProductCount(HttpSession session){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createBySuccess(0);
        }
        return iCartService.getCartProductCount(user.getId());
    }
}
