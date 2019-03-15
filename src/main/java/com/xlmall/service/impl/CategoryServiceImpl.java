package com.xlmall.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.xlmall.common.Const;
import com.xlmall.common.ServerResponse;
import com.xlmall.dao.CategoryMapper;
import com.xlmall.pojo.Category;
import com.xlmall.service.ICategoryService;
import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Set;

@Service("iCategoryService")
public class CategoryServiceImpl implements ICategoryService{

    @Autowired
    private CategoryMapper categoryMapper;

    @Override
    public ServerResponse<Category> addCategory(Category category) {
        //判断入参是否为空
        if(!StringUtils.isNotBlank(category.getName())){
            return ServerResponse.createByErrorMessage("参数错误,商品名不能为空");
        }

        //如果没有父类ID则设置为0，默认为根节点
        if(category.getParentId() == null){
            category.setParentId(Const.PARENT_ID);
        }

        //商品默认为可用状态
        category.setStatus(true);
        int rowCount = categoryMapper.insert(category);
        if(rowCount>0){
            return ServerResponse.createBySuccess("新增商品成功",category);
        }
        return ServerResponse.createByErrorMessage("新增商品失败");
    }

    @Override
    public ServerResponse<Category> setCategoryName(Integer categoryId, String categoryName) {
        //查询商品分类
        Category category = categoryMapper.selectByPrimaryKey(categoryId);
        if(category == null) {
            return ServerResponse.createByErrorMessage("未找到该商品分类信息");
        }
        //设置要修改的商品分类名
        category.setName(categoryName);
        int updateCount = categoryMapper.updateByPrimaryKeySelective(category);
        if(updateCount > 0){
            return ServerResponse.createBySuccess("修改商品分类成功",category);
        }
        return ServerResponse.createByErrorMessage("修改商品分类失败");
    }

    @Override
    public ServerResponse<List<Category>> getChildrenParallelCategoryByParentId(Integer parentId) {
        List<Category> categoryList = categoryMapper.selectByParentId(parentId);
        if(CollectionUtils.isEmpty(categoryList)){
            return ServerResponse.createBySuccessMessage("未查询到相关分类信息");
        }
        return ServerResponse.createBySuccess("查询分类成功",categoryList);
    }

    @Override
    public ServerResponse<List<Integer>> getChildrenDeepCategoryByParentId(Integer parentId) {
        Set<Category> categorySet = Sets.newHashSet();
        categorySet = findChildCategory(categorySet,parentId);
        List<Integer> categoryIdList = Lists.newArrayList();
        for(Category category :categorySet){
            categoryIdList.add(category.getId());
        }
        return ServerResponse.createBySuccess("获取Id成功",categoryIdList);
    }

    private Set<Category> findChildCategory(Set<Category> categorySet,Integer categoryId){
        Category category = categoryMapper.selectByPrimaryKey(categoryId);
        if(category != null){
            categorySet.add(category);
        }

        //查找该分类的子节点。如果有子节点则递归查找所有子分类。
        List<Category> categoryList = categoryMapper.selectByParentId(categoryId);
        for(Category categoryItem : categoryList){
            findChildCategory(categorySet,categoryItem.getId());
        }
        return categorySet;
    }
}
