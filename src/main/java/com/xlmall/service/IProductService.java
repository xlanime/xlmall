package com.xlmall.service;

import com.github.pagehelper.PageInfo;
import com.xlmall.common.ServerResponse;
import com.xlmall.pojo.Product;
import com.xlmall.vo.ProductDetailVo;
import com.xlmall.vo.ProductListVo;

import java.util.List;

public interface IProductService {

    ServerResponse<Product> saveOrNewProduct(Product product);

    ServerResponse<String> setSaleStatus(Integer productId,Integer status);

    ServerResponse<ProductDetailVo> manageProductDetail(Integer productId);

    ServerResponse<PageInfo> manageProductList(Integer pageNum, Integer pageSize);

    ServerResponse<PageInfo> productSearch(Integer productId,String productName,Integer pageNum,Integer pageSize);

    ServerResponse<ProductDetailVo> getProductDetail(Integer productId);

    ServerResponse<PageInfo> getProductListByKeywordAndCategoryId(String keyword, Integer categoryId, Integer pageNum, Integer pageSize,String orderBy);
}
