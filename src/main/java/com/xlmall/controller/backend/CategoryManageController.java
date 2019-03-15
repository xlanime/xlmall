package com.xlmall.controller.backend;

import com.xlmall.common.Const;
import com.xlmall.common.ServerResponse;
import com.xlmall.pojo.Category;
import com.xlmall.pojo.User;
import com.xlmall.service.ICategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * 商品品类管理类
 */
@Controller
@RequestMapping("/manage/category/")
public class CategoryManageController {

    @Autowired
    private ICategoryService iCategoryService;

    /**
     * 新增商品分类
     * @param category
     * @return
     */
    @RequestMapping(value = "add_category.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<Category> addCategory(Category category,HttpSession session){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorMessage("用户尚未登录");
        }
        //判断用户是否是管理员用户
        if(user.getRole() == Const.Role.ROLE_ADMIN){
            return iCategoryService.addCategory(category);
        }else{
            return ServerResponse.createByErrorMessage("非管理员用户，无权限新增商品分类");
        }
    }

    /**
     * 根据分类Id修改商品分类名称
     * @param categoryId
     * @param categoryName
     * @return
     */
    @RequestMapping(value = "set_category_name.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<Category> setCategoryName(Integer categoryId,String categoryName,HttpSession session){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorMessage("用户尚未登录");
        }
        //判断用户是否是管理员用户
        if(user.getRole() == Const.Role.ROLE_ADMIN){
            return iCategoryService.setCategoryName(categoryId,categoryName);
        }else{
            return ServerResponse.createByErrorMessage("非管理员用户，无权限新增商品分类");
        }
    }

    /**
     * 根据parentId查询该分类下所有平级子分类，不递归。
     * @param parentId
     * @param session
     * @return
     */
    @RequestMapping(value = "get_category.do",method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<List<Category>> getChildrenParallelCategoryByParentId(@RequestParam(value = "parentId",defaultValue = "0") Integer parentId, HttpSession session){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorMessage("用户尚未登录");
        }
        //判断用户是否是管理员用户
        if(user.getRole() == Const.Role.ROLE_ADMIN){
            return iCategoryService.getChildrenParallelCategoryByParentId(parentId);
        }else{
            return ServerResponse.createByErrorMessage("非管理员用户，无权限新增商品分类");
        }
    }

    /**
     * 递归查询当前分类的id及所有子分类的id
     * @param parentId
     * @param session
     * @return
     */
    @RequestMapping(value = "get_deep_category.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<List<Integer>> getChildrenDeepCategoryByParentId(@RequestParam(value = "parentId",defaultValue = "0")Integer parentId,HttpSession session){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorMessage("用户尚未登录");
        }
        //判断用户是否是管理员用户
        if(user.getRole() == Const.Role.ROLE_ADMIN){
            return iCategoryService.getChildrenDeepCategoryByParentId(parentId);
        }else{
            return ServerResponse.createByErrorMessage("非管理员用户，无权限新增商品分类");
        }
    }
}
