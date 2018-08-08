package com.gxc.phoneattribution;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.util.regex.Pattern;
import java.util.regex.Matcher;


public class MobileFromUtil {
    //正则表达式,抽取手机归属地
    //public static final String REGEX_GET_MOBILE="(?is)(<TR[^>]+>[/s]*<TD[^>]+>[/s]?卡号归属地[/s]?</TD>[/s]?<TD[^>]+>([^<]+)</TD>[/s]*</TR>)"; //2:from
    //public static final String REGEX_GET_MOBILE= "(浙江&nbsp;杭州市)"; //2:from
    public static final String REGEX_GET_MOBILE="浙江&nbsp;杭州市"; //2:from

    //正则表达式,审核要获取手机归属地的手机是否符合格式,可以只输入手机号码前7位
    public static final String REGEX_IS_MOBILE=
            "(?is)(^1[3|4|5|8][0-9]\\d{4,8}$)";

    /**
     * 获得手机号码归属地
     *
     * @param mobileNumber
     * @return
     * @throws Exception
     */
    public static String getMobileFrom(String mobileNumber) throws Exception {
        if(!veriyMobile(mobileNumber)){
            throw new Exception("不是完整的11位手机号或者正确的手机号前七位");
        }

        HttpGet get = null;
        CloseableHttpClient client = HttpClients.createDefault();
        int httpStatusCode;

        HttpEntity entity;
        String result=null;
        String userAgent = "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.181 Safari/537.36";

        try {

            get=new HttpGet("https://tcc.taobao.com/cc/json/mobile_tel_segment.htm?tel=" + mobileNumber);
           // HttpParams params = new BasicHttpParams();

            get.setHeader("User-Agent",userAgent);
            get.setHeader("Content-Encoding","utf-8");

            HttpResponse response = client.execute(get);
            httpStatusCode=response.getStatusLine().getStatusCode();

            if(httpStatusCode!=200){
                throw new Exception("网页内容获取异常！Http Status Code:"+httpStatusCode);
            }

            entity=response.getEntity();
            result =  EntityUtils.toString(entity);
            String[] str = result.split(",");
            System.out.println(str[str.length -1].split("'")[1]);
        } catch (RuntimeException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }finally{
            client.close();
        }

        return result;

    }


    /**
     * 从www.ip138.com返回的结果网页内容中获取手机号码归属地,结果为：省份 城市
     *
     * @param htmlSource
     * @return
     */
    public static String parseMobileFrom(String htmlSource){
        Pattern p=null;
        Matcher m=null;
        String result=null;

        p= Pattern.compile(REGEX_GET_MOBILE);
        m=p.matcher(htmlSource);

        while(m.find()){
            result=m.group(0).replaceAll("&nbsp;", " ");
//            if(m.start(2)>0){
//                result=m.group(2);
//                result=result.replaceAll("&nbsp;", " ");
//            }
        }


        return result;

    }

    /**
     * 验证手机号
     * @param mobileNumber
     * @return
     */
    public static boolean veriyMobile(String mobileNumber){
        Pattern p=null;
        Matcher m=null;

        p=Pattern.compile(REGEX_IS_MOBILE);
        m=p.matcher(mobileNumber);

        return m.matches();
    }

    /**
     * 测试
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        System.out.println(getMobileFrom("15757126215"));
    }

}