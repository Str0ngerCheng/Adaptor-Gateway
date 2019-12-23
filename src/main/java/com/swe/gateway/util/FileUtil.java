package com.swe.gateway.util;

import org.dom4j.DocumentHelper;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import java.io.*;
import java.net.URLDecoder;

/**
 * @author cbw
 */
public class FileUtil {
    /**
     * 以指定的编码读取文件路径指向的文件内容，默认编码方式为UTF-8
     *
     * @param filepath 文件路径
     * @param encodingType 文件编码方式
     * @return 返回文件内容
     */
    public static String readFileContent(String filepath, String encodingType) {
        String content = "";
        InputStreamReader isr;
        if (encodingType == null) {
            encodingType = "UTF-8";
        }
        try {
            isr = new InputStreamReader (
                    new FileInputStream (new File (filepath)), encodingType);
            BufferedReader br = new BufferedReader (isr);
            String tempcontent = "";
            while ((tempcontent = br.readLine()) != null) {
                content += tempcontent;
            }
            isr.close ();
        } catch (Exception e) {

            e.printStackTrace();
        }

        return content;
    }

    /**
     * 将字符串转换为XML格式，以便于测试，默认编码方式为UTF-8
     *
     * @param str 要转换的内容
     * @return 返回XML内容
     */
    public static String formatXml(String str) throws Exception {
        org.dom4j.Document document = null;
        document = DocumentHelper.parseText(str);
        // 格式化输出格式
        OutputFormat format = OutputFormat.createPrettyPrint();
        format.setEncoding ("UTF-8");
        StringWriter writer = new StringWriter ();
        // 格式化输出流
        XMLWriter xmlWriter = new XMLWriter(writer, format);
        // 将document写入到输出流
        xmlWriter.write(document);
        xmlWriter.close();
        return writer.toString();
    }

    /**
     * 获取项目的根目录，用于读取resources文件夹下的文件
     *
     * @return 项目根目录路径
     */
    public static String getResourcePath(){
        String path = new FileUtil().getClass().getClassLoader().getResource(".").getPath();
        try {
            path = URLDecoder.decode(path, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return path;
    }

    /**
     * 获取example文件夹路径 其中放着SOS服务的请求实例
     *
     * @return example文件夹路径
     */
    public static String getExamplePath(){
        return getResourcePath()+"example/";
    }

    /**
     * 获取模板文件路径 模板文件用作封装InsertObservation请求的模板
     * @return 模板文件路径
     */
    public static String getInstantFile(){
        return getResourcePath()+"templateFile/InsertObservation_Instant.xml";
    }

    /**
     * 获取临时文件路径 临时文件用作将动态生成的InsertObservation请求写入
     * @return 临时文件路径
     */
    public static String getTemporaryFile(){
        return getResourcePath()+"templateFile/InsertObservation_Temp.xml";
    }

}
