package com.xlmall.util;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.net.ftp.FTPClient;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

/**
 * FTP文件上传类
 */
@Data
@Slf4j
public class FTPUtil {

    private static final String ftpIp = PropertiesUtil.getProperty("ftp.server.ip");
    private static final String ftpUser = PropertiesUtil.getProperty("ftp.user");
    private static final String ftpPwd = PropertiesUtil.getProperty("ftp.pass");

    private String ip;
    private Integer port;
    private String user;
    private String pwd;
    private FTPClient ftpClient;

    public FTPUtil(String ip,Integer port,String user,String pwd){
        this.ip = ip;
        this.port = port;
        this.user = user;
        this.pwd = pwd;
    }

    /**
     * 上传文件
     * @param fileList
     * @return
     */
    public static boolean uploadFile(List<File> fileList) throws IOException{
        FTPUtil ftpUtil = new FTPUtil(ftpIp,21,ftpUser,ftpPwd);
        log.info("开始连接FTP服务器");
        boolean result = ftpUtil.uploadFile("img",fileList);
        log.info("上传结束，上传结果{}",result);
        return result;
    }

    //上传文件
    private boolean uploadFile(String remotePath,List<File> fileList) throws IOException{
        boolean uploaded = false;
        FileInputStream fis = null;
        //连接FTP服务器
        if(connectServer(this.ip,this.port,this.user,this.pwd)){
            try {
                ftpClient.changeWorkingDirectory(remotePath);//更换工作文件夹
                ftpClient.setBufferSize(1024);//设置缓冲区大小
                ftpClient.setControlEncoding("UTF-8");//设置编码
                ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);//设置文件类型为二进制类型，可以减少乱码问题
                ftpClient.enterLocalPassiveMode();//打开本地被动模式
                for(File fileItem:fileList){
                    fis = new FileInputStream(fileItem);
                    //存储文件，需要传递文件名和文件流
                    ftpClient.storeFile(fileItem.getName(),fis);
                }
                uploaded = true;
            } catch (IOException e) {
                log.error("切换工作文件夹异常："+e);
            }finally {
                fis.close();
                ftpClient.disconnect();
            }
        }
        return uploaded;
    }

    /**
     * 连接FTP服务器
     * @param ip
     * @param port
     * @param user
     * @param pwd
     * @return 连接是否成功
     */
    private boolean connectServer(String ip,Integer port,String user,String pwd){
        boolean isSuccess = false;
        ftpClient = new FTPClient();
        try {
            ftpClient.connect(ip);
            isSuccess = ftpClient.login(user,pwd);
        } catch (IOException e) {
            log.error("连接FTP服务器异常："+e);
        }
        return isSuccess;
    }
}
