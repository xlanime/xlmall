package com.xlmall.service;

import com.xlmall.common.ServerResponse;
import com.xlmall.pojo.Category;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ICategoryService {

    ServerResponse<Category> addCategory(Category category);

    ServerResponse<Category> setCategoryName(@Param("categoryId") Integer categoryId,@Param("categoryName") String categoryName);

    ServerResponse<List<Category>> getChildrenParallelCategoryByParentId(Integer parentId);

    ServerResponse<List<Integer>> getChildrenDeepCategoryByParentId(Integer parentId);
}
