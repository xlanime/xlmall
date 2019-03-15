package com.xlmall.controller.backend;

import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import com.xlmall.common.Const;
import com.xlmall.common.ResponseCode;
import com.xlmall.common.ServerResponse;
import com.xlmall.pojo.Product;
import com.xlmall.pojo.User;
import com.xlmall.service.IFileService;
import com.xlmall.service.IProductService;
import com.xlmall.service.IUserService;
import com.xlmall.util.PropertiesUtil;
import com.xlmall.vo.ProductDetailVo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Map;

@RestController
@RequestMapping("/manage/product/")
public class ProductManageController {

    @Autowired
    private IProductService iProductService;

    @Autowired
    private IUserService iUserService;

    @Autowired
    private IFileService iFileService;

    /**
     * 新增或修改商品
     * @param product
     * @param session
     * @return
     */
    @RequestMapping(value = "save.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<Product> saveOrNewProduct(Product product, HttpSession session){
        //判断用户是否登录及用户权限
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }

        if(iUserService.checkAdminRole(user).isSuccess()){
            //登录状态和权限验证通过后，填充业务
            return iProductService.saveOrNewProduct(product);
        }else{
            return ServerResponse.createByErrorMessage("非管理员用户无权限操作");
        }
    }

    /**
     * 设置商品的销售状态
     * @param productId
     * @param status
     * @param session
     * @return
     */
    @RequestMapping(value = "set_sale_status.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> setSaleStatus(Integer productId,Integer status,HttpSession session){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }

        if(iUserService.checkAdminRole(user).isSuccess()){
            return iProductService.setSaleStatus(productId,status);
        }else{
            return ServerResponse.createByErrorMessage("非管理员用户无权限操作");
        }
    }

    /**
     * 获取商品详情
     * @param productId
     * @param session
     * @return
     */
    @RequestMapping(value = "get_detail.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<ProductDetailVo> getDetail(Integer productId, HttpSession session){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }

        if(iUserService.checkAdminRole(user).isSuccess()){
            return iProductService.manageProductDetail(productId);
        }else{
            return ServerResponse.createByErrorMessage("非管理员用户无权限操作");
        }
    }

    /**
     * 获取商品列表
     * @param session
     * @return
     */
    @RequestMapping(value = "get_list.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<PageInfo> productList(@RequestParam(value = "pageNum",defaultValue = "1")Integer pageNum,
                                                @RequestParam(value = "pageSize",defaultValue = "10") Integer pageSize, HttpSession session){
        //判断用户是否登录及用户权限
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }

        if(iUserService.checkAdminRole(user).isSuccess()){
            return iProductService.manageProductList(pageNum,pageSize);
        }else{
            return ServerResponse.createByErrorMessage("非管理员用户无权限操作");
        }
    }

    /**
     * 商品搜索。可以根据id、name进行查询。
     * @param productId
     * @param productName
     * @param pageNum
     * @param pageSize
     * @param session
     * @return
     */
    @RequestMapping(value = "search.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<PageInfo> productSearch(Integer productId,String productName,@RequestParam(value = "pageNum",defaultValue = "1")Integer pageNum,
                                                @RequestParam(value = "pageSize",defaultValue = "10") Integer pageSize, HttpSession session){
        //判断用户是否登录及用户权限
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }

        if(iUserService.checkAdminRole(user).isSuccess()){
            return iProductService.productSearch(productId,productName,pageNum,pageSize);
        }else{
            return ServerResponse.createByErrorMessage("非管理员用户无权限操作");
        }
    }

    /**
     * 文件上传方法
     * @param multipartFile
     * @param request
     * @return
     */
    @RequestMapping(value = "upload.do")
    public ServerResponse upload(@RequestParam(value = "upload_file",required = false) MultipartFile multipartFile, HttpSession session,HttpServletRequest request){
        //判断用户是否登录及用户权限
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }

        if(iUserService.checkAdminRole(user).isSuccess()){
            //获取到当前项目的upload文件夹
            String path = request.getSession().getServletContext().getRealPath("upload");
            String targetFileName = iFileService.upload(multipartFile,path);
            //如果文件名为空认为上传失败
            if(StringUtils.isBlank(targetFileName)){
                return ServerResponse.createByErrorMessage("文件上传失败");
            }
            String url = PropertiesUtil.getProperty("ftp.server.http.prefix")+targetFileName;

            //上传结果保存到Map中返回。
            Map fileMap = Maps.newHashMap();
            fileMap.put("uri",targetFileName);
            fileMap.put("url",url);

            return ServerResponse.createBySuccess("上传文件成功",fileMap);
        }else{
            return ServerResponse.createByErrorMessage("非管理员用户无权限操作");
        }
    }

    /**
     * 富文本中图片的上传
     * 注意这里使用的富文本是Simditor。返回值也是根据Simditor的规定来生成的。
     * @param multipartFile
     * @param session
     * @param request
     * @return
     */
    @RequestMapping(value = "richtext_img_upload.do")
    public Map richTextImgUpload(@RequestParam(value = "upload_file",required = false) MultipartFile multipartFile,
                                 HttpSession session, HttpServletRequest request,
                                 HttpServletResponse response){
        Map res = Maps.newHashMap();
        //判断用户是否登录及用户权限
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            res.put("success",false);
            res.put("msg","尚未登录");
            return res;
        }

        if(iUserService.checkAdminRole(user).isSuccess()){
            //获取到当前项目的upload文件夹
            String path = request.getSession().getServletContext().getRealPath("upload");
            String targetFileName = iFileService.upload(multipartFile,path);

            if(StringUtils.isBlank(targetFileName)){
                res.put("success",false);
                res.put("msg","上传失败");
                return res;
            }
            String url = PropertiesUtil.getProperty("ftp.server.http.prefix")+targetFileName;

            //上传结果保存到Map中返回。
            res.put("success",false);
            res.put("msg","上传失败");
            res.put("file_path",url);
            //添加response的header
            response.addHeader("Access-Control-Allow-Headers","X-File-Name");
            return res;

        }else{
            res.put("success",false);
            res.put("msg","无权限操作");
            return res;
        }
    }

}
