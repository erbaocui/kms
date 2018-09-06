package com.thinkgem.jeesite.common.service.converter;

import com.thinkgem.jeesite.common.config.Constant;
import org.springframework.http.HttpRequest;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created with IntelliJ IDEA.
 * User: meng.zm
 * Date: 14-3-20
 * Time: 下午3:16
 * To change this template use File | Settings | File Templates.
 */

public class PdfConverter extends AbstractConverter{

    @Override
    public String File2Swf(String inputFullFile,String outputFile)throws Exception{
        //String SwfToolCommand = BRWebApplicationContext.getWebRootPath().replace("upload","SWFTools")
       // String SwfToolCommand ="D:\\Java\\SWFTools"+ File.separator + "pdf2swf.exe";
        String SwfToolCommand= Constant.SWF_COMMAND;
       // String SwfToolCommand="D:/Java/SWFTools/pdf2swf.exe";
        //String swfRootPath = BRWebApplicationContext.getWebRootPath() + File.separator + "swf";
        //String swfRootPath="D:\\upload";
        String swfRootPath=Constant.SWF_ROOT;
        //String swfRootPath="D:/upload";
       // String swfRootPath=localPath;
        Runtime runtime = Runtime.getRuntime();
        File file  = new File(swfRootPath);
        if(!file.exists()){
            file.mkdirs();
        }
        String command = SwfToolCommand + " " + inputFullFile + " " + "-o" + " " + swfRootPath + "/" + outputFile + ".swf";
        Process process = runtime.exec(command);
        InputStream is = process.getInputStream();
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String line = "";
        while ((line = br.readLine())!=null){
            System.out.println(line);
        }
        System.out.println("转换成功!");
        return swfRootPath + File.separator +  outputFile +".swf";
    }

    public static  void main( String[] args) throws Exception{
        PdfConverter officeConverter=new PdfConverter();
       // officeConverter.File2Swf("D:/upload/applypay20180612143844102.pdf","applypay20180612143844102");
    }

}
