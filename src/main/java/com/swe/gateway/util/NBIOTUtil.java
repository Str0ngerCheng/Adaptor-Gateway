package com.swe.gateway.util;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dom4j.DocumentHelper;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;


/**
 * @author ljj
 */

public class NBIOTUtil {
    public static final Logger logger = LogManager.getLogger(NBIOTUtil.class.getName());

    /**
     * 以指定的编码读取文件路径指向的文件内容，默认编码方式为UTF-8
     *
     * @param filepath 文件路径
     * @param encodingType 文件编码方式
     * @return 返回文件内容
     */

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


    public static void main(String []args){
        //System.out.println(HttpUtil.result());
    }
    public static class HttpUtil {

        public static String result(String deviceID) {
            //接口地址
            String requestUrl =  "http://jingkongyun.com/monitorcloud/platform/gemho/apiDev/getDataByDevice?"+"deviceId="+deviceID+"&start="+(System.currentTimeMillis()/1000-3800)+"&end="+System.currentTimeMillis()/1000;
            String string = httpRequest(requestUrl);

            return string;


        }


        public static String httpRequest(String requestUrl) {
            //buffer用于接受返回的字符
            StringBuffer buffer = new StringBuffer();
            try {
                //建立URL
                URL url = new URL(requestUrl);
                //打开http连接
                HttpURLConnection httpUrlConn = (HttpURLConnection) url.openConnection();
                httpUrlConn.setDoInput(true);
                httpUrlConn.setRequestMethod("GET");
                httpUrlConn.connect();

                //获得输入
                InputStream inputStream = httpUrlConn.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "utf-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                //将bufferReader的值给放到buffer里
                String str = null;
                while ((str = bufferedReader.readLine()) != null) {
                    buffer.append(str);
                }
                //关闭bufferReader和输入流
                bufferedReader.close();
                inputStreamReader.close();
                inputStream.close();
                //断开连接
                httpUrlConn.disconnect();

            } catch (Exception e) {
                logger.error(e);
                e.printStackTrace();
            }
            //返回字符串
            return buffer.toString();
        }

        static public void main(String[] argc)
        {
//        try{
//            FileOutputStream out=new FileOutputStream(getTemporaryFile ());
//            PrintStream p=new PrintStream(out);
//            for(int i=0;i<10;i++)
//                p.println("This is "+i+" line");
//        } catch (FileNotFoundException e){
//            e.printStackTrace();
//        }
//            // System.out.println (readFileContent (getTemporaryFile (),"UTF-8") );
        }
    }
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
            FileInputStream fileInputStream=new FileInputStream (new File (filepath));
            isr = new InputStreamReader (fileInputStream, encodingType);
            BufferedReader br = new BufferedReader (isr);
            String tempcontent = "";
            while ((tempcontent = br.readLine()) != null) {
                content += tempcontent;
            }
            br.close();
            isr.close ();
            fileInputStream.close();
            } catch (Exception e) {

            e.printStackTrace();
        }

        return content;
    }


    /**
     * 获取项目的根目录，用于读取resources文件夹下的文件
     *
     * @return 项目根目录路径
     */
    public static String getResourcePath(){
        String path = System.getProperty("user.dir");
        try {
            path = URLDecoder.decode(path, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return path;
    }

    /**
     * 获取模板文件路径 模板文件用作封装InsertObservation请求的模板
     * @return 模板文件路径
     */
    public static String getInstantFile(){
        return getResourcePath()+"/templateFile/InsertObservation_Instant.xml";
    }

    public static String getExamplePath(){
        return getResourcePath()+"/example/";
    }
    /**
     * 获取临时文件路径 临时文件用作将动态生成的InsertObservation请求写入
     * @return 临时文件路径
     */
    public static String getTemporaryFile(){
        return getResourcePath()+"/templateFile/InsertObservation_Temp.xml";
    }

    /**
     * 以指定的编码读取文件路径指向的文件内容，默认编码方式为UTF-8
     *
     * @param filepath 文件路径
     * @param encodingType 文件编码方式
     * @return 返回文件内容
     */



}



