package com.xlmall.controller.portal;

import com.github.pagehelper.PageInfo;
import com.xlmall.common.ServerResponse;
import com.xlmall.service.IProductService;
import com.xlmall.vo.ProductDetailVo;
import com.xlmall.vo.ProductListVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/product/")
public class ProductController {

    @Autowired
    private IProductService iProductService;

    /**
     * 获取商品详情
     * @param productId
     * @return
     */
    @RequestMapping(value = "get_detail")
    public ServerResponse<ProductDetailVo> getProductDetail(Integer productId){
        return iProductService.getProductDetail(productId);
    }

    /**
     * getProductListByKeywordAndCategoryId
     * 根据关键词或者CategoryId获取商品列表
     * @param keyword
     * @param categoryId
     * @param pageNum
     * @param pageSize
     * @return
     */
    @RequestMapping(value = "list.do")
    public ServerResponse<PageInfo> list(@RequestParam(value = "keyword",required = false) String keyword,
                                         @RequestParam(value = "categoryId",required = false)Integer categoryId,
                                         @RequestParam(value = "pageNum",defaultValue = "1")Integer pageNum,
                                         @RequestParam(value = "pageSize",defaultValue = "10")Integer pageSize,
                                         @RequestParam(value = "orderBy",defaultValue = "")String orderBy){
        return iProductService.getProductListByKeywordAndCategoryId(keyword,categoryId,pageNum,pageSize,orderBy);
    }

}
