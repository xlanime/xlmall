package com.xlmall.dao;

import com.xlmall.pojo.Product;
import com.xlmall.pojo.ProductExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface ProductMapper {
    int countByExample(ProductExample example);

    int deleteByExample(ProductExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(Product record);

    int insertSelective(Product record);

    List<Product> selectByExample(ProductExample example);

    Product selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") Product record, @Param("example") ProductExample example);

    int updateByExample(@Param("record") Product record, @Param("example") ProductExample example);

    int updateByPrimaryKeySelective(Product record);

    int updateByPrimaryKey(Product record);

    List<Product> getProductList();

    List<Product> productSearch(@Param("productId")Integer productId,@Param("productName")String productName);

    List<Product> selectByKeywordAndCategoryIds(@Param("keyword")String keyword,@Param("categoryIdList") List<Integer> categoryIdList);
}