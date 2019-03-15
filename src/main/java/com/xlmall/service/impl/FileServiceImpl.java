package com.xlmall.service.impl;

import com.google.common.collect.Lists;
import com.xlmall.service.IFileService;
import com.xlmall.util.FTPUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Slf4j
@Service("iFileService")
public class FileServiceImpl implements IFileService{

    /**
     * 上传文件
     * @param multipartFile
     * @param path
     * @return 上传后的文件名
     */
    @Override
    public String upload(MultipartFile multipartFile,String path){
        //获取文件名称
        String fileName = multipartFile.getOriginalFilename();

        //获取到文件后缀名，带"."
        String fileExtentionName = fileName.substring(fileName.lastIndexOf("."));

        //生成上传后的文件名，为了防止重复使用了UUID
        String uploadFileName = UUID.randomUUID().toString()+fileExtentionName;

        //根据path，判断如果文件夹路径是否存在。若不存在则创建路径
        File fileDir = new File(path);
        if(!fileDir.exists()){
            //设置文件夹具有可写权限
            fileDir.setWritable(true);
            //循环创建path路径下所有文件夹
            fileDir.mkdirs();
        }

        //声明目标文件
        File targetFile = new File(path,uploadFileName);

        try {
            //上传文件
            multipartFile.transferTo(targetFile);
            //将targetFile上传到ftp服务器
            boolean uploadRes = FTPUtil.uploadFile(Lists.<File>newArrayList(targetFile));
            //判断上传是否成功
            if(!uploadRes){
                return null;
            }
            //上传完毕删除文件夹下
            fileDir.delete();
        } catch (IOException e) {
            log.error("上传文件异常："+e);
        }

        //返回上传文件的名称
        return targetFile.getName();
    }
}
