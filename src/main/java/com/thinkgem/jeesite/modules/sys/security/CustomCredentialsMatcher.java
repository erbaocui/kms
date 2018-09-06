package com.thinkgem.jeesite.modules.sys.security;

import com.thinkgem.jeesite.common.utils.MD5Util;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.credential.SimpleCredentialsMatcher;

/**
 * Created by USER on 2018/6/20.
 */
public class CustomCredentialsMatcher extends SimpleCredentialsMatcher {

    @Override
    public boolean doCredentialsMatch(AuthenticationToken authcToken, AuthenticationInfo info) {
        UsernamePasswordToken token = (UsernamePasswordToken) authcToken;

        Object tokenCredentials = MD5Util.md5(String.valueOf(token.getPassword()));
        Object accountCredentials = info.getCredentials();
        //将密码加密与系统加密后的密码校验，内容一致就返回true,不一致就返回false
        return equals(tokenCredentials, accountCredentials);
    }
   public  static void main(String args[]){
        char[] abc="admin".toCharArray();
        String b=String.valueOf(abc);
       System.out.println(MD5Util.md5("admin"));
       System.out.println(b);
       System.out.println(MD5Util.md5(b));
   }

}
