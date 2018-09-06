package com.thinkgem.jeesite.common.config;

/**
 * Created by USER on 2018/4/25.
 */
public class Constant {
    //ftp
    public static final Integer FTP_PORT=Integer.valueOf(Global.getConfig("ftp.port"));
    public static final String FTP_IP=Global.getConfig("ftp.ip");
    public static final String FTP_USER=Global.getConfig("ftp.user");
    public static final String FTP_PASSWORD=Global.getConfig("ftp.password");
    public static final String FTP_ROOT=Global.getConfig("ftp.root");
    //swf
    public static final String SWF_COMMAND=Global.getConfig("swf.command");
    public static final String SWF_ROOT=Global.getConfig("swf.rootPath");
    //office
    public static final String OFFICE_HOME=Global.getConfig("office.home");
    public static final Integer OFFICE_PORT=Integer.valueOf(Global.getConfig("office.port"));


}
