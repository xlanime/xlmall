package com.xlmall.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

/**
 * 读取配置文件工具类
 */
@Slf4j
public class PropertiesUtil {

//    private static Logger logger = LoggerFactory.getLogger(PropertiesUtil.class);

    private static Properties props;

    /**
     * 静态块，初始化配置文件的读取
     */
    static {
        String fileName = "xlmall.properties";
        props = new Properties();
        try {
            props.load(new InputStreamReader(PropertiesUtil.class.getClassLoader().
                    getResourceAsStream(fileName),"UTF-8"));
        } catch (IOException e) {
            log.error("配置文件读取异常",e);
        }
    }


    /**
     * 根据key读取配置文件内容
     * @param key
     * @return
     */
    public static String getProperty(String key){
        //使用trim函数去掉字符串开头和结尾的空格。
        String value = props.getProperty(key.trim());
        if(StringUtils.isBlank(value)){
            return null;
        }
        return value.trim();
    }

    /**
     * 根据key读取配置文件内容,且如果value为空，则返回defaultValue
     * @param key
     * @param defaultValue
     * @return
     */
    public static String getProperty(String key,String defaultValue){

        String value = props.getProperty(key.trim());
        if(StringUtils.isBlank(value)){
            value = defaultValue;
        }
        return value.trim();
    }



}
