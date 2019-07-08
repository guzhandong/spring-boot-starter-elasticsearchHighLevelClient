package com.guzhandong.springframework.boot.elasticsearch.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*************************************************
 * @Package:      com.guzhandong.springframework.boot.ssh2.utils
 * @ClassName:    LogUtil
 * @Description:  日志工具，所的功能方法都是调用SLF4J的门面方法，在方法中添加了日志级别判断，提高日志效率，防止开发人员忘记写
 * @Author:       guzhandong
 * @CreateDate:   23/04/2016
 * @History：
 * @UpdateUser:   ${user}
 * @UpdateDate:   ${date} ${time}
 * @UpdateRemark: [说明本次修改内容]
 * @Version:      [v1.0]
 **************************************************
 */
public class LogUtil {

    private Logger logger;

    public static LogUtil getLogger (Logger logger) {
        return  new LogUtil(logger);
    }

    public static LogUtil getLogger (Class<?> clazz) {
        return  new LogUtil(clazz);
    }



    private LogUtil(Logger logger) {
        this.logger = logger;
    }
    private LogUtil(Class<?> clazz) {
        this.logger = LoggerFactory.getLogger(clazz);
    }

    public void info(String msg, Object ... args){
        if (logger.isInfoEnabled()) {
            logger.info(msg , args);
        }
    }
    public void debug(String msg,Object ... args){
        if (logger.isDebugEnabled()) {
            logger.debug(msg , args);
        }
    }
    public void error(String msg,Object ... args){
        if (logger.isErrorEnabled()) {
            logger.error(msg , args);
        }
    }
    public void warn(String msg,Object ... args){
        if (logger.isWarnEnabled()) {
            logger.warn(msg , args);
        }
    }
    public void info(String msg){
        if (logger.isInfoEnabled()) {
            logger.info(msg);
        }
    }
    public void debug(String msg){
        if (logger.isDebugEnabled()) {
            logger.debug(msg);
        }
    }
    public void error(String msg){
        if (logger.isErrorEnabled()) {
            logger.error(msg);
        }
    }
    public void warn(String msg){
        if (logger.isWarnEnabled()) {
            logger.warn(msg);
        }
    }

}
