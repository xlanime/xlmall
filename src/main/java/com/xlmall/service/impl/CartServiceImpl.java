package com.xlmall.service.impl;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.xlmall.common.Const;
import com.xlmall.common.ResponseCode;
import com.xlmall.common.ServerResponse;
import com.xlmall.dao.CartMapper;
import com.xlmall.dao.ProductMapper;
import com.xlmall.pojo.Cart;
import com.xlmall.pojo.Product;
import com.xlmall.service.ICartService;
import com.xlmall.util.BigDecimalUtil;
import com.xlmall.util.PropertiesUtil;
import com.xlmall.vo.CartProductVo;
import com.xlmall.vo.CartVo;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service("iCartService")
public class CartServiceImpl implements ICartService{

    @Autowired
    private CartMapper cartMapper;

    @Autowired
    private ProductMapper productMapper;

    @Override
    public ServerResponse<CartVo> add(Integer userId, Integer count, Integer productId) {
        //判断入参是否正确
        if(productId == null || count == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Cart cart = cartMapper.selectCartByUserIdAndProductId(userId,productId);
        //如果没有查到cart，则新增cart。如果查到了，则增加count。
        if(cart == null){
            Cart cartItem = new Cart();
            cartItem.setProductId(productId);
            cartItem.setUserId(userId);
            cartItem.setQuantity(count);
            cartItem.setChecked(Const.Checked.CHECKED);
            cartMapper.insert(cartItem);
        }else{
            count = cart.getQuantity()+count;
            cart.setQuantity(count);
            cartMapper.updateByPrimaryKey(cart);
        }
        return list(userId);
    }

    @Override
    public ServerResponse<CartVo> update(Integer userId, Integer count, Integer productId) {
        if(count == 0 || productId == 0){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Cart cart = cartMapper.selectCartByUserIdAndProductId(userId,productId);
        if(cart != null){
            cart.setQuantity(count);
        }
        cartMapper.updateByPrimaryKeySelective(cart);
        return list(userId);
    }

    @Override
    public ServerResponse<CartVo> delete(Integer userId, String productIds) {
        //前端传递的productIds是一个字符串，以逗号分隔
        List<String> productList = Splitter.on(",").splitToList(productIds);
        if(CollectionUtils.isEmpty(productList)){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        cartMapper.deleteByUserIdAndProductIds(userId,productList);
        return list(userId);
    }

    /**
     * 因为getCartVoLimit方法本来就是获取最新购物车信息，所以直接返回就可以了。
     * @param userId
     * @return
     */
    @Override
    public ServerResponse<CartVo> list(Integer userId) {
        CartVo cartVo = this.getCartVoLimit(userId);
        return ServerResponse.createBySuccess(cartVo);
    }

    @Override
    public ServerResponse<CartVo> selectOrUnSelect(Integer userId, Integer productId, Integer isChecked) {
        cartMapper.selectOrUnSelect(userId,productId,isChecked);
        return list(userId);
    }

    @Override
    public ServerResponse<Integer> getCartProductCount(Integer userId) {
        Integer num = cartMapper.getCartProductCount(userId);
        return ServerResponse.createBySuccess(num);
    }

    /**
     * 这个类的作用是用户操作完了之后，调用这个类返回最新的购物车信息
     * 根据用户Id获取CartVo对象并返回
     * @param userId
     * @return
     */
    private CartVo getCartVoLimit(Integer userId){
        CartVo cartVo = new CartVo();
        //获取到购物车列表
        List<Cart> cartList = cartMapper.selectCartByUserId(userId);
        List<CartProductVo> cartProductVoList = Lists.newArrayList();

        BigDecimal totalPrice = new BigDecimal("0");
        //首先从Cart组装CartProductVo
        if(CollectionUtils.isNotEmpty(cartList)){
            for(Cart cart:cartList){
                CartProductVo cartProductVo = new CartProductVo();
                cartProductVo.setId(cart.getId());
                cartProductVo.setUserId(userId);
                cartProductVo.setProductId(cart.getProductId());

                //根据购物车中商品Id获取商品。根据商品信息组装CartProductVo
                Product product = productMapper.selectByPrimaryKey(cart.getProductId());
                if(product != null){
                    cartProductVo.setProducMainImage(product.getMainImage());
                    cartProductVo.setProductName(product.getName());
                    cartProductVo.setProducSubtitle(product.getSubtitle());
                    cartProductVo.setProductStatus(product.getStatus());
                    cartProductVo.setProductPrice(product.getPrice());
                    cartProductVo.setProductStock(product.getStock());

                    //判断库存
                    int buyLimitCount = 0;
                    if(product.getStock() >= cart.getQuantity()){
                        //库存充足的时候，购物车中商品数量就是该数量
                        buyLimitCount = cart.getQuantity();
                        cartProductVo.setLimitQuantity(Const.Cart.LIMIT_NUM_SUCCESS);
                    }else{
                        //库存不充足的时候，数量就是库存数量
                        buyLimitCount = product.getStock();
                        cartProductVo.setLimitQuantity(Const.Cart.LIMIT_NUM_FAIL);
                        //更新库存
                        Cart cartForQuantity = new Cart();
                        cartForQuantity.setUserId(cart.getId());
                        cartForQuantity.setQuantity(buyLimitCount);
                        cartMapper.updateByPrimaryKeySelective(cartForQuantity);
                    }
                    cartProductVo.setQuantity(buyLimitCount);
                    //计算总价(根据商品是否勾选）= 数量X单价
                    cartProductVo.setProductTotalPrice(BigDecimalUtil.multiply(product.getPrice().doubleValue(),cartProductVo.getQuantity()));
                    cartProductVo.setProductChecked(cart.getChecked());
                }
                //如果商品勾选，则将金额计算到总价中
                if(cart.getChecked() == Const.Checked.CHECKED){
                    totalPrice = BigDecimalUtil.add(totalPrice.doubleValue(),cartProductVo.getProductTotalPrice().doubleValue());
                }

                cartProductVoList.add(cartProductVo);
            }
            //组装cartVo。
            cartVo.setCartTotalPrice(totalPrice);
            cartVo.setCartProductVoList(cartProductVoList);
            cartVo.setAllchecked(getAllCheckedStatus(userId));
            cartVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix"));
        }

        return cartVo;
    }

    private boolean getAllCheckedStatus(Integer userId){
        if(userId == null){
            return false;
        }
        //如果非选中的条数为0，则为全选
        return cartMapper.selectAllCheckedStatusByUserId(userId) == 0;
    }
}
