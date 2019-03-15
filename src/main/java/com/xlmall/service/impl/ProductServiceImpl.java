package com.xlmall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.xlmall.common.Const;
import com.xlmall.common.ResponseCode;
import com.xlmall.common.ServerResponse;
import com.xlmall.dao.CategoryMapper;
import com.xlmall.dao.ProductMapper;
import com.xlmall.pojo.Category;
import com.xlmall.pojo.Product;
import com.xlmall.service.ICategoryService;
import com.xlmall.service.IProductService;
import com.xlmall.util.DateTimeUtil;
import com.xlmall.util.PropertiesUtil;
import com.xlmall.vo.ProductDetailVo;
import com.xlmall.vo.ProductListVo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductServiceImpl implements IProductService {

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private ICategoryService iCategoryService;

    @Override
    public ServerResponse<Product> saveOrNewProduct(Product product) {
        if (product != null) {
            //接口约定，前端传递的图片为一串字符串，以逗号分隔。
            if (StringUtils.isNotBlank(product.getSubImages())) {
                String[] images = product.getSubImages().split(",");
                if (images.length > 0) {
                    product.setMainImage(images[0]);
                }
            }

            //如果有ID则为修改，如果没有ID则为新增
            if (product.getId() != null) {
                int rowCount = productMapper.updateByPrimaryKeySelective(product);
                if (rowCount > 0) {
                    return ServerResponse.createBySuccess("修改商品成功", product);
                }
                return ServerResponse.createByErrorMessage("修改商品失败");
            } else {
                int rowCount = productMapper.insert(product);
                if (rowCount > 0) {
                    return ServerResponse.createBySuccess("新增商品成功", product);
                }
                return ServerResponse.createByErrorMessage("新增商品失败");
            }
        }
        return ServerResponse.createByErrorMessage("输入参数不正确，Product不能为空");
    }

    @Override
    public ServerResponse<String> setSaleStatus(Integer productId, Integer status) {
        if (productId == null || status == null){
//          return ServerResponse.createByErrorMessage("参数错误，商品ID和销售状态不能为空");
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Product product = new Product();
        product.setId(productId);
        product.setStatus(status);
        int rowCount = productMapper.updateByPrimaryKeySelective(product);{
            if(rowCount > 0){
                return ServerResponse.createBySuccess("修改销售状态成功");
            }
            return ServerResponse.createByErrorMessage("修改销售状态失败");
        }
    }

    @Override
    public ServerResponse<ProductDetailVo> manageProductDetail(Integer productId){
        if(productId == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }

        Product product = productMapper.selectByPrimaryKey(productId);
        if(product == null){
            return ServerResponse.createByErrorMessage("获取商品信息失败，产品已下架或删除");
        }
        ProductDetailVo productDetailVo = assembleProductDetailVo(product);
        return ServerResponse.createBySuccess("获取商品信息成功",productDetailVo);
    }

    private ProductDetailVo assembleProductDetailVo(Product product){
        ProductDetailVo productDetailVo = new ProductDetailVo();
        productDetailVo.setId(product.getId());
        productDetailVo.setCategoryId(product.getCategoryId());
        productDetailVo.setName(product.getName());
        productDetailVo.setSubtitle(product.getSubtitle());
        productDetailVo.setMainImage(product.getMainImage());
        productDetailVo.setSubImages(product.getSubImages());
        productDetailVo.setDetail(product.getDetail());
        productDetailVo.setPrice(product.getPrice());
        productDetailVo.setStock(product.getStock());
        productDetailVo.setStatus(product.getStatus());

        //获取图片前缀及时间转型。
        productDetailVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix","http://img.xlmall.com/"));
        productDetailVo.setCreateTime(DateTimeUtil.dateToStr(product.getCreateTime()));
        productDetailVo.setUpdateTime(DateTimeUtil.dateToStr(product.getUpdateTime()));

        //获取商品分类父节点。如果没有则默认为根节点。
        Category category = categoryMapper.selectByPrimaryKey(product.getCategoryId());
        if(category == null){
            productDetailVo.setParentCategoryId(0);
        }
        productDetailVo.setParentCategoryId(category.getParentId());

        return productDetailVo;
    }

    @Override
    public ServerResponse<PageInfo> manageProductList(Integer pageNum, Integer pageSize){
        PageHelper.startPage(pageNum,pageSize);
        List<Product> productList = productMapper.getProductList();
        List<ProductListVo> productListVoList = Lists.newArrayList();
        for(Product product:productList){
            productListVoList.add(assembleProductListVo(product));
        }
        PageInfo pageInfo = new PageInfo(productListVoList);
        return ServerResponse.createBySuccess(pageInfo);
    }

    @Override
    public ServerResponse<PageInfo> productSearch(Integer productId, String productName, Integer pageNum, Integer pageSize) {
        //创建startPage
        PageHelper.startPage(pageNum,pageSize);

        //构造模糊查询条件
        productName = new StringBuilder("%").append(productName).append("%").toString();

        //获取到商品搜索结果
        List<Product> productList = productMapper.productSearch(productId,productName);
        //将Product类组装成productListVo。
        List<ProductListVo> productListVoList = Lists.newArrayList();
        for(Product product:productList){
            productListVoList.add(assembleProductListVo(product));
        }
        //创建pageInfo，并返回pageinfo
        PageInfo pageInfo = new PageInfo(productListVoList);
        return ServerResponse.createBySuccess(pageInfo);
    }

    private ProductListVo assembleProductListVo(Product product){
        ProductListVo productListVo = new ProductListVo();
        productListVo.setId(product.getId());
        productListVo.setCategoryId(product.getCategoryId());
        productListVo.setMainImage(product.getMainImage());
        productListVo.setName(product.getName());
        productListVo.setPrice(product.getPrice());
        productListVo.setStatus(product.getStatus());
        productListVo.setSubtitle(product.getSubtitle());
        productListVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix","http://img.xlmall.com/"));
        return productListVo;
    }

    @Override
    public ServerResponse<ProductDetailVo> getProductDetail(Integer productId){
        if(productId == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }

        //如果product的状态不为在线或者product为空，说明商品已下架或删除
        Product product = productMapper.selectByPrimaryKey(productId);
        if(product == null || product.getStatus()!= Const.ProductStatus.ON_SALE.getCode()){
            return ServerResponse.createByErrorMessage("获取商品信息失败，产品已下架或删除");
        }

        ProductDetailVo productDetailVo = assembleProductDetailVo(product);
        return ServerResponse.createBySuccess("获取商品信息成功",productDetailVo);
    }

    //根据关键词或者分类Id查询商品列表
    @Override
    public ServerResponse<PageInfo> getProductListByKeywordAndCategoryId(String keyword,Integer categoryId,Integer pageNum,Integer pageSize,String orderBy){
        //判断categoryId和keyword如果都不存在，返回参数错误。
        if(categoryId == null && StringUtils.isBlank(keyword)){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        //如果categoryID存在，首先查询有没有对应的category。若有则查询所有的子分类。（有现成的方法在categoryService）
        List<Integer> categoryIdList = Lists.newArrayList();
        if(categoryId != null){
            Category category = categoryMapper.selectByPrimaryKey(categoryId);
            //如果category不存在且关键词也不存在，就说明没有找到对应商品，返回空页即可。
            if(category == null && StringUtils.isBlank(keyword)){
                PageHelper.startPage(pageNum,pageSize);
                List<ProductListVo> productListVoList = Lists.newArrayList();
                PageInfo pageInfo = new PageInfo(productListVoList);
                return ServerResponse.createBySuccess("没有找到对应商品",pageInfo);
            }
            categoryIdList = iCategoryService.getChildrenDeepCategoryByParentId(categoryId).getData();
        }

        //如果keyword存在，构造keyword的条件。
        if(StringUtils.isNotBlank(keyword)){
            keyword = new StringBuilder("%").append(keyword).append("%").toString();
        }

        //判断是升序还是降序，构造条件。并使用pagehepler的order by方法。
        if(StringUtils.isNotBlank(orderBy)){
            if(Const.ProductListOrdeyBy.PRODUCT_ASC_DESC.contains(orderBy)){
                String[] orderbyArray = orderBy.split("_");
                PageHelper.orderBy(orderbyArray[0]+" "+orderbyArray[1]);
            }
        }

        //编写mapper中的查询语句。记得多条件以及in的写法。
        List<Product> productList = productMapper.selectByKeywordAndCategoryIds(StringUtils.isNotBlank(keyword)?keyword:null,categoryIdList.size()>0?categoryIdList:null);

        //组装出参
        List<ProductListVo> productListVoList = Lists.newArrayList();
        for(Product product:productList){
            ProductListVo productListVo = assembleProductListVo(product);
            productListVoList.add(productListVo);
        }

        PageHelper.startPage(pageNum,pageSize);
        PageInfo pageInfo = new PageInfo(productListVoList);
//        pageInfo.setList(productListVoList);

        return ServerResponse.createBySuccess("查询成功",pageInfo);
    }
}
